package scalacss.internal

import japgolly.univeq.UnivEq
import scala.annotation.nowarn

final case class ClassName(value: String) {
  assert(value.nonEmpty, "ClassName cannot be empty")
  assert(!value.exists(_.isWhitespace), s"ClassName '$value' cannot contain whitespace")
}

object ClassName {
  implicit def univEq: UnivEq[ClassName] = UnivEq.derive

  def parse(s: String): Iterator[ClassName] =
    s.split("\\s+").iterator.filter(_.nonEmpty).map(apply)
}

/**
  * A CSS attribute and its corresponding value.
  *
  * Example: `CssKV("margin-bottom", "12px")`
  */
final case class CssKV(key: String, value: String)
object CssKV {
  implicit def univEq: UnivEq[CssKV] = UnivEq.derive

  final class Lens(val get: CssKV => String, val set: (CssKV, String) => CssKV)
  val key   = new Lens(_.key  , (a, b) => CssKV(b, a.value))
  val value = new Lens(_.value, (a, b) => CssKV(a.key, b))
}

/** Defines the range of unicode characters the font face supports. */
case class UnicodeRange(from: Int, to: Int) {
  override def toString = "U+%x-%x".format(from, to)
}
object UnicodeRange {
  implicit def univEq: UnivEq[UnicodeRange] = UnivEq.derive
}

/**
 * Text to include in generated class names.
 *
 * [[mutable.Register.NameGen]]s can choose to include it in the output class names, or ignore it.
 */
final case class ClassNameHint(value: String)

object ClassNameHint {
  implicit def univEq: UnivEq[ClassNameHint] = UnivEq.derive
}

/**
  * Animation keyframes.
  *
  * @param name Name of animation.
  * @param frames Frame definitions.
  */
final case class Keyframes(name: KeyframeAnimationName, frames: Seq[(KeyframeSelector, StyleA)])

final case class FontFamily(name: String)

object FontFamily {
  implicit def univEq: UnivEq[FontFamily] = UnivEq.derive
}

/**
  * Font face declaration
  *
  * http://www.w3schools.com/cssref/css3_pr_font-face_rule.asp
  */
final case class FontFace[FF](fontFamily        : FF,
                              src               : NonEmptyVector[String],
                              fontStretchValue  : Option[Value] = None,
                              fontStyleValue    : Option[Value] = None,
                              fontWeightValue   : Option[Value] = None,
                              unicodeRangeValue : Option[UnicodeRange] = None,
                              fontDisplayValue  : Option[Value] = None,
                              extraUnicodeRanges: Vector[UnicodeRange] = Vector.empty) {
  import FontFace._
  def fontDisplay                                       = new FontDisplayBuilder[FF](v => copy(fontDisplayValue  = Some(v)))
  def fontStretch                                       = new FontStretchBuilder[FF](v => copy(fontStretchValue  = Some(v)))
  def fontStyle                                         = new FontStyleBuilder  [FF](v => copy(fontStyleValue    = Some(v)))
  def fontWeight                                        = new FontWeightBuilder [FF](v => copy(fontWeightValue   = Some(v)))
  def unicodeRange(from: Int, to: Int)                  = copy(unicodeRangeValue = Some(UnicodeRange(from, to)), extraUnicodeRanges = Vector.empty)
  def unicodeRange(r1: UnicodeRange, rn: UnicodeRange*) = copy(unicodeRangeValue = Some(r1), extraUnicodeRanges = rn.toVector)
}

object FontFace {
  /** Quotes a font-family string if it contains whitespace and is not already quoted. */
  def quoteIfNeeded(s: String): String = {
    def isQuoted =
      s.length >= 2 && ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")))
    if (s.exists(_.isWhitespace) && !isQuoted)
      Util.quote(s)
    else
      s
  }

  final class FontSrcSelector(val fontFamily: Option[FontFamily]) extends AnyVal {
    def src(src: String, additionalSrc: String*): FontFace[Option[FontFamily]] =
      FontFace(fontFamily, NonEmptyVector(src, additionalSrc.toVector))
  }

  final class FontDisplayBuilder[FF](private val b: Value => FontFace[FF]) extends AnyVal {
    def auto     = b(Literal.Typed.auto.value)
    def block    = b(Literal.block)
    def swap     = b(Literal.swap)
    def fallback = b(Literal.fallback)
    def optional = b(Literal.optional)
  }

  final class FontStretchBuilder[FF](private val b: Value => FontFace[FF]) extends AnyVal {
    def apply(p: Percentage[_]): FontFace[FF]                       = b(p.value)
    def apply(from: Percentage[_], to: Percentage[_]): FontFace[FF] = b(s"${from.value} ${to.value}")
    def condensed      = b(Literal.condensed)
    def expanded       = b(Literal.expanded)
    def extraCondensed = b(Literal.extraCondensed)
    def extraExpanded  = b(Literal.extraExpanded)
    def normal         = b(Literal.Typed.normal.value)
    def semiCondensed  = b(Literal.semiCondensed)
    def semiExpanded   = b(Literal.semiExpanded)
    def ultraCondensed = b(Literal.ultraCondensed)
    def ultraExpanded  = b(Literal.ultraExpanded)
  }

  final class FontStyleBuilder[FF](private val b: Value => FontFace[FF]) extends AnyVal {
    def italic                                              = b(Literal.italic)
    def normal                                              = b(Literal.Typed.normal.value)
    def oblique                                             = b(Literal.oblique)
    def oblique(angle: Value): FontFace[FF]                 = b(s"oblique $angle")
    def oblique(from: Value, to: Value): FontFace[FF]       = b(s"oblique $from $to")
    def oblique(angle: Angle[_]): FontFace[FF]              = b(s"oblique ${angle.value}")
    def oblique(from: Angle[_], to: Angle[_]): FontFace[FF] = b(s"oblique ${from.value} ${to.value}")
  }

  final class FontWeightBuilder[FF](private val b: Value => FontFace[FF]) extends AnyVal {
    def apply(weight: Int): FontFace[FF]        = b(weight.toString)
    def apply(from: Int, to: Int): FontFace[FF] = b(s"$from $to")
    def _100    = b("100")
    def _200    = b("200")
    def _300    = b("300")
    def _400    = b("400")
    def _500    = b("500")
    def _600    = b("600")
    def _700    = b("700")
    def _800    = b("800")
    def _900    = b("900")
    def bold    = b(Literal.bold)
    def bolder  = b(Literal.bolder)
    def lighter = b(Literal.lighter)
    def normal  = b(Literal.Typed.normal.value)
  }
}

sealed abstract class LengthUnit(val value: String)
object LengthUnit {
  case object ch    extends LengthUnit("ch")
  case object cm    extends LengthUnit("cm")
  case object cqb   extends LengthUnit("cqb")
  case object cqh   extends LengthUnit("cqh")
  case object cqi   extends LengthUnit("cqi")
  case object cqmax extends LengthUnit("cqmax")
  case object cqmin extends LengthUnit("cqmin")
  case object cqw   extends LengthUnit("cqw")
  case object dvb   extends LengthUnit("dvb")
  case object dvh   extends LengthUnit("dvh")
  case object dvi   extends LengthUnit("dvi")
  case object dvmax extends LengthUnit("dvmax")
  case object dvmin extends LengthUnit("dvmin")
  case object dvw   extends LengthUnit("dvw")
  case object em    extends LengthUnit("em")
  case object ex    extends LengthUnit("ex")
  case object in    extends LengthUnit("in")
  case object lvb   extends LengthUnit("lvb")
  case object lvh   extends LengthUnit("lvh")
  case object lvi   extends LengthUnit("lvi")
  case object lvmax extends LengthUnit("lvmax")
  case object lvmin extends LengthUnit("lvmin")
  case object lvw   extends LengthUnit("lvw")
  case object mm    extends LengthUnit("mm")
  case object pc    extends LengthUnit("pc")
  case object pt    extends LengthUnit("pt")
  case object px    extends LengthUnit("px")
  case object rem   extends LengthUnit("rem")
  case object svb   extends LengthUnit("svb")
  case object svh   extends LengthUnit("svh")
  case object svi   extends LengthUnit("svi")
  case object svmax extends LengthUnit("svmax")
  case object svmin extends LengthUnit("svmin")
  case object svw   extends LengthUnit("svw")
  case object vb    extends LengthUnit("vb")
  case object vh    extends LengthUnit("vh")
  case object vi    extends LengthUnit("vi")
  case object vmax  extends LengthUnit("vmax")
  case object vmin  extends LengthUnit("vmin")
  case object vw    extends LengthUnit("vw")
  implicit def univEq: UnivEq[LengthUnit] = UnivEq.derive
}

final case class Length[@specialized(scala.Int, scala.Double) N](n: N, u: LengthUnit) {
  def value = {
    val s = ValueT.stripDotZero(n.toString)
    if (s == "0" || s == "-0") "0" else s + u.value
  }

  def *(m: N)(implicit N: Numeric[N]): Length[N] =
    copy(n = N.times(this.n, m))

  def /(m: Double)(implicit N: Numeric[N]): Length[Double] =
    copy(n = N.toDouble(n) / m)

  def unary_-(implicit N: Numeric[N]): Length[N] =
    copy(n = N.negate(this.n))
}
object Length {
  @nowarn("cat=unused")
  implicit def univEq[N: UnivEq]: UnivEq[Length[N]] = UnivEq.derive
}

final case class Percentage[@specialized(scala.Int, scala.Double) N](n: N) {
  def value: Value = ValueT.stripDotZero(n.toString) + "%"
}
object Percentage {
  @nowarn("cat=unused")
  implicit def univEq[N: UnivEq]: UnivEq[Percentage[N]] = UnivEq.derive
  implicit def univEqX: UnivEq[Percentage[_]] = UnivEq.force // TODO This isn't actually true...
}

sealed abstract class ResolutionUnit(val value: String)
object ResolutionUnit {
  /** Dots per inch */
  case object dpi  extends ResolutionUnit("dpi")
  /** Dots per centimeter */
  case object dpcm extends ResolutionUnit("dpcm")
  /** Dots per pixel */
  case object dppx extends ResolutionUnit("dppx")

  implicit def univEq: UnivEq[ResolutionUnit] = UnivEq.derive
}

final case class Resolution[@specialized(scala.Int, scala.Double) N](n: N, u: ResolutionUnit) {
  def value = ValueT.stripDotZero(n.toString) + u.value

  def *(m: N)(implicit N: Numeric[N]): Resolution[N] =
    copy(n = N.times(this.n, m))

  def /(m: Double)(implicit N: Numeric[N]): Resolution[Double] =
    copy(n = N.toDouble(n) / m, u)

  @deprecated("Resolutions can't be negative, so this method is deprecated.", "1.1.0")
  def unary_-(implicit N: Numeric[N]): Resolution[N] =
    copy(n = N.negate(this.n), u)
}

final case class Ratio(x: Int, y: Int) {
  def value = s"$x/$y"
}

object Ratio {
  implicit def univEq: UnivEq[Ratio] = UnivEq.derive

  /** Traditional TV format in the 20th century. */
  def tvTraditional = Ratio(4, 3)

  /** Modern, 'widescreen', TV format. 16:9 */
  def tvWidescreen = Ratio(16, 9)

  /** The most common movie format since the 1960s. 1.85:1 */
  def movieTraditional = Ratio(91, 50)

  /** The 'widescreen', anamorphic, movie format. 2.39:1 */
  def movieWidescreen = Ratio(239, 100)
}

final case class Fr[@specialized(scala.Int, scala.Double) N](n: N) {
  def value: Value = {
    val s = ValueT.stripDotZero(n.toString)
    if (s == "-0")
      "0fr"
    else
      s + "fr"
  }

  def *(m: N)(implicit N: Numeric[N]): Fr[N] =
    copy(n = N.times(this.n, m))

  def /(m: Double)(implicit N: Numeric[N]): Fr[Double] =
    copy(n = N.toDouble(n) / m)
}
object Fr {
  @nowarn("cat=unused")
  implicit def univEq[N: UnivEq]: UnivEq[Fr[N]] = UnivEq.derive
}

sealed abstract class AngleUnit(val value: String)
object AngleUnit {
  /** Degrees (full circle = 360deg). */
  case object deg  extends AngleUnit("deg")
  /** Gradians (full circle = 400grad). */
  case object grad extends AngleUnit("grad")
  /** Radians (full circle = 2π rad). */
  case object rad  extends AngleUnit("rad")
  /** Turns (full circle = 1turn). */
  case object turn extends AngleUnit("turn")

  implicit def univEq: UnivEq[AngleUnit] = UnivEq.derive
}

final case class Angle[@specialized(scala.Int, scala.Double) N](n: N, u: AngleUnit) {
  def value: Value = {
    val s = ValueT.stripDotZero(n.toString)
    if (s == "-0")
      "0" + u.value
    else
      s + u.value
  }

  def *(m: N)(implicit N: Numeric[N]): Angle[N] =
    copy(n = N.times(this.n, m))

  def /(m: Double)(implicit N: Numeric[N]): Angle[Double] =
    copy(n = N.toDouble(n) / m)

  def unary_-(implicit N: Numeric[N]): Angle[N] =
    copy(n = N.negate(this.n))
}
object Angle {
  @nowarn("cat=unused")
  implicit def univEq[N: UnivEq]: UnivEq[Angle[N]] = UnivEq.derive
}
