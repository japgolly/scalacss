package japgolly.scalacss

import ValueT._

/** A ''typed'' literal. */
abstract class Literal(final val value: Value) {
  override def toString = s"Literal($value)"
}

/**
 * Most literals here are just strings.
 * Type-safety is usually provided by the TypedAttr methods.
 *
 * In some cases literals need to be typed to be part of a [[ValueClass]] so that they can be used as typed args.
 * Eg. `auto` in `margin(12 px, auto)`.
 *
 * For that reason exists the dichotomy between [[Literal]] with its untyped [[Value]]s, and [[Literal.Typed]] with its
 * subclassed [[Literal]]s.
 */
object Literal {

  object Typed {
    final val `0` = Length(0, LengthUnit.px)

    object auto extends Literal("auto")

    // <br-width>
    sealed trait BrWidth
    object thin   extends Literal("thin")   with BrWidth
    object medium extends Literal("medium") with BrWidth
    object thick  extends Literal("thick")  with BrWidth

    // <br-style>
    sealed trait BrStyle
    object none   extends Literal("none")   with BrStyle
    object hidden extends Literal("hidden") with BrStyle
    object dotted extends Literal("dotted") with BrStyle
    object dashed extends Literal("dashed") with BrStyle
    object solid  extends Literal("solid")  with BrStyle
    object double extends Literal("double") with BrStyle
    object groove extends Literal("groove") with BrStyle
    object ridge  extends Literal("ridge")  with BrStyle
    object inset  extends Literal("inset")  with BrStyle
    object outset extends Literal("outset") with BrStyle
  }

  /** Gets merged into [[Dsl]]. */
  trait TypedAliases {
    final def `0`    = Typed.`0`
    final def auto   = Typed.auto
    final def thin   = Typed.thin
    final def medium = Typed.medium
    final def thick  = Typed.thick
    final def none   = Typed.none
    final def hidden = Typed.hidden
    final def dotted = Typed.dotted
    final def dashed = Typed.dashed
    final def solid  = Typed.solid
    final def double = Typed.double
    final def groove = Typed.groove
    final def ridge  = Typed.ridge
    final def inset  = Typed.inset
    final def outset = Typed.outset
  }

  @inline def absolute              : Value = "absolute"
  @inline def active                : Value = "active"
  @inline def alias                 : Value = "alias"
  @inline def all                   : Value = "all"
  @inline def all_petite_caps       : Value = "all-petite-caps"
  @inline def all_scroll            : Value = "all-scroll"
  @inline def all_small_caps        : Value = "all-small-caps"
  @inline def always                : Value = "always"
  @inline def available             : Value = "available"
  @inline def avoid                 : Value = "avoid"
  @inline def avoid_column          : Value = "avoid-column"
  @inline def avoid_page            : Value = "avoid-page"
  @inline def balance               : Value = "balance"
  @inline def baseline              : Value = "baseline"
  @inline def bidi_override         : Value = "bidi-override"
  @inline def blink                 : Value = "blink"
  @inline def block                 : Value = "block"
  @inline def block_end             : Value = "block-end"
  @inline def block_start           : Value = "block-start"
  @inline def bold                  : Value = "bold"
  @inline def bolder                : Value = "bolder"
  @inline def border_box            : Value = "border-box"
  @inline def both                  : Value = "both"
  @inline def bottom                : Value = "bottom"
  @inline def break_all             : Value = "break-all"
  @inline def break_word            : Value = "break-word"
  @inline def capitalize            : Value = "capitalize"
  @inline def caption               : Value = "caption"
  @inline def cell                  : Value = "cell"
  @inline def center                : Value = "center"
  @inline def clip                  : Value = "clip"
  @inline def clone_                : Value = "clone"
  @inline def close_quote           : Value = "close-quote"
  @inline def collapse              : Value = "collapse"
  @inline def col_resize            : Value = "col-resize"
  @inline def column                : Value = "column"
  @inline def column_reverse        : Value = "column-reverse"
  @inline def condensed             : Value = "condensed"
  @inline def contain               : Value = "contain"
  @inline def contain_floats        : Value = "contain-floats"
  @inline def content               : Value = "content"
  @inline def content_box           : Value = "content-box"
  @inline def contents              : Value = "contents"
  @inline def context_menu          : Value = "context-menu"
  @inline def copy                  : Value = "copy"
  @inline def cover                 : Value = "cover"
  @inline def crisp_edges           : Value = "crisp-edges"
  @inline def crosshair             : Value = "crosshair"
  @inline def default               : Value = "default"
  @inline def disabled              : Value = "disabled"
  @inline def each_line             : Value = "each-line"
  @inline def ellipsis              : Value = "ellipsis"
  @inline def embed                 : Value = "embed"
  @inline def end                   : Value = "end"
  @inline def e_resize              : Value = "e-resize"
  @inline def ew_resize             : Value = "ew-resize"
  @inline def expanded              : Value = "expanded"
  @inline def extra_condensed       : Value = "extra-condensed"
  @inline def extra_expanded        : Value = "extra-expanded"
  @inline def fill                  : Value = "fill"
  @inline def fill_available        : Value = "fill-available"
  @inline def fit_content           : Value = "fit-content"
  @inline def fixed                 : Value = "fixed"
  @inline def flat                  : Value = "flat"
  @inline def flex                  : Value = "flex"
  @inline def flex_end              : Value = "flex-end"
  @inline def flex_start            : Value = "flex-start"
  @inline def flip                  : Value = "flip"
  @inline def from_image            : Value = "from-image"
  @inline def full_width            : Value = "full-width"
  @inline def grab                  : Value = "grab"
  @inline def grabbing              : Value = "grabbing"
  @inline def grid                  : Value = "grid"
  @inline def hanging               : Value = "hanging"
  @inline def help                  : Value = "help"
  @inline def hide                  : Value = "hide"
  @inline def historical_forms      : Value = "historical-forms"
  @inline def horizontal            : Value = "horizontal"
  @inline def horizontal_tb         : Value = "horizontal-tb"
  @inline def icon                  : Value = "icon"
  @inline def inactive              : Value = "inactive"
  @inline def inherit               : Value = "inherit"
  @inline def initial               : Value = "initial"
  @inline def inline                : Value = "inline"
  @inline def inline_block          : Value = "inline-block"
  @inline def inline_end            : Value = "inline-end"
  @inline def inline_flex           : Value = "inline-flex"
  @inline def inline_grid           : Value = "inline-grid"
  @inline def inline_start          : Value = "inline-start"
  @inline def inline_table          : Value = "inline-table"
  @inline def inside                : Value = "inside"
  @inline def inter_character       : Value = "inter-character"
  @inline def invert                : Value = "invert"
  @inline def isolate               : Value = "isolate"
  @inline def isolate_override      : Value = "isolate-override"
  @inline def italic                : Value = "italic"
  @inline def justify               : Value = "justify"
  @inline def keep_all              : Value = "keep-all"
  @inline def left                  : Value = "left"
  @inline def lighter               : Value = "lighter"
  @inline def line_through          : Value = "line-through"
  @inline def list_item             : Value = "list-item"
  @inline def loose                 : Value = "loose"
  @inline def lowercase             : Value = "lowercase"
  @inline def ltr                   : Value = "ltr"
  @inline def manipulation          : Value = "manipulation"
  @inline def manual                : Value = "manual"
  @inline def match_parent          : Value = "match-parent"
  @inline def max_content           : Value = "max-content"
  @inline def menu                  : Value = "menu"
  @inline def message_box           : Value = "message-box"
  @inline def middle                : Value = "middle"
  @inline def min_content           : Value = "min-content"
  @inline def mixed                 : Value = "mixed"
  @inline def move                  : Value = "move"
  @inline def ne_resize             : Value = "ne-resize"
  @inline def nesw_resize           : Value = "nesw-resize"
  @inline def no_close_quote        : Value = "no-close-quote"
  @inline def no_drop               : Value = "no-drop"
  @inline def no_open_quote         : Value = "no-open-quote"
  @inline def normal                : Value = "normal"
  @inline def not_allowed           : Value = "not-allowed"
  @inline def nowrap                : Value = "nowrap"
  @inline def n_resize              : Value = "n-resize"
  @inline def ns_resize             : Value = "ns-resize"
  @inline def nw_resize             : Value = "nw-resize"
  @inline def nwse_resize           : Value = "nwse-resize"
  @inline def oblique               : Value = "oblique"
  @inline def open_quote            : Value = "open-quote"
  @inline def ordinal               : Value = "ordinal"
  @inline def outside               : Value = "outside"
  @inline def over                  : Value = "over"
  @inline def overline              : Value = "overline"
  @inline def padding_box           : Value = "padding-box"
  @inline def page                  : Value = "page"
  @inline def pan_x                 : Value = "pan-x"
  @inline def pan_y                 : Value = "pan-y"
  @inline def petite_caps           : Value = "petite-caps"
  @inline def pixelated             : Value = "pixelated"
  @inline def plaintext             : Value = "plaintext"
  @inline def pointer               : Value = "pointer"
  @inline def pre                   : Value = "pre"
  @inline def pre_line              : Value = "pre-line"
  @inline def preserve_3d           : Value = "preserve-3d"
  @inline def pre_wrap              : Value = "pre-wrap"
  @inline def progress              : Value = "progress"
  @inline def relative              : Value = "relative"
  @inline def repeat                : Value = "repeat"
  @inline def right                 : Value = "right"
  @inline def round                 : Value = "round"
  @inline def row                   : Value = "row"
  @inline def row_resize            : Value = "row-resize"
  @inline def row_reverse           : Value = "row-reverse"
  @inline def rtl                   : Value = "rtl"
  @inline def ruby                  : Value = "ruby"
  @inline def ruby_base             : Value = "ruby-base"
  @inline def ruby_base_container   : Value = "ruby-base-container"
  @inline def ruby_text             : Value = "ruby-text"
  @inline def ruby_text_container   : Value = "ruby-text-container"
  @inline def run_in                : Value = "run-in"
  @inline def scale_down            : Value = "scale-down"
  @inline def scroll                : Value = "scroll"
  @inline def semi_condensed        : Value = "semi-condensed"
  @inline def semi_expanded         : Value = "semi-expanded"
  @inline def separate              : Value = "separate"
  @inline def se_resize             : Value = "se-resize"
  @inline def show                  : Value = "show"
  @inline def sideways              : Value = "sideways"
  @inline def sideways_left         : Value = "sideways-left"
  @inline def sideways_right        : Value = "sideways-right"
  @inline def slashed_zero          : Value = "slashed-zero"
  @inline def slice                 : Value = "slice"
  @inline def small_caps            : Value = "small-caps"
  @inline def small_caption         : Value = "small-caption"
  @inline def smooth                : Value = "smooth"
  @inline def space_around          : Value = "space-around"
  @inline def space_between         : Value = "space-between"
  @inline def s_resize              : Value = "s-resize"
  @inline def start                 : Value = "start"
  @inline def start_end             : Value = "start end"
  @inline def static                : Value = "static"
  @inline def status_bar            : Value = "status-bar"
  @inline def sticky                : Value = "sticky"
  @inline def stretch               : Value = "stretch"
  @inline def strict                : Value = "strict"
  @inline def style                 : Value = "style"
  @inline def sub                   : Value = "sub"
  @inline def super_                : Value = "super"
  @inline def sw_resize             : Value = "sw-resize"
  @inline def table                 : Value = "table"
  @inline def table_cell            : Value = "table-cell"
  @inline def table_column          : Value = "table-column"
  @inline def table_column_group    : Value = "table-column-group"
  @inline def table_footer_group    : Value = "table-footer-group"
  @inline def table_header_group    : Value = "table-header-group"
  @inline def table_row             : Value = "table-row"
  @inline def table_row_group       : Value = "table-row-group"
  @inline def text                  : Value = "text"
  @inline def text_bottom           : Value = "text-bottom"
  @inline def text_top              : Value = "text-top"
  @inline def titling_caps          : Value = "titling-caps"
  @inline def top                   : Value = "top"
  @inline def ultra_condensed       : Value = "ultra-condensed"
  @inline def ultra_expanded        : Value = "ultra-expanded"
  @inline def under                 : Value = "under"
  @inline def underline             : Value = "underline"
  @inline def unicase               : Value = "unicase"
  @inline def unset                 : Value = "unset"
  @inline def uppercase             : Value = "uppercase"
  @inline def upright               : Value = "upright"
  @inline def use_glyph_orientation : Value = "use-glyph-orientation"
  @inline def vertical              : Value = "vertical"
  @inline def vertical_lr           : Value = "vertical-lr"
  @inline def vertical_rl           : Value = "vertical-rl"
  @inline def vertical_text         : Value = "vertical-text"
  @inline def visible               : Value = "visible"
  @inline def wait_                 : Value = "wait"
  @inline def wavy                  : Value = "wavy"
  @inline def weight                : Value = "weight"
  @inline def wrap                  : Value = "wrap"
  @inline def wrap_reverse          : Value = "wrap-reverse"
  @inline def w_resize              : Value = "w-resize"
  @inline def zoom_in               : Value = "zoom-in"
  @inline def zoom_out              : Value = "zoom-out"

  @inline def xx_small              : Value = "xx-small"
  @inline def s_small               : Value = "s-small"
  @inline def small                 : Value = "small"
  @inline def large                 : Value = "large"
  @inline def x_large               : Value = "x-large"
  @inline def xx_large              : Value = "xx-large"

  @inline def larger                : Value = "larger"
  @inline def smaller               : Value = "smaller"
}

// =====================================================================================================================

object Color extends ColorOps[ValueT[Color]] {
  override protected def mkColor(s: String): ValueT[Color] =
    apply(s)

  // TODO A color macro would be good like #"639" or c"#dedede". It could verify that its valid hex x 3 or 6.

  @inline def apply(v: String): ValueT[ValueT.Color] =
    ValueT(v)
}

trait ColorOps[Out] {
  protected def mkColor(s: String): Out

  final def rgb(r: Int, g: Int, b: Int): Out =
    mkColor(s"rgb($r,$g,$b)")

  final def rgb(r: Percentage[_], g: Percentage[_], b: Percentage[_]): Out =
    mkColor(s"rgb(${r.value},${g.value},${b.value})")

  final def hsl(h: Int, s: Percentage[_], l: Percentage[_]): Out =
    mkColor(s"hsl($h,${s.value},${l.value})")

  final def rgba(r: Int, g: Int, b: Int, a: Double): Out =
    mkColor(s"rgba($r,$g,$b,$a)")

  final def hsla(h: Int, s: Percentage[_], l: Percentage[_], a: Double): Out =
    mkColor(s"hsla($h,${s.value},${l.value},$a)")

  /**
   * The currentColor keyword represents the calculated value of the element's color property. It allows to make the color properties inherited by properties or child's element properties that do not inherit it by default.
   *
   * It can also be used on properties that inherit the calculated value of the element's color property and will be equivalent to the inherit keyword on these elements, if any.
   */
  final def currentColor = mkColor("currentColor")

  /**
   * The transparent keyword represents a fully transparent color, i.e. the color seen will be the background color.
   * Technically, it is a black with alpha channel at its minimum value and is a shortcut for `rgba(0,0,0,0)`.
   */
  final def transparent = mkColor("transparent")

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