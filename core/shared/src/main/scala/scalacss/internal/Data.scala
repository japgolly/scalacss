package scalacss.internal

import japgolly.univeq.UnivEq

final case class ClassName(value: String)
object ClassName {
  implicit def univEq: UnivEq[ClassName] = UnivEq.derive
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

/**
  * Animation keyframes.
  *
  * @param name Name of animation.
  * @param frames Frame definitions.
  */
final case class Keyframes(name: KeyframeAnimationName, frames: Seq[(KeyframeSelector, StyleA)])

/**
  * Font face declaration
  *
  * http://www.w3schools.com/cssref/css3_pr_font-face_rule.asp
  */
final case class FontFace(fontFamily         : String,
                          uniqueName         : Boolean,
                          src                : NonEmptyVector[String],
                          fontStretchValue   : Option[Value] = None,
                          fontStyleValue     : Option[Value] = None,
                          fontWeightValue    : Option[Value] = None,
                          unicodeRangeValue  : Option[UnicodeRange] = None) {
  import FontFace._
  def fontStretch                      = new FontStretchBuilder(v => copy(fontStretchValue  = Some(v)))
  def fontStyle                        = new FontStyleBuilder  (v => copy(fontStyleValue    = Some(v)))
  def fontWeight                       = new FontWeightBuilder (v => copy(fontWeightValue   = Some(v)))
  def unicodeRange(from: Int, to: Int) = copy(unicodeRangeValue = Some(UnicodeRange(from, to)))
}

object FontFace {
  final class FontSrcSelector(val fontFamily: String, val uniqueName: Boolean) {
    def src(src: String, additionalSrc: String*): FontFace =
      FontFace(fontFamily, uniqueName, NonEmptyVector(src, additionalSrc.toVector))
  }

  final class FontStretchBuilder(private val b: Value => FontFace) extends AnyVal {
    def condensed      = b(Literal.condensed)
    def expanded       = b(Literal.expanded)
    def extraCondensed = b(Literal.extraCondensed)
    def extraExpanded  = b(Literal.extraExpanded)
    def normal         = b(Literal.normal)
    def semiCondensed  = b(Literal.semiCondensed)
    def semiExpanded   = b(Literal.semiExpanded)
    def ultraCondensed = b(Literal.ultraCondensed)
    def ultraExpanded  = b(Literal.ultraExpanded)
  }

  final class FontStyleBuilder(private val b: Value => FontFace) extends AnyVal {
    def italic  = b(Literal.italic)
    def normal  = b(Literal.normal)
    def oblique = b(Literal.oblique)
  }

  final class FontWeightBuilder(private val b: Value => FontFace) extends AnyVal {
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
    def normal  = b(Literal.normal)
  }
}

sealed abstract class LengthUnit(val value: String)
object LengthUnit {
  case object cm   extends LengthUnit("cm")
  case object ch   extends LengthUnit("ch")
  case object em   extends LengthUnit("em")
  case object ex   extends LengthUnit("ex")
  case object in   extends LengthUnit("in")
  case object mm   extends LengthUnit("mm")
  case object pc   extends LengthUnit("pc")
  case object pt   extends LengthUnit("pt")
  case object px   extends LengthUnit("px")
  case object rem  extends LengthUnit("rem")
  case object vh   extends LengthUnit("vh")
  case object vmin extends LengthUnit("vmin")
  case object vmax extends LengthUnit("vmax")
  case object vw   extends LengthUnit("vw")
  implicit def univEq: UnivEq[LengthUnit] = UnivEq.derive
}

final case class Length[@specialized(scala.Int, scala.Double) N](n: N, u: LengthUnit) {
  def value = {
    val s = n.toString
    if (s == "0") "0" else s + u.value
  }

  def *(m: N)(implicit N: Numeric[N]): Length[N] =
    copy(n = N.times(this.n, m))

  def /(m: Double)(implicit N: Numeric[N]): Length[Double] =
    copy(n = N.toDouble(n) / m)

  def unary_-(implicit N: Numeric[N]): Length[N] =
    copy(n = N.negate(this.n))
}
object Length {
  implicit def univEq[N: UnivEq]: UnivEq[Length[N]] = UnivEq.derive
}

final case class Percentage[@specialized(scala.Int, scala.Double) N](n: N) {
  def value: Value = n.toString + "%"
}
object Percentage {
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
  def value = n.toString + u.value

  def *(m: N)(implicit N: Numeric[N]): Resolution[N] =
    copy(n = N.times(this.n, m))

  def /(m: Double)(implicit N: Numeric[N]): Resolution[Double] =
    copy(n = N.toDouble(n) / m, u)

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