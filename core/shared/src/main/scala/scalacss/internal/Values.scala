package scalacss.internal

import ValueT._

/** A ''typed'' literal. */
abstract class Literal(final val value: Value) {
  override def toString = s"Literal($value)"
}

/** Gets merged into [[Dsl]]. */
trait TypedLiteralAliases {
  import Literal.Typed
  import scalacss.internal.Literal.Typed.TimingFunctionDirection

  final def `0`                                                             = Typed.`0`
  final def auto                                                            = Typed.auto
  final def contain                                                         = Typed.contain
  final def cubicBezier(x1: Double, y1: Double, x2: Double, y2: Double)     = new Typed.cubicBezier(x1, y1, x2, y2)
  final def dashed                                                          = Typed.dashed
  final def dotted                                                          = Typed.dotted
  final def double                                                          = Typed.double
  final def eachLine                                                        = Typed.eachLine
  final def ease                                                            = Typed.ease
  final def easeIn                                                          = Typed.easeIn
  final def easeInOut                                                       = Typed.easeInOut
  final def easeOut                                                         = Typed.easeOut
  final def end                                                             = Typed.end
  final def groove                                                          = Typed.groove
  final def hanging                                                         = Typed.hanging
  final def hidden                                                          = Typed.hidden
  final def inherit                                                         = Typed.inherit
  final def initial                                                         = Typed.initial
  final def inset                                                           = Typed.inset
  final def linear                                                          = Typed.linear
  final def medium                                                          = Typed.medium
  final def none                                                            = Typed.none
  final def noRepeat                                                        = Typed.noRepeat
  final def outset                                                          = Typed.outset
  final def repeat                                                          = Typed.repeat
  final def ridge                                                           = Typed.ridge
  final def round                                                           = Typed.round
  final def solid                                                           = Typed.solid
  final def space                                                           = Typed.space
  final def start                                                           = Typed.start
  final def stepEnd                                                         = Typed.stepEnd
  final def steps(steps: Int, direction: TimingFunctionDirection = end)     = new Typed.steps(steps, direction)
  final def stepStart                                                       = Typed.stepStart
  final def thick                                                           = Typed.thick
  final def thin                                                            = Typed.thin
  final def unset                                                           = Typed.unset
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

    class count(n: Int) extends Literal(n.toString)

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
    object hidden extends Literal("hidden") with BrStyle
    object dotted extends Literal("dotted") with BrStyle
    object dashed extends Literal("dashed") with BrStyle
    object solid  extends Literal("solid")  with BrStyle
    object double extends Literal("double") with BrStyle
    object groove extends Literal("groove") with BrStyle
    object ridge  extends Literal("ridge")  with BrStyle
    object inset  extends Literal("inset")  with BrStyle
    object outset extends Literal("outset") with BrStyle

    // <overflow-behaviour>
    sealed trait OverflowBehaviour { def value: Value }
    object auto    extends Literal("auto")    with OverflowBehaviour
    object contain extends Literal("contain") with OverflowBehaviour

    // <repeat-style>
    sealed trait RepeatStyle { def value: Value }
    object repeat   extends Literal("repeat")    with RepeatStyle
    object space    extends Literal("space")     with RepeatStyle
    object round    extends Literal("round")     with RepeatStyle
    object noRepeat extends Literal("no-repeat") with RepeatStyle

    // <timing-function>
    sealed trait TimingFunctionDirection { def value: Value }
    object start  extends Literal("start") with TimingFunctionDirection
    object end    extends Literal("end")   with TimingFunctionDirection

    sealed trait TimingFunction
    class cubicBezier(x1: Double, y1: Double, x2: Double, y2: Double) extends Literal(s"cubic-bezier($x1, $y1, $x2, $y2)") with TimingFunction
    class steps(steps: Int, direction: TimingFunctionDirection)       extends Literal(s"steps($steps, ${direction.value})") with TimingFunction
    object linear     extends Literal("linear")      with TimingFunction
    object ease       extends Literal("ease")        with TimingFunction
    object easeIn     extends Literal("ease-in")     with TimingFunction
    object easeInOut  extends Literal("ease-in-out") with TimingFunction
    object easeOut    extends Literal("ease-out")    with TimingFunction
    object stepStart  extends Literal("step-start")  with TimingFunction
    object stepEnd    extends Literal("step-end")    with TimingFunction

    // gap
    sealed trait GapStyle
    object normal extends Literal("normal") with GapStyle

    // Multi-purpose
    object none extends Literal("none") with BrStyle with OverflowBehaviour
  }

  def absolute           : Value = "absolute"
  def active             : Value = "active"
  def alias              : Value = "alias"
  def all                : Value = "all"
  def allPetiteCaps      : Value = "all-petite-caps"
  def allScroll          : Value = "all-scroll"
  def allSmallCaps       : Value = "all-small-caps"
  def alternate          : Value = "alternate"
  def alternateReverse   : Value = "alternate-reverse"
  def always             : Value = "always"
  def available          : Value = "available"
  def avoid              : Value = "avoid"
  def avoidColumn        : Value = "avoid-column"
  def avoidPage          : Value = "avoid-page"
  def backwards          : Value = "backwards"
  def balance            : Value = "balance"
  def baseline           : Value = "baseline"
  def bidiOverride       : Value = "bidi-override"
  def blink              : Value = "blink"
  def block              : Value = "block"
  def blockEnd           : Value = "block-end"
  def blockStart         : Value = "block-start"
  def bold               : Value = "bold"
  def bolder             : Value = "bolder"
  def borderBox          : Value = "border-box"
  def both               : Value = "both"
  def bottom             : Value = "bottom"
  def breakAll           : Value = "break-all"
  def breakWord          : Value = "break-word"
  def capitalize         : Value = "capitalize"
  def caption            : Value = "caption"
  def cell               : Value = "cell"
  def center             : Value = "center"
  def clip               : Value = "clip"
  def clone_             : Value = "clone"
  def closeQuote         : Value = "close-quote"
  def collapse           : Value = "collapse"
  def colResize          : Value = "col-resize"
  def column             : Value = "column"
  def columnReverse      : Value = "column-reverse"
  def condensed          : Value = "condensed"
  def containFloats      : Value = "contain-floats"
  def content            : Value = "content"
  def contentBox         : Value = "content-box"
  def contents           : Value = "contents"
  def contextMenu        : Value = "context-menu"
  def copy               : Value = "copy"
  def cover              : Value = "cover"
  def crispEdges         : Value = "crisp-edges"
  def crosshair          : Value = "crosshair"
  def default            : Value = "default"
  def disabled           : Value = "disabled"
  def ellipsis           : Value = "ellipsis"
  def embed              : Value = "embed"
  def eResize            : Value = "e-resize"
  def ewResize           : Value = "ew-resize"
  def expanded           : Value = "expanded"
  def extraCondensed     : Value = "extra-condensed"
  def extraExpanded      : Value = "extra-expanded"
  def fill               : Value = "fill"
  def fillAvailable      : Value = "fill-available"
  def firstBaseline      : Value = "first baseline"
  def fitContent         : Value = "fit-content"
  def fixed              : Value = "fixed"
  def flat               : Value = "flat"
  def flex               : Value = "flex"
  def flexEnd            : Value = "flex-end"
  def flexStart          : Value = "flex-start"
  def flip               : Value = "flip"
  def flowRoot           : Value = "flow-root"
  def forwards           : Value = "forwards"
  def fromImage          : Value = "from-image"
  def fullWidth          : Value = "full-width"
  def grab               : Value = "grab"
  def grabbing           : Value = "grabbing"
  def grid               : Value = "grid"
  def help               : Value = "help"
  def hide               : Value = "hide"
  def historicalForms    : Value = "historical-forms"
  def horizontal         : Value = "horizontal"
  def horizontalTB       : Value = "horizontal-tb"
  def icon               : Value = "icon"
  def inactive           : Value = "inactive"
  def infinite           : Value = "infinite"
  def inline             : Value = "inline"
  def inlineBlock        : Value = "inline-block"
  def inlineEnd          : Value = "inline-end"
  def inlineFlex         : Value = "inline-flex"
  def inlineGrid         : Value = "inline-grid"
  def inlineStart        : Value = "inline-start"
  def inlineTable        : Value = "inline-table"
  def inside             : Value = "inside"
  def interCharacter     : Value = "inter-character"
  def invert             : Value = "invert"
  def isolate            : Value = "isolate"
  def isolateOverride    : Value = "isolate-override"
  def italic             : Value = "italic"
  def justify            : Value = "justify"
  def keepAll            : Value = "keep-all"
  def lastBaseline       : Value = "last baseline"
  def left               : Value = "left"
  def lighter            : Value = "lighter"
  def lineThrough        : Value = "line-through"
  def listItem           : Value = "list-item"
  def loose              : Value = "loose"
  def lowercase          : Value = "lowercase"
  def ltr                : Value = "ltr"
  def manipulation       : Value = "manipulation"
  def manual             : Value = "manual"
  def matchParent        : Value = "match-parent"
  def maxContent         : Value = "max-content"
  def menu               : Value = "menu"
  def messageBox         : Value = "message-box"
  def middle             : Value = "middle"
  def minContent         : Value = "min-content"
  def mixed              : Value = "mixed"
  def move               : Value = "move"
  def neResize           : Value = "ne-resize"
  def neswResize         : Value = "nesw-resize"
  def noCloseQuote       : Value = "no-close-quote"
  def noDrop             : Value = "no-drop"
  def noOpenQuote        : Value = "no-open-quote"
  def notAllowed         : Value = "not-allowed"
  def nowrap             : Value = "nowrap"
  def nResize            : Value = "n-resize"
  def nsResize           : Value = "ns-resize"
  def nwResize           : Value = "nw-resize"
  def nwseResize         : Value = "nwse-resize"
  def oblique            : Value = "oblique"
  def openQuote          : Value = "open-quote"
  def ordinal            : Value = "ordinal"
  def outside            : Value = "outside"
  def over               : Value = "over"
  def overline           : Value = "overline"
  def paddingBox         : Value = "padding-box"
  def page               : Value = "page"
  def panX               : Value = "pan-x"
  def panY               : Value = "pan-y"
  def paused             : Value = "paused"
  def petiteCaps         : Value = "petite-caps"
  def pixelated          : Value = "pixelated"
  def plaintext          : Value = "plaintext"
  def pointer            : Value = "pointer"
  def pre                : Value = "pre"
  def preLine            : Value = "pre-line"
  def preserve3D         : Value = "preserve-3d"
  def preWrap            : Value = "pre-wrap"
  def progress           : Value = "progress"
  def relative           : Value = "relative"
  def reverse            : Value = "reverse"
  def right              : Value = "right"
  def row                : Value = "row"
  def rowResize          : Value = "row-resize"
  def rowReverse         : Value = "row-reverse"
  def rtl                : Value = "rtl"
  def ruby               : Value = "ruby"
  def rubyBase           : Value = "ruby-base"
  def rubyBaseContainer  : Value = "ruby-base-container"
  def rubyText           : Value = "ruby-text"
  def rubyTextContainer  : Value = "ruby-text-container"
  def runIn              : Value = "run-in"
  def running            : Value = "running"
  def safeCenter         : Value = "safe center"
  def scaleDown          : Value = "scale-down"
  def scroll             : Value = "scroll"
  def semiCondensed      : Value = "semi-condensed"
  def semiExpanded       : Value = "semi-expanded"
  def separate           : Value = "separate"
  def seResize           : Value = "se-resize"
  def show               : Value = "show"
  def sideways           : Value = "sideways"
  def sidewaysLeft       : Value = "sideways-left"
  def sidewaysRight      : Value = "sideways-right"
  def slashedZero        : Value = "slashed-zero"
  def slice              : Value = "slice"
  def smallCaps          : Value = "small-caps"
  def smallCaption       : Value = "small-caption"
  def smooth             : Value = "smooth"
  def spaceAround        : Value = "space-around"
  def spaceBetween       : Value = "space-between"
  def spaceEvenly        : Value = "space-evenly"
  def sResize            : Value = "s-resize"
  def startEnd           : Value = "start end"
  def static             : Value = "static"
  def statusBar          : Value = "status-bar"
  def sticky             : Value = "sticky"
  def stretch            : Value = "stretch"
  def strict             : Value = "strict"
  def style              : Value = "style"
  def sub                : Value = "sub"
  def super_             : Value = "super"
  def swResize           : Value = "sw-resize"
  def systemUI           : Value = "system-ui"
  def table              : Value = "table"
  def tableCell          : Value = "table-cell"
  def tableColumn        : Value = "table-column"
  def tableColumnGroup   : Value = "table-column-group"
  def tableFooterGroup   : Value = "table-footer-group"
  def tableHeaderGroup   : Value = "table-header-group"
  def tableRow           : Value = "table-row"
  def tableRowGroup      : Value = "table-row-group"
  def text               : Value = "text"
  def textBottom         : Value = "text-bottom"
  def textTop            : Value = "text-top"
  def titlingCaps        : Value = "titling-caps"
  def top                : Value = "top"
  def ultraCondensed     : Value = "ultra-condensed"
  def ultraExpanded      : Value = "ultra-expanded"
  def under              : Value = "under"
  def underline          : Value = "underline"
  def unicase            : Value = "unicase"
  def unsafeCenter       : Value = "unsafe center"
  def uppercase          : Value = "uppercase"
  def upright            : Value = "upright"
  def useGlyphOrientation: Value = "use-glyph-orientation"
  def vertical           : Value = "vertical"
  def verticalLR         : Value = "vertical-lr"
  def verticalRL         : Value = "vertical-rl"
  def verticalText       : Value = "vertical-text"
  def visible            : Value = "visible"
  def wait_              : Value = "wait"
  def wavy               : Value = "wavy"
  def weight             : Value = "weight"
  def wrap               : Value = "wrap"
  def wrapReverse        : Value = "wrap-reverse"
  def wResize            : Value = "w-resize"
  def zoomIn             : Value = "zoom-in"
  def zoomOut            : Value = "zoom-out"

  def xxSmall            : Value = "xx-small"
  def sSmall             : Value = "s-small"
  def small              : Value = "small"
  def large              : Value = "large"
  def xLarge             : Value = "x-large"
  def xxLarge            : Value = "xx-large"

  def larger             : Value = "larger"
  def smaller            : Value = "smaller"
}

// =====================================================================================================================

object Color extends ColorOps[ValueT[Color]] {
  override protected def mkColor(s: String): ValueT[Color] =
    apply(s)

  def apply(v: String): ValueT[Color] =
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
