import scalaz._
import shapeless._
import japgolly.nyaya.test.Domain

object Test {

  // --------------------------------------------------------------------------
  // Basics

  trait Condition
  case object Default extends Condition
  case class Psuedo(sel: String) extends Condition // like :hover
  // TODO psuedo selectors form a finite set. Should hardcode and give the same type safety as css attr keys.

  // Note: Don't replace with singleton types. Needs wrap or tag for FR-09.
  case class Key(cssName: String)
  type Value = String
  type KVs   = NonEmptyList[(Key, Value)]

  trait Style
  case class StaticStyle(value: Map[Condition, KVs]) extends Style

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
    def register(s: StaticStyle)(implicit g: StyleSheetGen): ApplicableStyle = {
      val a = g.aps(s, g name inc())
      _styles = a :: _styles
      a
    }
    def register[I](s: StyleFnT[I])(implicit g: StyleSheetGen): I => ApplicableStyle = {
      // equals/hashCode could fuck people here
      val m = s.d.toStream.foldLeft(Map.empty[I, ApplicableStyle])((q, i) =>
        q.updated(i, register(s.f(i))))
      // Add custom warning for failure
      m.apply
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

  // --------------------------------------------------------------------------
  // FR-02: I ⇒ Style

  case class StyleFnT[I](f: I => StaticStyle, d: Domain[I]) extends Style
  case class StyleFnP[I](f: I => StaticStyle) { // Doesn't really need own class. Just (I => Style) => Domain => StyleFnT
    def over(d: Domain[I]): StyleFnT[I] = StyleFnT(f, d)
  }

  // --------------------------------------------------------------------------
  // FR-04: Dev shall be able to define a style that depends on the current plaform (eg. IE 8, mobile)

  case class MediaQuery(q: String) extends Condition // like @media (max-width: 600px)
  // Later, this can be turned into an AST and run to omit unnecessary CSS.
  // Not now.

  // This will only be available on the JS side.
  // In fact, a separate library which is just a scalajs facade would be better.
  // If scalacss needs it, it can depend on it.
  object Platform {
    val name: String = "IE"
    val version: String = "10.0"
    // ...
    // https://github.com/bestiejs/platform.js
  }

  // TODO Inspecting in JS at runtime is one thing but it doesn't fully satisfy FR-04.
  // To have a style definition include a platform condition such that we can generate static output outside of JS:
  //   1. Generate CSS with conds like .ie8.ie9.x123 and gen some JS to apply one at runtime?
  //   2. Rescope this req to only work at JS runtime.
  // Note: Neither SASS or LESS have a solution. People just generate multiple CSS files and use shitty IE cond
  // comments to load the additional styles.

}

// =====================================================================================================================
object Example {
  import Test._

  val backgroundColor = Key("background-color")
  val fontWeight      = Key("font-weight")
  val resize          = Key("resize")
  val outline         = Key("outline")
  val paddingLeft     = Key("padding-left")

  implicit class KeyExt(val k: Key) extends AnyVal {
    def :=(v: Value) = (k,v)
  }

  // Better to have unit classes like PX, EM, EX, then use implicits like 1.as[PX] or something
  // Can still keep this nicer syntax tho (although then it's 2 ways of doing same thing)
  implicit class IntExt(val i: Int) extends AnyVal {
    def px = s"$i.px"
    def em = s"$i.em"
    def ex = s"$i.ex"
  }

  def quickStyle(a: (Key, Value), b: (Key, Value)*): StaticStyle =
    StaticStyle(Map(Default -> NonEmptyList(a, b: _*)))

  // Styles definitions

  val style1 = quickStyle(backgroundColor := "green", fontWeight := "bold")
  val style2 = quickStyle(resize := "none", outline := "none")

  val boolStyle = StyleFnT[Boolean](b => quickStyle(backgroundColor := (if (b) "none" else "#ddd")), Domain.boolean)
  val intStyle  = StyleFnP[Int]    (i => quickStyle(paddingLeft     := (i*4).ex))

  class Module1(implicit sss: SSS1) {
    val style1: ApplicableStyle = sss register quickStyle(backgroundColor := "red")
    val style2: ApplicableStyle = sss register quickStyle(resize := "none")
  }
  class Module2(maxWhatevers: Int)(implicit sss: SSS1) {
    val styleFn_b: Boolean => ApplicableStyle = sss register boolStyle
    val styleFn_i: Int     => ApplicableStyle = sss register intStyle.over(Domain.ofRange(0 to maxWhatevers))
  }

  // CSS gen

  implicit val g: StyleSheetGen = ???
  object SS3 {
    val oops: State[StyleSheetState, ApplicableStyle] = ss3_ap(style1)
  }
  object SS1 {
    private implicit val sss = new SSS1
    lazy val css = sss.css

    val style1a: ApplicableStyle = sss register style1
    val style2a: ApplicableStyle = sss register style2

    val styleInline: ApplicableStyle = sss register quickStyle(resize := "none", outline := "none")

    val styleFn_b: Boolean => ApplicableStyle = sss register boolStyle
    val styleFn_i: Int     => ApplicableStyle = sss register intStyle.over(Domain.ofRange(0 to 10))

    val m1 = new Module1
    val m2 = new Module2(maxWhatevers = 10)
  }
}

// TODO
// ====
// * Allow styles to declare preferred classNames.
// * Don't forget overlap between unit and composite CSS attributes (eg. paddingLeft & padding)
//
//#### Definition
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

