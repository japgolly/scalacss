package scalacss.internal

import scala.collection.immutable.SortedMap
import scala.runtime.AbstractFunction1

/**
 * A high-level style, that can describe a subject and its children in a variety of conditions.
 *
 * ==Types==
 *
 *   - [[StyleS]]: Static.    `{s}`
 *   - [[StyleF]]: Function.  `{i => s}`
 *   - [[StyleA]]: Applicable. `class="…"`
 */
sealed trait Style

/**
 * A single style that applied to a single subject.
 */
sealed abstract class Style1 extends Style

object Style {

  /**
   * Unsafe extensions to a style.
   *
   * Unsafe because by using this, you knowingly opt-out of type-safety.
   * There will be no relationship visible to the compiler between this additional CSS and its intended targets.
   *
   * An example of what this would be useful for, is styling the entire page, declaring the default font and size,
   * the properties of all `h1` elements, etc.
   */
  type UnsafeExts = Vector[UnsafeExt]

  /**
   * @param sel A CSS selector based on the intended parent's selector.
   *
   *            Example: `(_+" h1")` will style all child `h1` elements.
   *
   *            Example: `(_+".debug")` will specify a style only active when a `"debug"` class is present.
   */
  final case class UnsafeExt(sel: CssSelector => CssSelector, cond: Cond, style: StyleS)
}

/**
 * A static style.
 *
 * @param className Manually specifies this style's class name. By default it is automatically generated.
 * @param addClassNames Additional class names to be appended to the resulting [[StyleA]].
 *                      Allows ScalaCSS styles to use classname-based CSS libraries like Bootstrap.
 */
final case class StyleS(data         : SortedMap[Cond, AVs],
                        unsafeExts   : Style.UnsafeExts,
                        className    : Option[ClassName],
                        addClassNames: Vector[ClassName],
                        warnings     : Vector[Warning]) extends Style1 {

  def compose   (r: StyleS     )(implicit c: Compose): StyleS      = c(this, r)
  def compose[I](r: StyleF  [I])(implicit c: Compose): StyleF  [I] = c(this, r)
  def compose[I](r: StyleF.P[I])(implicit c: Compose): StyleF.P[I] = c(this, r)

  override def toString = inspect

  def inspectCss: StyleStream =
    Css.styleA(StyleA(className getOrElse ClassName("???"), addClassNames, this))(Env.empty)

  def inspect: String =
    StringRenderer.defaultPretty(inspectCss)

//  def conflicts: Map[Cond, Set[AV]] = {
//    data.toStream
//      .map { case (c, avs) => (c, AttrCmp.conflicts(avs.whole)) }
//      .filter(_._2.nonEmpty)
//      .toMap
//  }
}

object StyleS {
  /** Helper method for common case where only data is specified. */
  def data(d: SortedMap[Cond, AVs]): StyleS =
    new StyleS(d, Vector.empty, None, Vector.empty, Vector.empty)

  /** Helper method for common case where only one condition is specified. */
  def data1(c: Cond, avs: AVs): StyleS =
    data(empty.data.updated(c, avs))

  val empty: StyleS =
    data(SortedMap.empty)
}

/**
 * A function to a style. A style that depends on input provided when used.
 *
 * @tparam I Input required by the style.
 * @param domain The function domain. All possible (or legal) inputs to this function.
 */
final class StyleF[I](val f: I => StyleS, val domain: Domain[I]) extends Style1 {

  def mod(g: StyleS => StyleS): StyleF[I] =
    new StyleF(g compose f, domain)

  def compose   (r: StyleS     )(implicit c: Compose): StyleF[I]                   = c(this, r)
  def compose[J](r: StyleF  [J])(implicit c: Compose): StyleF[(I, J)]              = c(this, r)
  def compose[J](r: StyleF.P[J])(implicit c: Compose): Domain[J] => StyleF[(I, J)] = c(this, r)
}

object StyleF {

  def apply[I](f: I => StyleS): StyleF.P[I] =
    P(new StyleF(f, _))

  /**
   * A `Domain[I] => StyleF[I]` with additional methods.
   *
   * The P can stand for ''P''ending, or ''P''re, in that a `StyleF.P` is nearly a `StyleF`.
   * Names are hard ok? Shuttup!
   */
  sealed trait P[I] extends AbstractFunction1[Domain[I], StyleF[I]] {
    def compose   (r: StyleS     )(implicit c: Compose): StyleF.P[I]                              = c(this, r)
    def compose[J](r: StyleF  [J])(implicit c: Compose): Domain[I] => StyleF[(I, J)]              = c(this, r)
    def compose[J](r: StyleF.P[J])(implicit c: Compose): (Domain[I], Domain[J]) => StyleF[(I, J)] = c(this, r)
  }
  def P[I](f: Domain[I] => StyleF[I]): P[I] =
    new P[I] { override def apply(d: Domain[I]) = f(d) }
}

