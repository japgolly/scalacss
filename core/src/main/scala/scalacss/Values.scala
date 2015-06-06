package scalacss

import ValueT._

/** A ''typed'' literal. */
abstract class Literal(final val value: Value) {
  override def toString = s"Literal($value)"
}

/** Gets merged into [[Dsl]]. */
trait TypedLiteralAliases {
  import Literal.Typed
  final def inherit  = Typed.inherit
  final def initial  = Typed.initial
  final def unset    = Typed.unset
  final def `0`      = Typed.`0`
  final def auto     = Typed.auto
  final def hanging  = Typed.hanging
  final def eachLine = Typed.eachLine
  final def thin     = Typed.thin
  final def medium   = Typed.medium
  final def thick    = Typed.thick
  final def none     = Typed.none
  final def hidden   = Typed.hidden
  final def dotted   = Typed.dotted
  final def dashed   = Typed.dashed
  final def solid    = Typed.solid
  final def double   = Typed.double
  final def groove   = Typed.groove
  final def ridge    = Typed.ridge
  final def inset    = Typed.inset
  final def outset   = Typed.outset
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
object Literal extends TypedLiteralAliases {

  object Typed {
    final val `0` = Length(0, LengthUnit.px)

    object auto     extends Literal("auto")
    object hanging  extends Literal("hanging")
    object eachLine extends Literal("each-line")

    // any
    sealed trait Anywhere
    object inherit extends Literal("inherit") with Anywhere
    object initial extends Literal("initial") with Anywhere
    object unset   extends Literal("unset")   with Anywhere

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

  @inline def absolute           : Value = "absolute"
  @inline def active             : Value = "active"
  @inline def alias              : Value = "alias"
  @inline def all                : Value = "all"
  @inline def allPetiteCaps      : Value = "all-petite-caps"
  @inline def allScroll          : Value = "all-scroll"
  @inline def allSmallCaps       : Value = "all-small-caps"
  @inline def always             : Value = "always"
  @inline def available          : Value = "available"
  @inline def avoid              : Value = "avoid"
  @inline def avoidColumn        : Value = "avoid-column"
  @inline def avoidPage          : Value = "avoid-page"
  @inline def balance            : Value = "balance"
  @inline def baseline           : Value = "baseline"
  @inline def bidiOverride       : Value = "bidi-override"
  @inline def blink              : Value = "blink"
  @inline def block              : Value = "block"
  @inline def blockEnd           : Value = "block-end"
  @inline def blockStart         : Value = "block-start"
  @inline def bold               : Value = "bold"
  @inline def bolder             : Value = "bolder"
  @inline def borderBox          : Value = "border-box"
  @inline def both               : Value = "both"
  @inline def bottom             : Value = "bottom"
  @inline def breakAll           : Value = "break-all"
  @inline def breakWord          : Value = "break-word"
  @inline def capitalize         : Value = "capitalize"
  @inline def caption            : Value = "caption"
  @inline def cell               : Value = "cell"
  @inline def center             : Value = "center"
  @inline def clip               : Value = "clip"
  @inline def clone_             : Value = "clone"
  @inline def closeQuote         : Value = "close-quote"
  @inline def collapse           : Value = "collapse"
  @inline def colResize          : Value = "col-resize"
  @inline def column             : Value = "column"
  @inline def columnReverse      : Value = "column-reverse"
  @inline def condensed          : Value = "condensed"
  @inline def contain            : Value = "contain"
  @inline def containFloats      : Value = "contain-floats"
  @inline def content            : Value = "content"
  @inline def contentBox         : Value = "content-box"
  @inline def contents           : Value = "contents"
  @inline def contextMenu        : Value = "context-menu"
  @inline def copy               : Value = "copy"
  @inline def cover              : Value = "cover"
  @inline def crispEdges         : Value = "crisp-edges"
  @inline def crosshair          : Value = "crosshair"
  @inline def default            : Value = "default"
  @inline def disabled           : Value = "disabled"
  @inline def ellipsis           : Value = "ellipsis"
  @inline def embed              : Value = "embed"
  @inline def end                : Value = "end"
  @inline def eResize            : Value = "e-resize"
  @inline def ewResize           : Value = "ew-resize"
  @inline def expanded           : Value = "expanded"
  @inline def extraCondensed     : Value = "extra-condensed"
  @inline def extraExpanded      : Value = "extra-expanded"
  @inline def fill               : Value = "fill"
  @inline def fillAvailable      : Value = "fill-available"
  @inline def fitContent         : Value = "fit-content"
  @inline def fixed              : Value = "fixed"
  @inline def flat               : Value = "flat"
  @inline def flex               : Value = "flex"
  @inline def flexEnd            : Value = "flex-end"
  @inline def flexStart          : Value = "flex-start"
  @inline def flip               : Value = "flip"
  @inline def fromImage          : Value = "from-image"
  @inline def fullWidth          : Value = "full-width"
  @inline def grab               : Value = "grab"
  @inline def grabbing           : Value = "grabbing"
  @inline def grid               : Value = "grid"
  @inline def help               : Value = "help"
  @inline def hide               : Value = "hide"
  @inline def historicalForms    : Value = "historical-forms"
  @inline def horizontal         : Value = "horizontal"
  @inline def horizontalTB       : Value = "horizontal-tb"
  @inline def icon               : Value = "icon"
  @inline def inactive           : Value = "inactive"
  @inline def inline             : Value = "inline"
  @inline def inlineBlock        : Value = "inline-block"
  @inline def inlineEnd          : Value = "inline-end"
  @inline def inlineFlex         : Value = "inline-flex"
  @inline def inlineGrid         : Value = "inline-grid"
  @inline def inlineStart        : Value = "inline-start"
  @inline def inlineTable        : Value = "inline-table"
  @inline def inside             : Value = "inside"
  @inline def interCharacter     : Value = "inter-character"
  @inline def invert             : Value = "invert"
  @inline def isolate            : Value = "isolate"
  @inline def isolateOverride    : Value = "isolate-override"
  @inline def italic             : Value = "italic"
  @inline def justify            : Value = "justify"
  @inline def keepAll            : Value = "keep-all"
  @inline def left               : Value = "left"
  @inline def lighter            : Value = "lighter"
  @inline def lineThrough        : Value = "line-through"
  @inline def listItem           : Value = "list-item"
  @inline def loose              : Value = "loose"
  @inline def lowercase          : Value = "lowercase"
  @inline def ltr                : Value = "ltr"
  @inline def manipulation       : Value = "manipulation"
  @inline def manual             : Value = "manual"
  @inline def matchParent        : Value = "match-parent"
  @inline def maxContent         : Value = "max-content"
  @inline def menu               : Value = "menu"
  @inline def messageBox         : Value = "message-box"
  @inline def middle             : Value = "middle"
  @inline def minContent         : Value = "min-content"
  @inline def mixed              : Value = "mixed"
  @inline def move               : Value = "move"
  @inline def neResize           : Value = "ne-resize"
  @inline def neswResize         : Value = "nesw-resize"
  @inline def noCloseQuote       : Value = "no-close-quote"
  @inline def noDrop             : Value = "no-drop"
  @inline def noOpenQuote        : Value = "no-open-quote"
  @inline def normal             : Value = "normal"
  @inline def notAllowed         : Value = "not-allowed"
  @inline def nowrap             : Value = "nowrap"
  @inline def nResize            : Value = "n-resize"
  @inline def nsResize           : Value = "ns-resize"
  @inline def nwResize           : Value = "nw-resize"
  @inline def nwseResize         : Value = "nwse-resize"
  @inline def oblique            : Value = "oblique"
  @inline def openQuote          : Value = "open-quote"
  @inline def ordinal            : Value = "ordinal"
  @inline def outside            : Value = "outside"
  @inline def over               : Value = "over"
  @inline def overline           : Value = "overline"
  @inline def paddingBox         : Value = "padding-box"
  @inline def page               : Value = "page"
  @inline def panX               : Value = "pan-x"
  @inline def panY               : Value = "pan-y"
  @inline def petiteCaps         : Value = "petite-caps"
  @inline def pixelated          : Value = "pixelated"
  @inline def plaintext          : Value = "plaintext"
  @inline def pointer            : Value = "pointer"
  @inline def pre                : Value = "pre"
  @inline def preLine            : Value = "pre-line"
  @inline def preserve3D         : Value = "preserve-3d"
  @inline def preWrap            : Value = "pre-wrap"
  @inline def progress           : Value = "progress"
  @inline def relative           : Value = "relative"
  @inline def repeat             : Value = "repeat"
  @inline def right              : Value = "right"
  @inline def round              : Value = "round"
  @inline def row                : Value = "row"
  @inline def rowResize          : Value = "row-resize"
  @inline def rowReverse         : Value = "row-reverse"
  @inline def rtl                : Value = "rtl"
  @inline def ruby               : Value = "ruby"
  @inline def rubyBase           : Value = "ruby-base"
  @inline def rubyBaseContainer  : Value = "ruby-base-container"
  @inline def rubyText           : Value = "ruby-text"
  @inline def rubyTextContainer  : Value = "ruby-text-container"
  @inline def runIn              : Value = "run-in"
  @inline def scaleDown          : Value = "scale-down"
  @inline def scroll             : Value = "scroll"
  @inline def semiCondensed      : Value = "semi-condensed"
  @inline def semiExpanded       : Value = "semi-expanded"
  @inline def separate           : Value = "separate"
  @inline def seResize           : Value = "se-resize"
  @inline def show               : Value = "show"
  @inline def sideways           : Value = "sideways"
  @inline def sidewaysLeft       : Value = "sideways-left"
  @inline def sidewaysRight      : Value = "sideways-right"
  @inline def slashedZero        : Value = "slashed-zero"
  @inline def slice              : Value = "slice"
  @inline def smallCaps          : Value = "small-caps"
  @inline def smallCaption       : Value = "small-caption"
  @inline def smooth             : Value = "smooth"
  @inline def spaceAround        : Value = "space-around"
  @inline def spaceBetween       : Value = "space-between"
  @inline def sResize            : Value = "s-resize"
  @inline def start              : Value = "start"
  @inline def startEnd           : Value = "start end"
  @inline def static             : Value = "static"
  @inline def statusBar          : Value = "status-bar"
  @inline def sticky             : Value = "sticky"
  @inline def stretch            : Value = "stretch"
  @inline def strict             : Value = "strict"
  @inline def style              : Value = "style"
  @inline def sub                : Value = "sub"
  @inline def super_             : Value = "super"
  @inline def swResize           : Value = "sw-resize"
  @inline def table              : Value = "table"
  @inline def tableCell          : Value = "table-cell"
  @inline def tableColumn        : Value = "table-column"
  @inline def tableColumnGroup   : Value = "table-column-group"
  @inline def tableFooterGroup   : Value = "table-footer-group"
  @inline def tableHeaderGroup   : Value = "table-header-group"
  @inline def tableRow           : Value = "table-row"
  @inline def tableRowGroup      : Value = "table-row-group"
  @inline def text               : Value = "text"
  @inline def textBottom         : Value = "text-bottom"
  @inline def textTop            : Value = "text-top"
  @inline def titlingCaps        : Value = "titling-caps"
  @inline def top                : Value = "top"
  @inline def ultraCondensed     : Value = "ultra-condensed"
  @inline def ultraExpanded      : Value = "ultra-expanded"
  @inline def under              : Value = "under"
  @inline def underline          : Value = "underline"
  @inline def unicase            : Value = "unicase"
  @inline def uppercase          : Value = "uppercase"
  @inline def upright            : Value = "upright"
  @inline def useGlyphOrientation: Value = "use-glyph-orientation"
  @inline def vertical           : Value = "vertical"
  @inline def verticalLR         : Value = "vertical-lr"
  @inline def verticalRL         : Value = "vertical-rl"
  @inline def verticalText       : Value = "vertical-text"
  @inline def visible            : Value = "visible"
  @inline def wait_              : Value = "wait"
  @inline def wavy               : Value = "wavy"
  @inline def weight             : Value = "weight"
  @inline def wrap               : Value = "wrap"
  @inline def wrapReverse        : Value = "wrap-reverse"
  @inline def wResize            : Value = "w-resize"
  @inline def zoomIn             : Value = "zoom-in"
  @inline def zoomOut            : Value = "zoom-out"

  @inline def xxSmall            : Value = "xx-small"
  @inline def sSmall             : Value = "s-small"
  @inline def small              : Value = "small"
  @inline def large              : Value = "large"
  @inline def xLarge             : Value = "x-large"
  @inline def xxLarge            : Value = "xx-large"

  @inline def larger             : Value = "larger"
  @inline def smaller            : Value = "smaller"
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

  final def grey(amount: Int): Out =
    rgb(amount, amount, amount)

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
