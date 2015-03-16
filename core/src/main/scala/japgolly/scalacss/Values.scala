package japgolly.scalacss

import scala.language.reflectiveCalls
import shapeless.Witness

// @inline final etc to implicit defs etc

sealed case class Literal(value: String) //extends AnyVal

object ValueTypes {

  // These are (mostly) to make TypedAttrBase.av work.
  // Keep?
//  implicit def cssValueFromLiteral(s: Literal): Value = s.value
  //  implicit def cssValueFromLen(x: Length): Value = x.value
  //  implicit def cssValueFromPct(x: Percentage): Value = x.value
//  implicit def cssValueFromValueT(v: ValueT[_]): Value = v.value


  // -------------------------------------------------------------------------------------------------------------------
  // Literals

  final val w0   = Witness(0)
  /*
  final val w100 = Witness(100)
  final val w200 = Witness(200)
  final val w300 = Witness(300)
  final val w400 = Witness(400)
  final val w500 = Witness(500)
  final val w600 = Witness(600)
  final val w700 = Witness(700)
  final val w800 = Witness(800)
  final val w900 = Witness(900)
  implicit def cssValueFromInt(w: Witness.Lt[Int]): Value = {val i: Int = w.value; i.toString}
  implicit def cssValueFrom0(x: Int with AnyRef)(implicit ev: x.type =:= w0.T): Value = "0"
  */


  // -------------------------------------------------------------------------------------------------------------------
  // Rules

  sealed trait ValueClass

  /**
   * A CSS value that is valid for some context `T`.
   */
  final case class ValueT[T <: ValueClass](value: Value) /*extends AnyVal*/ {
    @inline def retype[A <: ValueClass] = this.asInstanceOf[ValueT[A]]
  }

  /*
  sealed trait ValueT[T]
  def ValueT[T](v: Value) = v.asInstanceOf[ValueT[T]]
  implicit class ValueTExt[T](val v: ValueT[T]) extends AnyVal {
    def value     = v.asInstanceOf[Value]
    def retype[A] = v.asInstanceOf[ValueT[A]]
  }
  */

  abstract class Rule[A, B <: ValueClass] {
//    type B <: ValueClass
    def apply(a: A): ValueT[B]

    final def >>[C <: ValueClass](f: B >=> C): A ==> C =
      Rule.applyT(a => f(this(a)))
  }

  type ==>[From              , To   <: ValueClass] = Rule[From, To]// { type B = To }
  type >=>[From <: ValueClass, To   <: ValueClass] = ValueT[From] ==> To
  type <==[To   <: ValueClass, From              ] = From ==> To
  type <=<[To   <: ValueClass, From <: ValueClass] = From >=> To

  object Rule {
    def apply[From, To <: ValueClass](f: From => Value): From ==> To =
      applyT(x => ValueT(f(x)))

    def applyT[From, To <: ValueClass](f: From => ValueT[To]): From ==> To =
      new Rule[From, To] {
//        override type B = To
        override def apply(x: From): ValueT[To] = f(x)
      }

    def retype[FromT <: ValueClass, To <: ValueClass]: FromT >=> To =
      applyT(_.retype)

//    def literal[From] = new LiteralB
//    def literal[To](l: Literal): l.type ==> To = apply(_.value)
//    def literal[To <: ValueClass] =
//      new {
//        def apply[L <: Literal](l: L): L ==> To = Rule(_ => l.value)
//      }
    def literal[L <: Literal, To <: ValueClass]: L ==> To = apply(_.value)
  }

//  trait LowPri {
    implicit def ruleApply[From, To <: ValueClass](f: From)(implicit r: From ==> To): ValueT[To] = r(f)
//    implicit def ruleId[A <: ValueClass]: A >=> A = Rule.retype
//  }

//  object Omfg extends LowPri {
//    implicit def ruleChain[A, B, C](implicit ab: A ==> B, bc: B >=> C): A ==> C =
    implicit def ruleChain[A, B <: ValueClass, C <: ValueClass](implicit ab: A ==> B, bc: B >=> C): A ==> C =
      ab >> bc
//  }

  // -------------------------------------------------------------------------------------------------------------------
  // Data types

  sealed abstract class LengthUnit(val value: String)
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

  sealed trait Length extends ValueClass
//  case object LengthZ extends Length {
//    override def value = "0"
//  }
  case class LengthI(n: Int, u: LengthUnit) {
    def value = n.toString + "." + u.value
  }

  case class Percentage(n: Int) {
    def value: Value = n.toString + "%"
  }

  sealed trait Integer extends ValueClass
  implicit val ruleInteger_I: Integer <== Int = Rule(_.toString)

  sealed trait Number extends ValueClass
  implicit val ruleNumber_I: Number <== Int    = Rule(_.toString)
  implicit def ruleNumber_D: Number <== Double = Rule(_.toString)

  sealed trait LenPct extends ValueClass
  //implicit def ruleLenPct_Z: LenPct <== w0.T       = Rule(_ => "0")
  implicit val ruleLenPct_L: LenPct <== LengthI    = Rule(_.value)
  implicit val ruleLenPct_P: LenPct <== Percentage = Rule(_.value)

  sealed trait LenPctNum extends ValueClass
  implicit val ruleLenPctNum_LP: LenPctNum <=< LenPct = Rule.retype
  implicit val ruleLenPctNum_N : LenPctNum <=< Number = Rule.retype

  sealed trait BrWidth extends ValueClass
  implicit val ruleBrWidth_1: BrWidth <== LengthI = Rule(_.value)
  implicit def ruleBrWidth_2[L <: Literal with Values.BrWidthLit]: BrWidth <== L = Rule.literal

  sealed trait BrStyle extends ValueClass
  implicit def ruleBrStyle_L[L <: Literal with Values.BrStyleLit]: BrWidth <== L = Rule.literal

  sealed trait Color extends ValueClass

  // -------------------------------------------------------------------------------------------------------------------

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
    def inherit = avl(Values.inherit)

    /** The initial CSS keyword applies the initial value of a property to an element. It is allowed on every CSS property and causes the element for which it is specified to use the initial value of the property. */
    def initial = avl(Values.initial)

    /** The unset CSS keyword is the combination of the initial and inherit keywords. Like these two other CSS-wide keywords, it can be applied to any CSS property, including the CSS shorthand all. This keyword resets the property to its inherited value if it inherits from its parent or to its initial value if not. In other words, it behaves like the inherit keyword in the first case and like the initial keyword in the second case. */
    def unset = avl(Values.unset)
  }

  abstract class TypedAttrT[T <: ValueClass] extends TypedAttrBase {
    final def apply(v: ValueT[T]): AV = av(v.value)
//    final def apply[From](f: From)(implicit r: From ==> T): AV = av(r(f).value)
  }

  abstract class TypedAttrT0[T <: ValueClass] extends TypedAttrT[T] {
    final def _0 = av("0")
  }

  abstract class TypedAttr_BrWidth extends TypedAttrT[BrWidth] {
    final def thin   = avl(Values.thin)
    final def medium = avl(Values.medium)
    final def thick  = avl(Values.thick)
  }

  abstract class TypedAttr_BrStyle extends TypedAttrBase { //TypedAttrT[BrStyle] {
    final def none   = avl(Values.none)
    final def hidden = avl(Values.hidden)
    final def dotted = avl(Values.dotted)
    final def dashed = avl(Values.dashed)
    final def solid  = avl(Values.solid)
    final def double = avl(Values.double)
    final def groove = avl(Values.groove)
    final def ridge  = avl(Values.ridge)
    final def inset  = avl(Values.inset)
    final def outset = avl(Values.outset)
  }

  abstract class TypedAttr_Color extends TypedAttrT[Color] with ColorOps[AV] {
    override protected final def mkColor(s: String): AV = av(s)
    final def apply(literal: String) = av(literal)
  }

  abstract class TypedAttr_Shape extends TypedAttrBase {
    final def rect(top: ValueT[Length], right: ValueT[Length], bottom: ValueT[Length], left: ValueT[Length]) =
      av(s"rect(${top.value},${right.value},${bottom.value},${left.value})")
  }

}
import ValueTypes._
// =====================================================================================================================
object Values {

  @inline def absolute              = Literal("absolute")
  @inline def active                = Literal("active")
  @inline def alias                 = Literal("alias")
  @inline def all                   = Literal("all")
  @inline def all_petite_caps       = Literal("all-petite-caps")
  @inline def all_scroll            = Literal("all-scroll")
  @inline def all_small_caps        = Literal("all-small-caps")
  @inline def always                = Literal("always")
  @inline def auto                  = Literal("auto")
  @inline def available             = Literal("available")
  @inline def avoid                 = Literal("avoid")
  @inline def avoid_column          = Literal("avoid-column")
  @inline def avoid_page            = Literal("avoid-page")
  @inline def balance               = Literal("balance")
  @inline def baseline              = Literal("baseline")
  @inline def bidi_override         = Literal("bidi-override")
  @inline def blink                 = Literal("blink")
  @inline def block                 = Literal("block")
  @inline def block_end             = Literal("block-end")
  @inline def block_start           = Literal("block-start")
  @inline def bold                  = Literal("bold")
  @inline def bolder                = Literal("bolder")
  @inline def border_box            = Literal("border-box")
  @inline def both                  = Literal("both")
  @inline def bottom                = Literal("bottom")
  @inline def break_all             = Literal("break-all")
  @inline def break_word            = Literal("break-word")
  @inline def capitalize            = Literal("capitalize")
  @inline def caption               = Literal("caption")
  @inline def cell                  = Literal("cell")
  @inline def center                = Literal("center")
  @inline def clip                  = Literal("clip")
  @inline def clone_                = Literal("clone")
  @inline def close_quote           = Literal("close-quote")
  @inline def collapse              = Literal("collapse")
  @inline def col_resize            = Literal("col-resize")
  @inline def column                = Literal("column")
  @inline def column_reverse        = Literal("column-reverse")
  @inline def complex               = Literal("complex")
  @inline def condensed             = Literal("condensed")
  @inline def contain               = Literal("contain")
  @inline def content               = Literal("content")
  @inline def content_box           = Literal("content-box")
  @inline def contents              = Literal("contents")
  @inline def context_menu          = Literal("context-menu")
  @inline def copy                  = Literal("copy")
  @inline def cover                 = Literal("cover")
  @inline def crisp_edges           = Literal("crisp-edges")
  @inline def crosshair             = Literal("crosshair")
  @inline def default               = Literal("default")
  @inline def disabled              = Literal("disabled")
  @inline def each_line             = Literal("each-line")
  @inline def ellipsis              = Literal("ellipsis")
  @inline def embed                 = Literal("embed")
  @inline def end                   = Literal("end")
  @inline def e_resize              = Literal("e-resize")
  @inline def ew_resize             = Literal("ew-resize")
  @inline def expanded              = Literal("expanded")
  @inline def extra_condensed       = Literal("extra-condensed")
  @inline def extra_expanded        = Literal("extra-expanded")
  @inline def fill                  = Literal("fill")
  @inline def fill_available        = Literal("fill-available")
  @inline def fit_content           = Literal("fit-content")
  @inline def fixed                 = Literal("fixed")
  @inline def flat                  = Literal("flat")
  @inline def flex                  = Literal("flex")
  @inline def flex_end              = Literal("flex-end")
  @inline def flex_start            = Literal("flex-start")
  @inline def flip                  = Literal("flip")
  @inline def from_image            = Literal("from-image")
  @inline def full_width            = Literal("full-width")
  @inline def grab                  = Literal("grab")
  @inline def grabbing              = Literal("grabbing")
  @inline def grid                  = Literal("grid")
  @inline def hanging               = Literal("hanging")
  @inline def help                  = Literal("help")
  @inline def hide                  = Literal("hide")
  @inline def historical_forms      = Literal("historical-forms")
  @inline def horizontal            = Literal("horizontal")
  @inline def horizontal_tb         = Literal("horizontal-tb")
  @inline def icon                  = Literal("icon")
  @inline def inactive              = Literal("inactive")
  @inline def inherit               = Literal("inherit")
  @inline def initial               = Literal("initial")
  @inline def inline                = Literal("inline")
  @inline def inline_block          = Literal("inline-block")
  @inline def inline_end            = Literal("inline-end")
  @inline def inline_flex           = Literal("inline-flex")
  @inline def inline_grid           = Literal("inline-grid")
  @inline def inline_start          = Literal("inline-start")
  @inline def inline_table          = Literal("inline-table")
  @inline def inside                = Literal("inside")
  @inline def inter_character       = Literal("inter-character")
  @inline def invert                = Literal("invert")
  @inline def isolate               = Literal("isolate")
  @inline def isolate_override      = Literal("isolate-override")
  @inline def italic                = Literal("italic")
  @inline def justify               = Literal("justify")
  @inline def keep_all              = Literal("keep-all")
  @inline def left                  = Literal("left")
  @inline def lighter               = Literal("lighter")
  @inline def line_through          = Literal("line-through")
  @inline def list_item             = Literal("list-item")
  @inline def loose                 = Literal("loose")
  @inline def lowercase             = Literal("lowercase")
  @inline def ltr                   = Literal("ltr")
  @inline def manipulation          = Literal("manipulation")
  @inline def manual                = Literal("manual")
  @inline def match_parent          = Literal("match-parent")
  @inline def max_content           = Literal("max-content")
  @inline def menu                  = Literal("menu")
  @inline def message_box           = Literal("message-box")
  @inline def middle                = Literal("middle")
  @inline def min_content           = Literal("min-content")
  @inline def mixed                 = Literal("mixed")
  @inline def move                  = Literal("move")
  @inline def ne_resize             = Literal("ne-resize")
  @inline def nesw_resize           = Literal("nesw-resize")
  @inline def no_close_quote        = Literal("no-close-quote")
  @inline def no_drop               = Literal("no-drop")
  @inline def no_open_quote         = Literal("no-open-quote")
  @inline def normal                = Literal("normal")
  @inline def not_allowed           = Literal("not-allowed")
  @inline def nowrap                = Literal("nowrap")
  @inline def n_resize              = Literal("n-resize")
  @inline def ns_resize             = Literal("ns-resize")
  @inline def nw_resize             = Literal("nw-resize")
  @inline def nwse_resize           = Literal("nwse-resize")
  @inline def oblique               = Literal("oblique")
  @inline def open_quote            = Literal("open-quote")
  @inline def ordinal               = Literal("ordinal")
  @inline def outside               = Literal("outside")
  @inline def over                  = Literal("over")
  @inline def overline              = Literal("overline")
  @inline def padding_box           = Literal("padding-box")
  @inline def page                  = Literal("page")
  @inline def pan_x                 = Literal("pan-x")
  @inline def pan_y                 = Literal("pan-y")
  @inline def petite_caps           = Literal("petite-caps")
  @inline def pixelated             = Literal("pixelated")
  @inline def plaintext             = Literal("plaintext")
  @inline def pointer               = Literal("pointer")
  @inline def pre                   = Literal("pre")
  @inline def pre_line              = Literal("pre-line")
  @inline def preserve_3d           = Literal("preserve-3d")
  @inline def pre_wrap              = Literal("pre-wrap")
  @inline def progress              = Literal("progress")
  @inline def relative              = Literal("relative")
  @inline def repeat                = Literal("repeat")
  @inline def right                 = Literal("right")
  @inline def round                 = Literal("round")
  @inline def row                   = Literal("row")
  @inline def row_resize            = Literal("row-resize")
  @inline def row_reverse           = Literal("row-reverse")
  @inline def rtl                   = Literal("rtl")
  @inline def ruby                  = Literal("ruby")
  @inline def ruby_base             = Literal("ruby-base")
  @inline def ruby_base_container   = Literal("ruby-base-container")
  @inline def ruby_text             = Literal("ruby-text")
  @inline def ruby_text_container   = Literal("ruby-text-container")
  @inline def run_in                = Literal("run-in")
  @inline def scale_down            = Literal("scale-down")
  @inline def scroll                = Literal("scroll")
  @inline def semi_condensed        = Literal("semi-condensed")
  @inline def semi_expanded         = Literal("semi-expanded")
  @inline def separate              = Literal("separate")
  @inline def se_resize             = Literal("se-resize")
  @inline def show                  = Literal("show")
  @inline def sideways              = Literal("sideways")
  @inline def sideways_left         = Literal("sideways-left")
  @inline def sideways_right        = Literal("sideways-right")
  @inline def slashed_zero          = Literal("slashed-zero")
  @inline def slice                 = Literal("slice")
  @inline def small_caps            = Literal("small-caps")
  @inline def small_caption         = Literal("small-caption")
  @inline def smooth                = Literal("smooth")
  @inline def space_around          = Literal("space-around")
  @inline def space_between         = Literal("space-between")
  @inline def s_resize              = Literal("s-resize")
  @inline def start                 = Literal("start")
  @inline def start_end             = Literal("start end")
  @inline def static                = Literal("static")
  @inline def status_bar            = Literal("status-bar")
  @inline def sticky                = Literal("sticky")
  @inline def stretch               = Literal("stretch")
  @inline def strict                = Literal("strict")
  @inline def style                 = Literal("style")
  @inline def sub                   = Literal("sub")
  @inline def super_                = Literal("super")
  @inline def sw_resize             = Literal("sw-resize")
  @inline def table                 = Literal("table")
  @inline def table_cell            = Literal("table-cell")
  @inline def table_column          = Literal("table-column")
  @inline def table_column_group    = Literal("table-column-group")
  @inline def table_footer_group    = Literal("table-footer-group")
  @inline def table_header_group    = Literal("table-header-group")
  @inline def table_row             = Literal("table-row")
  @inline def table_row_group       = Literal("table-row-group")
  @inline def text                  = Literal("text")
  @inline def text_bottom           = Literal("text-bottom")
  @inline def text_top              = Literal("text-top")
  @inline def titling_caps          = Literal("titling-caps")
  @inline def top                   = Literal("top")
  @inline def ultra_condensed       = Literal("ultra-condensed")
  @inline def ultra_expanded        = Literal("ultra-expanded")
  @inline def under                 = Literal("under")
  @inline def underline             = Literal("underline")
  @inline def unicase               = Literal("unicase")
  @inline def unset                 = Literal("unset")
  @inline def uppercase             = Literal("uppercase")
  @inline def upright               = Literal("upright")
  @inline def use_glyph_orientation = Literal("use-glyph-orientation")
  @inline def vertical              = Literal("vertical")
  @inline def vertical_lr           = Literal("vertical-lr")
  @inline def vertical_rl           = Literal("vertical-rl")
  @inline def vertical_text         = Literal("vertical-text")
  @inline def visible               = Literal("visible")
  @inline def wait_                 = Literal("wait")
  @inline def wavy                  = Literal("wavy")
  @inline def weight                = Literal("weight")
  @inline def wrap                  = Literal("wrap")
  @inline def wrap_reverse          = Literal("wrap-reverse")
  @inline def w_resize              = Literal("w-resize")
  @inline def zoom_in               = Literal("zoom-in")
  @inline def zoom_out              = Literal("zoom-out")

  @inline def xx_small              = Literal("xx-small")
  @inline def s_small               = Literal("s-small")
  @inline def small                 = Literal("small")
  @inline def large                 = Literal("large")
  @inline def x_large               = Literal("x-large")
  @inline def xx_large              = Literal("xx-large")

  @inline def larger                = Literal("larger")
  @inline def smaller               = Literal("smaller")

  // <br-width>
  sealed trait BrWidthLit
  object thin   extends Literal("thin")   with BrWidthLit
  object medium extends Literal("medium") with BrWidthLit
  object thick  extends Literal("thick")  with BrWidthLit

  // <br-style>
  sealed trait BrStyleLit
  object none   extends Literal("none")   with BrStyleLit
  object hidden extends Literal("hidden") with BrStyleLit
  object dotted extends Literal("dotted") with BrStyleLit
  object dashed extends Literal("dashed") with BrStyleLit
  object solid  extends Literal("solid")  with BrStyleLit
  object double extends Literal("double") with BrStyleLit
  object groove extends Literal("groove") with BrStyleLit
  object ridge  extends Literal("ridge")  with BrStyleLit
  object inset  extends Literal("inset")  with BrStyleLit
  object outset extends Literal("outset") with BrStyleLit
}

object Color extends ColorOps[ValueT[Color]] {
  override protected def mkColor(s: String): ValueT[Color] =
    apply(s)

  // TODO A color macro would be good like #"639" or c"#dedede". It could verify that its valid hex x 3 or 6.

  @inline def apply(v: String): ValueT[Color] =
    ValueT(v)
}

trait ColorOps[Out] {
  protected def mkColor(s: String): Out

  final def rgb(r: Int, g: Int, b: Int): Out =
    mkColor(s"rgb($r,$g,$b)")

  final def rgb(r: Percentage, g: Percentage, b: Percentage): Out =
    mkColor(s"rgb(${r.value},${g.value},${b.value})")

  final def hsl(h: Int, s: Percentage, l: Percentage): Out =
    mkColor(s"hsl($h,${s.value},${l.value})")

  final def rgba(r: Int, g: Int, b: Int, a: Double): Out =
    mkColor(s"rgba($r,$g,$b,$a)")

  final def hsla(h: Int, s: Percentage, l: Percentage, a: Double): Out =
    mkColor(s"hsla($h,${s.value},${l.value},$a)")

  final def currentColor         = mkColor("currentColor")
  final def transparent          = mkColor("transparent")

  final def aliceblue            = mkColor("aliceblue")
  final def antiquewhite         = mkColor("antiquewhite")
  final def aqua                 = mkColor("aqua")
  final def aquamarine           = mkColor("aquamarine")
  final def azure                = mkColor("azure")
  final def beige                = mkColor("beige")
  final def bisque               = mkColor("bisque")
  final def black                = mkColor("black")
  final def blanchedalmond       = mkColor("blanchedalmond")
  final def blue                 = mkColor("blue")
  final def blueviolet           = mkColor("blueviolet")
  final def brown                = mkColor("brown")
  final def burlywood            = mkColor("burlywood")
  final def cadetblue            = mkColor("cadetblue")
  final def chartreuse           = mkColor("chartreuse")
  final def chocolate            = mkColor("chocolate")
  final def coral                = mkColor("coral")
  final def cornflowerblue       = mkColor("cornflowerblue")
  final def cornsilk             = mkColor("cornsilk")
  final def crimson              = mkColor("crimson")
  final def cyan                 = mkColor("cyan")
  final def darkblue             = mkColor("darkblue")
  final def darkcyan             = mkColor("darkcyan")
  final def darkgoldenrod        = mkColor("darkgoldenrod")
  final def darkgray             = mkColor("darkgray")
  final def darkgreen            = mkColor("darkgreen")
  final def darkgrey             = mkColor("darkgrey")
  final def darkkhaki            = mkColor("darkkhaki")
  final def darkmagenta          = mkColor("darkmagenta")
  final def darkolivegreen       = mkColor("darkolivegreen")
  final def darkorange           = mkColor("darkorange")
  final def darkorchid           = mkColor("darkorchid")
  final def darkred              = mkColor("darkred")
  final def darksalmon           = mkColor("darksalmon")
  final def darkseagreen         = mkColor("darkseagreen")
  final def darkslateblue        = mkColor("darkslateblue")
  final def darkslategray        = mkColor("darkslategray")
  final def darkslategrey        = mkColor("darkslategrey")
  final def darkturquoise        = mkColor("darkturquoise")
  final def darkviolet           = mkColor("darkviolet")
  final def deeppink             = mkColor("deeppink")
  final def deepskyblue          = mkColor("deepskyblue")
  final def dimgray              = mkColor("dimgray")
  final def dimgrey              = mkColor("dimgrey")
  final def dodgerblue           = mkColor("dodgerblue")
  final def firebrick            = mkColor("firebrick")
  final def floralwhite          = mkColor("floralwhite")
  final def forestgreen          = mkColor("forestgreen")
  final def fuchsia              = mkColor("fuchsia")
  final def gainsboro            = mkColor("gainsboro")
  final def ghostwhite           = mkColor("ghostwhite")
  final def gold                 = mkColor("gold")
  final def goldenrod            = mkColor("goldenrod")
  final def gray                 = mkColor("gray")
  final def green                = mkColor("green")
  final def greenyellow          = mkColor("greenyellow")
  final def grey                 = mkColor("grey")
  final def honeydew             = mkColor("honeydew")
  final def hotpink              = mkColor("hotpink")
  final def indianred            = mkColor("indianred")
  final def indigo               = mkColor("indigo")
  final def ivory                = mkColor("ivory")
  final def khaki                = mkColor("khaki")
  final def lavender             = mkColor("lavender")
  final def lavenderblush        = mkColor("lavenderblush")
  final def lawngreen            = mkColor("lawngreen")
  final def lemonchiffon         = mkColor("lemonchiffon")
  final def lightblue            = mkColor("lightblue")
  final def lightcoral           = mkColor("lightcoral")
  final def lightcyan            = mkColor("lightcyan")
  final def lightgoldenrodyellow = mkColor("lightgoldenrodyellow")
  final def lightgray            = mkColor("lightgray")
  final def lightgreen           = mkColor("lightgreen")
  final def lightgrey            = mkColor("lightgrey")
  final def lightpink            = mkColor("lightpink")
  final def lightsalmon          = mkColor("lightsalmon")
  final def lightseagreen        = mkColor("lightseagreen")
  final def lightskyblue         = mkColor("lightskyblue")
  final def lightslategray       = mkColor("lightslategray")
  final def lightslategrey       = mkColor("lightslategrey")
  final def lightsteelblue       = mkColor("lightsteelblue")
  final def lightyellow          = mkColor("lightyellow")
  final def lime                 = mkColor("lime")
  final def limegreen            = mkColor("limegreen")
  final def linen                = mkColor("linen")
  final def magenta              = mkColor("magenta")
  final def maroon               = mkColor("maroon")
  final def mediumaquamarine     = mkColor("mediumaquamarine")
  final def mediumblue           = mkColor("mediumblue")
  final def mediumorchid         = mkColor("mediumorchid")
  final def mediumpurple         = mkColor("mediumpurple")
  final def mediumseagreen       = mkColor("mediumseagreen")
  final def mediumslateblue      = mkColor("mediumslateblue")
  final def mediumspringgreen    = mkColor("mediumspringgreen")
  final def mediumturquoise      = mkColor("mediumturquoise")
  final def mediumvioletred      = mkColor("mediumvioletred")
  final def midnightblue         = mkColor("midnightblue")
  final def mintcream            = mkColor("mintcream")
  final def mistyrose            = mkColor("mistyrose")
  final def moccasin             = mkColor("moccasin")
  final def navajowhite          = mkColor("navajowhite")
  final def navy                 = mkColor("navy")
  final def oldlace              = mkColor("oldlace")
  final def olive                = mkColor("olive")
  final def olivedrab            = mkColor("olivedrab")
  final def orange               = mkColor("orange")
  final def orangered            = mkColor("orangered")
  final def orchid               = mkColor("orchid")
  final def palegoldenrod        = mkColor("palegoldenrod")
  final def palegreen            = mkColor("palegreen")
  final def paleturquoise        = mkColor("paleturquoise")
  final def palevioletred        = mkColor("palevioletred")
  final def papayawhip           = mkColor("papayawhip")
  final def peachpuff            = mkColor("peachpuff")
  final def peru                 = mkColor("peru")
  final def pink                 = mkColor("pink")
  final def plum                 = mkColor("plum")
  final def powderblue           = mkColor("powderblue")
  final def purple               = mkColor("purple")
  final def red                  = mkColor("red")
  final def rosybrown            = mkColor("rosybrown")
  final def royalblue            = mkColor("royalblue")
  final def saddlebrown          = mkColor("saddlebrown")
  final def salmon               = mkColor("salmon")
  final def sandybrown           = mkColor("sandybrown")
  final def seagreen             = mkColor("seagreen")
  final def seashell             = mkColor("seashell")
  final def sienna               = mkColor("sienna")
  final def silver               = mkColor("silver")
  final def skyblue              = mkColor("skyblue")
  final def slateblue            = mkColor("slateblue")
  final def slategray            = mkColor("slategray")
  final def slategrey            = mkColor("slategrey")
  final def snow                 = mkColor("snow")
  final def springgreen          = mkColor("springgreen")
  final def steelblue            = mkColor("steelblue")
  final def tan                  = mkColor("tan")
  final def teal                 = mkColor("teal")
  final def thistle              = mkColor("thistle")
  final def tomato               = mkColor("tomato")
  final def turquoise            = mkColor("turquoise")
  final def violet               = mkColor("violet")
  final def wheat                = mkColor("wheat")
  final def white                = mkColor("white")
  final def whitesmoke           = mkColor("whitesmoke")
  final def yellow               = mkColor("yellow")
  final def yellowgreen          = mkColor("yellowgreen")
  final def rebeccapurple        = mkColor("rebeccapurple")
}