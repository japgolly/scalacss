package scalacss.internal

import japgolly.univeq._
import scala.annotation.nowarn
import scalacss.internal.{Resolution => Res}

object Media {

  /** bits per color component */
  type ColorBits = Int

  sealed abstract class OrientationValue(final val value: String)
  case object Landscape extends OrientationValue("landscape")
  case object Portrait  extends OrientationValue("portrait")

  sealed abstract class ScanValue(final val value: String)
  case object Progressive extends ScanValue("progressive")
  @deprecated("Use Interlace", "1.1.0")
  case object Interface   extends ScanValue("interface")
  case object Interlace   extends ScanValue("interlace")

  sealed abstract class ColorScheme(final val value: String)
  object ColorScheme {
    case object Light extends ColorScheme("light")
    case object Dark  extends ColorScheme("dark")
    implicit def univEq: UnivEq[ColorScheme] = UnivEq.derive
  }

  sealed abstract class ReducedMotion(final val value: String)
  object ReducedMotion {
    case object NoPreference extends ReducedMotion("no-preference")
    case object Reduce       extends ReducedMotion("reduce")
    implicit def univEq: UnivEq[ReducedMotion] = UnivEq.derive
  }

  sealed abstract class PointerValue(final val value: String)
  object PointerValue {
    case object None   extends PointerValue("none")
    case object Coarse extends PointerValue("coarse")
    case object Fine   extends PointerValue("fine")
    implicit def univEq: UnivEq[PointerValue] = UnivEq.derive
  }

  sealed abstract class HoverValue(final val value: String)
  object HoverValue {
    case object None  extends HoverValue("none")
    case object Hover extends HoverValue("hover")
    implicit def univEq: UnivEq[HoverValue] = UnivEq.derive
  }

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

  /** Detects if the user has requested a light or dark color theme. */
  case class PrefersColorScheme(value: ColorScheme) extends Feature

  /** Detects if the user has requested that the system minimize non-essential motion. */
  case class PrefersReducedMotion(value: Option[ReducedMotion]) extends Feature

  /** Tests whether the primary input mechanism is a pointing device and how accurate it is. */
  case class Pointer(value: Option[PointerValue]) extends Feature

  /** Tests whether the primary input mechanism can hover over elements. */
  case class Hover(value: Option[HoverValue]) extends Feature

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

  case class Query(head: Either[TypeExpr, Feature], tail: Vector[Feature]) extends FeatureOps[Query] {
    override protected def F = f => new Query(head, tail :+ f)

    private[scalacss] val cssSuffix: String = {
      val z = head.fold(cssTypeExpr, cssFeature)
      tail.foldLeft(z)(_ ~ _and_ ~ cssFeature(_))
    }

    override def toString =
      Media.css(NonEmptyVector one this)

    private def +:(qs: Vector[Query]): Vector[Query] =
      if (qs.exists(_ ==* this)) qs else qs :+ this

    @deprecated("Use .or", "1.1.0")
    def &(q2: Query): Cond =
      or(q2)

    def or(q2: Query, qn: Query*): Cond =
      Cond(None, qn.foldLeft(Vector1(this) +: q2)(_ +: _))

    def and(that: Query): Query = {
      def merge(base: Vector[Feature], extra: Vector[Feature], exclude: Option[Feature]): Vector[Feature] =
        extra.foldLeft(base)((acc, f) =>
          if (exclude.exists(_ ==* f) || acc.exists(_ ==* f)) acc else acc :+ f
        )

      def mergeType(t1: TypeExpr, t2: TypeExpr): TypeExpr =
        (t1, t2) match {
          case _ if t1 ==* t2               => t1
          case (Just(All), _)               => t2
          case (_, Just(All))               => t1
          case (Only(All), Just(x))         => Only(x)
          case (Just(x), Only(All))         => Only(x)
          case (Only(All), Only(x))         => Only(x)
          case (Only(x), Only(All))         => Only(x)
          case (Only(x), Just(y)) if x ==* y => t1
          case (Just(x), Only(y)) if x ==* y => t2
          case _ =>
            throw new IllegalArgumentException(
              s"Cannot combine incompatible media queries: '${cssTypeExpr(t1)}' and '${cssTypeExpr(t2)}'")
        }

      (this.head, that.head) match {
        case (Left(t1), Left(t2)) =>
          Query(Left(mergeType(t1, t2)), merge(this.tail, that.tail, None))

        case (Left(t), Right(f)) =>
          Query(Left(t), merge(this.tail, f +: that.tail, None))

        case (Right(f), Left(t)) =>
          Query(Left(t), merge(f +: this.tail, that.tail, None))

        case (Right(f1), Right(f2)) =>
          Query(Right(f1), merge(this.tail, f2 +: that.tail, Some(f1)))
      }
    }
  }

  implicit def univEqScanValue              : UnivEq[ScanValue        ] = UnivEq.derive
  implicit def univEqOrientationValue       : UnivEq[OrientationValue ] = UnivEq.derive
  @nowarn("cat=unused")
  implicit def univEqValueExpr   [T: UnivEq]: UnivEq[ValueExpr[T]     ] = UnivEq.derive
  implicit def univEqTypeA                  : UnivEq[TypeA            ] = UnivEq.derive
  implicit def univEqTypeExpr               : UnivEq[TypeExpr         ] = UnivEq.derive
//implicit def univEqHeight                 : UnivEq[Height        [_]] = UnivEq.force
//implicit def univEqWidth                  : UnivEq[Width         [_]] = UnivEq.force
//implicit def univEqDeviceHeight           : UnivEq[DeviceHeight  [_]] = UnivEq.force
//implicit def univEqDeviceWidth            : UnivEq[DeviceWidth   [_]] = UnivEq.force
//implicit def univEqResolution             : UnivEq[Resolution    [_]] = UnivEq.force
  implicit def univEqFeature                : UnivEq[Feature          ] = UnivEq.force // depends on N in Width, etc.
  implicit def univEqQuery                  : UnivEq[Query            ] = UnivEq.derive

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
    @deprecated("Use interlace", "1.1.0")
    final def interface         : Out = F(Scan(Interface))
    final def interlace         : Out = F(Scan(Interlace))

    final def grid        : Out = F(Grid(None))
    final def grid(v: Int): Out = F(Grid(Some(v)))

    final def prefersColorScheme(v: ColorScheme): Out = F(PrefersColorScheme(v))
    final def prefersColorSchemeLight           : Out = prefersColorScheme(ColorScheme.Light)
    final def prefersColorSchemeDark            : Out = prefersColorScheme(ColorScheme.Dark)

    final def prefersReducedMotion                  : Out = F(PrefersReducedMotion(None))
    final def prefersReducedMotion(v: ReducedMotion): Out = F(PrefersReducedMotion(Some(v)))
    final def prefersReducedMotionNoPreference      : Out = prefersReducedMotion(ReducedMotion.NoPreference)
    final def prefersReducedMotionReduce            : Out = prefersReducedMotion(ReducedMotion.Reduce)

    final def pointer                 : Out = F(Pointer(None))
    final def pointer(v: PointerValue): Out = F(Pointer(Some(v)))
    final def pointerNone             : Out = pointer(PointerValue.None)
    final def pointerCoarse           : Out = pointer(PointerValue.Coarse)
    final def pointerFine             : Out = pointer(PointerValue.Fine)

    final def hover               : Out = F(Hover(None))
    final def hover(v: HoverValue): Out = F(Hover(Some(v)))
    final def hoverNone           : Out = hover(HoverValue.None)
    final def hoverHover          : Out = hover(HoverValue.Hover)
  }

  // Ensure string concat doesn't accidentally call toString on something stupid.
  private implicit class StringExt(private val _s: String) extends AnyVal {
    def ~(b: String) = _s + b
  }

  private final def media = "@media "
  private final def `:`   = ":"
  private final def _and_ = " and "
  private final def `,`   = ", "
  private final def `(`   = "("
  private final def `)`   = ")"
  private final def min   = "min-"
  private final def max   = "max-"

  private def paren(c: String): String =
    `(` ~ c ~ `)`

  private def opt[T](ov: Option[T], name: String)(implicit tc: T => String): String =
    ov.fold(name)(name ~ `:` ~ tc(_))

  private def cssValueExpr[T](e: ValueExpr[T], name: String)(implicit tc: T => String): String =
    e match {
      case Eql(t) =>       name ~ `:` ~ tc(t)
      case Min(t) => min ~ name ~ `:` ~ tc(t)
      case Max(t) => max ~ name ~ `:` ~ tc(t)
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
      case Orientation(v)          => "orientation:" ~ v.value
      case Scan(v)                 => "scan:"        ~ v.value
      case Grid(v)                 => opt(v, "grid")
      case PrefersColorScheme(v)   => "prefers-color-scheme:" ~ v.value
      case PrefersReducedMotion(v) => opt(v.map(_.value), "prefers-reduced-motion")
      case Pointer(v)              => opt(v.map(_.value), "pointer")
      case Hover(v)                => opt(v.map(_.value), "hover")
    }
    // Parentheses are required around expressions; failing to use them is an error.
    f => paren(x(f))
  }

  val cssTypeExpr: TypeExpr => String = {
    case Just(t) => t.value
    case Not(t)  => "not "  ~ t.value // Applies to whole Query. Don't wrap in parenthesis.
    case Only(t) => "only " ~ t.value // Applies to whole Query. Don't wrap in parenthesis.
  }

  def cssQueries(qs: NonEmptyVector[Query]): String =
    qs.reduceMapLeft1(_.cssSuffix)(_ ~ `,` ~ _)

  def css(qs: NonEmptyVector[Query]): CssMediaQuery =
    media ~ cssQueries(qs)
}
