package scalacss

import scalaz.{\/, -\/, \/-}
import Style.{UnsafeExt, UnsafeExts}
import ValueT._

object DslBase {
  import Media.{Resolution => _, _}

  // media.
  object MediaQueryEmpty extends TypeAOps[Query] with FeatureOps[Query] {
    override protected def F = f => new Query(\/-(f), Vector.empty)
    override protected def T = t => new Query(-\/(Just(t)), Vector.empty)
    def not  = new MediaQueryNeedType(Not)
    def only = new MediaQueryNeedType(Only)
  }

  // media.{not,only}
  final class MediaQueryNeedType(f: TypeA => TypeExpr) extends TypeAOps[Query] {
    override protected def T = t => new Query(-\/(f(t)), Vector.empty)
  }

  final class DslInt(val self: Int) extends AnyVal {
    def :/:(y: Int) = Ratio(self, y)
  }

  final class DslNum[N](val self: N) extends AnyVal {
    @inline private def mkUnit(u: LengthUnit): Length[N] =
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

    /** Dots per inch */
    @inline def dpi  = Resolution(self, ResolutionUnit.dpi)

    /** Dots per centimeter */
    @inline def dpcm = Resolution(self, ResolutionUnit.dpcm)

    /**
     * This unit represents the number of dots per px unit. Due to the 1:96 fixed ratio of CSS in to CSS px, 1dppx is
     * equivalent to 96dpi, that corresponds to the default resolution of images displayed in CSS as defined by
     * image-resolution.
     */
    @inline def dppx = Resolution(self, ResolutionUnit.dppx)

    @inline def *(l: Length[N])    (implicit N: Numeric[N]) = l * self
    @inline def *(l: Resolution[N])(implicit N: Numeric[N]) = l * self
  }

  //final class DslStr(val self: String) extends AnyVal {
  //}

  /** Untyped attributes */
  final class DslAttr(val self: Attr) extends AnyVal {
    @inline def :=(value: Value)    : AV = AV(self, value)
    @inline def :=(value: ValueT[_]): AV = AV(self, value.value)
    @inline def :=(value: Literal)  : AV = AV(self, value.value)
  }

  /** Typed attributes */
  final class DslAttrT(val self: TypedAttrBase) extends AnyVal {
    @inline def :=!(value: Value)    : AV = AV(self, value)
    @inline def :=!(value: ValueT[_]): AV = AV(self, value.value)
    @inline def :=!(value: Literal)  : AV = AV(self, value.value)

    @deprecated("Using := bypasses the type-safety of a typed attribute. Use the attribute's methods for type-safety, or :=! to bypass without warning.","always")
    @inline def :=(value: Value)    : AV = AV(self, value)
    @deprecated("Using := bypasses the type-safety of a typed attribute. Use the attribute's methods for type-safety, or :=! to bypass without warning.","always")
    @inline def :=(value: ValueT[_]): AV = AV(self, value.value)
    @deprecated("Using := bypasses the type-safety of a typed attribute. Use the attribute's methods for type-safety, or :=! to bypass without warning.","always")
    @inline def :=(value: Literal)  : AV = AV(self, value.value)
  }

  final class DslAV(val self: AV) extends AnyVal {
    @inline def &(b: AV) : AVs = AVs(self) + b
    @inline def &(b: AVs): AVs = self +: b
  }

  final class DslAVs(val self: AVs) extends AnyVal {
    @inline def &(b: AV) : AVs = self + b
    @inline def &(b: AVs): AVs = self ++ b
  }

  final class DslCond(c: Cond, b: DslBase) {
    @inline def apply(t: ToStyle*)(implicit u: Compose): StyleS = c applyToStyle b.mixin(t: _*)
  }

  final class ToStyle(val s: StyleS) extends AnyVal
}

import DslBase._

// =====================================================================================================================
abstract class DslBase
  extends AttrAliasesAndValueTRules
     with TypedLiteralAliases
     with ColorOps[ValueT[Color]] {

  @inline override protected final def mkColor(s: String) = Color(s)

  @inline implicit final def autoDslInt  (a: Int)          : DslInt         = new DslInt(a)
  @inline implicit final def autoDslNumI (a: Int)          : DslNum[Int]    = new DslNum[Int](a)
  @inline implicit final def autoDslNumD (a: Double)       : DslNum[Double] = new DslNum[Double](a)
  //nline implicit final def autoDslStr  (a: String)       : DslStr         = new DslStr(a)
  @inline implicit final def autoDslAttr (a: Attr)         : DslAttr        = new DslAttr(a)
  @inline implicit final def autoDslAttrT(a: TypedAttrBase): DslAttrT       = new DslAttrT(a)
  @inline implicit final def autoDslAV   (a: AV)           : DslAV          = new DslAV(a)
  @inline implicit final def autoDslAVs  (a: AVs)          : DslAVs         = new DslAVs(a)

  @inline implicit final def DslCond[C <% Cond](x: C): DslCond = new DslCond(x, this)

  @inline implicit final def ToAVToAV(x: ToAV): AV = x.av

  @inline implicit final def CondPseudo    (x: Pseudo)     : Cond = Cond(Some(x), Vector.empty)
  @inline implicit final def CondMediaQuery(x: Media.Query): Cond = Cond(None, Vector1(x))

  @inline implicit final def ToStyleToAV           (x: ToAV)      : ToStyle = ToStyleAV(x.av)
  @inline implicit final def ToStyleAV             (x: AV)        : ToStyle = ToStyleAVs(AVs(x))
          implicit final def ToStyleAVs            (x: AVs)       : ToStyle = new ToStyle(StyleS.data1(Cond.empty, x))
          implicit final def ToStyleCAV [C <% Cond](x: (C, AV))   : ToStyle = new ToStyle(StyleS.data1(x._1, AVs(x._2)))
          implicit final def ToStyleCAVs[C <% Cond](x: (C, AVs))  : ToStyle = new ToStyle(StyleS.data1(x._1, x._2))
  @inline implicit final def ToStyleUnsafeExt      (x: UnsafeExt) : ToStyle = ToStyleUnsafeExts(Vector1(x))
          implicit final def ToStyleUnsafeExts     (x: UnsafeExts): ToStyle = new ToStyle(StyleS.empty.copy(unsafeExts = x))
  @inline implicit final def ToStyleStyleS         (x: StyleS)    : ToStyle = new ToStyle(x)
  @inline implicit final def ToStyleStyleA         (x: StyleA)    : ToStyle = new ToStyle(x.style)

  protected def styleS(t: ToStyle*)(implicit c: Compose): StyleS

  def addClassName(cn: String): StyleS =
    StyleS.empty.copy(addClassNames = Vector1(ClassName(cn)))

  def addClassNames(cn: String*): StyleS =
    StyleS.empty.copy(addClassNames = cn.foldLeft(Vector.empty[ClassName])(_ :+ ClassName(_)))

  def media = MediaQueryEmpty

  def unsafeExt(f: String => String)(t: ToStyle*)(implicit c: Compose): UnsafeExt =
    UnsafeExt(f, styleS(t: _*))

  def unsafeChild(n: String)(t: ToStyle*)(implicit c: Compose): Style.UnsafeExt =
    unsafeExt(_ + " " + n)(t: _*)

  def unsafeRoot(sel: String)(t: ToStyle*)(implicit c: Compose): Style.UnsafeExt =
    unsafeExt(_ => sel)(t: _*)

  @inline def mixin(t: ToStyle*)(implicit c: Compose) =
    styleS(t: _*)(c)

  @inline def mixinIf(b: Boolean)(t: ToStyle*)(implicit c: Compose): StyleS =
    if (b) styleS(t: _*)(c) else StyleS.empty

  @inline def mixinIfElse(b: Boolean)(t: ToStyle*)(f: ToStyle*)(implicit c: Compose): StyleS =
    styleS((if (b) t else f): _*)(c)

  @inline implicit def colourLiteralMacro(sc: StringContext) =
    new Macros.ColourLiteral(sc)
}

// =====================================================================================================================
object DslMacros {

  trait MStyle {
    def apply                   (t: ToStyle*)(implicit c: Compose): StyleA
    def apply(className: String)(t: ToStyle*)(implicit c: Compose): StyleA
  }

  trait MStyleF2 {
    protected def create[I](manualName: Option[String], d: Domain[I], f: I => StyleS, classNameSuffix: (I, Int) => String): I => StyleA

    final def bool(f: Boolean => StyleS): Boolean => StyleA =
      create(None, Domain.boolean, f, defaultStyleFClassNameSuffixB)

    final def int(r: Range)(f: Int => StyleS): Int => StyleA =
      create(None, Domain ofRange r, f, defaultStyleFClassNameSuffixI)
  }

  trait MStyleF extends MStyleF2 {

    /** Manually specify a name */
    final def apply(name: String): MStyleF2 =
      new MStyleF2 {
        override protected def create[I](u: Option[String], d: Domain[I], f: I => StyleS, classNameSuffix: (I, Int) => String) =
          MStyleF.this.create(Some(name), d, f, classNameSuffix)
      }

    final def apply[I](d: Domain[I])(f: I => StyleS, classNameSuffix: (I, Int) => String = defaultStyleFClassNameSuffix): I => StyleA =
      create(None, d, f, classNameSuffix)
  }

  trait MKeyframes {
    def apply(frames: (Percentage[Int], StyleA)*): Keyframes
  }

  val defaultStyleFClassNameSuffix: (Any, Int) => String =
    (_, index) => (index + 1).toString

  val defaultStyleFClassNameSuffixB: (Boolean, Int) => String =
    (b, _) => if (b) "t" else "f"

  val defaultStyleFClassNameSuffixI: (Int, Int) => String =
    (i, _) => i.toString
}

// =====================================================================================================================
object Dsl extends DslBase {

  override protected def styleS(t: ToStyle*)(implicit c: Compose) =
    style(t: _*)

  def style(className: String)(t: ToStyle*)(implicit c: Compose): StyleS =
    style(t: _*).copy(className = Option(className) map ClassName)

  def style(t: ToStyle*)(implicit c: Compose): StyleS =
    if (t.isEmpty) StyleS.empty
    else t.map(_.s).reduce(_ compose _)
}
