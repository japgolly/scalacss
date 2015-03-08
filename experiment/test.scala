import scalaz._
import shapeless._

object Test {

  // --------------------------------------------------------------------------
  // Basics

  trait Condition
  case object Default extends Condition
  case class Psuedo(sel: String) extends Condition // like :hover
  // media query etc

  // Note: Don't replace with singleton types. Needs wrap or tag for FR-09.
  case class Key(cssName: String)
  type Value = String
  type KVs   = NonEmptyList[(Key, Value)]
  type Style = Map[Condition, KVs]


  // --------------------------------------------------------------------------
  // Style => StyleSheet (CSS)

  trait ApplicableStyle {
    def className: String
    def style: Style
  }

  trait StyleSheet {
    def styles: List[ApplicableStyle]
  }

  type ClassName       = String
  type StyleSheetState = Int
  trait StyleSheetGen {
    val name: Int => ClassName
    val aps: (Style, ClassName) => ApplicableStyle
  }
  // Style → StyleSheet → (StyleSheet, ApplicableStyle)
  // 1. Implicit with mutable var?
  // 2. Trait with mutable var?
  // 3. StateT somehow?
  def ss3_ap(style: Style)(implicit g: StyleSheetGen): State[StyleSheetState, ApplicableStyle] =
    State { s =>
      val as = g.aps(style, g.name(s))
      val s2 = s + 1
      (s2, as)
    }
  class SSS1 {
    private var _i = 0
    private var _styles: List[ApplicableStyle] = Nil
    private def inc(): Int = { val j = _i; _i = j + 1; j }
    def register(s: Style)(implicit g: StyleSheetGen): ApplicableStyle = {
      val a = g.aps(s, g name inc())
      _styles = a :: _styles
      a
    }
    def styles: List[ApplicableStyle] = _styles
    def css: String = ??? // use styles, blah blah blah
  }
  // (3) won't work stupid, it would need a (S₁ → … → Sₙ → StyleObject). Too annoying.
  // (1) is sweet. Easily passable to modules. So much so that Style (as opposed to ApplicableStyle) would pretty much
  // never be needed, right? Composition might need it.
  // An idea might be to have creation ops as a trait, then stick it on different ctxs - allows same creation syntax
  // but can change all creations in scope from Style => ApplicableStyle and vica-versa via a single line change (ie.
  // to the ctx with the ops on it.)

}

// =====================================================================================================================
object Example {
  import Test._

  val backgroundColor = Key("backgroundColor")
  val fontWeight      = Key("fontWeight")
  val resize          = Key("resize")
  val outline         = Key("outline")

  implicit class KeyExt(val k: Key) extends AnyVal {
    def :=(v: Value) = (k,v)
  }

  val style1: Style = Map(Default -> NonEmptyList(backgroundColor := "green", fontWeight := "bold"))
  val style2: Style = Map(Default -> NonEmptyList(resize := "none", outline := "none"))

  class Module1(implicit sss: SSS1) {
    val style1: ApplicableStyle = sss register Map(Default -> NonEmptyList(backgroundColor := "red"))
    val style2: ApplicableStyle = sss register Map(Default -> NonEmptyList(resize := "none"))
  }

  implicit val g: StyleSheetGen = ???
  object SS3 {
    val oops: State[StyleSheetState, ApplicableStyle] = ss3_ap(style1)
  }
  object SS1 {
    private implicit val sss = new SSS1
    val style1a: ApplicableStyle = sss register style1
    val style2a: ApplicableStyle = sss register style2
    val styleInline: ApplicableStyle = sss register Map(Default -> NonEmptyList(resize := "none", outline := "none"))
    val module = new Module1
    module.style1.className

    lazy val css = sss.css
  }
}

// TODO
// ====
// * Allow styles to declare preferred classNames.
//
//#### Definition
// FR-02: Dev shall be able to define a style that depends on required input. (ie. a function)
// FR-04: Dev shall be able to define a style that depends on the current plaform (eg. IE 8, mobile)
// FR-01: Dev shall be able to define a style that requires a specific configuration of children such that the compiler will enforce that the children are styled.
// FR-17: Dev shall be able to define a style that affects unspecified, optionally existant children. (Must like & in LESS. Required for FR-15.)
// FR-20: For styles that require repeated declaration with different keys (eg `-moz-`), Dev shall be able to specify the style and its variants with a single declaration.

//#### Composition
// FR-05: Dev shall be able to compose styles to form a new style.
// FR-06: Dev shall be able to define a style that extends an existing style.
// FR-10: Dev shall be able to specify a composition strategy when merging styles.
// FR-11: Solution shall provide a composition stategy that prevents composition.
// FR-12: Solution shall provide a composition stategy that overrides (i.e. discards one side and chooses the other).
// FR-13: Dev shall be able to create a composition stategy that merges attribute values.
// FR-14: Dev shall be able to specify different composition strategies per attribute type. (i.e. `border-top`, the `margin` family)
// FR-18: When looking at a style definition, Dev shall be able to passively understand if any attributes are being overridden.
// FR-19: When looking at a style definition, Dev shall be able to passively understand which attributes are being overridden. **[pri=low]**

