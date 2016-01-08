import scalaz.Equal
import scalaz.std.stream.streamEqual
import scalaz.std.option.optionEqual

package object scalacss {
  private[this] implicit def stringEqual: Equal[String] = Equal.equalA

  /**
   * A CSS value, like `"none"`, `"solid 3px black"`.
   */
  type Value = String

  private[scalacss] final val _important = " !important"

  final case class ClassName(value: String)
  implicit def classNameEquality: Equal[ClassName] = Equal.equalA

  /**
   * Describes the context of a number of CSS attribute-value pairs.
   *
   * Examples: `"div"`, `".debug"`, `"h3.bottom"`, `"a:visited"`.
   */
  type CssSelector = String

  /**
   * A CSS attribute and its corresponding value.
   *
   * Example: `CssKV("margin-bottom", "12px")`
   */
  final case class CssKV(key: String, value: String)
  object CssKV {
    implicit val equality: Equal[CssKV] = Equal.equalA

    final class Lens(val get: CssKV => String, val set: (CssKV, String) => CssKV)
    val key   = new Lens(_.key  , (a, b) => CssKV(b, a.value))
    val value = new Lens(_.value, (a, b) => CssKV(a.key, b))
  }

  /**
   * A media query in CSS.
   *
   * Examples: `"@media screen and (device-aspect-ratio: 16/9)"`.
   */
  type CssMediaQuery  = String
  type CssMediaQueryO = Option[CssMediaQuery]

  /**
    * Keyframes animation in CSS
    */
  type KeyframeAnimationName = ClassName
  type KeyframeAnimationSelector = Percentage[Int]

  sealed trait CssEntry
  case class CssStyleEntry(mq     : CssMediaQueryO,
                           sel    : CssSelector,
                           content: NonEmptyVector[CssKV]) extends CssEntry
  case class CssKeyframesAnimation(name: KeyframeAnimationName,
                                   frames: Map[KeyframeAnimationSelector, StylesStream]) extends CssEntry

  type StylesStream = Stream[CssStyleEntry]
  type KeyframesStream = Stream[CssKeyframesAnimation]

  object CssStyleEntry {
    implicit val equality: Equal[CssStyleEntry] = {
      val A = Equal[CssMediaQueryO]
      val B = Equal[CssSelector]
      val C = Equal[NonEmptyVector[CssKV]]
      new Equal[CssStyleEntry] {
        override val equalIsNatural =
          A.equalIsNatural
        override def equal(a: CssStyleEntry, b: CssStyleEntry): Boolean =
          B.equal(a.sel, b.sel) &&
          A.equal(a.mq, b.mq) &&
          C.equal(a.content, b.content)
      }
    }
  }

  object CssKeyframesAnimation {
    implicit val equality: Equal[CssKeyframesAnimation] = {
      val A = Equal[KeyframeAnimationName]
      new Equal[CssKeyframesAnimation] {
        override val equalIsNatural =
          A.equalIsNatural
        override def equal(a: CssKeyframesAnimation, b: CssKeyframesAnimation): Boolean =
          A.equal(a.name, b.name) &&
            streamEqual[String].equal(a.frames.keys.toStream.map(_.value), b.frames.keys.toStream.map(_.value)) &&
            a.frames.keys.forall(k => streamEqual[CssStyleEntry].equal(a.frames(k), b.frames(k)))
      }
    }
  }

  /**
   * A stylesheet in its entirety. Normally turned into a `.css` file or a `&lt;style&gt;` tag.
   */
  type Css = Stream[CssEntry]
  implicit val cssEquality: Equal[Css] = streamEqual(new Equal[CssEntry] {
    override def equal(a1: CssEntry, a2: CssEntry): Boolean = { (a1, a2) match {
      case (a: CssStyleEntry, b: CssStyleEntry) => CssStyleEntry.equality.equal(a, b)
      case (a: CssKeyframesAnimation, b: CssKeyframesAnimation) => CssKeyframesAnimation.equality.equal(a, b)
      case _ => false
    }}})

  type WarningMsg = String
  final case class Warning(cond: Cond, msg: WarningMsg)

  /**
   * Applicable style.
   *
   * A style that needs no more processing and can be applied to some target.
   *
   * @param addClassNames Additional class names that the style has requested be appended.
   *                      Allows ScalaCSS styles to use classname-based CSS libraries like Bootstrap.
   */
  final case class StyleA(className: ClassName, addClassNames: Vector[ClassName], style: StyleS) {
    /** Value to be applied to a HTML element's `class` attribute. */
    val htmlClass: String =
      (className.value /: addClassNames)(_ + " " + _.value)
  }

  /** Faster than Vector(a) */
  @inline private[scalacss] def Vector1[A](a: A): Vector[A] =
    Vector.empty :+ a

  /**
   * Text to include in generated class names.
   *
   * [[mutable.Register.NameGen]]s can choose to include it in the output class names, or ignore it.
   */
  case class ClassNameHint(value: String)

  /**
    * Keyframes animation
    *
    * @param name Name of animation
    * @param frames Seq of frame definitions
    */
  final case class Keyframes(name: KeyframeAnimationName, frames: Seq[(KeyframeAnimationSelector, StyleA)])
}
