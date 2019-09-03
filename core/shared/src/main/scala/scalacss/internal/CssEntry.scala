package scalacss.internal

import japgolly.univeq.UnivEq
import scala.collection.immutable.ListMap

sealed trait CssEntry

object CssEntry {

  case class Style(mq     : CssMediaQueryO,
                   sel    : CssSelector,
                   content: NonEmptyVector[CssKV]) extends CssEntry

  case class Keyframes(name  : KeyframeAnimationName,
                       frames: ListMap[KeyframeSelector, StyleStream]) extends CssEntry

  case class FontFace(fontFamily  : String,
                      src         : NonEmptyVector[String],
                      fontStretch : Option[Value],
                      fontStyle   : Option[Value],
                      fontWeight  : Option[Value],
                      unicodeRange: Option[UnicodeRange]) extends CssEntry

  @inline implicit def univEqStyle    : UnivEq[Style    ] = UnivEq.derive
  @inline implicit def univEqKeyframes: UnivEq[Keyframes] = UnivEq.derive
  @inline implicit def univEqFontFace : UnivEq[FontFace ] = UnivEq.derive
  @inline implicit def univEq         : UnivEq[CssEntry ] = UnivEq.derive
}
