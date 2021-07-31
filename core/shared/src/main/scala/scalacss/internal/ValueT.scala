package scalacss.internal

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration
import scalacss.internal.Literal.{Typed => LT}
import scalacss.internal.{Literal => L}

trait ToAV {
  def av: AV
}

/**
 * A CSS value that is valid for some context `T`.
 */
final case class ValueT[T <: ValueT.ValueClass](value: Value) {
  @inline def retype[A <: ValueT.ValueClass] = this.asInstanceOf[ValueT[A]]
}

object ValueT {

  // TODO ValueT.mkString won't handle strings with both ' and "
  def mkString(s: String): Value =
    if (s contains "'")
      s""""$s""""
    else
      s"'$s'"

  @inline def mkStrings(a: String, sep: String, b: String): Value =
    mkString(a) + sep + mkString(b)

  /*
  sealed trait ValueT[T]
  def ValueT[T](v: Value) = v.asInstanceOf[ValueT[T]]
  implicit class ValueTExt[T](val v: ValueT[T]) extends AnyVal {
    def value     = v.asInstanceOf[Value]
    def retype[A] = v.asInstanceOf[ValueT[A]]
  }
  */

  /**
   * Represented a context in which a finite set of types are valid.
   *
   * Example:
   * {{{
   *   &lt;br-width&gt; = &lt;length&gt; | thin | medium | thick
   * }}}
   */
  sealed trait ValueClass

  // Note: only ValueClass. Use the implicit rules below in place of subtyping.
  sealed trait Integer           extends ValueClass
  sealed trait Number            extends ValueClass
  sealed trait Time              extends ValueClass
  sealed trait Len               extends ValueClass
  sealed trait Pct               extends ValueClass
  sealed trait LenPct            extends ValueClass
  sealed trait LenPctAuto        extends ValueClass
  sealed trait LenPctNum         extends ValueClass
  sealed trait BrWidth           extends ValueClass
  sealed trait BrStyle           extends ValueClass
  sealed trait Color             extends ValueClass
  sealed trait WidStyCol         extends ValueClass
  sealed trait RepeatStyle       extends ValueClass
  sealed trait OverflowBehaviour extends ValueClass
  sealed trait GapStyle          extends ValueClass

  // =========
  //   Rules
  // =========

  type ==>[From              , To   <: ValueClass] = Rule[From, To]
  type >=>[From <: ValueClass, To   <: ValueClass] = ValueT[From] ==> To
  type <==[To   <: ValueClass, From              ] = From ==> To
  type <=<[To   <: ValueClass, From <: ValueClass] = From >=> To

  abstract class Rule[A, B <: ValueClass] {
    def apply(a: A): ValueT[B]

    final def >>[C <: ValueClass](f: B >=> C): A ==> C =
      Rule.applyT(a => f(this(a)))
  }

  object Rule {
    def apply[From, To <: ValueClass](f: From => Value): From ==> To =
      applyT(x => ValueT(f(x)))

    def applyT[From, To <: ValueClass](f: From => ValueT[To]): From ==> To =
      new Rule[From, To] {
        override def apply(x: From): ValueT[To] = f(x)
      }

    @inline def retype[FromT <: ValueClass, To <: ValueClass]: FromT >=> To =
      applyT(_.retype)

    @inline def literal[L <: Literal, To <: ValueClass]: L ==> To =
      apply(_.value)
  }

  object Rules extends Rules
  abstract class Rules {
    @inline implicit def ruleApply[From, To <: ValueClass](f: From)(implicit r: From ==> To): ValueT[To] =
      r(f)

    @inline implicit def ruleChain[A, B <: ValueClass, C <: ValueClass](implicit ab: A ==> B, bc: B >=> C): A ==> C =
      ab >> bc

    @inline implicit def ruleAnywhere[L <: Literal with LT.Anywhere, To <: ValueClass]: L ==> To =
      Rule.literal

    @inline implicit def ruleLen_L[N] : Len     <== Length[N]      = Rule(_.value)
    @inline implicit def rulePct_P[N] : Pct     <== Percentage[N]  = Rule(_.value)
    @inline implicit def ruleInteger_I: Integer <== Int            = Rule(_.toString)
    @inline implicit def ruleNumber_I : Number  <== Int            = Rule(_.toString)
    @inline implicit def ruleNumber_D : Number  <== Double         = Rule(_.toString)
    @inline implicit def ruleTime_FD  : Time    <== FiniteDuration =
      Rule(d => d.unit match {
        case TimeUnit.MICROSECONDS
           | TimeUnit.MILLISECONDS
           | TimeUnit.NANOSECONDS => d.toMillis.toString + "ms"
        case _                    => d.toSeconds.toString + "s"
      })

    @inline implicit def ruleLenPct_L     : LenPct      <=< Len          = Rule.retype
    @inline implicit def ruleLenPct_P     : LenPct      <=< Pct          = Rule.retype
    @inline implicit def ruleLenPctAuto_LP: LenPctAuto  <=< LenPct       = Rule.retype
    @inline implicit def ruleLenPctAuto_A : LenPctAuto  <== LT.auto.type = Rule.literal
    @inline implicit def ruleLenPctNum_LP : LenPctNum   <=< LenPct       = Rule.retype
    @inline implicit def ruleLenPctNum_N  : LenPctNum   <=< Number       = Rule.retype
    @inline implicit def ruleBrWidth_1    : BrWidth     <=< Len          = Rule.retype
    @inline implicit def ruleWidStyCol_W  : WidStyCol   <=< BrWidth      = Rule.retype
    @inline implicit def ruleWidStyCol_S  : WidStyCol   <=< BrStyle      = Rule.retype
    @inline implicit def ruleWidStyCol_C  : WidStyCol   <=< Color        = Rule.retype

    @inline implicit def ruleBrWidth_2[L <: Literal with LT.BrWidth]: BrWidth <== L = Rule.literal
    @inline implicit def ruleBrStyle_L[L <: Literal with LT.BrStyle]: BrStyle <== L = Rule.literal
    @inline implicit def ruleRepeatStyle[L <: Literal with LT.RepeatStyle]: RepeatStyle <== L = Rule.literal
    @inline implicit def ruleOverflowBehaviour[L <: Literal with LT.OverflowBehaviour]: OverflowBehaviour <== L = Rule.literal

    @inline implicit def ruleGapStyle_l[L <: Literal with LT.GapStyle]: GapStyle <== L = Rule.literal
    @inline implicit def ruleGapStyle_L: GapStyle <=< Len = Rule.retype
    @inline implicit def ruleGapStyle_P: GapStyle <=< Pct = Rule.retype

    // diverging implicit expansion requires these ↙ :(
    @inline implicit def ruleWidStyCol_L : WidStyCol  <=< Len = Rule.retype
    @inline implicit def ruleWidStyCol_P : WidStyCol  <=< Pct = Rule.retype
    @inline implicit def ruleLenPctNum_I : LenPctNum  <== Int = Rule(_.toString)
    @inline implicit def ruleLenPctNum_L : LenPctNum  <=< Len = Rule.retype
    @inline implicit def ruleLenPctNum_P : LenPctNum  <=< Pct = Rule.retype
    @inline implicit def ruleLenPctAuto_L: LenPctAuto <=< Len = Rule.retype
    @inline implicit def ruleLenPctAuto_P: LenPctAuto <=< Pct = Rule.retype
  }


  // ================================
  // Attributes with type-safe values
  // ================================

  object TypedAttrBase {
    implicit def `Be the attr you were born to be!`(t: TypedAttrBase): Attr = t.attr
  }

  abstract class TypedAttrBase {
    val attr: Attr
    protected def av(v: Value)   : AV = AV(attr, v)
    protected def avl(v: Literal): AV = av(v.value)

    override def toString = attr.toString

    /**
     * The inherit CSS-value causes the element for which it is specified to take the computed value of the property from its parent element. It is allowed on every CSS property.
     *
     * For inherited properties, this reinforces the default behavior, and is only needed to override another rule.  For non-inherited properties, this specifies a behavior that typically makes relatively little sense and you may consider using initial instead, or unset on the all property.
     */
    def inherit = avl(L.inherit)

    /** The initial CSS keyword applies the initial value of a property to an element. It is allowed on every CSS property and causes the element for which it is specified to use the initial value of the property. */
    def initial = avl(L.initial)

    /** The unset CSS keyword is the combination of the initial and inherit keywords. Like these two other CSS-wide keywords, it can be applied to any CSS property, including the CSS shorthand all. This keyword resets the property to its inherited value if it inherits from its parent or to its initial value if not. In other words, it behaves like the inherit keyword in the first case and like the initial keyword in the second case. */
    def unset = avl(L.unset)
  }

  abstract class TypedAttrT1[T <: ValueClass] extends TypedAttrBase {
    final def apply(v: ValueT[T]): AV = av(v.value)
//        final def apply[From](f: From)(implicit r: From ==> T): AV = av(r(f).value)
  }

  private[internal] def concat(sep: String, a: ValueT[_], b: ValueT[_]): Value =
    a.value + sep + b.value

  private[internal] def concat(sep: String, a: ValueT[_], b: ValueT[_], c: ValueT[_]): Value =
    concat(sep, a, b) + sep + c.value

  private[internal] def concat(sep: String, a: ValueT[_], b: ValueT[_], c: ValueT[_], d: ValueT[_]): Value =
    concat(sep, a, b, c) + sep + d.value

  abstract class TypedAttrT2[T <: ValueClass] extends TypedAttrBase {
    final def apply(both: ValueT[T])                           : AV = av(both.value)
    final def apply(horizontal: ValueT[T], vertical: ValueT[T]): AV = av(concat(" ", horizontal, vertical))
  }

  abstract class TypedAttrT3[T <: ValueClass](sep: String) extends TypedAttrBase {
    final def apply(a: ValueT[T])                            : AV = av(a.value)
    final def apply(a: ValueT[T], b: ValueT[T])              : AV = av(concat(sep, a, b))
    final def apply(a: ValueT[T], b: ValueT[T], c: ValueT[T]): AV = av(concat(sep, a, b, c))
  }

  abstract class TypedAttrT4Edges[T <: ValueClass](sep: String) extends TypedAttrBase {
    final type V = ValueT[T]

    protected def attrT: Attr
    protected def attrR: Attr
    protected def attrB: Attr
    protected def attrL: Attr

    /** Applies the same setting to all four edges. */
    final def apply(all: V): AV =
      av(all.value)

    /**
     * @param v Value for vertical edges.
     * @param h Value for horizontal edges.
     */
    final def apply(v: V, h: V): AV =
      av(concat(sep, v, h))

    /**
     * @param t Value for top edge.
     * @param h Value for horizontal edges.
     * @param b Value for bottom edge.
     */
    final def apply(t: V, h: V, b: V): AV =
      av(concat(sep, t, h, b))

    /**
     * @param t Value for top edge.
     * @param r Value for right edge.
     * @param b Value for bottom edge.
     * @param l Value for left edge.
     */
    final def apply(t: V, r: V, b: V, l: V): AV =
      av(concat(sep, t, r, b, l))

    final def horizontal(h: V): AVs =
      AVs(attrL, h.value).add(attrR, h.value)

    final def vertical(v: V): AVs =
      AVs(attrT, v.value).add(attrB, v.value)
  }

  abstract class TypedAttrTN[T <: ValueClass](sep: String) extends TypedAttrBase {
    final def apply(v1: ValueT[T], vn: ValueT[T]*): AV =
      av(vn.foldLeft(v1.value)(_ + sep + _.value))
  }

  abstract class TypedAttr_BrWidth extends TypedAttrT1[BrWidth]
    with BrWidthOps

  abstract class TypedAttr_BrStyle extends TypedAttrBase //TypedAttrT[BrStyle]
    with BrStyleOps

  abstract class TypedAttr_Color extends TypedAttrT1[Color]
    with ColourOps

  abstract class TypedAttr_Shape extends TypedAttrBase {
    final def rect(top: ValueT[Len], right: ValueT[Len], bottom: ValueT[Len], left: ValueT[Len]) =
      av(s"rect(${top.value},${right.value},${bottom.value},${left.value})")
  }

  abstract class TypedAttr_BrWidthStyleColour extends TypedAttrT3[WidStyCol](" ")
    with BrWidthOps with BrStyleOps with ColourOps

  abstract class TypedAttr_LenPctAuto extends TypedAttrT1[LenPctAuto] with ZeroLit {
    final def auto = avl(LT.auto)
  }

  abstract class TypedAttr_Length extends TypedAttrT1[LenPct] with ZeroLit {
    override val attr = attr2(CanIUse2.intrinsicWidthTransforms)
    protected def attr2: Transform => Attr
    final def auto       = avl(LT.auto)
    final def available  = av(L.available)
    final def borderBox  = av(L.borderBox)
    final def contentBox = av(L.contentBox)
    final def fitContent = av(L.fitContent)
    final def maxContent = av(L.maxContent)
    final def minContent = av(L.minContent)
  }

  abstract class TypedAttr_MaxLength extends TypedAttrT1[LenPct] with ZeroLit {
    override val attr = attr2(CanIUse2.intrinsicWidthTransforms)
    protected def attr2: Transform => Attr
    final def fillAvailable = av(L.fillAvailable)
    final def fitContent    = av(L.fitContent)
    final def maxContent    = av(L.maxContent)
    final def minContent    = av(L.minContent)
    final def none          = avl(LT.none)
    final def containFloats = av(L.containFloats)
  }

  abstract class TypedAttr_MinLength extends TypedAttrT1[LenPct] with ZeroLit {
    override val attr = attr2(CanIUse2.intrinsicWidthTransforms)
    protected def attr2: Transform => Attr
    final def auto          = avl(LT.auto)
    final def fillAvailable = av(L.fillAvailable)
    final def fitContent    = av(L.fitContent)
    final def maxContent    = av(L.maxContent)
    final def minContent    = av(L.minContent)
    final def containFloats = av(L.containFloats)
  }


  // ================
  // Operation mixins
  // ================

  trait ZeroLit {
    this: TypedAttrBase =>

    /** The digit zero. `"0"`. */
    final def `0` = av("0")

    // final def apply(i: zeroType.T) = av("0")
    // "overloaded method value apply with alternatives"
  }

  trait BrWidthOps extends ZeroLit {
    this: TypedAttrBase =>
    final def thin   = avl(LT.thin)
    final def medium = avl(LT.medium)
    final def thick  = avl(LT.thick)
  }

  trait BrStyleOps {
    this: TypedAttrBase =>
    final def none   = avl(LT.none)
    final def hidden = avl(LT.hidden)
    final def dotted = avl(LT.dotted)
    final def dashed = avl(LT.dashed)
    final def solid  = avl(LT.solid)
    final def double = avl(LT.double)
    final def groove = avl(LT.groove)
    final def ridge  = avl(LT.ridge)
    final def inset  = avl(LT.inset)
    final def outset = avl(LT.outset)
  }

  trait GapStyleOps extends ZeroLit {
    this: TypedAttrBase =>
    final def normal = avl(LT.normal)
  }

  trait RepeatStyleOps {
    this: TypedAttrBase =>
    def repeat  : AV = avl(LT.repeat)
    def space   : AV = avl(LT.space)
    def round   : AV = avl(LT.round)
    def noRepeat: AV = avl(LT.noRepeat)
  }

  trait OverflowBehaviourOps {
    this: TypedAttrBase =>
    def none   : AV = avl(LT.none)
    def auto   : AV = avl(LT.auto)
    def contain: AV = avl(LT.contain)
  }

  trait ColourOps extends ColorOps[AV] {
    this: TypedAttrBase =>
    override protected final def mkColor(s: String): AV = av(s)
  }
}
