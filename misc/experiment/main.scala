import scalaz._
import shapeless._
import shapeless.ops.hlist._
import shapeless.syntax.singleton._
import nyaya.test.Domain
import ScalaExt._

import CompositeStyleStuff._

object Test {

  // --------------------------------------------------------------------------
  // Basics

  trait Condition
  case object Default extends Condition
  case class Psuedo(sel: String) extends Condition // like :hover
  // TODO psuedo selectors form a finite set. Should hardcode and give the same type safety as css attr keys.

  type KeyF = Environment => Value => List[CssAttr]
  case class Key(humanName: String, f: KeyF, cmp: Key => KeyComparison)
  type Value = String
  type KV    = (Key, Value)
  type KVs   = NonEmptyList[KV]

  implicit val keyEquality: Eq[Key] = Equal.equalRef // (or by humanName? in which case maybe rename to id/key?)
  sealed trait KeyComparison
  case object SameKey         extends KeyComparison
  case object FullOverride    extends KeyComparison // margin fully overrides margin-left
  case object PartialOverride extends KeyComparison // margin-left partially overrides margin
  case object Unrelated       extends KeyComparison
  // Write laws for this ↑

  trait Style
  trait SingleStyle extends Style
  case class StaticStyle(values        : Map[Condition, (KVs, List[Warning])],
                         unsafeChildren: UnsafeChildren,
                         className     : Option[String]) extends SingleStyle

  // Keys like "label a", ">li"
  // Replace & with style class, prepend if no &. (eg. "&.debug", ">li", "&>li")
  type UnsafeChildren = Map[String, StaticStyle]

  // --------------------------------------------------------------------------
  // FR-02: I => Style

  case class StyleFnT[I](f: I => StaticStyle, d: Domain[I]) extends SingleStyle
  case class StyleFnP[I](f: I => StaticStyle) { // Doesn't really need own class. Just (I => Style) => Domain => StyleFnT
    def over(d: Domain[I]): StyleFnT[I] = StyleFnT(f, d)
  }

  // --------------------------------------------------------------------------
  // FR-04: Dev shall be able to define a style that depends on the current plaform (eg. IE 8, mobile)

  case class MediaQuery(q: String) extends Condition // like @media (max-width: 600px)
  // Later, this can be turned into an AST and run to omit unnecessary CSS.
  // Not now.

  // Env details are gathered in JS-land but blank on the server-side (unless manually specified).
  sealed trait Browser
  case object IE extends Browser
  case object Chrome extends Browser
  // ... whatever
  case class Environment(screenWidth : Option[Int],
                         screenHeight: Option[Int],
                         browser     : Set[Browser], // This is Set instead of Option cos we might be able to rule out
                                                     // some browsers but not all. More importantly, it allows the
                                                     // server to generate CSS for select browsers instead of all-or-1.
                         browserVer  : Option[String])
                         // etc etc more later

  // TODO Inspecting in JS at runtime is one thing but it doesn't fully satisfy FR-04.
  // To have a style definition include a platform condition such that we can generate static output outside of JS:
  //   1. Generate CSS with conds like .ie8.ie9.x123 and gen some JS to apply one at runtime?
  //   2. Rescope this req to only work at JS runtime.
  // Note: Neither SASS or LESS have a solution. People just generate multiple CSS files and use shitty IE cond
  // comments to load the additional styles.

  // --------------------------------------------------------------------------
  // Style composites
  // FR-01: Dev shall be able to define a style that requires a specific configuration of children such that the compiler will enforce that the children are styled.

  implicit class SStyleExt[S <: SingleStyle](val s: S) extends AnyVal {
    def named(w: Witness): Named[w.T, S] = Named(s)
  }

  implicit class NSStyleExt[W, S <: SingleStyle](val n: Named[W, S]) extends AnyVal {
    def :*:[W2, S2](b: Named[W2,S2]) = // todo prevent dup names
      CompositeStyle(b :: n :: HNil)
  }

  type CompositeStyleAux[l <: HList] = CompositeStyle { type L = l }

  private def CompositeStyle[_L <: HList](_l: _L): CompositeStyleAux[_L] =
    new CompositeStyle {
      type L = _L
      val l = _l
    }

  trait CompositeStyle extends Style {
    type L <: HList
    val l: L
    def :*:[W2, S2](b: Named[W2,S2]) = // todo prevent dup names
      CompositeStyle(b :: l)
  }

  // --------------------------------------------------------------------------
  // Composition

  type Warning = String

  case class Merger(f: (KVs, KV) => (List[KV], List[Warning])) {
    @inline def apply(lo: KVs, hi: KV) = f(lo, hi)
    def and(m: Merger): Merger = ??? // TODO make semigroup
    // def map (g: EndoFn[List[KV]]) = Merger((lo,hi) => f(lo,hi) map1 g)
    // def mapW(g: EndoFn[List[Warning]]) = Merger((lo,hi) => f(lo,hi) map2 g)
  }
  object Merger {
    val concat  = Merger((lo,hi) => (lo.list ::: hi :: Nil, Nil))
    val replace = Merger((lo,hi) => (hi :: Nil, Nil))
    val warn    = Merger((lo,hi) => (Nil, s"${hi._1.humanName} overrides ${lo.list.map(_._1.humanName) mkString ","}." :: Nil))
  }

  def compose     (a: StaticStyle, b: StaticStyle)(implicit m: Merger): StaticStyle = ???
  def compose[  B](a: StaticStyle, b: StyleFnT[B])(implicit m: Merger): StyleFnT[B] = ???
  def compose[  B](a: StaticStyle, b: StyleFnP[B])(implicit m: Merger): StyleFnP[B] = ???
  def compose[A  ](a: StyleFnT[A], b: StaticStyle)(implicit m: Merger): StyleFnT[A] = ???
  def compose[A,B](a: StyleFnT[A], b: StyleFnT[B])(implicit m: Merger): StyleFnT[(A,B)] = ???
  def compose[A,B](a: StyleFnT[A], b: StyleFnP[B])(implicit m: Merger): Domain[B] => StyleFnT[(A,B)] = ???
  def compose[A  ](a: StyleFnP[A], b: StaticStyle)(implicit m: Merger): StyleFnP[A] = ???
  def compose[A,B](a: StyleFnP[A], b: StyleFnT[B])(implicit m: Merger): Domain[A] => StyleFnT[(A,B)] = ???
  def compose[A,B](a: StyleFnP[A], b: StyleFnP[B])(implicit m: Merger): (Domain[A], Domain[B]) => StyleFnT[(A,B)] = ???

  implicit val recommendMerger = Merger.concat and Merger.warn

  def extend(a: StaticStyle, b: StaticStyle) = compose(a,b)(Merger.replace)
  // ...
  // TODO Maybe use implicits with dep-types for compose. extend would be a one-liner then.


  // --------------------------------------------------------------------------
  // Style => StyleSheet (CSS)
  // --------------------------------------------------------------------------

  case class CssAttr(key: String, value: String)

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
    val aps: (SingleStyle, ClassName) => ApplicableStyle
  }

  class SSS1(implicit g: StyleSheetGen) {
    private var _i = 0
    private var _styles: List[ApplicableStyle] = Nil
    private def inc(): Int = { val j = _i; _i = j + 1; j }
    def registerAs(cn: ClassName, s: StaticStyle): ApplicableStyle = {
      val a = g.aps(s, cn)
      _styles = a :: _styles
      a
    }
    def register(s: StaticStyle): ApplicableStyle =
      registerAs(s.className getOrElse g.name(inc()), s)
    def register[I](s: StyleFnT[I]): I => ApplicableStyle = {
      // equals/hashCode could fuck people here
      val m = s.d.toStream.foldLeft(Map.empty[I, ApplicableStyle])((q, i) =>
        q.updated(i, register(s.f(i))))
      // Add custom warning for failure
      m.apply
    }
    def registerC[M <: HList](c: CompositeStyle)(implicit mapper: Mapper.Aux[registerS.type, c.L, M], usage: MkUsage[M]) : usage.Out = usage apply mapper(c.l)
    object registerS extends Poly1 {
      implicit def caseStaticW[W] = at[Named[W,StaticStyle]](_ map (register(_)))
      implicit def caseStyleFnW[W,I] = at[Named[W,StyleFnT[I]]](_ map (register(_)))
    }
    def styles: List[ApplicableStyle] = _styles
    def css: String = ??? // use styles, blah blah blah
  }
}



// =====================================================================================================================
object Example {
  import Test._

  def prefixes(k: String): KeyF = e => v => {
    var r = CssAttr(k ,v) :: Nil
    if (e.browser contains IE) r ::= CssAttr(s"-ms-$k", v)
    // blah blah
    r
  }

  def simpleKey(name: String) = Key(name, _ => CssAttr(name, _) :: Nil, ???)

  val backgroundColor = simpleKey("background-color")
  val fontWeight      = simpleKey("font-weight")
  val resize          = simpleKey("resize")
  val outline         = simpleKey("outline")
  val paddingLeft     = simpleKey("padding-left")
  val borderRadius    = Key("border-radius", prefixes("border-radius"), ???)

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
    StaticStyle(Map(Default -> (NonEmptyList(a, b: _*), Nil)), Map.empty, None)

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

  val compStyle = {
    val container = quickStyle(fontWeight := "bold")
    val label     = quickStyle(backgroundColor := "red")
    val checkbox  = boolStyle
    container.named('cont) :*: label.named('label) :*: checkbox.named('chk)
  }

  val hasUnsafeChildren =
    StaticStyle(
      Map(Default -> (NonEmptyList(fontWeight := "bold"), Nil)),
      Map("button.red" -> quickStyle(backgroundColor := "red")),
      None)

  // CSS gen

  implicit val g: StyleSheetGen = null
  object SS1 {
    private implicit val sss = new SSS1
    lazy val css = sss.css

    val style1a: ApplicableStyle = sss register style1
    val style2a: ApplicableStyle = sss.registerAs("style2", style2)

    val styleInline: ApplicableStyle = sss register quickStyle(resize := "none", outline := "none")

    val styleFn_b: Boolean => ApplicableStyle = sss register boolStyle
    val styleFn_i: Int     => ApplicableStyle = sss register intStyle.over(Domain.ofRange(0 to 10))

    val m1 = new Module1
    val m2 = new Module2(maxWhatevers = 10)
    m2.styleFn_i(7).className

    val compStyleA = sss registerC compStyle
    compStyleA('cont, o => _('label, l => _('chk, c => s"${o.className}|${l.className}|${c(true).className}")))
  }
}

// TODO: Composition (type-level)
// ==============================
// FR-05: Dev shall be able to compose styles to form a new style.
// FR-06: Dev shall be able to define a style that extends an existing style.
// FR-10: Dev shall be able to specify a composition strategy when merging styles.
// FR-11: Solution shall provide a composition stategy that prevents composition.
// FR-12: Solution shall provide a composition stategy that overrides (i.e. discards one side and chooses the other).
// FR-13: Dev shall be able to create a composition stategy that merges attribute values.
// FR-14: Dev shall be able to specify different composition strategies per attribute type. (i.e. `border-top`, the `margin` family)
// FR-18: When looking at a style definition, Dev shall be able to passively understand if any attributes are being overridden.
// FR-19: When looking at a style definition, Dev shall be able to passively understand which attributes are being overridden. **[pri=low]**
// * Don't forget overlap between unit and composite CSS attributes (eg. paddingLeft & padding)
//
// Ideas:
// - Keys are just keys. Merge policy implicits required at merge time.
// - Keys have a concept of overrideability. Prevent override by default, specify when override ok?
// - Fuck it at the type level, just put runtime warnings in (conflicts are discovered immediately as all CSS is static)
// - Have different key sets: 1) fast, untyped, no override protection. 2) HList-backed with override features.
