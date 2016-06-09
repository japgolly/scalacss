import japgolly.univeq._

package object scalacss {

  /**
   * A CSS value, like `"none"`, `"solid 3px black"`.
   */
  type Value = String

  private[scalacss] final val _important = " !important"

  /** Defines the range of unicode characters the font face supports. */
  case class UnicodeRange(from: Int, to: Int) {
    override def toString = "U+%x-%x".format(from, to)
  }
  object UnicodeRange {
    implicit def univEq: UnivEq[UnicodeRange] = UnivEq.derive
  }

  final case class ClassName(value: String)
  object ClassName {
    implicit def univEq: UnivEq[ClassName] = UnivEq.derive
  }

  /**
   * Describes the context of a number of CSS attribute-value pairs.
   *
   * Examples: `"div"`, `".debug"`, `"h3.bottom"`, `"a:visited"`.
   */
  type CssSelector = String

  /**
   * A CSS attribute and its corresponding value.
   *
   * Example: `CssKV("margin-bottom", "12px")`
   */
  final case class CssKV(key: String, value: String)
  object CssKV {
    implicit def univEq: UnivEq[CssKV] = UnivEq.derive

    final class Lens(val get: CssKV => String, val set: (CssKV, String) => CssKV)
    val key   = new Lens(_.key  , (a, b) => CssKV(b, a.value))
    val value = new Lens(_.value, (a, b) => CssKV(a.key, b))
  }

  /**
   * A media query in CSS.
   *
   * Examples: `"@media screen and (device-aspect-ratio: 16/9)"`.
   */
  type CssMediaQuery  = String
  type CssMediaQueryO = Option[CssMediaQuery]

  /**
   * Keyframe animations in CSS.
   *
   * This is the name of a `@keyframes` group.
   */
  type KeyframeAnimationName = ClassName
  type KeyframeSelector      = Percentage[Int]

  sealed trait CssEntry

  case class CssStyleEntry(mq     : CssMediaQueryO,
                           sel    : CssSelector,
                           content: NonEmptyVector[CssKV]) extends CssEntry

  case class CssKeyframesEntry(name  : KeyframeAnimationName,
                               frames: Map[KeyframeSelector, StyleStream]) extends CssEntry

  case class CssFontFace(fontFamily  : String,
                         src         : NonEmptyVector[String],
                         fontStretch : Option[Value],
                         fontStyle   : Option[Value],
                         fontWeight  : Option[Value],
                         unicodeRange: Option[UnicodeRange]) extends CssEntry

  implicit def univEqCssStyleEntry    : UnivEq[CssStyleEntry    ] = UnivEq.derive
  implicit def univEqCssKeyframesEntry: UnivEq[CssKeyframesEntry] = UnivEq.derive
  implicit def univEqCssFontFace      : UnivEq[CssFontFace      ] = UnivEq.derive
  implicit def univEqCssEntry         : UnivEq[CssEntry         ] = UnivEq.derive

  type StyleStream    = Stream[CssStyleEntry]
  type KeyframeStream = Stream[CssKeyframesEntry]
  type FontFaceStream = Stream[CssFontFace]

  /**
   * A stylesheet in its entirety. Normally turned into a `.css` file or a `&lt;style&gt;` tag.
   */
  type Css = Stream[CssEntry]
  implicit def univEqCss: UnivEq[Css] = UnivEq.univEqStream

  type WarningMsg = String
  final case class Warning(cond: Cond, msg: WarningMsg)

  /**
   * Applicable style.
   *
   * A style that needs no more processing and can be applied to some target.
   *
   * @param addClassNames Additional class names that the style has requested be appended.
   *                      Allows ScalaCSS styles to use classname-based CSS libraries like Bootstrap.
   */
  final case class StyleA(className: ClassName, addClassNames: Vector[ClassName], style: StyleS) {
    def classNameIterator: Iterator[ClassName] =
      Iterator.single(className) ++ addClassNames

    /** Value to be applied to a HTML element's `class` attribute. */
    val htmlClass: String =
      classNameIterator.map(_.value).mkString(" ")

    def +(s : StyleA)(implicit c: Compose): StyleA =
      StyleA(className, s.className +: (addClassNames ++ s.addClassNames), style.compose(s.style))
  }

  /** Faster than Vector(a) */
  private[scalacss] def Vector1[A](a: A): Vector[A] =
    Vector.empty :+ a

  /**
   * Text to include in generated class names.
   *
   * [[mutable.Register.NameGen]]s can choose to include it in the output class names, or ignore it.
   */
  final case class ClassNameHint(value: String)

  /**
    * Animation keyframes.
    *
    * @param name Name of animation.
    * @param frames Frame definitions.
    */
  final case class Keyframes(name: KeyframeAnimationName, frames: Seq[(KeyframeSelector, StyleA)])

  /**
    * Font face declaration
    *
    * http://www.w3schools.com/cssref/css3_pr_font-face_rule.asp
    */
  final case class FontFace(fontFamily         : String,
                            src                : NonEmptyVector[String],
                            fontStretchValue   : Option[Value] = None,
                            fontStyleValue     : Option[Value] = None,
                            fontWeightValue    : Option[Value] = None,
                            unicodeRangeValue  : Option[UnicodeRange] = None) {
    import FontFace._
    def fontStretch                      = new FontStretchBuilder(v => copy(fontStretchValue  = Some(v)))
    def fontStyle                        = new FontStyleBuilder  (v => copy(fontStyleValue    = Some(v)))
    def fontWeight                       = new FontWeightBuilder (v => copy(fontWeightValue   = Some(v)))
    def unicodeRange(from: Int, to: Int) = copy(unicodeRangeValue = Some(UnicodeRange(from, to)))
  }

  object FontFace {
    final class FontSrcSelector(private val fontFamily: String) extends AnyVal {
      def src(src: String, additionalSrc: String*): FontFace =
        FontFace(fontFamily, NonEmptyVector(src, additionalSrc.toVector))
    }

    final class FontStretchBuilder(private val b: Value => FontFace) extends AnyVal {
      def condensed      = b(Literal.condensed)
      def expanded       = b(Literal.expanded)
      def extraCondensed = b(Literal.extraCondensed)
      def extraExpanded  = b(Literal.extraExpanded)
      def normal         = b(Literal.normal)
      def semiCondensed  = b(Literal.semiCondensed)
      def semiExpanded   = b(Literal.semiExpanded)
      def ultraCondensed = b(Literal.ultraCondensed)
      def ultraExpanded  = b(Literal.ultraExpanded)
    }

    final class FontStyleBuilder(private val b: Value => FontFace) extends AnyVal {
      def italic  = b(Literal.italic)
      def normal  = b(Literal.normal)
      def oblique = b(Literal.oblique)
    }

    final class FontWeightBuilder(private val b: Value => FontFace) extends AnyVal {
      def _100    = b("100")
      def _200    = b("200")
      def _300    = b("300")
      def _400    = b("400")
      def _500    = b("500")
      def _600    = b("600")
      def _700    = b("700")
      def _800    = b("800")
      def _900    = b("900")
      def bold    = b(Literal.bold)
      def bolder  = b(Literal.bolder)
      def lighter = b(Literal.lighter)
      def normal  = b(Literal.normal)
    }
  }

  // TODO Move all of this to .internal

  def Need[A](a: => A) = new Need(() => a)
  final class Need[+A](thunk: () => A) {
    private[this] var _thunk = thunk
    lazy val value: A = {
      val t = _thunk
      _thunk = null
      t()
    }
  }

  def optionAppend[A](oa: Option[A], ob: Option[A])(f: (A, A) => A): Option[A] =
    (oa, ob) match {
      case (None   , None   ) => None
      case (Some(a), None   ) => Some(a)
      case (None   , Some(b)) => Some(b)
      case (Some(a), Some(b)) => Some(f(a, b))
    }

  def memo[A: UnivEq, B](f: A => B): A => B = {
    val m = scala.collection.mutable.HashMap.empty[A, B]
    a => m.get(a).getOrElse {
      val b = f(a)
      m.update(a, b)
      b
    }
  }
}
