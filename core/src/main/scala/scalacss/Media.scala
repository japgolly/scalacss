package scalacss

import scalaz.{Equal, \/}
import scalaz.syntax.equal._
import scalacss.{Resolution => Res}

object Media {

  /** bits per color component */
  type ColorBits = Int

  sealed abstract class OrientationValue(final val value: String)
  case object Landscape extends OrientationValue("landscape")
  case object Portrait  extends OrientationValue("portrait")

  sealed abstract class ScanValue(final val value: String)
  case object Progressive extends ScanValue("progressive")
  case object Interface   extends ScanValue("interface")

  sealed trait ValueExpr[T]
  case class Eql[T](eql: T) extends ValueExpr[T]
  case class Max[T](max: T) extends ValueExpr[T]
  case class Min[T](min: T) extends ValueExpr[T]

  sealed trait Feature

  /** Indicates the number of bits per color component of the output device.  If the device is not a color device, this value is zero. */
  case class Color(bits: Option[ValueExpr[ColorBits]]) extends Feature

  /** Indicates the number of entries in the color look-up table for the output device. */
  case class ColorIndex(index: Option[ValueExpr[Int]]) extends Feature

  /** Describes the aspect ratio of the targeted display area of the output device. */
  case class AspectRatio(ratio: ValueExpr[Ratio]) extends Feature

  /** The height media feature describes the height of the output device's rendering surface (such as the height of the viewport or of the page box on a printer). */
  case class Height[N](length: ValueExpr[Length[N]]) extends Feature

  case class Width[N](length: ValueExpr[Length[N]]) extends Feature

  /** Describes the aspect ratio of the output device. */
  case class DeviceAspectRatio(ratio: ValueExpr[Ratio]) extends Feature

  /** Describes the height of the output device (meaning the entire screen or page, rather than just the rendering area, such as the document window). */
  case class DeviceHeight[N](length: ValueExpr[Length[N]]) extends Feature

  /** Describes the width of the output device (meaning the entire screen or page, rather than just the rendering area, such as the document window). */
  case class DeviceWidth[N](length: ValueExpr[Length[N]]) extends Feature

  /** Indicates the number of bits per pixel on a monochrome (greyscale) device.  If the device isn't monochrome, the device's value is 0. */
  case class Monochrome(bitsPerPx: Option[ValueExpr[Int]]) extends Feature

  /** Indicates the resolution (pixel density) of the output device. */
  case class Resolution[N](res: ValueExpr[Res[N]]) extends Feature

  /** Indicates whether the viewport is in landscape (the display is wider than it is tall) or portrait (the display is taller than it is wide) mode. */
  case class Orientation(value: OrientationValue) extends Feature

  /** Describes the scanning process of television output devices. */
  case class Scan(value: ScanValue) extends Feature

  /** Determines whether the output device is a grid device or a bitmap device.  If the device is grid-based (such as a TTY terminal or a phone display with only one font), the value is 1.  Otherwise it is zero. */
  case class Grid(value: Option[Int]) extends Feature

  sealed abstract class TypeA(final val value: String)
  case object All extends TypeA("all")

  sealed abstract class Type(value: String) extends TypeA(value)
  case object Aural      extends Type("aural")
  case object Braille    extends Type("braille")
  case object Embossed   extends Type("embossed")
  case object Handheld   extends Type("handheld")
  case object Print      extends Type("print")
  case object Projection extends Type("projection")
  case object Screen     extends Type("screen")
  case object TTY        extends Type("tty")
  case object TV         extends Type("tv")

  sealed trait TypeExpr
  case class Just(t: TypeA) extends TypeExpr
  case class Only(t: TypeA) extends TypeExpr
  case class Not (t: TypeA) extends TypeExpr

  // Hmmm, well this will never be true when false, it shouldn't be false when true.
  // Should really use Shapeless' typeclass derivation here...
  implicit def queryEquality: Equal[Query] = Equal.equalA

  case class Query(head: TypeExpr \/ Feature, tail: Vector[Feature]) extends FeatureOps[Query] {
    override protected def F = f => new Query(head, tail :+ f)

    override def toString =
      css(NonEmptyVector one this)

    def +:(qs: Vector[Query]): Vector[Query] =
      if (qs.exists(_ === this)) qs else qs :+ this

    def +(q2: Query): Vector[Query] =
      Vector1(this) +: q2

    def &(q2: Query): Cond =
      Cond(None, this + q2)
  }

  trait TypeAOps[Out] {
    protected def T: TypeA => Out
    final def all       : Out = T(All)
    final def aural     : Out = T(Aural)
    final def braille   : Out = T(Braille)
    final def embossed  : Out = T(Embossed)
    final def handheld  : Out = T(Handheld)
    final def print     : Out = T(Print)
    final def projection: Out = T(Projection)
    final def screen    : Out = T(Screen)
    final def tty       : Out = T(TTY)
    final def tv        : Out = T(TV)
  }

  trait FeatureOps[Out] {
    protected def F: Feature => Out

    final def color                 : Out = F(Color(None))
    final def color   (v: ColorBits): Out = F(Color(Some(Eql(v))))
    final def minColor(v: ColorBits): Out = F(Color(Some(Min(v))))
    final def maxColor(v: ColorBits): Out = F(Color(Some(Max(v))))

    final def colorIndex           : Out = F(ColorIndex(None))
    final def colorIndex   (v: Int): Out = F(ColorIndex(Some(Eql(v))))
    final def minColorIndex(v: Int): Out = F(ColorIndex(Some(Min(v))))
    final def maxColorIndex(v: Int): Out = F(ColorIndex(Some(Max(v))))

    final def aspectRatio   (v: Ratio): Out = F(AspectRatio(Eql(v)))
    final def minAspectRatio(v: Ratio): Out = F(AspectRatio(Min(v)))
    final def maxAspectRatio(v: Ratio): Out = F(AspectRatio(Max(v)))

    final def height   [N](v: Length[N]): Out = F(Height(Eql(v)))
    final def minHeight[N](v: Length[N]): Out = F(Height(Min(v)))
    final def maxHeight[N](v: Length[N]): Out = F(Height(Max(v)))

    final def width   [N](v: Length[N]): Out = F(Width(Eql(v)))
    final def minWidth[N](v: Length[N]): Out = F(Width(Min(v)))
    final def maxWidth[N](v: Length[N]): Out = F(Width(Max(v)))

    final def deviceAspectRatio   (v: Ratio): Out = F(DeviceAspectRatio(Eql(v)))
    final def minDeviceAspectRatio(v: Ratio): Out = F(DeviceAspectRatio(Min(v)))
    final def maxDeviceAspectRatio(v: Ratio): Out = F(DeviceAspectRatio(Max(v)))

    final def deviceHeight   [N](v: Length[N]): Out = F(DeviceHeight(Eql(v)))
    final def minDeviceHeight[N](v: Length[N]): Out = F(DeviceHeight(Min(v)))
    final def maxDeviceHeight[N](v: Length[N]): Out = F(DeviceHeight(Max(v)))

    final def deviceWidth   [N](v: Length[N]): Out = F(DeviceWidth(Eql(v)))
    final def minDeviceWidth[N](v: Length[N]): Out = F(DeviceWidth(Min(v)))
    final def maxDeviceWidth[N](v: Length[N]): Out = F(DeviceWidth(Max(v)))

    final def monochrome           : Out = F(Monochrome(None))
    final def monochrome   (v: Int): Out = F(Monochrome(Some(Eql(v))))
    final def minMonochrome(v: Int): Out = F(Monochrome(Some(Min(v))))
    final def maxMonochrome(v: Int): Out = F(Monochrome(Some(Max(v))))

    final def resolution   [N](v: Res[N]): Out = F(Resolution(Eql(v)))
    final def minResolution[N](v: Res[N]): Out = F(Resolution(Min(v)))
    final def maxResolution[N](v: Res[N]): Out = F(Resolution(Max(v)))

    final def orientation(v: OrientationValue): Out = F(Orientation(v))
    final def landscape                       : Out = F(Orientation(Landscape))
    final def portrait                        : Out = F(Orientation(Portrait))

    final def scan(v: ScanValue): Out = F(Scan(v))
    final def progressive       : Out = F(Scan(Progressive))
    final def interface         : Out = F(Scan(Interface))

    final def grid        : Out = F(Grid(None))
    final def grid(v: Int): Out = F(Grid(Some(v)))
  }

  // Ensure string concat doesn't accidentally call toString on something stupid.
  private implicit class StringExt(val _s: String) extends AnyVal {
    def ~(b: String) = _s + b
  }

  private final def media = "@media "
  private final def `:`   = ":"
  private final def and   = " and "
  private final def `,`   = ", "
  private final def `(`   = "("
  private final def `)`   = ")"
  private final def min   = "min-"
  private final def max   = "max-"

  private def paren(c: String): String =
    `(` ~ c ~ `)`

  private def opt[T](ov: Option[T], name: String)(implicit tc: T => String): String =
    ov.fold(name)(name ~ `:` ~ _)

  private def cssValueExpr[T](e: ValueExpr[T], name: String)(implicit tc: T => String): String =
    e match {
      case Eql(t) =>       name ~ `:` ~ t
      case Min(t) => min ~ name ~ `:` ~ t
      case Max(t) => max ~ name ~ `:` ~ t
    }

  private def cssValueExprO[T](o: Option[ValueExpr[T]], name: String)(implicit tc: T => String): String =
    o.fold(name)(cssValueExpr(_, name))

  private implicit def cssInt   (i: Int)      : String = i.toString
  private implicit def cssRes   (r: Res[_])   : String = r.value
  private implicit def cssLength(l: Length[_]): String = l.value
  private implicit def cssRatio (r: Ratio)    : String = r.value

  val cssFeature: Feature => String = {
    val x: Feature => String = {
      case AspectRatio(v)       => cssValueExpr (v, "aspect-ratio")
      case Height(v)            => cssValueExpr (v, "height")
      case Width(v)             => cssValueExpr (v, "width")
      case DeviceAspectRatio(v) => cssValueExpr (v, "device-aspect-ratio")
      case DeviceHeight(v)      => cssValueExpr (v, "device-height")
      case DeviceWidth(v)       => cssValueExpr (v, "device-width")
      case Color(v)             => cssValueExprO(v, "color")
      case ColorIndex(v)        => cssValueExprO(v, "color-index")
      case Resolution(v)        => cssValueExpr (v, "resolution")
      case Monochrome(v)        => cssValueExprO(v, "monochrome")
      case Orientation(v)       => "orientation:" ~ v.value
      case Scan(v)              => "scan:"        ~ v.value
      case Grid(v)              => opt(v, "grid")
    }
    // Parentheses are required around expressions; failing to use them is an error.
    f => paren(x(f))
  }

  val cssTypeExpr: TypeExpr => String = {
    case Just(t) => t.value
    case Not(t)  => "not "  ~ t.value // Applies to whole Query. Don't wrap in parenthesis.
    case Only(t) => "only " ~ t.value // Applies to whole Query. Don't wrap in parenthesis.
  }

  def cssQuery(q: Query): String = {
    val z = q.head.fold(cssTypeExpr, cssFeature)
    q.tail.foldLeft(z)(_ ~ and ~ cssFeature(_))
  }

  def cssQueries(qs: NonEmptyVector[Query]): String =
    qs.reduceMapLeft1(cssQuery)(_ ~ `,` ~ _)

  def css(qs: NonEmptyVector[Query]): CssMediaQuery =
    media ~ cssQueries(qs)
}
