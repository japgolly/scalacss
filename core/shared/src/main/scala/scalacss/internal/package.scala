package scalacss

import japgolly.univeq._

package object internal {

  /**
   * A CSS value, like `"none"`, `"solid 3px black"`.
   */
  type Value = String

  private[internal] val _important = " !important"

  /**
   * Describes the context of a number of CSS attribute-value pairs.
   *
   * Examples: `"div"`, `".debug"`, `"h3.bottom"`, `"a:visited"`.
   */
  type CssSelector = String

  /**
   * A media query in CSS.
   *
   * Examples: `"@media screen and (device-aspect-ratio: 16/9)"`.
   */
  type CssMediaQuery  = String
  type CssMediaQueryO = Option[CssMediaQuery]

  /**
   * Keyframe animations in CSS.
   *
   * This is the name of a `@keyframes` group.
   */
  type KeyframeAnimationName = ClassName
  type KeyframeSelector      = Percentage[_]

  type StyleStream    = Vector[CssEntry.Style]
  type KeyframeStream = List[CssEntry.Keyframes]
  type FontFaceStream = List[CssEntry.FontFace]

  /**
   * A stylesheet in its entirety. Normally turned into a `.css` file or a `&lt;style&gt;` tag.
   */
  type Css = Vector[CssEntry]
  implicit def univEqCss: UnivEq[Css] = UnivEq.univEqVector

  type WarningMsg = String
  final case class Warning(cond: Cond, msg: WarningMsg)

  /** Faster than Vector(a) */
  def Vector1[A](a: A): Vector[A] =
    Vector.empty :+ a

  def optionAppend[A](oa: Option[A], ob: Option[A])(f: (A, A) => A): Option[A] =
    (oa, ob) match {
      case (None   , None   ) => None
      case (Some(a), None   ) => Some(a)
      case (None   , Some(b)) => Some(b)
      case (Some(a), Some(b)) => Some(f(a, b))
    }

  def memo[A: UnivEq, B](f: A => B): A => B = {
    val m = scala.collection.mutable.HashMap.empty[A, B]
    a => m.getOrElse(a, {
      val b = f(a)
      m.update(a, b)
      b
    })
  }
}
