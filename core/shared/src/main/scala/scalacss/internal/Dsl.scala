package scalacss.internal

import scalacss.internal.Style.{UnsafeExt, UnsafeExts}
import scalacss.internal.ValueT._

object DslBase {
  import Media.{Resolution => _, _}

  // media.
  object MediaQueryEmpty extends TypeAOps[Query] with FeatureOps[Query] {
    override protected def F = f => new Query(Right(f), Vector.empty)
    override protected def T = t => new Query(Left(Just(t)), Vector.empty)
    def not  = new MediaQueryNeedType(Not.apply)
    def only = new MediaQueryNeedType(Only.apply)
  }

  // media.{not,only}
  final class MediaQueryNeedType(f: TypeA => TypeExpr) extends TypeAOps[Query] {
    override protected def T = t => new Query(Left(f(t)), Vector.empty)
  }

  final class DslInt(private val self: Int) extends AnyVal {
    def :/:(y: Int) = Ratio(self, y)
  }

  final class DslNum[N](private val self: N) extends AnyVal {
    private def mkUnit(u: LengthUnit): Length[N] =
      Length(self, u)

    /** Centimeters. */
    def cm = mkUnit(LengthUnit.cm)

    /**
     * This unit represents the width, or more precisely the advance measure, of the glyph '0' (zero, the Unicode
     * character U+0030) in the element's font.
     */
    def ch = mkUnit(LengthUnit.ch)

    /**
     * This unit represents the calculated font-size of the element.
     * If used on the `font-size` property itself, it represents the inherited `font-size` of the element.
     */
    def em = mkUnit(LengthUnit.em)

    /**
     * This unit represents the x-height of the element's font.
     * On fonts with the 'x' letter, this is generally the height of lowercase letters in the font;
     * 1ex ≈ 0.5em in many fonts.
     */
    def ex = mkUnit(LengthUnit.ex)

    /** Inches (1in = 2.54 cm). */
    def in = mkUnit(LengthUnit.in)

    /** Millimeters. */
    def mm = mkUnit(LengthUnit.mm)

    /** Picas (1pc = 12pt). */
    def pc = mkUnit(LengthUnit.pc)

    /** Points (1pt = 1/72 of 1in). */
    def pt = mkUnit(LengthUnit.pt)

    /**
     * Pixel. Relative to the viewing device.
     * For screen display, typically one device pixel (dot) of the display.
     * For printers and very high resolution screens one CSS pixel implies multiple device pixels, so that the number
     * of pixel per inch stays around 96.
     */
    def px = mkUnit(LengthUnit.px)

    /**
     * This unit represents the `font-size` of the root element (e.g. the `font-size` of the `&lt;html&gt;` element).
     * When used on the `font-size` on this root element, it represents its initial value.
     */
    def rem = mkUnit(LengthUnit.rem)

    /** 1/100th of the height of the viewport. */
    def vh = mkUnit(LengthUnit.vh)

    /** 1/100th of the minimum value between the height and the width of the viewport. */
    def vmin = mkUnit(LengthUnit.vmin)

    /** 1/100th of the maximum value between the height and the width of the viewport. */
    def vmax = mkUnit(LengthUnit.vmax)

    /** 1/100th of the width of the viewport. */
    def vw = mkUnit(LengthUnit.vw)

    /** Size as a percentage. */
    def %% = Percentage(self)

    /** Dots per inch */
    def dpi  = Resolution(self, ResolutionUnit.dpi)

    /** Dots per centimeter */
    def dpcm = Resolution(self, ResolutionUnit.dpcm)

    /**
     * This unit represents the number of dots per px unit. Due to the 1:96 fixed ratio of CSS in to CSS px, 1dppx is
     * equivalent to 96dpi, that corresponds to the default resolution of images displayed in CSS as defined by
     * image-resolution.
     */
    def dppx = Resolution(self, ResolutionUnit.dppx)

    def *(l: Length[N])    (implicit N: Numeric[N]) = l * self
    def *(l: Resolution[N])(implicit N: Numeric[N]) = l * self
  }

  //final class DslStr(val self: String) extends AnyVal {
  //}

  /** Untyped attributes */
  final class DslAttr(private val self: Attr) extends AnyVal {
    @inline def :=(value: Value)    : AV = AV(self, value)
    @inline def :=(value: ValueT[_]): AV = AV(self, value.value)
    @inline def :=(value: Literal)  : AV = AV(self, value.value)
  }

  /** Typed attributes */
  final class DslAttrT(private val self: TypedAttrBase) extends AnyVal {
    def :=!(value: Value)    : AV = AV(self, value)
    def :=!(value: ValueT[_]): AV = AV(self, value.value)
    def :=!(value: Literal)  : AV = AV(self, value.value)

    @deprecated("Using := bypasses the type-safety of a typed attribute. Use the attribute's methods for type-safety, or :=! to bypass without warning.","always")
    @inline def :=(value: Value)    : AV = AV(self, value)
    @deprecated("Using := bypasses the type-safety of a typed attribute. Use the attribute's methods for type-safety, or :=! to bypass without warning.","always")
    @inline def :=(value: ValueT[_]): AV = AV(self, value.value)
    @deprecated("Using := bypasses the type-safety of a typed attribute. Use the attribute's methods for type-safety, or :=! to bypass without warning.","always")
    @inline def :=(value: Literal)  : AV = AV(self, value.value)
  }

  final class DslAV(private val self: AV) extends AnyVal {
    def &(b: AV) : AVs = AVs(self) + b
    def &(b: AVs): AVs = self +: b
  }

  final class DslAVs(private val self: AVs) extends AnyVal {
    @inline def &(b: AV) : AVs = self + b
    @inline def &(b: AVs): AVs = self ++ b
  }

  final class DslCond(c: Cond, b: DslBase) {
    def apply(t: ToStyle*)(implicit u: Compose): StyleS =
      c applyToStyle b.mixin(t: _*)
  }

  final class ToStyle(val s: StyleS) extends AnyVal
}

import DslBase._

// =====================================================================================================================
abstract class DslBase
  extends AttrAliasesAndValueTRules
     with Macros.Dsl.Base
     with TypedLiteralAliases
     with ColorOps[ValueT[Color]] {

  final type PseudoType = scalacss.internal.PseudoType
  final val  PseudoType = scalacss.internal.PseudoType

  final type Pseudo     = scalacss.internal.Pseudo
  final val  Pseudo     = scalacss.internal.Pseudo

  final type Domain[A]  = scalacss.internal.Domain[A]
  final val  Domain     = scalacss.internal.Domain

  final type Style      = scalacss.internal.Style
  final val  Style      = scalacss.internal.Style

  final type StyleS     = scalacss.internal.StyleS
  final val  StyleS     = scalacss.internal.StyleS

  final type StyleF[I]  = scalacss.internal.StyleF[I]
  final val  StyleF     = scalacss.internal.StyleF

  @inline override protected final def mkColor(s: String) = Color(s)

  @inline implicit final def autoDslInt  (a: Int)          : DslInt         = new DslInt(a)
  @inline implicit final def autoDslNumI (a: Int)          : DslNum[Int]    = new DslNum[Int](a)
  @inline implicit final def autoDslNumD (a: Double)       : DslNum[Double] = new DslNum[Double](a)
  //nline implicit final def autoDslStr  (a: String)       : DslStr         = new DslStr(a)
  @inline implicit final def autoDslAttr (a: Attr)         : DslAttr        = new DslAttr(a)
  @inline implicit final def autoDslAttrT(a: TypedAttrBase): DslAttrT       = new DslAttrT(a)
  @inline implicit final def autoDslAV   (a: AV)           : DslAV          = new DslAV(a)
  @inline implicit final def autoDslAVs  (a: AVs)          : DslAVs         = new DslAVs(a)

          implicit final def DslCond[C](x: C)(implicit f: C => Cond): DslCond = new DslCond(f(x), this)

  @inline implicit final def ToAVToAV(x: ToAV): AV = x.av

          implicit final def CondPseudo    (x: Pseudo)     : Cond = Cond(Some(x), Vector.empty)
          implicit final def CondMediaQuery(x: Media.Query): Cond = Cond(None, Vector1(x))

          implicit final def ToStyleToAV      (x: ToAV)                           : ToStyle = ToStyleAV(x.av)
          implicit final def ToStyleAV        (x: AV)                             : ToStyle = ToStyleAVs(AVs(x))
          implicit final def ToStyleAVs       (x: AVs)                            : ToStyle = new ToStyle(StyleS.data1(Cond.empty, x))
          implicit final def ToStyleCAV [C]   (x: (C, AV)) (implicit f: C => Cond): ToStyle = new ToStyle(StyleS.data1(f(x._1), AVs(x._2)))
          implicit final def ToStyleCAVs[C]   (x: (C, AVs))(implicit f: C => Cond): ToStyle = new ToStyle(StyleS.data1(f(x._1), x._2))
          implicit final def ToStyleUnsafeExt (x: UnsafeExt)                      : ToStyle = ToStyleUnsafeExts(Vector1(x))
          implicit final def ToStyleUnsafeExts(x: UnsafeExts)                     : ToStyle = new ToStyle(StyleS.empty.copy(unsafeExts = x))
  @inline implicit final def ToStyleStyleS    (x: StyleS)                         : ToStyle = new ToStyle(x)
          implicit final def ToStyleStyleA    (x: StyleA)                         : ToStyle = new ToStyle(x.style)

  protected def styleS(t: ToStyle*)(implicit c: Compose): StyleS

  def addClassName(cn: String): StyleS =
    StyleS.empty.copy(addClassNames = Vector1(ClassName(cn)))

  def addClassNames(cn: String*): StyleS =
    StyleS.empty.copy(addClassNames = cn.foldLeft(Vector.empty[ClassName])(_ :+ ClassName(_)))

  def media = MediaQueryEmpty

  def unsafeExt(f: String => String)(t: ToStyle*)(implicit c: Compose): UnsafeExt =
    UnsafeExt(f, Cond.empty, styleS(t: _*))

  def unsafeChild(n: String)(t: ToStyle*)(implicit c: Compose): Style.UnsafeExt =
    unsafeExt(_ + " " + n)(t: _*)

  def unsafeRoot(sel: String)(t: ToStyle*)(implicit c: Compose): Style.UnsafeExt =
    unsafeExt(_ => sel)(t: _*)

  def mixin(t: ToStyle*)(implicit c: Compose) =
    styleS(t: _*)(c)

  def mixinIf(b: Boolean)(t: ToStyle*)(implicit c: Compose): StyleS =
    if (b) styleS(t: _*)(c) else StyleS.empty

  def mixinIfElse(b: Boolean)(t: ToStyle*)(f: ToStyle*)(implicit c: Compose): StyleS =
    styleS((if (b) t else f): _*)(c)
}

// =====================================================================================================================
object DslMacros {

  trait MStyle {
    def apply                   (t: ToStyle*)(implicit c: Compose): StyleA
    def apply(className: String)(t: ToStyle*)(implicit c: Compose): StyleA
  }

  trait MStyleF2 {
    protected def create[I: StyleLookup](manualName: Option[String], d: Domain[I], f: I => StyleS, classNameSuffix: (I, Int) => String): I => StyleA

    final def bool(f: Boolean => StyleS): Boolean => StyleA =
      create(None, Domain.boolean, f, defaultStyleFClassNameSuffixB)

    final def int(r: Range)(f: Int => StyleS): Int => StyleA =
      create(None, Domain ofRange r, f, defaultStyleFClassNameSuffixI)
  }

  trait MStyleF extends MStyleF2 {

    /** Manually specify a name */
    final def apply(name: String): MStyleF2 =
      new MStyleF2 {
        override protected def create[I: StyleLookup](u: Option[String], d: Domain[I], f: I => StyleS, classNameSuffix: (I, Int) => String) =
          MStyleF.this.create(Some(name), d, f, classNameSuffix)
      }

    final def apply[I: StyleLookup](d: Domain[I])(f: I => StyleS, classNameSuffix: (I, Int) => String = defaultStyleFClassNameSuffix): I => StyleA =
      create(None, d, f, classNameSuffix)
  }

  trait MKeyframes {
    def apply(frames: (KeyframeSelector, StyleA)*): Keyframes
  }

  trait MFontFace {
    def apply(config: FontFace.FontSrcSelector => FontFace[Option[String]]): FontFace[String]
    def apply(fontFamily: String)(config: FontFace.FontSrcSelector => FontFace[Option[String]]): FontFace[String]
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
    style(t: _*).copy(className = Option(className) map ClassName.apply)

  def style(t: ToStyle*)(implicit c: Compose): StyleS =
    if (t.isEmpty) StyleS.empty
    else t.map(_.s).reduce(_ compose _)
}
