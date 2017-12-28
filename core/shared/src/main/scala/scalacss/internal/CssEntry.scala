package scalacss.internal

import japgolly.univeq.UnivEq

import scala.collection.{mutable => scm}

sealed trait CssEntry

object CssEntry {

  case class Style(mq     : CssMediaQueryO,
                   sel    : CssSelector,
                   content: NonEmptyVector[CssKV]) extends CssEntry

  case class Keyframes(name  : KeyframeAnimationName,
                       frames: scm.LinkedHashMap[KeyframeSelector, StyleStream]) extends CssEntry

  case class FontFace(fontFamily  : String,
                      src         : NonEmptyVector[String],
                      fontStretch : Option[Value],
                      fontStyle   : Option[Value],
                      fontWeight  : Option[Value],
                      unicodeRange: Option[UnicodeRange]) extends CssEntry

  implicit def univEqStyle          : UnivEq[Style    ] = UnivEq.derive
  implicit def univLHM[K: UnivEq, V]: UnivEq[scm.LinkedHashMap[K, V]] = UnivEq.force
  implicit def univEqKeyframes      : UnivEq[Keyframes] = UnivEq.derive
  implicit def univEqFontFace       : UnivEq[FontFace ] = UnivEq.derive
  implicit def univEq               : UnivEq[CssEntry ] = UnivEq.derive
}
