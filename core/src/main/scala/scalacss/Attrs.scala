package scalacss

import scalaz.Need
import scalacss.{Literal => L}
import L.{Typed => LT}
import ValueT._

object Attrs {

  private[scalacss] lazy val valuesForAllAttr = NonEmptyVector[Attr](
    alignContent, alignItems, alignSelf, animation, animationDelay, animationDirection, animationDuration,
    animationFillMode, animationIterationCount, animationName, animationPlayState, animationTimingFunction,
    backfaceVisibility, background, backgroundAttachment, backgroundBlendMode, backgroundClip, backgroundColor,
    backgroundImage, backgroundOrigin, backgroundPosition, backgroundRepeat, backgroundSize, blockSize, border,
    borderBlockEnd, borderBlockEndColor, borderBlockEndStyle, borderBlockEndWidth, borderBlockStart,
    borderBlockStartColor, borderBlockStartStyle, borderBlockStartWidth, borderBottom, borderBottomColor,
    borderBottomLeftRadius, borderBottomRightRadius, borderBottomStyle, borderBottomWidth, borderCollapse, borderColor,
    borderImage, borderImageOutset, borderImageRepeat, borderImageSlice, borderImageSource, borderImageWidth,
    borderInlineEnd, borderInlineEndColor, borderInlineEndStyle, borderInlineEndWidth, borderInlineStart,
    borderInlineStartColor, borderInlineStartStyle, borderInlineStartWidth, borderLeft, borderLeftColor,
    borderLeftStyle, borderLeftWidth, borderRadius, borderRight, borderRightColor, borderRightStyle, borderRightWidth,
    borderSpacing, borderStyle, borderTop, borderTopColor, borderTopLeftRadius, borderTopRightRadius, borderTopStyle,
    borderTopWidth, borderWidth, bottom, boxDecorationBreak, boxShadow, boxSizing, breakAfter, breakBefore, breakInside,
    captionSide, clear, clip, clipPath, color, columnCount, columnFill, columnGap, columnRule, columnRuleColor,
    columnRuleStyle, columnRuleWidth, columns, columnSpan, columnWidth, content, counterIncrement, counterReset, cursor,
    display, emptyCells, filter, flex, flexBasis, flexDirection, flexFlow, flexGrow, flexShrink, flexWrap, float, font,
    fontFamily, fontFeatureSettings, fontKerning, fontLanguageOverride, fontSize, fontSizeAdjust, fontStretch,
    fontStyle, fontSynthesis, fontVariant, fontVariantAlternates, fontVariantCaps, fontVariantEastAsian,
    fontVariantLigatures, fontVariantNumeric, fontVariantPosition, fontWeight, grid, gridArea, gridAutoColumns,
    gridAutoFlow, gridAutoPosition, gridAutoRows, gridColumn, gridColumnEnd, gridColumnStart, gridRow, gridRowEnd,
    gridRowStart, gridTemplate, gridTemplateAreas, gridTemplateColumns, gridTemplateRows, height, hyphens,
    imageOrientation, imageRendering, imageResolution, imeMode, inlineSize, isolation, justifyContent, left,
    letterSpacing, lineBreak, lineHeight, listStyle, listStyleImage, listStylePosition, listStyleType, margin,
    marginBlockEnd, marginBlockStart, marginBottom, marginInlineEnd, marginInlineStart, marginLeft, marginRight,
    marginTop, marks, mask, maskType, maxBlockSize, maxHeight, maxInlineSize, maxWidth, minBlockSize, minHeight,
    minInlineSize, minWidth, mixBlendMode, objectFit, objectPosition, offsetBlockEnd, offsetBlockStart, offsetInlineEnd,
    offsetInlineStart, opacity, order, orphans, outline, outlineColor, outlineOffset, outlineStyle, outlineWidth,
    overflow, overflowWrap, overflowX, overflowY, padding, paddingBlockEnd, paddingBlockStart, paddingBottom,
    paddingInlineEnd, paddingInlineStart, paddingLeft, paddingRight, paddingTop, pageBreakAfter, pageBreakBefore,
    pageBreakInside, perspective, perspectiveOrigin, pointerEvents, position, quotes, resize, right, rubyAlign,
    rubyMerge, rubyPosition, scrollBehavior, shapeImageThreshold, shapeMargin, shapeOutside, tableLayout, tabSize,
    textAlign, textAlignLast, textCombineUpright, textDecoration, textDecorationColor, textDecorationLine,
    textDecorationStyle, textIndent, textOrientation, textOverflow, textRendering, textShadow, textTransform,
    textUnderlinePosition, top, touchAction, transform, transformOrigin, transformStyle, transition, transitionDelay,
    transitionDuration, transitionProperty, transitionTimingFunction, unicodeRange, verticalAlign, visibility,
    whiteSpace, widows, width, willChange, wordBreak, wordSpacing, wordWrap, writingMode, zIndex,
    boxReflect, flowFrom, flowInto, regionFragment, textSizeAdjust, textStroke, textStrokeColor, textStrokeWidth,
    textEmphasis, textEmphasisColor, textEmphasisPosition, textEmphasisStyle, userSelect)

  lazy val values: NonEmptyVector[Attr] =
    Vector[Attr](all, unicodeBidi, direction) ++: valuesForAllAttr

  /**
   * The CSS align-content property aligns a flex container's lines within the flex container when there is extra space on the cross-axis. This property has no effect on single line flexible boxes.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/align-content">MDN</a>
   */
  object alignContent extends TypedAttrBase {
    override val attr = Attr.real("align-content", Transform keys CanIUse.flexbox)
    @inline def center       = av(L.center)
    @inline def flexEnd      = av(L.flexEnd)
    @inline def flexStart    = av(L.flexStart)
    @inline def spaceAround  = av(L.spaceAround)
    @inline def spaceBetween = av(L.spaceBetween)
    @inline def stretch      = av(L.stretch)
  }

  /**
   * The CSS align-items property aligns flex items of the current flex line the same way as justify-content but in the perpendicular direction.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/align-items">MDN</a>
   */
  object alignItems extends TypedAttrBase {
    override val attr = Attr.real("align-items", Transform keys CanIUse.flexbox)
    @inline def baseline  = av(L.baseline)
    @inline def center    = av(L.center)
    @inline def flexEnd   = av(L.flexEnd)
    @inline def flexStart = av(L.flexStart)
    @inline def stretch   = av(L.stretch)
  }

  /**
   * The align-self CSS property aligns flex items of the current flex line overriding the align-items value. If any of the flex item's cross-axis margin is set to auto, then align-self is ignored.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/align-self">MDN</a>
   */
  object alignSelf extends TypedAttrBase {
    override val attr = Attr.real("align-self", Transform keys CanIUse.flexbox)
    @inline def auto      = avl(LT.auto)
    @inline def baseline  = av(L.baseline)
    @inline def center    = av(L.center)
    @inline def flexEnd   = av(L.flexEnd)
    @inline def flexStart = av(L.flexStart)
    @inline def stretch   = av(L.stretch)
  }

  /**
   * The animation-delay CSS property specifies when the animation should start. This lets the animation sequence begin some time after it's applied to an element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/animation-delay">MDN</a>
   */
  object animationDelay extends TypedAttrTN[Time](",") {
    override val attr = Attr.real("animation-delay", Transform keys CanIUse.animation)
  }

  /**
   * The animation-direction CSS property indicates whether the animation should play in reverse on alternate cycles.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/animation-direction">MDN</a>
   */
  object animationDirection extends TypedAttrBase {
    override val attr: Attr = Attr.real("animation-direction", Transform keys CanIUse.animation)
    @inline def normal            = av(L.normal)
    @inline def reverse           = av(L.reverse)
    @inline def alternate         = av(L.alternate)
    @inline def alternateReverse  = av(L.alternateReverse)
  }

  /**
   * The animation-duration CSS property specifies the length of time that an animation should take to complete one cycle.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/animation-duration">MDN</a>
   */
  object animationDuration extends TypedAttrTN[Time](",") {
    override val attr = Attr.real("animation-duration", Transform keys CanIUse.animation)
  }

  /**
   * The animation-fill-mode CSS property specifies how a CSS animation should apply styles to its target before and after it is executing.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/animation-fill-mode">MDN</a>
   */
  object animationFillMode extends TypedAttrBase {
    override val attr: Attr = Attr.real("animation-fill-mode", Transform keys CanIUse.animation)
    @inline def forwards  = av(L.forwards)
    @inline def backwards = av(L.backwards)
    @inline def both      = av(L.both)
  }

  /**
   * The animation-iteration-count CSS property defines the number of times an animation cycle should be played before stopping.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/animation-iteration-count">MDN</a>
   */
  object animationIterationCount extends TypedAttrBase {
    override val attr: Attr = Attr.real("animation-iteration-count", Transform keys CanIUse.animation)
    @inline def infinite       = av(L.infinite)
    @inline def count(n: Int)  = avl(new LT.count(n))
  }

  /**
   * The animation-name CSS property specifies a list of animations that should be applied to the selected element. Each name indicates a @keyframes at-rule that defines the property values for the animation sequence.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/animation-name">MDN</a>
   */
  object animationName extends TypedAttrBase {
    override val attr = Attr.real("animation-name", Transform keys CanIUse.animation)
    def apply(a: Keyframes): AV = av(a.name.value)
  }

  /**
   * The animation-play-state CSS property determines whether an animation is running or paused. You can query this property's value to determine whether or not the animation is currently running; in addition, you can set its value to pause and resume playback of an animation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/animation-play-state">MDN</a>
   */
  object animationPlayState extends TypedAttrBase {
    override val attr: Attr = Attr.real("animation-play-state", Transform keys CanIUse.animation)
    @inline def running = av(L.running)
    @inline def paused  = av(L.paused)
  }

  /**
   * The CSS animation-timing-function property specifies how a CSS animation should progress over the duration of each cycle. The possible values are one or several &lt;timing-function>.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/animation-timing-function">MDN</a>
   */
  object animationTimingFunction extends TypedAttrBase {
    override val attr: Attr = Attr.real("animation-timing-function", Transform keys CanIUse.animation)
    @inline def cubicBezier(x1: Double, y1: Double, x2: Double, y2: Double) = avl(new LT.cubicBezier(x1, y1, x2, y2))
    @inline def steps(steps: Int, direction: LT.TimingFunctionDirection)    = avl(new LT.steps(steps, direction))
    @inline def linear                                                      = avl(LT.linear)
    @inline def ease                                                        = avl(LT.ease)
    @inline def easeIn                                                      = avl(LT.easeIn)
    @inline def easeInOut                                                   = avl(LT.easeInOut)
    @inline def easeOut                                                     = avl(LT.easeOut)
    @inline def stepStart                                                   = avl(LT.stepStart)
    @inline def stepEnd                                                     = avl(LT.stepEnd)
  }

  /**
   * The CSS backface-visibility property determines whether or not the back face of the element is visible when facing the user. The back face of an element always is a transparent background, letting, when visible, a mirror image of the front face be displayed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/backface-visibility">MDN</a>
   */
  object backfaceVisibility extends TypedAttrBase {
    override val attr = Attr.real("backface-visibility", Transform keys CanIUse.transforms)
    @inline def hidden  = avl(LT.hidden)
    @inline def visible = av(L.visible)
  }

  /**
   * If a background-image is specified, the background-attachment CSS property determines whether that image's position is fixed within the viewport, or scrolls along with its containing block.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background-attachment">MDN</a>
   */
  final val backgroundAttachment = Attr.real("background-attachment", Transform keys CanIUse.backgroundAttachment)

  /**
   * The background-blend-mode CSS property describes how the element's background images should blend with each other and the element's background color.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background-blend-mode">MDN</a>
   */
  final val backgroundBlendMode = Attr.real("background-blend-mode")

  /**
   * Technical review completed. Editorial review completed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background-clip">MDN</a>
   */
  final val backgroundClip = Attr.real("background-clip")

  /**
   * The background-color CSS property sets the background color of an element, either through a color value or the keyword transparent.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background-color">MDN</a>
   */
  object backgroundColor extends TypedAttr_Color {
    override val attr = Attr.real("background-color")
  }

  /**
   * The CSS background-image property sets one or several background images for an element. The images are drawn on stacking context layers on top of each other. The first layer specified is drawn as if it is closest to the user. The borders of the element are then drawn on top of them, and the background-color is drawn beneath them.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background-image">MDN</a>
   */
  final val backgroundImage = Attr.real("background-image", CanIUse2.backgroundImageTransforms)

  /**
   * The background-origin CSS property determines the background positioning area, that is the position of the origin of an image specified using the background-image CSS property.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background-origin">MDN</a>
   */
  final val backgroundOrigin = Attr.real("background-origin")

  /**
   * The background-position CSS property sets the initial position, relative to the background position layer defined by background-origin for each defined background image.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background-position">MDN</a>
   */
  final val backgroundPosition = Attr.real("background-position")

  /**
   * The background-repeat CSS property defines how background images are repeated. A background image can be repeated along the horizontal axis, the vertical axis, both axes, or not repeated at all.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background-repeat">MDN</a>
   */
  final val backgroundRepeat = Attr.real("background-repeat")

  /**
   * The background-size CSS property specifies the size of the background images. The size of the image can be fully constrained or only partially in order to preserve its intrinsic ratio.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background-size">MDN</a>
   */
  final val backgroundSize = Attr.real("background-size")

  /**
   * The border-bottom-color CSS property sets the color of the bottom border of an element. Note that in many cases the shorthand CSS properties border-color or border-bottom are more convenient and preferable.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-bottom-color">MDN</a>
   */
  object borderBottomColor extends TypedAttr_Color {
    override val attr = Attr.real("border-bottom-color")
  }

  /**
   * The border-bottom-left-radius CSS property sets the rounding of the bottom-left corner of the element. The rounding can be a circle or an ellipse, or if one of the value is 0 no rounding is done and the corner is square.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-bottom-left-radius">MDN</a>
   */
  object borderBottomLeftRadius extends TypedAttrT2[LenPct] with ZeroLit {
    override val attr = Attr.real("border-bottom-left-radius", Transform keys CanIUse.borderRadius)
  }

  /**
   * The border-bottom-right-radius CSS property sets the rounding of the bottom-right corner of the element. The rounding can be a circle or an ellipse, or if one of the value is 0 no rounding is done and the corner is square.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-bottom-right-radius">MDN</a>
   */
  object borderBottomRightRadius extends TypedAttrT2[LenPct] with ZeroLit {
    override val attr = Attr.real("border-bottom-right-radius", Transform keys CanIUse.borderRadius)
  }

  /**
   * The border-bottom-style CSS property sets the line style of the bottom border of a box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-bottom-style">MDN</a>
   */
  object borderBottomStyle extends TypedAttr_BrStyle {
    override val attr = Attr.real("border-bottom-style")
  }

  /**
   * The border-bottom-width CSS property sets the width of the bottom border of a box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-bottom-width">MDN</a>
   */
  object borderBottomWidth extends TypedAttr_BrWidth {
    override val attr = Attr.real("border-bottom-width")
  }

  /**
   * The border-collapse CSS property selects a table's border model. This has a big influence on the look and style of the table cells.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-collapse">MDN</a>
   */
  object borderCollapse extends TypedAttrBase {
    override val attr = Attr.real("border-collapse")
    @inline def collapse = av(L.collapse)
    @inline def separate = av(L.separate)
  }

  /**
   * The border-image-outset property describes by what amount the border image area extends beyond the border box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-outset">MDN</a>
   */
  final val borderImageOutset = Attr.real("border-image-outset", Transform keys CanIUse.borderImage)

  /**
   * The border-image-repeat CSS property defines how the middle part of a border image is handled so that it can match the size of the border. It has a one-value syntax that describes the behavior of all the sides, and a two-value syntax that sets a different value for the horizontal and vertical behavior.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-repeat">MDN</a>
   */
  final val borderImageRepeat = Attr.real("border-image-repeat", Transform keys CanIUse.borderImage)

  /**
   * The border-image-slice CSS property divides the image specified by border-image-source in nine regions: the four corners, the four edges and the middle. It does this by specifying 4 inwards offsets.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-slice">MDN</a>
   */
  final val borderImageSlice = Attr.real("border-image-slice", Transform keys CanIUse.borderImage)

  /**
   * The border-image-source CSS property defines the &lt;image> to use instead of the style of the border. If this property is set to none, the style defined by border-style is used instead.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-source">MDN</a>
   */
  final val borderImageSource = Attr.real("border-image-source", Transform keys CanIUse.borderImage)

  /**
   * The border-image-width CSS property defines the width of the border. If specified, it overrides the border-width property.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-width">MDN</a>
   */
  final val borderImageWidth = Attr.real("border-image-width", Transform keys CanIUse.borderImage)

  /**
   * The border-left-color CSS property sets the color of the bottom border of an element. Note that in many cases the shorthand CSS properties border-color or border-left are more convenient and preferable.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-left-color">MDN</a>
   */
  object borderLeftColor extends TypedAttr_Color {
    override val attr = Attr.real("border-left-color")
  }

  /**
   * The border-left-style CSS property sets the line style of the left border of a box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-left-style">MDN</a>
   */
  object borderLeftStyle extends TypedAttr_BrStyle {
    override val attr = Attr.real("border-left-style")
  }

  /**
   * The border-left-width CSS property sets the width of the left border of a box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-left-width">MDN</a>
   */
  object borderLeftWidth extends TypedAttr_BrWidth {
    override val attr = Attr.real("border-left-width")
  }

  /**
   * The border-right-color CSS property sets the color of the right border of an element. Note that in many cases the shorthand CSS properties  border-color or border-right are more convenient and preferable.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-right-color">MDN</a>
   */
  object borderRightColor extends TypedAttr_Color {
    override val attr = Attr.real("border-right-color")
  }

  /**
   * The border-right-style CSS property sets the line style of the right border of a box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-right-style">MDN</a>
   */
  object borderRightStyle extends TypedAttr_BrStyle {
    override val attr = Attr.real("border-right-style")
  }

  /**
   * The border-right-width CSS property sets the width of the right border of a box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-right-width">MDN</a>
   */
  object borderRightWidth extends TypedAttr_BrWidth {
    override val attr = Attr.real("border-right-width")
  }

  /**
   * The border-spacing CSS property specifies the distance between the borders of adjacent cells (only for the separated borders model). This is equivalent to the cellspacing attribute in presentational HTML, but an optional second value can be used to set different horizontal and vertical spacing.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-spacing">MDN</a>
   */
  object borderSpacing extends TypedAttrT2[Len] with ZeroLit {
    override val attr = Attr.real("border-spacing")
  }

  /**
   * The border-top-color CSS property sets the color of the top border of an element. Note that in many cases the shorthand CSS properties border-color or border-top are more convenient and preferable.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-top-color">MDN</a>
   */
  object borderTopColor extends TypedAttr_Color {
    override val attr = Attr.real("border-top-color")
  }

  /**
   * The border-top-left-radius CSS property sets the rounding of the top-left corner of the element. The rounding can be a circle or an ellipse, or if one of the value is 0,no rounding is done and the corner is square.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-top-left-radius">MDN</a>
   */
  object borderTopLeftRadius extends TypedAttrT2[LenPct] with ZeroLit {
    override val attr = Attr.real("border-top-left-radius", Transform keys CanIUse.borderRadius)
  }

  /**
   * The border-top-right-radius CSS property sets the rounding of the top-right corner of the element. The rounding can be a circle or an ellipse, or if one of the value is 0 no rounding is done and the corner is square.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-top-right-radius">MDN</a>
   */
  object borderTopRightRadius extends TypedAttrT2[LenPct] with ZeroLit {
    override val attr = Attr.real("border-top-right-radius", Transform keys CanIUse.borderRadius)
  }

  /**
   * The border-top-style CSS property sets the line style of the top border of a box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-top-style">MDN</a>
   */
  object borderTopStyle extends TypedAttr_BrStyle {
    override val attr = Attr.real("border-top-style")
  }

  /**
   * The border-top-width CSS property sets the width of the top border of a box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-top-width">MDN</a>
   */
  object borderTopWidth extends TypedAttr_BrWidth {
    override val attr = Attr.real("border-top-width")
  }

  /**
   * The bottom CSS property participates in specifying the position of positioned elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/bottom">MDN</a>
   */
  object bottom extends TypedAttr_LenPctAuto {
    override val attr = Attr.real("bottom")
  }

  /**
   * The box-decoration-break CSS property specifies how the background, padding, border, border-image, box-shadow, margin and clip of an element is applied when the box for the element is fragmented.  Fragmentation occurs when an inline box wraps onto multiple lines, or when a block spans more than one column inside a column layout container, or spans a page break when printed.  Each piece of the rendering for the element is called a fragment.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/box-decoration-break">MDN</a>
   */
  object boxDecorationBreak extends TypedAttrBase {
    override val attr = Attr.real("box-decoration-break", Transform keys CanIUse.boxdecorationbreak)
    @inline def clone_ = av(L.clone_)
    @inline def slice  = av(L.slice)
  }

  /**
   * The -webkit-box-reflect CSS property lets you reflect the content of an element in one specific direction.
   *
   * Note: This feature is not intended to be used by Web sites. To achieve reflection on the Web, the standard way is
   * to use the CSS `element()` function.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/-webkit-box-reflect">MDN</a>
   */
  final val boxReflect = Attr.real("box-reflect", Transform keys CanIUse.reflections)

  /**
   * The box-shadow CSS property describes one or more shadow effects as a comma-separated list.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/box-shadow">MDN</a>
   */
  final val boxShadow = Attr.real("box-shadow", Transform keys CanIUse.boxshadow)

  /**
   * The box-sizing CSS property is used to alter the default CSS box model used to calculate widths and heights of elements. It is possible to use this property to emulate the behavior of browsers that do not correctly support the CSS box model specification.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/box-sizing">MDN</a>
   */
  object boxSizing extends TypedAttrBase {
    override val attr = Attr.real("box-sizing", Transform keys CanIUse.css3Boxsizing)
    @inline def borderBox  = av(L.borderBox)
    @inline def contentBox = av(L.contentBox)
    @inline def paddingBox = av(L.paddingBox)
  }

  /**
   * The break-after CSS property describes how the page, column or region break behavior after the generated box. If there is no generated box, the property is ignored.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/break-after">MDN</a>
   */
  object breakAfter extends TypedAttrBase {
    override val attr = Attr.real("break-after")
    @inline def always      = av(L.always)
    @inline def auto        = avl(LT.auto)
    @inline def avoid       = av(L.avoid)
    @inline def avoidColumn = av(L.avoidColumn)
    @inline def avoidPage   = av(L.avoidPage)
    @inline def column      = av(L.column)
    @inline def left        = av(L.left)
    @inline def page        = av(L.page)
    @inline def right       = av(L.right)
  }

  /**
   * The break-before CSS property describes how the page, column or region break behavior before the generated box. If there is no generated box, the property is ignored.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/break-before">MDN</a>
   */
  object breakBefore extends TypedAttrBase {
    override val attr = Attr.real("break-before")
    @inline def always      = av(L.always)
    @inline def auto        = avl(LT.auto)
    @inline def avoid       = av(L.avoid)
    @inline def avoidColumn = av(L.avoidColumn)
    @inline def avoidPage   = av(L.avoidPage)
    @inline def column      = av(L.column)
    @inline def left        = av(L.left)
    @inline def page        = av(L.page)
    @inline def right       = av(L.right)
  }

  /**
   * The break-inside CSS property describes how the page, column or region break inside the generated box. If there is no generated box, the property is ignored.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/break-inside">MDN</a>
   */
  object breakInside extends TypedAttrBase {
    override val attr = Attr.real("break-inside")
    @inline def auto        = avl(LT.auto)
    @inline def avoid       = av(L.avoid)
    @inline def avoidColumn = av(L.avoidColumn)
    @inline def avoidPage   = av(L.avoidPage)
  }

  /**
   * The caption-side CSS property positions the content of a table's &lt;caption> on the specified side.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/caption-side">MDN</a>
   */
  object captionSide extends TypedAttrBase {
    override val attr = Attr.real("caption-side",
      Transform.values(CanIUse.logicalProps)(L.blockStart, L.blockEnd, L.inlineStart, L.inlineEnd))
    @inline def blockEnd    = av(L.blockEnd)
    @inline def blockStart  = av(L.blockStart)
    @inline def bottom      = av(L.bottom)
    @inline def inlineEnd   = av(L.inlineEnd)
    @inline def inlineStart = av(L.inlineStart)
    @inline def top         = av(L.top)
  }

  /**
   * The clear CSS property specifies whether an element can be next to floating elements that precede it or must be moved down (cleared) below them.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/clear">MDN</a>
   */
  object clear extends TypedAttrBase {
    override val attr = Attr.real("clear",
      Transform.values(CanIUse.logicalProps)(L.inlineStart, L.inlineEnd))
    @inline def both        = av(L.both)
    @inline def inlineEnd   = av(L.inlineEnd)
    @inline def inlineStart = av(L.inlineStart)
    @inline def left        = av(L.left)
    @inline def none        = avl(LT.none)
    @inline def right       = av(L.right)
  }

  /**
   * The clip CSS property defines what portion of an element is visible. The clip property applies only to absolutely positioned elements, that is elements with position:absolute or position:fixed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/clip">MDN</a>
   */
  object clip extends TypedAttr_Shape {
    override val attr = Attr.real("clip")
    @inline def auto = avl(LT.auto)
  }

  /**
   * The clip-path property prevents a portion of an element from drawing by defining a clipping region.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/clip-path">MDN</a>
   */
  final val clipPath = Attr.real("clip-path", Transform keys CanIUse.clipPath)

  /**
   * The CSS color property sets the foreground color of an element's text content, and its decorations. It doesn't affect any other characteristic of the element; it should really be called text-color and would have been named so, save for historical reasons and its appearance in CSS Level 1.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/color">MDN</a>
   */
  object color extends TypedAttr_Color {
    override val attr = Attr.real("color")
  }

  /**
   * The column-count CSS property describes the number of columns of the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/column-count">MDN</a>
   */
  object columnCount extends TypedAttrT1[Number] {
    override val attr = Attr.real("column-count", Transform keys CanIUse.multicolumn)
    @inline def auto = avl(LT.auto)
  }

  /**
   * The column-fill CSS property controls how contents are partitioned into columns. Contents are either balanced, which means that contents in all columns will have the same height or, when using auto, just take up the room the content needs.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/column-fill">MDN</a>
   */
  object columnFill extends TypedAttrBase {
    override val attr = Attr.real("column-fill", Transform keys CanIUse.multicolumn)
    @inline def auto    = avl(LT.auto)
    @inline def balance = av(L.balance)
  }

  /**
   * The column-gap CSS property sets the size of the gap between columns for elements which are specified to display as a multi-column element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/column-gap">MDN</a>
   */
  object columnGap extends TypedAttrT1[Len] with ZeroLit {
    override val attr = Attr.real("column-gap", Transform keys CanIUse.multicolumn)
    @inline def normal = av(L.normal)
  }

  /**
   * The column-rule-color CSS property lets you set the color of the rule drawn between columns in multi-column layouts.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/column-rule-color">MDN</a>
   */
  object columnRuleColor extends TypedAttr_Color {
    override val attr = Attr.real("column-rule-color", Transform keys CanIUse.multicolumn)
  }

  /**
   * The column-rule-style CSS property lets you set the style of the rule drawn between columns in multi-column layouts.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/column-rule-style">MDN</a>
   */
  object columnRuleStyle extends TypedAttr_BrStyle {
    override val attr = Attr.real("column-rule-style", Transform keys CanIUse.multicolumn)
  }

  /**
   * The column-rule-width CSS property lets you set the width of the rule drawn between columns in multi-column layouts.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/column-rule-width">MDN</a>
   */
  object columnRuleWidth extends TypedAttr_BrWidth {
    override val attr = Attr.real("column-rule-width", Transform keys CanIUse.multicolumn)
  }

  /**
   * The column-span CSS property makes it possible for an element to span across all columns when its value is set to all. An element that spans more than one column is called a spanning element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/column-span">MDN</a>
   */
  object columnSpan extends TypedAttrBase {
    override val attr = Attr.real("column-span", Transform keys CanIUse.multicolumn)
    @inline def all  = av(L.all)
    @inline def none = avl(LT.none)
  }

  /**
   * The column-width CSS property suggests an optimal column width. This is not a absolute value but a mere hint. Browser will adjust the width of the column around that suggested value, allowing to achieve scalable designs that fit different screen size. Especially in presence of the column-count CSS property which has precedence, to set an exact column width, all length values must be specified. In horizontal text these are width, column-width, column-gap, and column-rule-width.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/column-width">MDN</a>
   */
  object columnWidth extends TypedAttrT1[Len] with ZeroLit {
    override val attr = Attr.real("column-width", Transform keys CanIUse.multicolumn)
    @inline def auto = avl(LT.auto)
  }

  /**
   * The content CSS property is used with the ::before and ::after pseudo-elements to generate content in an element. Objects inserted using the content property are anonymous replaced elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/content">MDN</a>
   */
  final val content = Attr.real("content")

  /**
   * The counter-increment CSS property is used to increase the value of CSS Counters by a given value. The counter's value can be reset using the counter-reset CSS property.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/counter-increment">MDN</a>
   */
  final val counterIncrement = Attr.real("counter-increment", Transform keys CanIUse.counters)

  /**
   * The counter-reset CSS property is used to reset CSS Counters to a given value.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/counter-reset">MDN</a>
   */
  final val counterReset = Attr.real("counter-reset", Transform keys CanIUse.counters)

  /**
   * The cursor CSS property specifies the mouse cursor displayed when the mouse pointer is over an element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/cursor">MDN</a>
   */
  object cursor extends TypedAttrBase {
    override val attr = Attr.real("cursor",
      Transform.values(CanIUse.css3CursorsNewer)(L.grab, L.grabbing, L.zoomIn, L.zoomOut))

    /** Indicating an alias or shortcut is to be created. */
    @inline def alias = av(L.alias)

    /** Cursor showing that something can be scrolled in any direction (panned). */
    @inline def allScroll = av(L.allScroll)

    /** The browser determines the cursor to display based on the current context. */
    @inline def auto = avl(LT.auto)

    /** Indicating that cells can be selected. */
    @inline def cell = av(L.cell)

    /** The item/column can be resized horizontally. Often rendered as arrows pointing left and right with a vertical bar separating. */
    @inline def colResize = av(L.colResize)

    /** A context menu is available under the cursor. Only IE 10 and up have implemented this on Windows */
    @inline def contextMenu = av(L.contextMenu)

    /** Indicating that something can be copied. */
    @inline def copy = av(L.copy)

    /** Cross cursor, often used to indicate selection in a bitmap. */
    @inline def crosshair = av(L.crosshair)

    /** Default cursor, typically an arrow. */
    @inline def default = av(L.default)

    /** The east edge is to be moved. */
    @inline def eResize = av(L.eResize)

    /** Indicates a bidirectional resize cursor. */
    @inline def ewResize = av(L.ewResize)

    /** Indicates that something can be grabbed (dragged to be moved). */
    @inline def grab = av(L.grab)

    /** Indicates that something can be grabbed (dragged to be moved). */
    @inline def grabbing = av(L.grabbing)

    /** Indicating help is available. */
    @inline def help = av(L.help)

    /** The hovered object may be moved. */
    @inline def move = av(L.move)

    /** The north-east edge is to be moved. */
    @inline def neResize = av(L.neResize)

    /** Indicates a bidirectional resize cursor. */
    @inline def neswResize = av(L.neswResize)

    /** Cursor showing that a drop is not allowed at the current location. */
    @inline def noDrop = av(L.noDrop)

    /** No cursor is rendered. */
    @inline def none = avl(LT.none)

    /** Cursor showing that something cannot be done. */
    @inline def notAllowed = av(L.notAllowed)

    /** The north edge is to be moved. */
    @inline def nResize = av(L.nResize)

    /** Indicates a bidirectional resize cursor. */
    @inline def nsResize = av(L.nsResize)

    /** The north-west edge is to be moved. */
    @inline def nwResize = av(L.nwResize)

    /** Indicates a bidirectional resize cursor. */
    @inline def nwseResize = av(L.nwseResize)

    /** Used when hovering over links, typically a hand. */
    @inline def pointer = av(L.pointer)

    /** The program is busy in the background but the user can still interact with the interface (unlike for wait). */
    @inline def progress = av(L.progress)

    /** The item/row can be resized vertically. Often rendered as arrows pointing up and down with a horizontal bar separating them. */
    @inline def rowResize = av(L.rowResize)

    /** The south-east edge is to be moved. */
    @inline def seResize = av(L.seResize)

    /** The south edge is to be moved. */
    @inline def sResize = av(L.sResize)

    /** The south-west edge is to be moved. */
    @inline def swResize = av(L.swResize)

    /** Indicating text can be selected, typically an I-beam. */
    @inline def text = av(L.text)

    /** Indicating that vertical text can be selected, typically a sideways I-beam. */
    @inline def verticalText = av(L.verticalText)

    /** The program is busy (sometimes an hourglass or a watch). */
    @inline def wait_ = av(L.wait_)

    /** The west edge is to be moved. */
    @inline def wResize = av(L.wResize)

    /** Indicates that something can be zoomed (magnified) in. */
    @inline def zoomIn = av(L.zoomIn)

    /** Indicates that something can be zoomed (magnified) out. */
    @inline def zoomOut = av(L.zoomOut)
  }

  /**
   * Set the direction CSS property to match the direction of the text: rtl for languages written from right-to-left (like Hebrew or Arabic) text and ltr for other scripts. This is typically done as part of the document (e.g., using the dir attribute in HTML) rather than through direct use of CSS.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/direction">MDN</a>
   */
  object direction extends TypedAttrBase {
    override val attr = Attr.real("direction")
    @inline def ltr = av(L.ltr)
    @inline def rtl = av(L.rtl)
  }

  /**
   * The display CSS property specifies the type of rendering box used for an element. In HTML, default display property values are taken from behaviors described in the HTML specifications or from the browser/user default stylesheet. The default value in XML is inline.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/display">MDN</a>
   */
  object display extends TypedAttrBase {
    override val attr = Attr.real("display",
      Transform.values(CanIUse.flexbox)(L.flex, L.inlineFlex) *
      Transform.values(CanIUse.grid   )(L.grid, L.inlineGrid))

    @inline def block             = av(L.block)
    @inline def contents          = av(L.contents)
    @inline def flex              = av(L.flex)
    @inline def grid              = av(L.grid)
    @inline def inline            = av(L.inline)
    @inline def inlineBlock       = av(L.inlineBlock)
    @inline def inlineFlex        = av(L.inlineFlex)
    @inline def inlineGrid        = av(L.inlineGrid)
    @inline def inlineTable       = av(L.inlineTable)
    @inline def listItem          = av(L.listItem)
    @inline def none              = avl(LT.none)
    @inline def ruby              = av(L.ruby)
    @inline def rubyBase          = av(L.rubyBase)
    @inline def rubyBaseContainer = av(L.rubyBaseContainer)
    @inline def rubyText          = av(L.rubyText)
    @inline def rubyTextContainer = av(L.rubyTextContainer)
    @inline def runIn             = av(L.runIn)
    @inline def table             = av(L.table)
    @inline def tableCell         = av(L.tableCell)
    @inline def tableColumn       = av(L.tableColumn)
    @inline def tableColumnGroup  = av(L.tableColumnGroup)
    @inline def tableFooterGroup  = av(L.tableFooterGroup)
    @inline def tableHeaderGroup  = av(L.tableHeaderGroup)
    @inline def tableRow          = av(L.tableRow)
    @inline def tableRowGroup     = av(L.tableRowGroup)
  }

  /**
   * The empty-cells CSS property specifies how user agents should render borders and backgrounds around cells that have no visible content.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/empty-cells">MDN</a>
   */
  object emptyCells extends TypedAttrBase {
    override val attr = Attr.real("empty-cells")
    @inline def hide = av(L.hide)
    @inline def show = av(L.show)
  }

  /**
   * The CSS filter property provides for effects like blurring or color shifting on an element’s rendering before the element is displayed. Filters are commonly used to adjust the rendering of an image, a background, or a border.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/filter">MDN</a>
   */
  final val filter = Attr.real("filter", Transform keys CanIUse.filters)

  /**
   * The CSS flex-basis property specifies the flex basis which is the initial main size of a flex item. The property determines the size of the content-box unless specified otherwise using box-sizing.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/flex-basis">MDN</a>
   */
  final val flexBasis = Attr.real("flex-basis", Transform keys CanIUse.flexbox)

  /**
   * The CSS flex-direction property specifies how flex items are placed in the flex container defining the main axis and the direction (normal or reversed).
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/flex-direction">MDN</a>
   */
  object flexDirection extends TypedAttrBase {
    override val attr = Attr.real("flex-direction", Transform keys CanIUse.flexbox)
    @inline def column        = av(L.column)
    @inline def columnReverse = av(L.columnReverse)
    @inline def row           = av(L.row)
    @inline def rowReverse    = av(L.rowReverse)
  }

  /**
   * The CSS flex-grow property specifies the flex grow factor of a flex item.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/flex-grow">MDN</a>
   */
  object flexGrow extends TypedAttrT1[Number] {
    override val attr = Attr.real("flex-grow", Transform keys CanIUse.flexbox)
  }

  /**
   * The CSS flex-shrink property specifies the flex shrink factor of a flex item.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/flex-shrink">MDN</a>
   */
  object flexShrink extends TypedAttrT1[Number] {
    override val attr = Attr.real("flex-shrink", Transform keys CanIUse.flexbox)
  }

  /**
   * The CSS flex-wrap property specifies whether the children are forced into a single line or if the items can be flowed on multiple lines.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/flex-wrap">MDN</a>
   */
  object flexWrap extends TypedAttrBase {
    override val attr = Attr.real("flex-wrap", Transform keys CanIUse.flexbox)
    @inline def nowrap      = av(L.nowrap)
    @inline def wrap        = av(L.wrap)
    @inline def wrapReverse = av(L.wrapReverse)
  }

  /**
   * The float CSS property specifies that an element should be taken from the normal flow and placed along the left or right side of its container, where text and inline elements will wrap around it. A floating element is one where the computed value of float is not none.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/float">MDN</a>
   */
  object float extends TypedAttrBase {
    override val attr = Attr.real("float",
      Transform.values(CanIUse.logicalProps)(L.inlineStart, L.inlineEnd))
    @inline def inlineEnd   = av(L.inlineEnd)
    @inline def inlineStart = av(L.inlineStart)
    @inline def left        = av(L.left)
    @inline def none        = avl(LT.none)
    @inline def right       = av(L.right)
  }

  /**
   * Flows content from a named flow (specified by a corresponding flow-into) through selected elements to form a dynamic chain of layout regions.
   *
   * @see <a href="https://docs.webplatform.org/wiki/css/properties/flow-from">WPD</a>
   */
  final val flowFrom = Attr.real("flow-from", Transform keys CanIUse.regions)

  /**
   * Diverts the selected element's content into a named flow, used to thread content through different layout regions specified by flow-from.
   *
   * @see <a href="https://docs.webplatform.org/wiki/css/properties/flow-into">WPD</a>
   */
  final val flowInto = Attr.real("flow-into", Transform keys CanIUse.regions)

  /**
   * The font-family CSS property allows for a prioritized list of font family names and/or generic family names to be specified for the selected element. Unlike most other CSS properties, values are separated by a comma to indicate that they are alternatives. The browser will select the first font on the list that is installed on the computer, or that can be downloaded using the information provided by a @font-face at-rule.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-family">MDN</a>
   */
  final val fontFamily = Attr.real("font-family")

  /**
   * The font-feature-settings CSS property allows control over advanced typographic features in OpenType fonts.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-feature-settings">MDN</a>
   */
  final val fontFeatureSettings = Attr.real("font-feature-settings", Transform keys CanIUse.fontFeature)

  /**
   * The font-kerning CSS property controls the usage of the kerning information; that is, it controls how letters are spaced. The kerning information is stored in the font, and if the font is well-kerned, this feature allows spacing between characters to be very similar, whatever the characters are.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-kerning">MDN</a>
   */
  object fontKerning extends TypedAttrBase {
    override val attr = Attr.real("font-kerning", Transform keys CanIUse.fontKerning)
    @inline def auto   = avl(LT.auto)
    @inline def none   = avl(LT.none)
    @inline def normal = av(L.normal)
  }

  /**
   * The font-language-override CSS property controls the usage of language-specific glyphs in a typeface.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-language-override">MDN</a>
   */
  final val fontLanguageOverride = Attr.real("font-language-override")

  /**
   * The font-size CSS property specifies the size of the font – specifically the desired height of glyphs from the font. Setting the font size may, in turn, change the size of other items, since it is used to compute the value of em and ex length units.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-size">MDN</a>
   */
  object fontSize extends TypedAttrT1[LenPct] with ZeroLit {
    override val attr = Attr.real("font-size")
    @inline def large   = av(L.large)
    @inline def larger  = av(L.larger)
    @inline def medium  = avl(LT.medium)
    @inline def small   = av(L.small)
    @inline def smaller = av(L.smaller)
    @inline def sSmall  = av(L.sSmall)
    @inline def xLarge  = av(L.xLarge)
    @inline def xxSmall = av(L.xxSmall)
    @inline def xxLarge = av(L.xxLarge)
  }

  /**
   * The font-size-adjust CSS property specifies that font size should be chosen based on the height of lowercase letters rather than the height of capital letters.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-size-adjust">MDN</a>
   */
  object fontSizeAdjust extends TypedAttrT1[Number] {
    override val attr = Attr.real("font-size-adjust", Transform keys CanIUse.fontSizeAdjust)
    @inline def none = avl(LT.none)
  }

  /**
   * The font-stretch CSS property selects a normal, condensed, or expanded face from a font.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-stretch">MDN</a>
   */
  object fontStretch extends TypedAttrBase {
    override val attr = Attr.real("font-stretch", Transform keys CanIUse.fontStretch)
    @inline def condensed      = av(L.condensed)
    @inline def expanded       = av(L.expanded)
    @inline def extraCondensed = av(L.extraCondensed)
    @inline def extraExpanded  = av(L.extraExpanded)
    @inline def normal         = av(L.normal)
    @inline def semiCondensed  = av(L.semiCondensed)
    @inline def semiExpanded   = av(L.semiExpanded)
    @inline def ultraCondensed = av(L.ultraCondensed)
    @inline def ultraExpanded  = av(L.ultraExpanded)
  }

  /**
   * The font-style CSS property allows italic or oblique faces to be selected within a font-family.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-style">MDN</a>
   */
  object fontStyle extends TypedAttrBase {
    override val attr = Attr.real("font-style")
    @inline def italic  = av(L.italic)
    @inline def normal  = av(L.normal)
    @inline def oblique = av(L.oblique)
  }

  /**
   * The font-synthesis CSS property controls which missing typefaces, bold or italic, may be synthesized by the browser.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-synthesis">MDN</a>
   */
  final val fontSynthesis = Attr.real("font-synthesis")

  /**
   * The font-variant-alternates CSS property controls the usage of alternate glyphs associated to alternative names defined in @font-feature-values.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-alternates">MDN</a>
   */
  final val fontVariantAlternates = Attr.real("font-variant-alternates")

  /**
   * The font-variant-caps CSS property controls the usage of alternate glyphs for capital letters. Scripts can have capital letter glyphs of different sizes, the normal uppercase glyphs, small capital glyphs, and petite capital glyphs. This property controls which alternate glyphs to use.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-caps">MDN</a>
   */
  object fontVariantCaps extends TypedAttrBase {
    override val attr = Attr.real("font-variant-caps")
    @inline def allPetiteCaps = av(L.allPetiteCaps)
    @inline def allSmallCaps  = av(L.allSmallCaps)
    @inline def normal        = av(L.normal)
    @inline def petiteCaps    = av(L.petiteCaps)
    @inline def smallCaps     = av(L.smallCaps)
    @inline def titlingCaps   = av(L.titlingCaps)
    @inline def unicase       = av(L.unicase)
  }

  /**
   * The font-variant-east-asian CSS property controls the usage of alternate glyphs for East Asian scripts, like Japanese and Chinese.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-east-asian">MDN</a>
   */
  final val fontVariantEastAsian = Attr.real("font-variant-east-asian")

  /**
   * The font-variant-ligatures CSS property controls which ligatures and contextual forms are used in textual content of the elements it applies to. This leads to more harmonized forms in the resulting text.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-ligatures">MDN</a>
   */
  final val fontVariantLigatures = Attr.real("font-variant-ligatures")

  /**
   * The font-variant-numeric CSS property controls the usage of alternate glyphs for numbers, fractions, and ordinal markers.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-numeric">MDN</a>
   */
  final val fontVariantNumeric = Attr.real("font-variant-numeric")

  /**
   * The font-variant-position CSS property controls the usage of alternate glyphs of smaller size positioned as superscript or subscript regarding the baseline of the font, which is set unchanged. These glyphs are likely to be used in &lt;sub> and &lt;sup> elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-position">MDN</a>
   */
  object fontVariantPosition extends TypedAttrBase {
    override val attr = Attr.real("font-variant-position")
    @inline def normal = av(L.normal)
    @inline def sub    = av(L.sub)
    @inline def super_ = av(L.super_)
  }

  /**
   * The font-weight CSS property specifies the weight or boldness of the font. However, some fonts are not available in all weights; some are available only on normal and bold.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-weight">MDN</a>
   */
  object fontWeight extends TypedAttrBase {
    override val attr = Attr.real("font-weight")
    @inline def _100    = av("100")
    @inline def _200    = av("200")
    @inline def _300    = av("300")
    @inline def _400    = av("400")
    @inline def _500    = av("500")
    @inline def _600    = av("600")
    @inline def _700    = av("700")
    @inline def _800    = av("800")
    @inline def _900    = av("900")
    @inline def bold    = av(L.bold)
    @inline def bolder  = av(L.bolder)
    @inline def lighter = av(L.lighter)
    @inline def normal  = av(L.normal)
  }

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-auto-columns">MDN</a>
   */
  final val gridAutoColumns = Attr.real("grid-auto-columns", Transform keys CanIUse.grid)

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-auto-flow">MDN</a>
   */
  final val gridAutoFlow = Attr.real("grid-auto-flow", Transform keys CanIUse.grid)

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-auto-position">MDN</a>
   */
  final val gridAutoPosition = Attr.real("grid-auto-position", Transform keys CanIUse.grid)

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-auto-rows">MDN</a>
   */
  final val gridAutoRows = Attr.real("grid-auto-rows", Transform keys CanIUse.grid)

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-column-start">MDN</a>
   */
  final val gridColumnStart = Attr.real("grid-column-start", Transform keys CanIUse.grid)

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-column-end">MDN</a>
   */
  final val gridColumnEnd = Attr.real("grid-column-end", Transform keys CanIUse.grid)

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-row-start">MDN</a>
   */
  final val gridRowStart = Attr.real("grid-row-start", Transform keys CanIUse.grid)

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-row-end">MDN</a>
   */
  final val gridRowEnd = Attr.real("grid-row-end", Transform keys CanIUse.grid)

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-template-areas">MDN</a>
   */
  final val gridTemplateAreas = Attr.real("grid-template-areas", Transform keys CanIUse.grid)

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-template-rows">MDN</a>
   */
  final val gridTemplateRows = Attr.real("grid-template-rows", Transform keys CanIUse.grid)

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-template-columns">MDN</a>
   */
  final val gridTemplateColumns = Attr.real("grid-template-columns", Transform keys CanIUse.grid)

  /**
   * The height CSS property specifies the height of the content area of an element. The content area is inside the padding, border, and margin of the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/height">MDN</a>
   */
  object height extends TypedAttr_Length {
    override protected def attr2 = Attr.real("height", _)
  }

  /**
   * The hyphens CSS property tells the browser how to go about splitting words to improve the layout of text when line-wrapping. On HTML, the language is determined by the lang attribute: browsers will hyphenate only if this attribute is present and if an appropriate hyphenation dictionary is available. On XML, the xml:lang attribute must be used.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/hyphens">MDN</a>
   */
  object hyphens extends TypedAttrBase {
    override val attr = Attr.real("hyphens", Transform keys CanIUse.hyphens)
    @inline def auto   = avl(LT.auto)
    @inline def manual = av(L.manual)
    @inline def none   = avl(LT.none)
  }

  /**
   * The image-rendering CSS property provides a hint to the user agent about how to handle its image rendering.  It applies to any images appearing on the element properties, but has no effect on non-scaled images.. For example, if the natural size of the image is 100×100px but the page author specifies the dimensions to 200×200px (or 50×50px), then the image will be upscaled (or downscaled) to the new dimensions using the specified algorithm. Scaling may also apply due to user interaction (zooming).
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/image-rendering">MDN</a>
   */
  object imageRendering extends TypedAttrBase {
    override val attr = Attr.real("image-rendering",
      Transform.values(CanIUse.crispEdges)(L.crispEdges, L.pixelated))
    @inline def auto       = avl(LT.auto)
    @inline def crispEdges = av(L.crispEdges)
    @inline def pixelated  = av(L.pixelated)
  }

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/image-resolution">MDN</a>
   */
  final val imageResolution = Attr.real("image-resolution")

  /**
   * The image-orientation CSS property describes how to correct the default orientation of an image.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/image-orientation">MDN</a>
   */
  final val imageOrientation = Attr.real("image-orientation")

  /**
   * The ime-mode CSS property controls the state of the input method editor for text fields.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/ime-mode">MDN</a>
   */
  object imeMode extends TypedAttrBase {
    override val attr = Attr.real("ime-mode")
    @inline def active   = av(L.active)
    @inline def auto     = avl(LT.auto)
    @inline def disabled = av(L.disabled)
    @inline def inactive = av(L.inactive)
    @inline def normal   = av(L.normal)
  }

  /**
   * The isolation CSS property defines if the element must create a new stacking context.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/isolation">MDN</a>
   */
  object isolation extends TypedAttrBase {
    override val attr = Attr.real("isolation")
    @inline def auto    = avl(LT.auto)
    @inline def isolate = av(L.isolate)
  }

  /**
   * The CSS justify-content property defines how a browser distributes available space between and around elements when aligning flex items in the main-axis of the current line. The alignment is done after the lengths and auto margins are applied, meaning that, if there is at least one flexible element, with flex-grow different than 0, it will have no effect as there won't be any available space.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/justify-content">MDN</a>
   */
  object justifyContent extends TypedAttrBase {
    override val attr = Attr.real("justify-content", Transform keys CanIUse.flexbox)
    @inline def center       = av(L.center)
    @inline def flexEnd      = av(L.flexEnd)
    @inline def flexStart    = av(L.flexStart)
    @inline def spaceAround  = av(L.spaceAround)
    @inline def spaceBetween = av(L.spaceBetween)
  }

  /**
   * The left CSS property specifies part of the position of positioned elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/left">MDN</a>
   */
  object left extends TypedAttr_LenPctAuto {
    override val attr = Attr.real("left")
  }

  /**
   * The letter-spacing CSS property specifies spacing behavior between text characters.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/letter-spacing">MDN</a>
   */
  object letterSpacing extends TypedAttrT1[Len] with ZeroLit {
    override val attr = Attr.real("letter-spacing", Transform keys CanIUse.letterSpacing)
    @inline def normal = av(L.normal)
  }

  /**
   * The line-break CSS property is used to specify how (or if) to break lines.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/line-break">MDN</a>
   */
  object lineBreak extends TypedAttrBase {
    override val attr = Attr.real("line-break")
    @inline def auto   = avl(LT.auto)
    @inline def loose  = av(L.loose)
    @inline def normal = av(L.normal)
    @inline def strict = av(L.strict)
  }

  /**
   * On block level elements, the line-height CSS property specifies the minimal height of line boxes within the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/line-height">MDN</a>
   */
  object lineHeight extends TypedAttrT1[LenPctNum] with ZeroLit {
    override val attr = Attr.real("line-height")
    @inline def normal = av(L.normal)
  }

  /**
   * The list-style-image CSS property sets the image that will be used as the list item marker.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/list-style-image">MDN</a>
   */
  final val listStyleImage = Attr.real("list-style-image")

  /**
   * The list-style-position CSS property specifies the position of the marker box in the principal block box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/list-style-position">MDN</a>
   */
  object listStylePosition extends TypedAttrBase {
    override val attr = Attr.real("list-style-position")
    @inline def inside  = av(L.inside)
    @inline def outside = av(L.outside)
  }

  /**
   * The list-style-type CSS property specifies appearance of a list item element. As it is the only one which defaults to display:list-item, this is usually a &lt;li> element, but can be any element with this display value.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/list-style-type">MDN</a>
   */
  final val listStyleType = Attr.real("list-style-type")

  /**
   * The margin-bottom CSS property of an element sets the margin space required on the bottom of an element. A negative value is also allowed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/margin-bottom">MDN</a>
   */
  object marginBottom extends TypedAttr_LenPctAuto {
    override val attr = Attr.real("margin-bottom")
  }

  /**
   * The margin-left CSS property of an element sets the margin space required on the left side of a box associated with an element. A negative value is also allowed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/margin-left">MDN</a>
   */
  object marginLeft extends TypedAttr_LenPctAuto {
    override val attr = Attr.real("margin-left")
  }

  /**
   * The margin-right CSS property of an element sets the margin space required on the right side of an element. A negative value is also allowed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/margin-right">MDN</a>
   */
  object marginRight extends TypedAttr_LenPctAuto {
    override val attr = Attr.real("margin-right")
  }

  /**
   * The margin-top CSS property of an element sets the margin space required on the top of an element. A negative value is also allowed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/margin-top">MDN</a>
   */
  object marginTop extends TypedAttr_LenPctAuto {
    override val attr = Attr.real("margin-top")
  }

  /**
   * The marks CSS property adds crop and/or cross marks to the presentation of the document. Crop marks indicate where the page should be cut. Cross marks are used to align sheets.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/marks">MDN</a>
   */
  final val marks = Attr.real("marks")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/mask">MDN</a>
   */
  final val mask = Attr.real("mask", Transform keys CanIUse.masks)
  // TODO mask is shorthand - missing mask properties: http://www.w3.org/TR/css-masking/

  /**
   * The CSS mask-type properties defines if a SVG &lt;mask> element is a luminance or an alpha mask.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/mask-type">MDN</a>
   */
  final val maskType = Attr.real("mask-type", Transform keys CanIUse.masks)

  /**
   * The max-height CSS property is used to set the maximum height of a given element. It prevents the used value of the height property from becoming larger than the value specified for max-height.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/max-height">MDN</a>
   */
  object maxHeight extends TypedAttr_MaxLength {
    override protected def attr2 = Attr.real("max-height", _)
  }

  /**
   * The max-width CSS property is used to set the maximum width of a given element. It prevents the used value of the width property from becoming larger than the value specified for max-width.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/max-width">MDN</a>
   */
  object maxWidth extends TypedAttr_MaxLength {
    override protected def attr2 = Attr.real("max-width", _)
  }

  /**
   * The min-height CSS property is used to set the minimum height of a given element. It prevents the used value of the height property from becoming smaller than the value specified for min-height.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/min-height">MDN</a>
   */
  object minHeight extends TypedAttr_MinLength {
    override protected def attr2 = Attr.real("min-height", _)
  }

  /**
   * The min-width CSS property is used to set the minimum width of a given element. It prevents the used value of the width property from becoming smaller than the value specified for min-width.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/min-width">MDN</a>
   */
  object minWidth extends TypedAttr_MinLength {
    override protected def attr2 = Attr.real("min-width", _)
  }

  /**
   * The mix-blend-mode CSS property describes how an element content should blend with the content of the element that is below it and the element's background.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/mix-blend-mode">MDN</a>
   */
  final val mixBlendMode = Attr.real("mix-blend-mode")

  /**
   * The object-fit CSS property specifies how the contents of a replaced element should be fitted to the box established by its used height and width.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/object-fit">MDN</a>
   */
  object objectFit extends TypedAttrBase {
    override val attr = Attr.real("object-fit", Transform keys CanIUse.objectFit)
    @inline def contain   = av(L.contain)
    @inline def cover     = av(L.cover)
    @inline def fill      = av(L.fill)
    @inline def none      = avl(LT.none)
    @inline def scaleDown = av(L.scaleDown)
  }

  /**
   * The object-position property determines the alignment of the replaced element inside its box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/object-position">MDN</a>
   */
  final val objectPosition = Attr.real("object-position")

  /**
   * The opacity CSS property specifies the transparency of an element, that is, the degree to which the background behind the element is overlaid.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/opacity">MDN</a>
   */
  object opacity extends TypedAttrT1[Number] {
    override val attr = Attr.real("opacity", Transform keys CanIUse.opacity)
  }

  /**
   * The CSS order property specifies the order used to lay out flex items in their flex container. Elements are laid out by ascending order of the order value. Elements with the same order value are laid out in the order they appear in the source code.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/order">MDN</a>
   */
  object order extends TypedAttrT1[Integer] {
    override val attr = Attr.real("order", Transform keys CanIUse.flexbox)
  }

  /**
   * The orphans CSS property refers to the minimum number of lines in a block container that must be left at the bottom of the page. This property is normally used to control how page breaks occur.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/orphans">MDN</a>
   */
  object orphans extends TypedAttrT1[Integer] {
    override val attr = Attr.real("orphans")
  }

  /**
   * The outline-color CSS property sets the color of the outline of an element. An outline is a line that is drawn around elements, outside the border edge, to make the element stand out.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/outline-color">MDN</a>
   */
  object outlineColor extends TypedAttr_Color {
    override val attr = Attr.real("outline-color", Transform keys CanIUse.outline)
    @inline def invert = av(L.invert)
  }

  /**
   * The outline-offset CSS property is used to set space between an outline and the edge or border of an element. An outline is a line that is drawn around elements, outside the border edge.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/outline-offset">MDN</a>
   */
  object outlineOffset extends TypedAttrT1[Len] with ZeroLit {
    override val attr = Attr.real("outline-offset", Transform keys CanIUse.outline)
  }

  /**
   * The outline-style CSS property is used to set the style of the outline of an element. An outline is a line that is drawn around elements, outside the border edge, to make the element stand out.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/outline-style">MDN</a>
   */
  object outlineStyle extends TypedAttr_BrStyle {
    override val attr = Attr.real("outline-style", Transform keys CanIUse.outline)
    @inline def auto = avl(LT.auto)
  }

  /**
   * The outline-width CSS property is used to set the width of the outline of an element. An outline is a line that is drawn around elements, outside the border edge, to make the element stand out:
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/outline-width">MDN</a>
   */
  object outlineWidth extends TypedAttr_BrWidth {
    override val attr = Attr.real("outline-width", Transform keys CanIUse.outline)
  }

  /**
   * The overflow CSS property specifies whether to clip content, render scrollbars or just display content when it overflows its block level container.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/overflow">MDN</a>
   */
  object overflow extends TypedAttrBase {
    override val attr = Attr.real("overflow")
    @inline def auto    = avl(LT.auto)
    @inline def hidden  = avl(LT.hidden)
    @inline def scroll  = av(L.scroll)
    @inline def visible = av(L.visible)
  }

  /**
   * REDIRECT https://developer.mozilla.org/en-US/docs/CSS/word-wrap
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/overflow-wrap">MDN</a>
   */
  final val overflowWrap = Attr.real("overflow-wrap")

  /**
   * The overflow-x CSS property specifies whether to clip content, render a scroll bar or display overflow content of a block-level element, when it overflows at the left and right edges.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/overflow-x">MDN</a>
   */
  object overflowX extends TypedAttrBase {
    override val attr = Attr.real("overflow-x")
    @inline def auto    = avl(LT.auto)
    @inline def hidden  = avl(LT.hidden)
    @inline def scroll  = av(L.scroll)
    @inline def visible = av(L.visible)
  }

  /**
   * The overflow-y CSS property specifies whether to clip content, render a scroll bar, or display overflow content of a block-level element, when it overflows at the top and bottom edges.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/overflow-y">MDN</a>
   */
  object overflowY extends TypedAttrBase {
    override val attr = Attr.real("overflow-y")
    @inline def auto    = avl(LT.auto)
    @inline def hidden  = avl(LT.hidden)
    @inline def scroll  = av(L.scroll)
    @inline def visible = av(L.visible)
  }

  /**
   * The padding-bottom CSS property of an element sets the height of the padding area at the bottom of an element. The padding area is the space between the content of the element and it's border. Contrary to margin-bottom values, negative values of padding-bottom are invalid.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/padding-bottom">MDN</a>
   */
  object paddingBottom extends TypedAttrT1[LenPct] with ZeroLit {
    override val attr = Attr.real("padding-bottom")
  }

  /**
   * The padding-left CSS property of an element sets the padding space required on the left side of an element. The padding area is the space between the content of the element and it's border. A negative value is not allowed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/padding-left">MDN</a>
   */
  object paddingLeft extends TypedAttrT1[LenPct] with ZeroLit {
    override val attr = Attr.real("padding-left")
  }

  /**
   * The padding-right CSS property of an element sets the padding space required on the right side of an element. The padding area is the space between the content of the element and its border. Negative values are not allowed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/padding-right">MDN</a>
   */
  object paddingRight extends TypedAttrT1[LenPct] with ZeroLit {
    override val attr = Attr.real("padding-right")
  }

  /**
   * The padding-top CSS property of an element sets the padding space required on the top of an element. The padding area is the space between the content of the element and its border. Contrary to margin-top values, negative values of padding-top are invalid.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/padding-top">MDN</a>
   */
  object paddingTop extends TypedAttrT1[LenPct] with ZeroLit {
    override val attr = Attr.real("padding-top")
  }

  /**
   * The page-break-after CSS property adjusts page breaks after the current element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/page-break-after">MDN</a>
   */
  object pageBreakAfter extends TypedAttrBase {
    override val attr = Attr.real("page-break-after", Transform keys CanIUse.pageBreak)
    @inline def always = av(L.always)
    @inline def auto   = avl(LT.auto)
    @inline def avoid  = av(L.avoid)
    @inline def left   = av(L.left)
    @inline def right  = av(L.right)
  }

  /**
   * The page-break-before CSS property adjusts page breaks before the current element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/page-break-before">MDN</a>
   */
  object pageBreakBefore extends TypedAttrBase {
    override val attr = Attr.real("page-break-before", Transform keys CanIUse.pageBreak)
    @inline def always = av(L.always)
    @inline def auto   = avl(LT.auto)
    @inline def avoid  = av(L.avoid)
    @inline def left   = av(L.left)
    @inline def right  = av(L.right)
  }

  /**
   * The page-break-inside CSS property adjusts page breaks inside the current element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/page-break-inside">MDN</a>
   */
  object pageBreakInside extends TypedAttrBase {
    override val attr = Attr.real("page-break-inside", Transform keys CanIUse.pageBreak)
    @inline def auto  = avl(LT.auto)
    @inline def avoid = av(L.avoid)
  }

  /**
   * The perspective CSS property determines the distance between the z=0 plane and the user in order to give to the 3D-positioned element some perspective. Each 3D element with z>0 becomes larger; each 3D-element with z&lt;0 becomes smaller. The strength of the effect is determined by the value of this property.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/perspective">MDN</a>
   */
  object perspective extends TypedAttrT1[Len] with ZeroLit {
    override val attr = Attr.real("perspective", Transform keys CanIUse.transforms)
    @inline def none = avl(LT.none)
  }

  /**
   * The perspective-origin CSS property determines the position the viewer is looking at. It is used as the vanishing point by the perspective property.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/perspective-origin">MDN</a>
   */
  final val perspectiveOrigin = Attr.real("perspective-origin", Transform keys CanIUse.transforms)

  /**
   * The CSS property pointer-events allows authors to control under what circumstances (if any) a particular graphic element can become the target of mouse events. When this property is unspecified, the same characteristics of the visiblePainted value apply to SVG content.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/pointer-events">MDN</a>
   */
  final val pointerEvents = Attr.real("pointer-events")

  /**
   * The position CSS property chooses alternative rules for positioning elements, designed to be useful for scripted animation effects.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/position">MDN</a>
   */
  object position extends TypedAttrBase {
    override val attr = Attr.real("position")
    @inline def absolute = av(L.absolute)
    @inline def fixed    = av(L.fixed)
    @inline def relative = av(L.relative)
    @inline def static   = av(L.static)
    @inline def sticky   = av(L.sticky)
  }

  /**
   * The quotes CSS property indicates how user agents should render quotation marks.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/quotes">MDN</a>
   */
  object quotes extends TypedAttrBase with QuotesOps {
    override val attr = Attr.real("quotes")
    @inline def none = avl(LT.none)

    override protected def next(v: Value): Accum = new Accum(v)
    final class Accum(v: Value) extends ToAV with QuotesOps {
      override def av: AV = AV(attr, v)
      override protected def next(v: Value): Accum = new Accum(this.v + " " + v)
    }
  }
  trait QuotesOps {
    protected def next(v: Value): quotes.Accum
    final def apply(openQuote: String, closeQuote: String) =
      next(mkStrings(openQuote, " ", closeQuote))
  }

  /**
   * Controls whether the last region in a chain displays additional 'overset' content according its default overflow property, or	if it displays a fragment of content as if it were flowing into a subsequent region.
   *
   * @see <a href="https://docs.webplatform.org/wiki/css/properties/region-fragment">WPD</a>
   */
  final val regionFragment = Attr.real("region-fragment", Transform keys CanIUse.regions)

  /**
   * The resize CSS property lets you control the resizability of an element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/resize">MDN</a>
   */
  object resize extends TypedAttrBase {
    override val attr = Attr.real("resize",
      Transform.keys(CanIUse.resize) *
      Transform.values(CanIUse.logicalProps)(L.block, L.inline))
    @inline def block      = av(L.block)
    @inline def both       = av(L.both)
    @inline def horizontal = av(L.horizontal)
    @inline def inline     = av(L.inline)
    @inline def none       = avl(LT.none)
    @inline def vertical   = av(L.vertical)
  }

  /**
   * The right CSS property specifies part of the position of positioned elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/right">MDN</a>
   */
  object right extends TypedAttr_LenPctAuto {
    override val attr = Attr.real("right")
  }

  /**
   * The ruby-align CSS property defines the distribution of the different ruby elements over the base.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/ruby-align">MDN</a>
   */
  object rubyAlign extends TypedAttrBase {
    override val attr = Attr.real("ruby-align")
    @inline def center       = av(L.center)
    @inline def spaceAround  = av(L.spaceAround)
    @inline def spaceBetween = av(L.spaceBetween)
    @inline def start        = avl(L.start)
  }

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/ruby-merge">MDN</a>
   */
  final val rubyMerge = Attr.real("ruby-merge")

  /**
   * The ruby-position CSS property defines the position of a ruby element relatives to its base element. It can be position over the element (over), under it (under), or between the characters, on their right side (inter-character).
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/ruby-position">MDN</a>
   */
  object rubyPosition extends TypedAttrBase {
    override val attr = Attr.real("ruby-position")
    @inline def interCharacter = av(L.interCharacter)
    @inline def over           = av(L.over)
    @inline def under          = av(L.under)
  }

  /**
   * The scroll-behavior CSS property specifies the scrolling behavior for a scrolling box, when scrolling happens due to navigation or CSSOM scrolling APIs. Any other scrolls, e.g. those that are performed by the user, are not affected by this property. When this property is specified on the root element, it applies to the viewport instead.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/scroll-behavior">MDN</a>
   */
  object scrollBehavior extends TypedAttrBase {
    override val attr = Attr.real("scroll-behavior", Transform keys CanIUse.scrollBehavior)
    @inline def auto   = avl(LT.auto)
    @inline def smooth = av(L.smooth)
  }

  /**
   * The shape-image-threshold CSS property defines the alpha channel threshold used to extract the shape using an image as the value for shape-outside. A value of 0.5 means that the shape will enclose all the pixels that are more than 50% opaque.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/shape-image-threshold">MDN</a>
   */
  object shapeImageThreshold extends TypedAttrT1[Number] {
    override val attr = Attr.real("shape-image-threshold", Transform keys CanIUse.shapes)
  }

  /**
   * The shape-margin CSS property adds a margin to shape-outside.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/shape-margin">MDN</a>
   */
  object shapeMargin extends TypedAttrT1[LenPct] with ZeroLit {
    override val attr = Attr.real("shape-margin", Transform keys CanIUse.shapes)
  }

  /**
   * The shape-outside CSS property uses shape values to define the float area for a float and will cause inline content to wrap around the shape instead of the float's bounding box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/shape-outside">MDN</a>
   */
  final val shapeOutside = Attr.real("shape-outside", Transform keys CanIUse.shapes)

  /**
   * The table-layout CSS property defines the algorithm to be used to layout the table cells, rows, and columns.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/table-layout">MDN</a>
   */
  object tableLayout extends TypedAttrBase {
    override val attr = Attr.real("table-layout")
    @inline def auto  = avl(LT.auto)
    @inline def fixed = av(L.fixed)
  }

  /**
   * The tab-size CSS property is used to customize the width of a tab (U+0009) character.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/tab-size">MDN</a>
   */
  final val tabSize = Attr.real("tab-size", Transform keys CanIUse.css3Tabsize)

  /**
   * The text-align CSS property describes how inline content like text is aligned in its parent block element. text-align does not control the alignment of block elements itself, only their inline content.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-align">MDN</a>
   */
  object textAlign extends TypedAttrBase {
    override val attr = Attr.real("text-align",
      Transform.values(CanIUse.logicalProps)(L.start.value, L.end.value))
    @inline def center      = av(L.center)
    @inline def end         = avl(L.end)
    @inline def justify     = av(L.justify)
    @inline def left        = av(L.left)
    @inline def matchParent = av(L.matchParent)
    @inline def right       = av(L.right)
    @inline def start       = avl(L.start)
    @inline def startEnd    = av(L.startEnd)
  }

  /**
   * The text-align-last CSS property describes how the last line of a block or a line, right before a forced line break, is aligned.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-align-last">MDN</a>
   */
  object textAlignLast extends TypedAttrBase {
    override val attr = Attr.real("text-align-last", Transform keys CanIUse.textAlignLast)
    @inline def auto    = avl(LT.auto)
    @inline def center  = av(L.center)
    @inline def end     = avl(L.end)
    @inline def justify = av(L.justify)
    @inline def left    = av(L.left)
    @inline def right   = av(L.right)
    @inline def start   = avl(L.start)
  }

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-combine-upright">MDN</a>
   */
  final val textCombineUpright = Attr.real("text-combine-upright")

  /**
   * The text-decoration-color CSS property sets the color used when drawing underlines, overlines, or strike-throughs specified by text-decoration-line. This is the preferred way to color these text decorations, rather than using combinations of other HTML elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-decoration-color">MDN</a>
   */
  object textDecorationColor extends TypedAttr_Color {
    override val attr = Attr.real("text-decoration-color", Transform keys CanIUse.textDecoration)
  }

  /**
   * The text-decoration-line CSS property sets what kind of line decorations are added to an element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-decoration-line">MDN</a>
   */
  object textDecorationLine extends TypedAttrBase with TextDecorationLineOps {
    override val attr = Attr.real("text-decoration-line", Transform keys CanIUse.textDecoration)
    @inline def none = avl(LT.none)

    override protected def next(v: Value): Accum = new Accum(v)
    final class Accum(v: Value) extends ToAV with TextDecorationLineOps {
      override def av: AV = AV(attr, v)
      override protected def next(v: Value): Accum = new Accum(this.v + " " + v)
    }
  }
  trait TextDecorationLineOps {
    protected def next(v: Value): textDecorationLine.Accum
    final def underline   = next(L.underline)
    final def overline    = next(L.overline)
    final def lineThrough = next(L.lineThrough)
    final def blink       = next(L.blink)
  }

  /**
   * The text-decoration-style CSS property defines the style of the lines specified by text-decoration-line. The style applies to all lines, there is no way to define different style for each of the line defined by text-decoration-line.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-decoration-style">MDN</a>
   */
  object textDecorationStyle extends TypedAttrBase {
    override val attr = Attr.real("text-decoration-style", Transform keys CanIUse.textDecoration)
    @inline def dashed = avl(LT.dashed)
    @inline def dotted = avl(LT.dotted)
    @inline def double = avl(LT.double)
    @inline def solid  = avl(LT.solid)
    @inline def wavy   = av(L.wavy)
  }

  /**
   * The text-emphasis-color property specifies the foreground color of the emphasis marks.
   *
   * @see <a href="https://docs.webplatform.org/wiki/css/properties/text-emphasis-color">WPD</a>
   */
  final val textEmphasisColor = Attr.real("text-emphasis-color", Transform keys CanIUse.textEmphasis)

  /**
   * This property describes where emphasis marks are drawn at.
   *
   * @see <a href="http://www.w3.org/TR/css-text-decor-3/#text-emphasis-position">w3.org</a>
   */
  final val textEmphasisPosition = Attr.real("text-emphasis-position", Transform keys CanIUse.textEmphasis)

  /**
   * The text-emphasis-style property applies special emphasis marks to an element's text.
   *
   * @see <a href="https://docs.webplatform.org/wiki/css/properties/text-emphasis-style">WPD</a>
   */
  final val textEmphasisStyle = Attr.real("text-emphasis-style", Transform keys CanIUse.textEmphasis)

  /**
   * The text-indent CSS property specifies how much horizontal space should be left before the beginning of the first line of the text content of an element. Horizontal spacing is with respect to the left (or right, for right-to-left layout) edge of the containing block element's box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-indent">MDN</a>
   */
  object textIndent extends TypedAttrBase with ZeroLit {
    override val attr = Attr.real("text-indent") // TODO There should be a CanIUse for hanging|each-line
    @inline def apply(v: ValueT[LenPct])                                         : AV = av(v.value)
    @inline def apply(v: ValueT[LenPct], h: LT.hanging.type)                     : AV = av(s"${v.value} ${h.value}")
    @inline def apply(v: ValueT[LenPct], h: LT.eachLine.type)                    : AV = av(s"${v.value} ${h.value}")
    @inline def apply(v: ValueT[LenPct], h: LT.hanging.type, e: LT.eachLine.type): AV = av(s"${v.value} ${h.value} ${e.value}")
  }

  /**
   * The text-orientation CSS property defines the orientation of the text in a line. This property only has an effect in vertical mode, that is when writing-mode is not horizontal-tb. It is useful to control the display of writing in languages using vertical script, but also to deal with vertical table headers.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-orientation">MDN</a>
   */
  object textOrientation extends TypedAttrBase {
    override val attr = Attr.real("text-orientation")
    @inline def mixed               = av(L.mixed)
    @inline def sideways            = av(L.sideways)
    @inline def sidewaysLeft        = av(L.sidewaysLeft)
    @inline def sidewaysRight       = av(L.sidewaysRight)
    @inline def upright             = av(L.upright)
    @inline def useGlyphOrientation = av(L.useGlyphOrientation)
  }

  /**
   * The text-overflow CSS property determines how overflowed content that is not displayed is signaled to the users. It can be clipped, or display an ellipsis ('…', U+2026 Horizontal Ellipsis) or a Web author-defined string.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-overflow">MDN</a>
   */
  final val textOverflow = Attr.real("text-overflow", Transform keys CanIUse.textOverflow)

  /**
   * The text-rendering CSS property provides information to the rendering engine about what to optimize for when rendering text. The browser makes trade-offs among speed, legibility, and geometric precision. The text-rendering property is an SVG property that is not defined in any CSS standard. However, Gecko and WebKit browsers let you apply this property to HTML and XML content on Windows, Mac OS X and Linux.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-rendering">MDN</a>
   */
  final val textRendering = Attr.real("text-rendering")

  /**
   * The text-shadow CSS property adds shadows to text. It accepts a comma-separated list of shadows to be applied to the text and text-decorations of the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-shadow">MDN</a>
   */
  final val textShadow = Attr.real("text-shadow", Transform keys CanIUse.textshadow)

  /**
   * On mobile devices, the text-size-adjust CSS property allows Web authors to control if and how the text-inflating algorithm is applied to the textual content of the element it is applied to.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-size-adjust">MDN</a>
   */
  object textSizeAdjust extends TypedAttrT1[Pct] {
    override val attr = Attr.real("text-size-adjust", Transform keys CanIUse.textSizeAdjust)
    @inline def auto = avl(LT.auto)
    @inline def none = avl(LT.none)
  }

  /**
   * Apple extension. Specifies the color of the outline (stroke) of text.
   *
   * @see <a href="https://developer.apple.com/library/safari/documentation/AppleApplications/Reference/SafariCSSRef/Articles/StandardCSSProperties.html#//apple_ref/doc/uid/TP30001266--webkit-text-stroke-color">Safari CSS Reference</a>
   */
  final val textStrokeColor = Attr.real("text-stroke-color", Transform keys CanIUse.textStroke)

  /**
   * Apple extension. Specifies the width for the text outline.
   *
   * @see <a href="https://developer.apple.com/library/safari/documentation/AppleApplications/Reference/SafariCSSRef/Articles/StandardCSSProperties.html#//apple_ref/doc/uid/TP30001266--webkit-text-stroke-width">Safari CSS Reference</a>
   */
  final val textStrokeWidth = Attr.real("text-stroke-width", Transform keys CanIUse.textStroke)

  /**
   * The text-transform CSS property specifies how to capitalize an element's text. It can be used to make text appear in all-uppercase or all-lowercase, or with each word capitalized.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-transform">MDN</a>
   */
  object textTransform extends TypedAttrBase {
    override val attr = Attr.real("text-transform")
    @inline def capitalize = av(L.capitalize)
    @inline def fullWidth  = av(L.fullWidth)
    @inline def lowercase  = av(L.lowercase)
    @inline def none       = avl(LT.none)
    @inline def uppercase  = av(L.uppercase)
  }

  /**
   * The CSS text-underline-position property specifies the position of the underline which is set using the text-decoration property underline value.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-underline-position">MDN</a>
   */
  object textUnderlinePosition extends TypedAttrBase {
    override val attr = Attr.real("text-underline-position")
    @inline def auto       = avl(LT.auto)
    @inline def under      = av(L.under)
    @inline def left       = av(L.left)
    @inline def right      = av(L.right)
    @inline def underLeft  = av("under left")
    @inline def underRight = av("under right")
  }

  /**
   * The top CSS property specifies part of the position of positioned elements. It has no effect on non-positioned elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/top">MDN</a>
   */
  object top extends TypedAttr_LenPctAuto {
    override val attr = Attr.real("top")
  }

  /**
   * The touch-action CSS property specifies whether and how a given region can be manipulated by the user (for instance, by panning or zooming).
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/touch-action">MDN</a>
   */
  final val touchAction = Attr.real("touch-action", Transform keys CanIUse.touchAction)

  /**
   * The CSS transform property lets you modify the coordinate space of the CSS visual formatting model. Using it, elements can be translated, rotated, scaled, and skewed according to the values set.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/transform">MDN</a>
   */
  final val transform = Attr.real("transform", Transform keys CanIUse.transforms)

  /**
   * The transform-origin CSS property lets you modify the origin for transformations of an element. For example, the transform-origin of the rotate() function is the centre of rotation. (This property is applied by first translating the element by the negated value of the property, then applying the element's transform, then translating by the property value.)
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/transform-origin">MDN</a>
   */
  final val transformOrigin = Attr.real("transform-origin", Transform keys CanIUse.transforms)

  /**
   * The transform-style CSS property determines if the children of the element are positioned in the 3D-space or are flattened in the plane of the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/transform-style">MDN</a>
   */
  object transformStyle extends TypedAttrBase {
    override val attr = Attr.real("transform-style", Transform keys CanIUse.transforms)
    @inline def flat        = av(L.flat)
    @inline def preserve3D = av(L.preserve3D)
  }

  /**
   * The transition-delay CSS property specifies the amount of time to wait between a change being requested to a property that is to be transitioned and the start of the transition effect.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/transition-delay">MDN</a>
   */
  object transitionDelay extends TypedAttrTN[Time](",") {
    override val attr = Attr.real("transition-delay", Transform keys CanIUse.transitions)
  }

  /**
   * The transition-duration CSS property specifies the number of seconds or milliseconds a transition animation should take to complete. By default, the value is 0s, meaning that no animation will occur.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/transition-duration">MDN</a>
   */
  object transitionDuration extends TypedAttrTN[Time](",") {
    override val attr = Attr.real("transition-duration", Transform keys CanIUse.transitions)
  }

  /**
   * The transition-property CSS property is used to specify the names of CSS properties to which a transition effect should be applied.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/transition-property">MDN</a>
   */
  final val transitionProperty = Attr.real("transition-property", Transform keys CanIUse.transitions)

  /**
   * The CSS transition-timing-function property is used to describe how the intermediate values of the CSS properties being affected by a transition effect are calculated. This in essence lets you establish an acceleration curve, so that the speed of the transition can vary over its duration.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/transition-timing-function">MDN</a>
   */
  object transitionTimingFunction extends TypedAttrBase {
    override val attr: Attr = Attr.real("transition-timing-function", Transform keys CanIUse.transitions)
    @inline def cubicBezier(x1: Double, y1: Double, x2: Double, y2: Double)   = avl(new LT.cubicBezier(x1, y1, x2, y2))
    @inline def steps(steps: Int, direction: LT.TimingFunctionDirection)      = avl(new LT.steps(steps, direction))
    @inline def linear                                                        = avl(LT.linear)
    @inline def ease                                                          = avl(LT.ease)
    @inline def easeIn                                                        = avl(LT.easeIn)
    @inline def easeInOut                                                     = avl(LT.easeInOut)
    @inline def easeOut                                                       = avl(LT.easeOut)
    @inline def stepStart                                                     = avl(LT.stepStart)
    @inline def stepEnd                                                       = avl(LT.stepEnd)
  }

  /**
   * The unicode-bidi CSS property together with the direction property relates to the handling of bidirectional text in a document. For example, if a block of text contains both left-to-right and right-to-left text then the user-agent uses a complex Unicode algorithm to decide how to display the text. This property overrides this algorithm and allows the developer to control the text embedding.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/unicode-bidi">MDN</a>
   */
  object unicodeBidi extends TypedAttrBase {
    override val attr = Attr.real("unicode-bidi")
    @inline def bidiOverride    = av(L.bidiOverride)
    @inline def embed           = av(L.embed)
    @inline def isolate         = av(L.isolate)
    @inline def isolateOverride = av(L.isolateOverride)
    @inline def normal          = av(L.normal)
    @inline def plaintext       = av(L.plaintext)
  }

  /**
   * The unicode-range CSS descriptor sets the specific range of characters to be downloaded from a font defined by @font-face and made available for use on the current page.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/unicode-range">MDN</a>
   */
  final val unicodeRange = Attr.real("unicode-range")

  /**
   * Controls the actual Selection operation. This doesn't have any effect on content loaded as chrome, except in textboxes. A similar property 'user-focus' was proposed in early drafts of a predecessor of css3-ui but was rejected by the working group.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/user-select">MDN</a>
   */
  final val userSelect = Attr.real("user-select", Transform keys CanIUse.userSelectNone)

  /**
   * The vertical-align CSS property specifies the vertical alignment of an inline or table-cell box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/vertical-align">MDN</a>
   */
  object verticalAlign extends TypedAttrT1[LenPct] with ZeroLit {
    override val attr = Attr.real("vertical-align")
    @inline def baseline    = av(L.baseline)
    @inline def bottom      = av(L.bottom)
    @inline def middle      = av(L.middle)
    @inline def sub         = av(L.sub)
    @inline def super_      = av(L.super_)
    @inline def textBottom = av(L.textBottom)
    @inline def textTop    = av(L.textTop)
    @inline def top         = av(L.top)
  }

  /**
   * The visibility CSS property has two purposes:
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/visibility">MDN</a>
   */
  object visibility extends TypedAttrBase {
    override val attr = Attr.real("visibility")
    @inline def collapse = av(L.collapse)
    @inline def hidden   = avl(LT.hidden)
    @inline def visible  = av(L.visible)
  }

  /**
   * The white-space CSS property is used to to describe how white spaces inside the element is handled.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/white-space">MDN</a>
   */
  object whiteSpace extends TypedAttrBase {
    override val attr = Attr.real("white-space")
    @inline def normal   = av(L.normal)
    @inline def nowrap   = av(L.nowrap)
    @inline def pre      = av(L.pre)
    @inline def preLine = av(L.preLine)
    @inline def preWrap = av(L.preWrap)
  }

  /**
   * The widows CSS property defines how many minimum lines must be left on top of a new page, on a paged media. In typography, a widow is the last line of a paragraph appearing alone at the top of a page. Setting the widows property allows to prevent widows to be left.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/widows">MDN</a>
   */
  object widows extends TypedAttrT1[Integer] {
    override val attr = Attr.real("widows")
  }

  /**
   * The width CSS property specifies the width of the content area of an element. The content area is inside the padding, border, and margin of the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/width">MDN</a>
   */
  object width extends TypedAttr_Length {
    override protected def attr2 = Attr.real("width", _)
  }

  /**
   * The will-change CSS property provides a way for authors to hint browsers about the kind of changes to be expected on an element, so that the browser can setup appropriate optimizations ahead of time before the element is actually changed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/will-change">MDN</a>
   */
  final val willChange = Attr.real("will-change")

  /**
   * The word-break CSS property is used to specify how (or if) to break lines within words.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/word-break">MDN</a>
   */
  object wordBreak extends TypedAttrBase {
    override val attr = Attr.real("word-break", Transform keys CanIUse.wordBreak)
    @inline def breakAll = av(L.breakAll)
    @inline def keepAll  = av(L.keepAll)
    @inline def normal    = av(L.normal)
  }

  /**
   * The word-spacing CSS property specifies spacing behavior between tags and words.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/word-spacing">MDN</a>
   */
  object wordSpacing extends TypedAttrT1[Len] with ZeroLit {
    override val attr = Attr.real("word-spacing")
    @inline def normal = av(L.normal)
  }

  /**
   * The word-wrap CSS property is used to specify whether or not the browser may break lines within words in order to prevent overflow (in other words, force wrapping) when an otherwise unbreakable string is too long to fit in its containing box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/word-wrap">MDN</a>
   */
  object wordWrap extends TypedAttrBase {
    override val attr = Attr.real("word-wrap", Transform keys CanIUse.wordwrap)
    @inline def breakWord = av(L.breakWord)
    @inline def normal     = av(L.normal)
  }

  /**
   * The writing-mode CSS property defines whether lines of text are laid out horizontally or vertically and the direction in which blocks progress.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/writing-mode">MDN</a>
   */
  object writingMode extends TypedAttrBase {
    override val attr = Attr.real("writing-mode", Transform keys CanIUse.writingMode)
    @inline def horizontalTB = av(L.horizontalTB)
    @inline def verticalLR   = av(L.verticalLR)
    @inline def verticalRL   = av(L.verticalRL)
  }

  /**
   * The z-index CSS property specifies the z-order of an element and its descendants. When elements overlap, z-order determines which one covers the other. An element with a larger z-index generally covers an element with a lower one.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/z-index">MDN</a>
   */
  object zIndex extends TypedAttrT1[Integer] {
    override val attr = Attr.real("z-index")
    @inline def auto = avl(LT.auto)
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Alias Attributes
  // -------------------------------------------------------------------------------------------------------------------

  /**
   * The CSS all shorthand property resets all properties, but unicode-bidi and direction to their initial or inherited value.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/all">MDN</a>
   */
  object all extends TypedAttrBase {
    override val attr = new AliasAttr("all", Attr.genSimple("all"), Need(Attrs.valuesForAllAttr))
  }

  /**
   * The animation CSS property is a shorthand property for animation-name, animation-duration, animation-timing-function, animation-delay, animation-iteration-count, animation-direction, animation-fill-mode and animation-play-state.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/animation">MDN</a>
   */
  final val animation = Attr.alias("animation", Transform keys CanIUse.animation)(_(
    animationName, animationDuration, animationTimingFunction, animationDelay, animationIterationCount,
    animationDirection, animationFillMode, animationPlayState))

  /**
   * The background CSS property is a shorthand for setting the individual background values in a single place in the style sheet. background can be used to set the values for one or more of: background-clip, background-color, background-image, background-origin, background-position, background-repeat, background-size, and background-attachment.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background">MDN</a>
   */
  final val background = Attr.alias("background", CanIUse2.backgroundImageTransforms)(_(
    backgroundClip, backgroundColor, backgroundImage, backgroundOrigin, backgroundPosition, backgroundRepeat,
    backgroundSize, backgroundAttachment))

  /**
   * The block-size CSS property defines the horizontal or vertical size of an element's block depending on it's writing mode. It corresponds to the width or the height property depending on the value defined for writing-mode.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/block-size">MDN</a>
   */
  object blockSize extends TypedAttr_Length {
    override protected def attr2 = Attr.alias("block-size", _)(_(height, width))
  }

  /**
   * The border CSS property is a shorthand property for setting the individual border property values in a single place in the style sheet. border can be used to set the values for one or more of: border-width, border-style, border-color.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border">MDN</a>
   */
  object border extends TypedAttr_BrWidthStyleColour {
    override val attr = Attr.alias("border")(_(
      borderWidth, borderStyle, borderColor))
  }

  /**
   * The border-block-end CSS property is a shorthand property for setting the individual logical block end border property values in a single place in the style sheet. border-block-end can be used to set the values for one or more of: border-block-end-width, border-block-end-style, border-block-end-color. It maps to a physical border depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top, border-right, border-bottom, or border-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-end">MDN</a>
   */
  object borderBlockEnd extends TypedAttr_BrWidthStyleColour {
    override val attr = Attr.alias("border-block-end")(_(
      borderBlockEndWidth, borderBlockEndStyle, borderBlockEndColor))
  }

  /**
   * The border-block-end-color CSS property defines the color of the logical block end border of an element, which maps to a physical border color depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-color, border-right-color, border-bottom-color, or border-left-color property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-end-color">MDN</a>
   */
  object borderBlockEndColor extends TypedAttr_Color {
    override val attr = Attr.alias("border-block-end-color")(_(
      borderLeftColor, borderTopColor, borderRightColor, borderBottomColor))
  }

  /**
   * The border-block-end-style CSS property defines the style of the logical block end border of an element, which maps to a physical border style depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-style, border-right-style, border-bottom-style, or border-left-style property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-end-style">MDN</a>
   */
  object borderBlockEndStyle extends TypedAttr_BrStyle {
    override val attr = Attr.alias("border-block-end-style")(_(
      borderLeftStyle, borderTopStyle, borderRightStyle, borderBottomStyle))
  }

  /**
   * The border-block-end-width CSS property defines the width of the logical block end border of an element, which maps to a physical border width depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-width, border-right-width, border-bottom-width, or border-left-width property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-end-width">MDN</a>
   */
  object borderBlockEndWidth extends TypedAttr_BrWidth {
    override val attr = Attr.alias("border-block-end-width")(_(
      borderLeftWidth, borderTopWidth, borderRightWidth, borderBottomWidth))
  }

  /**
   * The border-block-start CSS property is a shorthand property for setting the individual logical block start border property values in a single place in the style sheet. border-block-start can be used to set the values for one or more of: border-block-start-width, border-block-start-style, border-block-start-color. It maps to a physical border depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top, border-right, border-bottom, or border-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-start">MDN</a>
   */
  object borderBlockStart extends TypedAttr_BrWidthStyleColour {
    override val attr = Attr.alias("border-block-start")(_(
      borderBlockStartWidth, borderBlockStartStyle, borderBlockStartColor))
  }

  /**
   * The border-block-start-color CSS property defines the color of the logical block start border of an element, which maps to a physical border color depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-color, border-right-color, border-bottom-color, or border-left-color property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-start-color">MDN</a>
   */
  object borderBlockStartColor extends TypedAttr_Color {
    override val attr = Attr.alias("border-block-start-color")(_(
      borderLeftColor, borderTopColor, borderRightColor, borderBottomColor))
  }

  /**
   * The border-block-start-style CSS property defines the style of the logical block start border of an element, which maps to a physical border style depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-style, border-right-style, border-bottom-style, or border-left-style property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-start-style">MDN</a>
   */
  object borderBlockStartStyle extends TypedAttr_BrStyle {
    override val attr = Attr.alias("border-block-start-style")(_(
      borderLeftStyle, borderTopStyle, borderRightStyle, borderBottomStyle))
  }

  /**
   * The border-block-start-width CSS property defines the width of the logical block start border of an element, which maps to a physical border width depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-width, border-right-width, border-bottom-width, or border-left-width property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-start-width">MDN</a>
   */
  object borderBlockStartWidth extends TypedAttr_BrWidth {
    override val attr = Attr.alias("border-block-start-width")(_(
      borderLeftWidth, borderTopWidth, borderRightWidth, borderBottomWidth))
  }

  /**
   * The border-bottom CSS property is a shorthand that sets the values of border-bottom-color, border-bottom-style, and border-bottom-width. These properties describe the bottom border of elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-bottom">MDN</a>
   */
  object borderBottom extends TypedAttr_BrWidthStyleColour {
    override val attr = Attr.alias("border-bottom")(_(
      borderBottomColor, borderBottomStyle, borderBottomWidth))
  }

  /**
   * The border-color CSS property is a shorthand for setting the color of the four sides of an element's border: border-top-color, border-right-color, border-bottom-color, border-left-color
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-color">MDN</a>
   */
  object borderColor extends TypedAttrT4Edges[Color](" ") with ColourOps {
    override val attr = Attr.alias("border-color")(_(
      borderTopColor, borderRightColor, borderBottomColor, borderLeftColor))
    override protected def attrT = borderTopColor
    override protected def attrR = borderRightColor
    override protected def attrB = borderBottomColor
    override protected def attrL = borderLeftColor
  }

  /**
   * The border-image CSS property allows drawing an image on the borders of elements. This makes drawing complex looking widgets much simpler than it has been and removes the need for nine boxes in some cases.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-image">MDN</a>
   */
  final val borderImage = Attr.alias("border-image", Transform keys CanIUse.borderImage)(_(
    borderImageOutset, borderImageRepeat, borderImageSlice, borderImageSource, borderImageWidth))

  /**
   * The border-inline-end CSS property is a shorthand property for setting the individual logical inline end border property values in a single place in the style sheet. border-inline-end can be used to set the values for one or more of: border-inline-end-width, border-inline-end-style, border-inline-end-color. It maps to a physical border depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top, border-right, border-bottom, or border-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-end">MDN</a>
   */
  object borderInlineEnd extends TypedAttr_BrWidthStyleColour {
    override val attr = Attr.alias("border-inline-end")(_(
      borderInlineEndWidth, borderInlineEndStyle, borderInlineEndColor))
  }

  /**
   * The border-inline-end-color CSS property defines the color of the logical inline end border of an element, which maps to a physical border color depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-color, border-right-color, border-bottom-color, or border-left-color property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-end-color">MDN</a>
   */
  object borderInlineEndColor extends TypedAttr_Color {
    override val attr = Attr.alias("border-inline-end-color")(_(
      borderTopColor, borderRightColor, borderBottomColor, borderLeftColor))
  }

  /**
   * The border-inline-end-style CSS property defines the style of the logical inline end border of an element, which maps to a physical border style depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-style, border-right-style, border-bottom-style, or border-left-style property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-end-style">MDN</a>
   */
  object borderInlineEndStyle extends TypedAttr_BrStyle {
    override val attr = Attr.alias("border-inline-end-style")(_(
      borderTopStyle, borderRightStyle, borderBottomStyle, borderLeftStyle))
  }

  /**
   * The border-inline-end-width CSS property defines the width of the logical inline end border of an element, which maps to a physical border width depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-width, border-right-width, border-bottom-width, or border-left-width property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-end-width">MDN</a>
   */
  object borderInlineEndWidth extends TypedAttr_BrWidth {
    override val attr = Attr.alias("border-inline-end-width")(_(
      borderTopWidth, borderRightWidth, borderBottomWidth, borderLeftWidth))
  }

  /**
   * The border-inline-start CSS property is a shorthand property for setting the individual logical inline start border property values in a single place in the style sheet. border-inline-start can be used to set the values for one or more of: border-inline-start-width, border-inline-start-style, border-inline-start-color. It maps to a physical border depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top, border-right, border-bottom, or border-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-start">MDN</a>
   */
  object borderInlineStart extends TypedAttr_BrWidthStyleColour {
    override val attr = Attr.alias("border-inline-start")(_(
      borderInlineStartWidth, borderInlineStartStyle, borderInlineStartColor))
  }

  /**
   * The border-inline-start-color CSS property defines the color of the logical inline start border of an element, which maps to a physical border color depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-color, border-right-color, border-bottom-color, or border-left-color property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-start-color">MDN</a>
   */
  object borderInlineStartColor extends TypedAttr_Color {
    override val attr = Attr.alias("border-inline-start-color")(_(
      borderTopColor, borderRightColor, borderBottomColor, borderLeftColor))
  }

  /**
   * The border-inline-start-style CSS property defines the style of the logical inline start border of an element, which maps to a physical border style depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-style, border-right-style, border-bottom-style, or border-left-style property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-start-style">MDN</a>
   */
  object borderInlineStartStyle extends TypedAttr_BrStyle {
    override val attr = Attr.alias("border-inline-start-style")(_(
      borderTopStyle, borderRightStyle, borderBottomStyle, borderLeftStyle))
  }

  /**
   * The border-inline-start-width CSS property defines the width of the logical inline start border of an element, which maps to a physical border width depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-width, border-right-width, border-bottom-width, or border-left-width property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-start-width">MDN</a>
   */
  object borderInlineStartWidth extends TypedAttr_BrWidth {
    override val attr = Attr.alias("border-inline-start-width")(_(
      borderTopWidth, borderRightWidth, borderBottomWidth, borderLeftWidth))
  }

  /**
   * The border-left CSS property is a shorthand that sets the values of border-left-color, border-left-style, and border-left-width. These properties describe the left border of elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-left">MDN</a>
   */
  object borderLeft extends TypedAttr_BrWidthStyleColour {
    override val attr = Attr.alias("border-left")(_(
      borderLeftColor, borderLeftStyle, borderLeftWidth))
  }

  /**
   * The border-radius CSS property allows Web authors to define how rounded border corners are. The curve of each corner is defined using one or two radii, defining its shape: circle or ellipse.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-radius">MDN</a>
   */
  object borderRadius extends TypedAttrBase with RorderRadiusOps[BorderRadiusNext] with ZeroLit {
    override val attr = Attr.alias("border-radius", Transform keys CanIUse.borderRadius)(_(
      borderTopLeftRadius, borderTopRightRadius, borderBottomRightRadius, borderBottomLeftRadius))
    override protected def n(v: Value) = new BorderRadiusNext(v)
  }
  final class BorderRadiusNext(a: Value) extends RorderRadiusOps[AV] with ToAV {
    override def av: AV = AV(borderRadius.attr, a)
    override protected def n(b: Value) = AV(borderRadius.attr, s"$a / $b")
  }
  sealed trait RorderRadiusOps[Next] {
    protected def n(v: Value): Next
    final type V = ValueT[LenPct]
    final def apply(radius: V)                        : Next = n(radius.value)
    final def apply(tl_br : V, tr_bl: V)              : Next = n(concat(" ", tl_br, tr_bl))
    final def apply(tl    : V, tr_bl: V, br: V)       : Next = n(concat(" ", tl, tr_bl, br))
    final def apply(tl    : V, tr   : V, br: V, bl: V): Next = n(concat(" ", tl,tr,br,bl))
  }

  /**
   * The border-right CSS property is a shorthand that sets the values of border-right-color, border-right-style, and border-right-width. These properties describe the right border of elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-right">MDN</a>
   */
  object borderRight extends TypedAttr_BrWidthStyleColour {
    override val attr = Attr.alias("border-right")(_(
      borderRightColor, borderRightStyle, borderRightWidth))
  }

  /**
   * The border-style CSS property is a shorthand property for setting the line style for all four sides of the elements border.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-style">MDN</a>
   */
  object borderStyle extends TypedAttrT4Edges[BrStyle](" ") with BrStyleOps {
    override val attr = Attr.alias("border-style")(_(
      borderTopStyle, borderRightStyle, borderBottomStyle, borderLeftStyle))
    override protected def attrT = borderTopStyle
    override protected def attrR = borderRightStyle
    override protected def attrB = borderBottomStyle
    override protected def attrL = borderLeftStyle
  }

  /**
   * The border-top CSS property is a shorthand that sets the values of border-top-color, border-top-style, and border-top-width. These properties describe the top border of elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-top">MDN</a>
   */
  object borderTop extends TypedAttr_BrWidthStyleColour {
    override val attr = Attr.alias("border-top")(_(
      borderTopColor, borderTopStyle, borderTopWidth))
  }

  /**
   * The border-width CSS property sets the width of the border of a box. Using the shorthand property border is often more convenient.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-width">MDN</a>
   */
  object borderWidth extends TypedAttrT4Edges[BrWidth](" ") with BrWidthOps {
    override val attr = Attr.alias("border-width")(_(
      borderTopWidth, borderRightWidth, borderBottomWidth, borderLeftWidth))
    override protected def attrT = borderTopWidth
    override protected def attrR = borderRightWidth
    override protected def attrB = borderBottomWidth
    override protected def attrL = borderLeftWidth
  }

  /**
   * The columns CSS property is a shorthand property allowing to set both the column-width and the column-count properties at the same time.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/columns">MDN</a>
   */
  final val columns = Attr.alias("columns", Transform keys CanIUse.multicolumn)(_(
    columnWidth, columnCount))

  /**
   * In multi-column layouts, the column-rule CSS property specifies a straight line, or "rule")( to be drawn between each column. It is a convenient shorthand to avoid setting each of the individual column-rule-* properties separately : column-rule-width, column-rule-style and column-rule-color.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/column-rule">MDN</a>
   */
  object columnRule extends TypedAttr_BrWidthStyleColour {
    override val attr = Attr.alias("column-rule", Transform keys CanIUse.multicolumn)(_(
      columnRuleWidth, columnRuleStyle, columnRuleColor))
  }

  /**
   * The flex CSS property is a shorthand property specifying the ability of a flex item to alter its dimensions to fill available space. Flex items can be stretched to use available space proportional to their flex grow factor or their flex shrink factor to prevent overflow.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/flex">MDN</a>
   */
  final val flex = Attr.alias("flex", Transform keys CanIUse.flexbox)(_(
    flexGrow, flexShrink, flexBasis))

  /**
   * The CSS flex-flow property is a shorthand property for flex-direction and flex-wrap individual properties.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/flex-flow">MDN</a>
   */
  final val flexFlow = Attr.alias("flex-flow", Transform keys CanIUse.flexbox)(_(
    flexDirection, flexWrap))

  /**
   * The font CSS property is either a shorthand property for setting font-style, font-variant, font-weight, font-size, line-height and font-family, or a way to set the element's font to a system font, using specific keywords.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font">MDN</a>
   */
  final val font = Attr.alias("font")(_(
    fontStyle, fontVariant, fontWeight, fontSize, lineHeight, fontFamily))

  /**
   * The font-variant CSS property selects a normal, or small-caps face from a font family. Setting the CSS Level 2 (Revision 1) values of the  font-variant property, that is normal or small-caps, is also possible by using the font shorthand.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant">MDN</a>
   */
  final val fontVariant = Attr.alias("font-variant")(_(
    fontVariantAlternates, fontVariantCaps, fontVariantEastAsian, fontVariantLigatures, fontVariantNumeric,
    fontVariantPosition))

  /**
   * @see <a href="http://www.w3.org/TR/css3-grid-layout/#grid-shorthand">css3-grid-layout#grid</a>
   */
  final val grid = Attr.alias("grid", Transform keys CanIUse.grid)(_(
    gridTemplate, gridAutoFlow, gridAutoColumns, gridAutoRows))

  /**
   * @see <a href="http://www.w3.org/TR/css3-grid-layout/#common-uses">css3-grid-layout#common-uses</a>
   */
  final val gridArea = Attr.alias("grid-area", Transform keys CanIUse.grid)(_(
    gridColumn, gridRow))

  /**
   * @see <a href="http://www.w3.org/TR/css3-grid-layout/#placement-shorthands">css3-grid-layout#placement-shorthands</a>
   */
  final val gridColumn = Attr.alias("grid-column", Transform keys CanIUse.grid)(_(
    gridColumnStart, gridColumnEnd))

  /**
   * @see <a href="http://www.w3.org/TR/css3-grid-layout/#placement-shorthands">css3-grid-layout#placement-shorthands</a>
   */
  final val gridRow = Attr.alias("grid-row", Transform keys CanIUse.grid)(_(
    gridRowStart, gridRowEnd))

  /**
   * @see <a href="http://www.w3.org/TR/css3-grid-layout/#propdef-grid-template">css3-grid-layout#grid-template</a>
   */
  final val gridTemplate = Attr.alias("grid-template", Transform keys CanIUse.grid)(_(
    gridTemplateAreas, gridTemplateColumns, gridTemplateRows))

  /**
   * The inline-size CSS property defines the horizontal or vertical size of an element's block depending on it's writing mode. It corresponds to the width or the height property depending on the value defined for writing-mode.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/inline-size">MDN</a>
   */
  object inlineSize extends TypedAttr_Length {
    override protected def attr2 = Attr.alias("inline-size", _)(_(height, width))
  }

  /**
   * The list-style CSS property is a shorthand property for setting list-style-type, list-style-image and list-style-position.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/list-style">MDN</a>
   */
  final val listStyle = Attr.alias("list-style")(_(
    listStyleType, listStyleImage, listStylePosition))

  /**
   * The margin CSS property sets the margin for all four sides. It is a shorthand to avoid setting each side separately with the other margin properties: margin-top, margin-right, margin-bottom and margin-left.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/margin">MDN</a>
   */
  object margin extends TypedAttrT4Edges[LenPctAuto](" ") with ZeroLit {
    override val attr = Attr.alias("margin")(_(
      marginTop, marginRight, marginBottom, marginLeft))
    override protected def attrT = marginTop
    override protected def attrR = marginRight
    override protected def attrB = marginBottom
    override protected def attrL = marginLeft
    @inline def auto = avl(LT.auto)
  }

  /**
   * The margin-block-end CSS property defines the logical block end margin of an element, which maps to a physical margin depending on the element's writing mode, directionality, and text orientation. It corresponds to the margin-top, margin-right, margin-bottom, or margin-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/margin-block-end">MDN</a>
   */
  object marginBlockEnd extends TypedAttr_LenPctAuto {
    override val attr = Attr.alias("margin-block-end")(_(margin))
  }

  /**
   * The margin-block-start CSS property defines the logical block start margin of an element, which maps to a physical margin depending on the element's writing mode, directionality, and text orientation. It corresponds to the margin-top, margin-right, margin-bottom, or margin-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/margin-block-start">MDN</a>
   */
  object marginBlockStart extends TypedAttr_LenPctAuto {
    override val attr = Attr.alias("margin-block-start")(_(margin))
  }

  /**
   * The margin-inline-end CSS property defines the logical inline end margin of an element, which maps to a physical margin depending on the element's writing mode, directionality, and text orientation. In other words, it corresponds to the margin-top, margin-right, margin-bottom or margin-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/margin-inline-end">MDN</a>
   */
  object marginInlineEnd extends TypedAttr_LenPctAuto {
    override val attr = Attr.alias("margin-inline-end")(_(margin))
  }

  /**
   * The margin-inline-start CSS property defines the logical inline end margin of an element, which maps to a physical margin depending on the element's writing mode, directionality, and text orientation. It corresponds to the margin-top, margin-right, margin-bottom, or margin-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/margin-inline-start">MDN</a>
   */
  object marginInlineStart extends TypedAttr_LenPctAuto {
    override val attr = Attr.alias("margin-inline-start")(_(margin))
  }

  /**
   * The max-block-size CSS property defines the horizontal or vertical maximal size of an element's block depending on its writing mode. It corresponds to the max-width or the max-height property, depending on the value defined for writing-mode. If the writing mode is vertically oriented, the value of max-block-size relates to the maximal width of the element, otherwise it relates to the maximal height of the element. It relates to max-inline-size, which defines the other dimension of the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/max-block-size">MDN</a>
   */
  object maxBlockSize extends TypedAttr_MaxLength {
    override protected def attr2 = Attr.alias("max-block-size", _)(_(maxHeight, maxWidth))
  }

  /**
   * The max-inline-size CSS property defines the horizontal or vertical maximal size of an element's block depending on its writing mode. It corresponds to the max-width or the max-height property depending on the value defined for writing-mode. If the writing mode is vertically oriented, the value of max-inline-size relates to the maximal height of the element, otherwise it relates to the maximal width of the element. It relates to max-block-size, which defines the other dimension of the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/max-inline-size">MDN</a>
   */
  object maxInlineSize extends TypedAttr_MaxLength {
    override protected def attr2 = Attr.alias("max-inline-size", _)(_(maxHeight, maxWidth))
  }

  /**
   * The min-block-size CSS property defines the horizontal or vertical minimal size of an element's block depending on its writing mode. It corresponds to the min-width or the min-height property, depending on the value defined for writing-mode. If the writing mode is vertically oriented, the value of min-block-size relates to the minimal width of the element, otherwise it relates to the minimal height of the element. It relates to min-inline-size, which defines the other dimension of the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/min-block-size">MDN</a>
   */
  object minBlockSize extends TypedAttr_MinLength {
    override protected def attr2 = Attr.alias("min-block-size", _)(_(minHeight, minWidth))
  }

  /**
   * The min-inline-size CSS property defines the horizontal or vertical minimal size of an element's block depending on its writing mode. It corresponds to the min-width or the min-height property, depending on the value defined for writing-mode. If the writing mode is vertically oriented, the value of min-inline-size relates to the minimal height of the element, otherwise it relates to the minimal width of the element. It relates to min-block-size, which defines the other dimension of the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/min-inline-size">MDN</a>
   */
  object minInlineSize extends TypedAttr_MinLength {
    override protected def attr2 = Attr.alias("min-inline-size", _)(_(minHeight, minWidth))
  }

  /**
   * The offset-block-end CSS property defines the logical block end offset of an element, which maps to a physical offset depending on the element's writing mode, directionality, and text orientation. It corresponds to the top, right, bottom, or left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/offset-block-end">MDN</a>
   */
  object offsetBlockEnd extends TypedAttr_LenPctAuto {
    override val attr = Attr.alias("offset-block-end")(_(
      top, left, right, bottom))
  }

  /**
   * The offset-block-start CSS property defines the logical block start offset of an element, which maps to a physical offset depending on the element's writing mode, directionality, and text orientation. It corresponds to the top, right, bottom, or left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/offset-block-start">MDN</a>
   */
  object offsetBlockStart extends TypedAttr_LenPctAuto {
    override val attr = Attr.alias("offset-block-start")(_(
      top, left, right, bottom))
  }

  /**
   * The offset-inline-end CSS property defines the logical inline end offset of an element, which maps to a physical offset depending on the element's writing mode, directionality, and text orientation. It corresponds to the top, right, bottom, or left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/offset-inline-end">MDN</a>
   */
  object offsetInlineEnd extends TypedAttr_LenPctAuto {
    override val attr = Attr.alias("offset-inline-end")(_(
      top, left, right, bottom))
  }

  /**
   * The offset-inline-start CSS property defines the logical inline start offset of an element, which maps to a physical offset depending on the element's writing mode, directionality, and text orientation. It corresponds to the top, right, bottom, or left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/offset-inline-start">MDN</a>
   */
  object offsetInlineStart extends TypedAttr_LenPctAuto {
    override val attr = Attr.alias("offset-inline-start")(_(
      top, left, right, bottom))
  }

  /**
   * The CSS outline property is a shorthand property for setting one or more of the individual outline properties outline-style, outline-width and outline-color in a single declaration. In most cases the use of this shortcut is preferable and more convenient.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/outline">MDN</a>
   */
  object outline extends TypedAttr_BrWidthStyleColour {
    override val attr = Attr.alias("outline", Transform keys CanIUse.outline)(_(
      outlineStyle, outlineWidth, outlineColor)) // not outlineOffset
  }

  /**
   * The padding CSS property sets the required padding space on all sides of an element. The padding area is the space between the content of the element and its border. Negative values are not allowed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/padding">MDN</a>
   */
  object padding extends TypedAttrT4Edges[LenPct](" ") with ZeroLit {
    override val attr = Attr.alias("padding")(_(
      paddingLeft, paddingRight, paddingTop, paddingBottom))
    override protected def attrT = paddingTop
    override protected def attrR = paddingRight
    override protected def attrB = paddingBottom
    override protected def attrL = paddingLeft
  }

  /**
   * The padding-block-end CSS property defines the logical block end padding of an element, which maps to a physical padding depending on the element's writing mode, directionality, and text orientation. It corresponds to the padding-top, padding-right, padding-bottom, or padding-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/padding-block-end">MDN</a>
   */
  object paddingBlockEnd extends TypedAttrT1[LenPct] with ZeroLit {
    override val attr = Attr.alias("padding-block-end")(_(padding))
  }

  /**
   * The padding-block-start CSS property defines the logical block start padding of an element, which maps to a physical padding depending on the element's writing mode, directionality, and text orientation. It corresponds to the padding-top, padding-right, padding-bottom, or padding-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/padding-block-start">MDN</a>
   */
  object paddingBlockStart extends TypedAttrT1[LenPct] with ZeroLit {
    override val attr = Attr.alias("padding-block-start")(_(padding))
  }

  /**
   * The padding-inline-end CSS property defines the logical inline end padding of an element, which maps to a physical padding depending on the element's writing mode, directionality, and text orientation. It corresponds to the padding-top, padding-right, padding-bottom, or padding-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/padding-inline-end">MDN</a>
   */
  object paddingInlineEnd extends TypedAttrT1[LenPct] with ZeroLit {
    override val attr = Attr.alias("padding-inline-end")(_(padding))
  }

  /**
   * The padding-inline-start CSS property defines the logical inline start padding of an element, which maps to a physical padding depending on the element's writing mode, directionality, and text orientation. It corresponds to the padding-top, padding-right, padding-bottom, or padding-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/padding-inline-start">MDN</a>
   */
  object paddingInlineStart extends TypedAttrT1[LenPct] with ZeroLit {
    override val attr = Attr.alias("padding-inline-start")(_(padding))
  }

  /**
   * The text-decoration CSS property is used to set the text formatting to underline, overline, line-through or blink. Underline and overline decorations are positioned under the text, line-through over it.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-decoration">MDN</a>
   */
  final val textDecoration = Attr.alias("text-decoration", Transform keys CanIUse.textDecoration)(_(
    textDecorationColor, textDecorationLine, textDecorationStyle))

  /**
   * The text-emphasis property will apply special emphasis marks to the elements text. Slightly similar to the text-decoration property only that this property can have affect on the line-height. It also is noted that this is shorthand for text-emphasis-style and for text-emphasis-color.
   *
   * Note that `text-emphasis-position` is not reset in this shorthand. This is because typically the shape and color vary, but the position is consistent for a particular language throughout the document. Therefore the position should inherit independently.
   *
   * @see <a href="https://docs.webplatform.org/wiki/css/properties/text-emphasis">WPD</a>
   */
  final val textEmphasis = Attr.alias("text-emphasis", Transform keys CanIUse.textEmphasis)(_(
    textEmphasisColor, textEmphasisStyle)) // Not textEmphasisPosition

  /**
   * Apple extension. Specifies the color of the outline (stroke) of text.
   *
   * @see <a href="https://developer.apple.com/library/safari/documentation/AppleApplications/Reference/SafariCSSRef/Articles/StandardCSSProperties.html#//apple_ref/doc/uid/TP30001266-_webkit_text_stroke">Safari CSS Reference</a>
   */
  final val textStroke = Attr.alias("text-stroke", Transform keys CanIUse.textStroke)(_(
    textStrokeColor, textStrokeWidth))

  /**
   * The CSS transition property is a shorthand property for transition-property, transition-duration, transition-timing-function, and transition-delay. It allows to define the transition between two states of an element. Different states may be defined using pseudo-classes like :hover or :active or dynamically set using JavaScript.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/transition">MDN</a>
   */
  final val transition = Attr.alias("transition", Transform keys CanIUse.transitions)(_(
    transitionProperty, transitionDuration, transitionTimingFunction, transitionDelay))

//  /**
//   * xxxxxxxx
//   *
//   * @see <a href="xxxxxxxx">MDN</a>
//   */
//  final val xxxxxxxx = Attr.real("xxxxxxxx")
}
