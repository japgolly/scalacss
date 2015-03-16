package japgolly.scalacss

import shapeless.Witness
import Style.{UnsafeExt, UnsafeExts}
import ValueT._

object DslBase {
  val zeroType = Witness(0)

  final class DslInt(val self: Int) extends AnyVal {
    
    @inline private def mkUnit(u: LengthUnit): Length =
      Length(self, u)

    /** Centimeters. */
    @inline def cm = mkUnit(LengthUnit.cm)

    /**
     * This unit represents the width, or more precisely the advance measure, of the glyph '0' (zero, the Unicode
     * character U+0030) in the element's font.
     */
    @inline def ch = mkUnit(LengthUnit.ch)

    /**
     * This unit represents the calculated font-size of the element.
     * If used on the `font-size` property itself, it represents the inherited `font-size` of the element.
     */
    @inline def em = mkUnit(LengthUnit.em)

    /**
     * This unit represents the x-height of the element's font.
     * On fonts with the 'x' letter, this is generally the height of lowercase letters in the font;
     * 1ex ≈ 0.5em in many fonts.
     */
    @inline def ex = mkUnit(LengthUnit.ex)

    /** Inches (1in = 2.54 cm). */
    @inline def in = mkUnit(LengthUnit.in)

    /** Millimeters. */
    @inline def mm = mkUnit(LengthUnit.mm)

    /** Picas (1pc = 12pt). */
    @inline def pc = mkUnit(LengthUnit.pc)

    /** Points (1pt = 1/72 of 1in). */
    @inline def pt = mkUnit(LengthUnit.pt)

    /**
     * Pixel. Relative to the viewing device.
     * For screen display, typically one device pixel (dot) of the display.
     * For printers and very high resolution screens one CSS pixel implies multiple device pixels, so that the number
     * of pixel per inch stays around 96.
     */
    @inline def px = mkUnit(LengthUnit.px)

    /**
     * This unit represents the `font-size` of the root element (e.g. the `font-size` of the `&lt;html&gt;` element).
     * When used on the `font-size` on this root element, it represents its initial value.
     */
    @inline def rem = mkUnit(LengthUnit.rem)

    /** 1/100th of the height of the viewport. */
    @inline def vh = mkUnit(LengthUnit.vh)

    /** 1/100th of the minimum value between the height and the width of the viewport. */
    @inline def vmin = mkUnit(LengthUnit.vmin)

    /** 1/100th of the maximum value between the height and the width of the viewport. */
    @inline def vmax = mkUnit(LengthUnit.vmax)

    /** 1/100th of the width of the viewport. */
    @inline def vw = mkUnit(LengthUnit.vw)

    /** Size as a percentage. */
    @inline def %% = Percentage(self)

    @inline def *(l: Length): Length = l.copy(n = l.n * self)
    @inline def /(l: Length): Length = l.copy(n = l.n / self)
  }
  
  final class DslAttr(val self: Attr) extends AnyVal {
    /** Untyped assignment */
    @inline def :=(value: Value): AV = AV(self, value)
  }

  final class DslTypedAttrBase(val self: TypedAttrBase) extends AnyVal {
    /** Untyped assignment */
    @inline def :=!(value: Value): AV = AV(self, value)
  }

//  final class DslTypedAttrT[T <: ValueClass](val self: TypedAttrT[T]) extends AnyVal {
//    /** Typed assignment */
//    @inline def :=(value: ValueT[T]): AV = self(value)
//  }

  final class DslAV(val self: AV) extends AnyVal {
    @inline def &(b: AV) : AVs = NonEmptyVector(self, b)
    @inline def &(b: AVs): AVs = self +: b
  }

  final class DslAVs(val self: AVs) extends AnyVal {
    @inline def &(b: AV) : AVs = self :+ b
    @inline def &(b: AVs): AVs = self ++ b
  }

  final class DslCond(val c: Cond) extends AnyVal {
    @inline def apply()             : ToStyle = new ToStyle(StyleS.empty)
    @inline def apply(avs: AVs)     : ToStyle = new ToStyle(StyleS.data1(c, avs))
    @inline def apply(h: AV, t: AV*): ToStyle = apply(NonEmptyVector(h, t: _*))
  }

  final class ToStyle(val s: StyleS) extends AnyVal
}

import DslBase._

// =====================================================================================================================
abstract class DslBase extends ValueT.Rules {

  @inline implicit final def untypedCssValueFromZero   (z: zeroType.T): Value = "0"
  @inline implicit final def untypedCssValueFromLength (x: Length)    : Value = x.value
  @inline implicit final def untypedCssValueFromPercent(x: Percentage): Value = x.value

  @inline implicit final def autoDslInt (a: Int) : DslInt  = new DslInt(a)
  @inline implicit final def autoDslAttr(a: Attr): DslAttr = new DslAttr(a)
  @inline implicit final def autoDslAV  (a: AV)  : DslAV   = new DslAV(a)
  @inline implicit final def autoDslAVs (a: AVs) : DslAVs  = new DslAVs(a)

  @inline implicit final def autoDslTypedAttrBase(a: TypedAttrBase): DslTypedAttrBase = new DslTypedAttrBase(a)
  // @inline implicit final def autoDslTypedAttrT[T <: ValueClass](a: TypedAttrT[T]): DslTypedAttrT[T] = new DslTypedAttrT(a)

  @inline implicit final def DslCond[C <% Cond](x: C): DslCond = new DslCond(x)

  @inline implicit final def CondPseudo(x: Pseudo): Cond = Cond(Some(x))

  @inline implicit final def ToStyleAV             (x: AV)        : ToStyle = ToStyleAVs(NonEmptyVector(x))
          implicit final def ToStyleAVs            (x: AVs)       : ToStyle = new ToStyle(StyleS.data1(Cond.empty, x))
          implicit final def ToStyleCAV [C <% Cond](x: (C, AV))   : ToStyle = new ToStyle(StyleS.data1(x._1, NonEmptyVector(x._2)))
          implicit final def ToStyleCAVs[C <% Cond](x: (C, AVs))  : ToStyle = new ToStyle(StyleS.data1(x._1, x._2))
  @inline implicit final def ToStyleUnsafeExt      (x: UnsafeExt) : ToStyle = ToStyleUnsafeExts(Vector1(x))
          implicit final def ToStyleUnsafeExts     (x: UnsafeExts): ToStyle = new ToStyle(StyleS.empty.copy(unsafeExts = x))
  @inline implicit final def ToStyleStyleS         (x: StyleS)    : ToStyle = new ToStyle(x)

  protected def styleS(t: ToStyle*)(implicit c: Compose): StyleS

  def unsafeExt(f: String => String)(t: ToStyle*)(implicit c: Compose): UnsafeExt =
    UnsafeExt(f, styleS(t: _*))

  def unsafeChild(n: String)(t: ToStyle*)(implicit c: Compose): Style.UnsafeExt =
    unsafeExt(_ + " " + n)(t: _*)
}

// =====================================================================================================================
object Dsl extends DslBase {

  override protected def styleS(t: ToStyle*)(implicit c: Compose) =
    style(t: _*)

  def style(className: String = null)(t: ToStyle*)(implicit c: Compose): StyleS =
    style(t: _*).copy(className = Option(className))

  def style(t: ToStyle*)(implicit c: Compose): StyleS =
    if (t.isEmpty) StyleS.empty
    else t.map(_.s).reduce(_ compose _)
}
