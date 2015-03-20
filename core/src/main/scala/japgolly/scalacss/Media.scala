package japgolly.scalacss

import scalaz.{Cord, \/}
import scalaz.syntax.traverse1._
import japgolly.scalacss.{Resolution => Res}

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
//  object Feature {

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
//  }

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

  case class Query(head: TypeExpr \/ Feature, tail: Vector[Feature])

  private final val media = Cord("@media ")
  private final val `:`   = Cord(":")
  private final val and   = Cord(" and ")
  private final val `,`   = Cord(", ")
  private final val `(`   = Cord("(")
  private final val `)`   = Cord(")")
  private final val min   = Cord("min-")
  private final val max   = Cord("max-")

  private def paren(c: Cord): Cord =
    `(` ++ c ++ `)`

  private def opt[T](ov: Option[T], name: Cord)(implicit tc: T => Cord): Cord =
    ov.fold(name)(name ++ `:` ++ _)

  private def cssValueExpr[T](e: ValueExpr[T], name: Cord)(implicit tc: T => Cord): Cord =
    e match {
      case Eql(t) =>        name ++ `:` ++ t
      case Min(t) => min ++ name ++ `:` ++ t
      case Max(t) => max ++ name ++ `:` ++ t
    }

  private def cssValueExprO[T](o: Option[ValueExpr[T]], name: Cord)(implicit tc: T => Cord): Cord =
    o.fold(name)(cssValueExpr(_, name))

  private implicit def cssInt   (i: Int)      : Cord = i.toString
  private implicit def cssRes   (r: Res[_])   : Cord = r.value
  private implicit def cssLength(l: Length[_]): Cord = l.value
  private implicit def cssRatio (r: Ratio)    : Cord = r.value

  val cssFeature: Feature => Cord = {
    val x: Feature => Cord = {
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
      case Orientation(v)       => Cord("orientation:", v.value)
      case Scan(v)              => Cord("scan:", v.value)
      case Grid(v)              => opt(v, "grid")
    }
    // Parentheses are required around expressions; failing to use them is an error.
    f => paren(x(f))
  }

  val cssTypeExpr: TypeExpr => Cord = {
    case Just(t) => t.value
    case Not(t)  => Cord("not ") ++ t.value  // Applies to whole Query. Don't wrap in parenthesis.
    case Only(t) => Cord("only ") ++ t.value // Applies to whole Query. Don't wrap in parenthesis.
  }

  def cssQuery(q: Query): Cord = {
    val z = q.head.fold(cssTypeExpr, cssFeature)
    q.tail.foldLeft(z)(_ ++ and ++ cssFeature(_))
  }

  def cssQueries(qs: NonEmptyVector[Query]): Cord =
    qs.foldMapLeft1(cssQuery)(_ ++ `,` ++ cssQuery(_))

  def css(qs: NonEmptyVector[Query]): CssMediaQuery =
    (media ++ cssQueries(qs)).toString
}
