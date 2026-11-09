package scalacss

import japgolly.univeq._
import scala.reflect.ClassTag

package object internal {

  type ArraySeq[+A] = scala.collection.immutable.ArraySeq[A]
  @inline def ArraySeq = scala.collection.immutable.ArraySeq

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

  type StyleStream    = ArraySeq[CssEntry.Style]
  type KeyframeStream = List[CssEntry.Keyframes]
  type FontFaceStream = List[CssEntry.FontFace]

  /**
   * A stylesheet in its entirety. Normally turned into a `.css` file or a `&lt;style&gt;` tag.
   */
  type Css = ArraySeq[CssEntry]
  implicit def univEqCss: UnivEq[Css] = UnivEq.univEqSciArraySeq

  type WarningMsg = String
  final case class Warning(cond: Cond, msg: WarningMsg)

  @inline def ArraySeq1[A: ClassTag](a: A): ArraySeq[A] =
    ArraySeq(a)

  def optionAppend[A](oa: Option[A], ob: Option[A])(f: (A, A) => A): Option[A] =
    (oa, ob) match {
      case (None   , None   ) => None
      case (Some(a), None   ) => Some(a)
      case (None   , Some(b)) => Some(b)
      case (Some(a), Some(b)) => Some(f(a, b))
    }

  def memo[A: UnivEq, B](f: A => B): A => B = {
    var m = UnivEq.emptyMap[A, B]
    a => m.synchronized {
      m.getOrElse(a, {
        val b = f(a)
        m = m.updated(a, b)
        b
      })
    }
  }
}
