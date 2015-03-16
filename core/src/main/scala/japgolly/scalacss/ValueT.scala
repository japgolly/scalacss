package japgolly.scalacss

/**
 * A CSS value that is valid for some context `T`.
 */
final case class ValueT[T <: ValueT.ValueClass](value: Value) {
  @inline def retype[A <: ValueT.ValueClass] = this.asInstanceOf[ValueT[A]]
}

object ValueT {

  /*
  sealed trait ValueT[T]
  def ValueT[T](v: Value) = v.asInstanceOf[ValueT[T]]
  implicit class ValueTExt[T](val v: ValueT[T]) extends AnyVal {
    def value     = v.asInstanceOf[Value]
    def retype[A] = v.asInstanceOf[ValueT[A]]
  }
  */

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
  }

  final case class Length(n: Int, u: LengthUnit) {
    def value = n.toString + u.value
    @inline def *(m: Int): Length = copy(n = this.n * m)
    @inline def /(m: Int): Length = copy(n = this.n / m)
  }

  final case class Percentage(n: Int) {
    def value: Value = n.toString + "%"
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Classes

  /**
   * Represented a context in which a finite set of types are valid.
   *
   * Example:
   * {{{
   *   &lt;br-width&gt; = &lt;length&gt; | thin | medium | thick
   * }}}
   */
  sealed trait ValueClass

  sealed trait Integer   extends ValueClass
  sealed trait Number    extends ValueClass
  sealed trait Len       extends ValueClass
  sealed trait LenPct    extends ValueClass
  sealed trait LenPctNum extends ValueClass
  sealed trait BrWidth   extends ValueClass
  sealed trait BrStyle   extends ValueClass
  sealed trait Color     extends ValueClass

  // -------------------------------------------------------------------------------------------------------------------
  // Rules

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

    def retype[FromT <: ValueClass, To <: ValueClass]: FromT >=> To =
      applyT(_.retype)

    def literal[L <: Literal, To <: ValueClass]: L ==> To =
      apply(_.value)
  }

  object Rules extends Rules
  abstract class Rules {
    @inline implicit def ruleApply[From, To <: ValueClass](f: From)(implicit r: From ==> To): ValueT[To] = r(f)

    @inline implicit def ruleChain[A, B <: ValueClass, C <: ValueClass](implicit ab: A ==> B, bc: B >=> C): A ==> C =
      ab >> bc

    @inline implicit def ruleLen_L: Len <== Length = Rule(_.value)

    @inline implicit def ruleInteger_I: Integer <== Int = Rule(_.toString)

    @inline implicit def ruleNumber_I: Number <== Int    = Rule(_.toString)
    @inline implicit def ruleNumber_D: Number <== Double = Rule(_.toString)

    @inline implicit def ruleLenPct_L: LenPct <=< Len        = Rule.retype
    @inline implicit def ruleLenPct_P: LenPct <== Percentage = Rule(_.value)

    @inline implicit def ruleLenPctNum_LP: LenPctNum <=< LenPct = Rule.retype
    @inline implicit def ruleLenPctNum_N : LenPctNum <=< Number = Rule.retype
    // diverging @inline implicit expansion requires these ↙ :(
    @inline implicit def ruleLenPctNum_L : LenPctNum <=< Len = Rule.retype
    @inline implicit def ruleLenPctNum_I : LenPctNum <== Int = Rule(_.toString)

    @inline implicit def ruleBrWidth_1                                      : BrWidth <=< Len = Rule.retype
    @inline implicit def ruleBrWidth_2[L <: Literal with Literal.BrWidthLit]: BrWidth <== L   = Rule.literal

    @inline implicit def ruleBrStyle_L[L <: Literal with Literal.BrStyleLit]: BrStyle <== L = Rule.literal
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Attributes with type-safe values

  object TypedAttrBase {
    implicit def `Be the attr you were born to be!`(t: TypedAttrBase): Attr = t.attr
  }

  abstract class TypedAttrBase {
    val attr: Attr
    protected def av(v: Value)   : AV = AV(attr, v)
    protected def avl(v: Literal): AV = av(v.value)

    /**
     * The inherit CSS-value causes the element for which it is specified to take the computed value of the property from its parent element. It is allowed on every CSS property.
     *
     * For inherited properties, this reinforces the default behavior, and is only needed to override another rule.  For non-inherited properties, this specifies a behavior that typically makes relatively little sense and you may consider using initial instead, or unset on the all property.
     */
    def inherit = avl(Literal.inherit)

    /** The initial CSS keyword applies the initial value of a property to an element. It is allowed on every CSS property and causes the element for which it is specified to use the initial value of the property. */
    def initial = avl(Literal.initial)

    /** The unset CSS keyword is the combination of the initial and inherit keywords. Like these two other CSS-wide keywords, it can be applied to any CSS property, including the CSS shorthand all. This keyword resets the property to its inherited value if it inherits from its parent or to its initial value if not. In other words, it behaves like the inherit keyword in the first case and like the initial keyword in the second case. */
    def unset = avl(Literal.unset)
  }

  abstract class TypedAttrT[T <: ValueClass] extends TypedAttrBase {
    final def apply(v: ValueT[T]): AV = av(v.value)
//        final def apply[From](f: From)(implicit r: From ==> T): AV = av(r(f).value)
  }

  abstract class TypedAttrT0[T <: ValueClass] extends TypedAttrT[T] {
    /** The digit zero. `"0"`. */
    final def _0 = av("0")
  }

  abstract class TypedAttr_BrWidth extends TypedAttrT[BrWidth] {
    final def thin   = avl(Literal.thin)
    final def medium = avl(Literal.medium)
    final def thick  = avl(Literal.thick)
  }

  abstract class TypedAttr_BrStyle extends TypedAttrBase { //TypedAttrT[BrStyle] {
    final def none   = avl(Literal.none)
    final def hidden = avl(Literal.hidden)
    final def dotted = avl(Literal.dotted)
    final def dashed = avl(Literal.dashed)
    final def solid  = avl(Literal.solid)
    final def double = avl(Literal.double)
    final def groove = avl(Literal.groove)
    final def ridge  = avl(Literal.ridge)
    final def inset  = avl(Literal.inset)
    final def outset = avl(Literal.outset)
  }

  abstract class TypedAttr_Color extends TypedAttrT[Color] with ColorOps[AV] {
    override protected final def mkColor(s: String): AV = av(s)
    final def apply(literal: String) = av(literal)
  }

  abstract class TypedAttr_Shape extends TypedAttrBase {
    final def rect(top: ValueT[Len], right: ValueT[Len], bottom: ValueT[Len], left: ValueT[Len]) =
      av(s"rect(${top.value},${right.value},${bottom.value},${left.value})")
  }
}
