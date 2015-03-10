package japgolly.scalacss

import AttrComparison.{FullOverride, Unrelated}

// TODO Rename and move HighLevelTmp

trait Attrs {

  /**
   * The CSS align-content property aligns a flex container's lines within the flex container when there is extra space on the cross-axis. This property has no effect on single line flexible boxes.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/align-content">MDN</a>
   */
  final val alignContent = Attr.simple("align-content")

  /**
   * The CSS align-items property aligns flex items of the current flex line the same way as justify-content but in the perpendicular direction.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/align-items">MDN</a>
   */
  final val alignItems = Attr.simple("align-items")

  /**
   * The align-self CSS property aligns flex items of the current flex line overriding the align-items value. If any of the flex item's cross-axis margin is set to auto, then align-self is ignored.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/align-self">MDN</a>
   */
  final val alignSelf = Attr.simple("align-self")

  /**
   * The animation-delay CSS property specifies when the animation should start. This lets the animation sequence begin some time after it's applied to an element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/animation-delay">MDN</a>
   */
  final val animationDelay = Attr.simple("animation-delay")

  /**
   * The animation-direction CSS property indicates whether the animation should play in reverse on alternate cycles.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/animation-direction">MDN</a>
   */
  final val animationDirection = Attr.simple("animation-direction")

  /**
   * The animation-duration CSS property specifies the length of time that an animation should take to complete one cycle.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/animation-duration">MDN</a>
   */
  final val animationDuration = Attr.simple("animation-duration")

  /**
   * The animation-fill-mode CSS property specifies how a CSS animation should apply styles to its target before and after it is executing.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/animation-fill-mode">MDN</a>
   */
  final val animationFillMode = Attr.simple("animation-fill-mode")

  /**
   * The animation-iteration-count CSS property defines the number of times an animation cycle should be played before stopping.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/animation-iteration-count">MDN</a>
   */
  final val animationIterationCount = Attr.simple("animation-iteration-count")

  /**
   * The animation-name CSS property specifies a list of animations that should be applied to the selected element. Each name indicates a @keyframes at-rule that defines the property values for the animation sequence.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/animation-name">MDN</a>
   */
  final val animationName = Attr.simple("animation-name")

  /**
   * The animation-play-state CSS property determines whether an animation is running or paused. You can query this property's value to determine whether or not the animation is currently running; in addition, you can set its value to pause and resume playback of an animation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/animation-play-state">MDN</a>
   */
  final val animationPlayState = Attr.simple("animation-play-state")

  /**
   * The CSS animation-timing-function property specifies how a CSS animation should progress over the duration of each cycle. The possible values are one or several &lt;timing-function>.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/animation-timing-function">MDN</a>
   */
  final val animationTimingFunction = Attr.simple("animation-timing-function")

  /**
   * The CSS backface-visibility property determines whether or not the back face of the element is visible when facing the user. The back face of an element always is a transparent background, letting, when visible, a mirror image of the front face be displayed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/backface-visibility">MDN</a>
   */
  final val backfaceVisibility = Attr.simple("backface-visibility")

  /**
   * If a background-image is specified, the background-attachment CSS property determines whether that image's position is fixed within the viewport, or scrolls along with its containing block.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background-attachment">MDN</a>
   */
  final val backgroundAttachment = Attr.simple("background-attachment")

  /**
   * The background-blend-mode CSS property describes how the element's background images should blend with each other and the element's background color.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background-blend-mode">MDN</a>
   */
  final val backgroundBlendMode = Attr.simple("background-blend-mode")

  /**
   * Technical review completed. Editorial review completed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background-clip">MDN</a>
   */
  final val backgroundClip = Attr.simple("background-clip")

  /**
   * The background-color CSS property sets the background color of an element, either through a color value or the keyword transparent.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background-color">MDN</a>
   */
  final val backgroundColor = Attr.simple("background-color")

  /**
   * The CSS background-image property sets one or several background images for an element. The images are drawn on stacking context layers on top of each other. The first layer specified is drawn as if it is closest to the user. The borders of the element are then drawn on top of them, and the background-color is drawn beneath them.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background-image">MDN</a>
   */
  final val backgroundImage = Attr.simple("background-image")

  /**
   * The background-origin CSS property determines the background positioning area, that is the position of the origin of an image specified using the background-image CSS property.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background-origin">MDN</a>
   */
  final val backgroundOrigin = Attr.simple("background-origin")

  /**
   * The background-position CSS property sets the initial position, relative to the background position layer defined by background-origin for each defined background image.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background-position">MDN</a>
   */
  final val backgroundPosition = Attr.simple("background-position")

  /**
   * The background-repeat CSS property defines how background images are repeated. A background image can be repeated along the horizontal axis, the vertical axis, both axes, or not repeated at all.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background-repeat">MDN</a>
   */
  final val backgroundRepeat = Attr.simple("background-repeat")

  /**
   * The background-size CSS property specifies the size of the background images. The size of the image can be fully constrained or only partially in order to preserve its intrinsic ratio.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background-size">MDN</a>
   */
  final val backgroundSize = Attr.simple("background-size")

  /**
   * The block-size CSS property defines the horizontal or vertical size of an element's block depending on it's writing mode. It corresponds to the width or the height property depending on the value defined for writing-mode.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/block-size">MDN</a>
   */
  final val blockSize = Attr.simple("block-size")

  /**
   * The border-block-end-color CSS property defines the color of the logical block end border of an element, which maps to a physical border color depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-color, border-right-color, border-bottom-color, or border-left-color property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-end-color">MDN</a>
   */
  final val borderBlockEndColor = Attr.simple("border-block-end-color")

  /**
   * The border-block-end-style CSS property defines the style of the logical block end border of an element, which maps to a physical border style depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-style, border-right-style, border-bottom-style, or border-left-style property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-end-style">MDN</a>
   */
  final val borderBlockEndStyle = Attr.simple("border-block-end-style")

  /**
   * The border-block-end-width CSS property defines the width of the logical block end border of an element, which maps to a physical border width depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-width, border-right-width, border-bottom-width, or border-left-width property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-end-width">MDN</a>
   */
  final val borderBlockEndWidth = Attr.simple("border-block-end-width")

  /**
   * The border-block-start-color CSS property defines the color of the logical block start border of an element, which maps to a physical border color depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-color, border-right-color, border-bottom-color, or border-left-color property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-start-color">MDN</a>
   */
  final val borderBlockStartColor = Attr.simple("border-block-start-color")

  /**
   * The border-block-start-style CSS property defines the style of the logical block start border of an element, which maps to a physical border style depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-style, border-right-style, border-bottom-style, or border-left-style property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-start-style">MDN</a>
   */
  final val borderBlockStartStyle = Attr.simple("border-block-start-style")

  /**
   * The border-block-start-width CSS property defines the width of the logical block start border of an element, which maps to a physical border width depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-width, border-right-width, border-bottom-width, or border-left-width property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-start-width">MDN</a>
   */
  final val borderBlockStartWidth = Attr.simple("border-block-start-width")

  /**
   * The border-bottom-color CSS property sets the color of the bottom border of an element. Note that in many cases the shorthand CSS properties border-color or border-bottom are more convenient and preferable.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-bottom-color">MDN</a>
   */
  final val borderBottomColor = Attr.simple("border-bottom-color")

  /**
   * The border-bottom-left-radius CSS property sets the rounding of the bottom-left corner of the element. The rounding can be a circle or an ellipse, or if one of the value is 0 no rounding is done and the corner is square.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-bottom-left-radius">MDN</a>
   */
  final val borderBottomLeftRadius = Attr.simple("border-bottom-left-radius")

  /**
   * The border-bottom-right-radius CSS property sets the rounding of the bottom-right corner of the element. The rounding can be a circle or an ellipse, or if one of the value is 0 no rounding is done and the corner is square.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-bottom-right-radius">MDN</a>
   */
  final val borderBottomRightRadius = Attr.simple("border-bottom-right-radius")

  /**
   * The border-bottom-style CSS property sets the line style of the bottom border of a box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-bottom-style">MDN</a>
   */
  final val borderBottomStyle = Attr.simple("border-bottom-style")

  /**
   * The border-bottom-width CSS property sets the width of the bottom border of a box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-bottom-width">MDN</a>
   */
  final val borderBottomWidth = Attr.simple("border-bottom-width")

  /**
   * The border-collapse CSS property selects a table's border model. This has a big influence on the look and style of the table cells.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-collapse">MDN</a>
   */
  final val borderCollapse = Attr.simple("border-collapse")

  /**
   * The border-image CSS property allows drawing an image on the borders of elements. This makes drawing complex looking widgets much simpler than it has been and removes the need for nine boxes in some cases.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-image">MDN</a>
   */
  final val borderImage = Attr.simple("border-image")

  /**
   * The border-image-outset property describes by what amount the border image area extends beyond the border box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-outset">MDN</a>
   */
  final val borderImageOutset = Attr.simple("border-image-outset")

  /**
   * The border-image-repeat CSS property defines how the middle part of a border image is handled so that it can match the size of the border. It has a one-value syntax that describes the behavior of all the sides, and a two-value syntax that sets a different value for the horizontal and vertical behavior.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-repeat">MDN</a>
   */
  final val borderImageRepeat = Attr.simple("border-image-repeat")

  /**
   * The border-image-slice CSS property divides the image specified by border-image-source in nine regions: the four corners, the four edges and the middle. It does this by specifying 4 inwards offsets.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-slice">MDN</a>
   */
  final val borderImageSlice = Attr.simple("border-image-slice")

  /**
   * The border-image-source CSS property defines the &lt;image> to use instead of the style of the border. If this property is set to none, the style defined by border-style is used instead.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-source">MDN</a>
   */
  final val borderImageSource = Attr.simple("border-image-source")

  /**
   * The border-image-width CSS property defines the width of the border. If specified, it overrides the border-width property.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-width">MDN</a>
   */
  final val borderImageWidth = Attr.simple("border-image-width")

  /**
   * The border-inline-end-color CSS property defines the color of the logical inline end border of an element, which maps to a physical border color depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-color, border-right-color, border-bottom-color, or border-left-color property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-end-color">MDN</a>
   */
  final val borderInlineEndColor = Attr.simple("border-inline-end-color")

  /**
   * The border-inline-end-style CSS property defines the style of the logical inline end border of an element, which maps to a physical border style depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-style, border-right-style, border-bottom-style, or border-left-style property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-end-style">MDN</a>
   */
  final val borderInlineEndStyle = Attr.simple("border-inline-end-style")

  /**
   * The border-inline-end-width CSS property defines the width of the logical inline end border of an element, which maps to a physical border width depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-width, border-right-width, border-bottom-width, or border-left-width property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-end-width">MDN</a>
   */
  final val borderInlineEndWidth = Attr.simple("border-inline-end-width")

  /**
   * The border-inline-start-color CSS property defines the color of the logical inline start border of an element, which maps to a physical border color depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-color, border-right-color, border-bottom-color, or border-left-color property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-start-color">MDN</a>
   */
  final val borderInlineStartColor = Attr.simple("border-inline-start-color")

  /**
   * The border-inline-start-style CSS property defines the style of the logical inline start border of an element, which maps to a physical border style depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-style, border-right-style, border-bottom-style, or border-left-style property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-start-style">MDN</a>
   */
  final val borderInlineStartStyle = Attr.simple("border-inline-start-style")

  /**
   * The border-inline-start-width CSS property defines the width of the logical inline start border of an element, which maps to a physical border width depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top-width, border-right-width, border-bottom-width, or border-left-width property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-start-width">MDN</a>
   */
  final val borderInlineStartWidth = Attr.simple("border-inline-start-width")

  /**
   * The border-left-color CSS property sets the color of the bottom border of an element. Note that in many cases the shorthand CSS properties border-color or border-left are more convenient and preferable.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-left-color">MDN</a>
   */
  final val borderLeftColor = Attr.simple("border-left-color")

  /**
   * The border-left-style CSS property sets the line style of the left border of a box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-left-style">MDN</a>
   */
  final val borderLeftStyle = Attr.simple("border-left-style")

  /**
   * The border-left-width CSS property sets the width of the left border of a box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-left-width">MDN</a>
   */
  final val borderLeftWidth = Attr.simple("border-left-width")

  /**
   * The border-radius CSS property allows Web authors to define how rounded border corners are. The curve of each corner is defined using one or two radii, defining its shape: circle or ellipse.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-radius">MDN</a>
   */
  final val borderRadius = Attr.simple("border-radius")

  /**
   * The border-right-color CSS property sets the color of the right border of an element. Note that in many cases the shorthand CSS properties  border-color or border-right are more convenient and preferable.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-right-color">MDN</a>
   */
  final val borderRightColor = Attr.simple("border-right-color")

  /**
   * The border-right-style CSS property sets the line style of the right border of a box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-right-style">MDN</a>
   */
  final val borderRightStyle = Attr.simple("border-right-style")

  /**
   * The border-right-width CSS property sets the width of the right border of a box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-right-width">MDN</a>
   */
  final val borderRightWidth = Attr.simple("border-right-width")

  /**
   * The border-spacing CSS property specifies the distance between the borders of adjacent cells (only for the separated borders model). This is equivalent to the cellspacing attribute in presentational HTML, but an optional second value can be used to set different horizontal and vertical spacing.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-spacing">MDN</a>
   */
  final val borderSpacing = Attr.simple("border-spacing")

  /**
   * Technical review completed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-style">MDN</a>
   */
  final val borderStyle = Attr.simple("border-style")

  /**
   * The border-top-color CSS property sets the color of the top border of an element. Note that in many cases the shorthand CSS properties border-color or border-top are more convenient and preferable.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-top-color">MDN</a>
   */
  final val borderTopColor = Attr.simple("border-top-color")

  /**
   * The border-top-left-radius CSS property sets the rounding of the top-left corner of the element. The rounding can be a circle or an ellipse, or if one of the value is 0,no rounding is done and the corner is square.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-top-left-radius">MDN</a>
   */
  final val borderTopLeftRadius = Attr.simple("border-top-left-radius")

  /**
   * The border-top-right-radius CSS property sets the rounding of the top-right corner of the element. The rounding can be a circle or an ellipse, or if one of the value is 0 no rounding is done and the corner is square.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-top-right-radius">MDN</a>
   */
  final val borderTopRightRadius = Attr.simple("border-top-right-radius")

  /**
   * The border-top-style CSS property sets the line style of the top border of a box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-top-style">MDN</a>
   */
  final val borderTopStyle = Attr.simple("border-top-style")

  /**
   * The border-top-width CSS property sets the width of the top border of a box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-top-width">MDN</a>
   */
  final val borderTopWidth = Attr.simple("border-top-width")

  /**
   * Technical review completed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-width">MDN</a>
   */
  final val borderWidth = Attr.simple("border-width")

  /**
   * The bottom CSS property participates in specifying the position of positioned elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/bottom">MDN</a>
   */
  final val bottom = Attr.simple("bottom")

  /**
   * The box-decoration-break CSS property specifies how the background, padding, border, border-image, box-shadow, margin and clip of an element is applied when the box for the element is fragmented.  Fragmentation occurs when an inline box wraps onto multiple lines, or when a block spans more than one column inside a column layout container, or spans a page break when printed.  Each piece of the rendering for the element is called a fragment.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/box-decoration-break">MDN</a>
   */
  final val boxDecorationBreak = Attr.simple("box-decoration-break")

  /**
   * The box-shadow CSS property describes one or more shadow effects as a comma-separated list.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/box-shadow">MDN</a>
   */
  final val boxShadow = Attr.simple("box-shadow")

  /**
   * The box-sizing CSS property is used to alter the default CSS box model used to calculate widths and heights of elements. It is possible to use this property to emulate the behavior of browsers that do not correctly support the CSS box model specification.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/box-sizing">MDN</a>
   */
  final val boxSizing = Attr.simple("box-sizing")

  /**
   * The break-after CSS property describes how the page, column or region break behavior after the generated box. If there is no generated box, the property is ignored.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/break-after">MDN</a>
   */
  final val breakAfter = Attr.simple("break-after")

  /**
   * The break-before CSS property describes how the page, column or region break behavior before the generated box. If there is no generated box, the property is ignored.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/break-before">MDN</a>
   */
  final val breakBefore = Attr.simple("break-before")

  /**
   * The break-inside CSS property describes how the page, column or region break inside the generated box. If there is no generated box, the property is ignored.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/break-inside">MDN</a>
   */
  final val breakInside = Attr.simple("break-inside")

  /**
   * The caption-side CSS property positions the content of a table's &lt;caption> on the specified side.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/caption-side">MDN</a>
   */
  final val captionSide = Attr.simple("caption-side")

  /**
   * The clear CSS property specifies whether an element can be next to floating elements that precede it or must be moved down (cleared) below them.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/clear">MDN</a>
   */
  final val clear = Attr.simple("clear")

  /**
   * The clip CSS property defines what portion of an element is visible. The clip property applies only to absolutely positioned elements, that is elements with position:absolute or position:fixed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/clip">MDN</a>
   */
  final val clip = Attr.simple("clip")

  /**
   * The clip-path property prevents a portion of an element from drawing by defining a clipping region.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/clip-path">MDN</a>
   */
  final val clipPath = Attr.simple("clip-path")

  /**
   * The CSS color property sets the foreground color of an element's text content, and its decorations. It doesn't affect any other characteristic of the element; it should really be called text-color and would have been named so, save for historical reasons and its appearance in CSS Level 1.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/color">MDN</a>
   */
  final val color = Attr.simple("color")

  /**
   * The column-count CSS property describes the number of columns of the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/column-count">MDN</a>
   */
  final val columnCount = Attr.simple("column-count")

  /**
   * The column-fill CSS property controls how contents are partitioned into columns. Contents are either balanced, which means that contents in all columns will have the same height or, when using auto, just take up the room the content needs.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/column-fill">MDN</a>
   */
  final val columnFill = Attr.simple("column-fill")

  /**
   * The column-gap CSS property sets the size of the gap between columns for elements which are specified to display as a multi-column element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/column-gap">MDN</a>
   */
  final val columnGap = Attr.simple("column-gap")

  /**
   * The column-rule-color CSS property lets you set the color of the rule drawn between columns in multi-column layouts.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/column-rule-color">MDN</a>
   */
  final val columnRuleColor = Attr.simple("column-rule-color")

  /**
   * The column-rule-style CSS property lets you set the style of the rule drawn between columns in multi-column layouts.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/column-rule-style">MDN</a>
   */
  final val columnRuleStyle = Attr.simple("column-rule-style")

  /**
   * The column-rule-width CSS property lets you set the width of the rule drawn between columns in multi-column layouts.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/column-rule-width">MDN</a>
   */
  final val columnRuleWidth = Attr.simple("column-rule-width")

  /**
   * The column-span CSS property makes it possible for an element to span across all columns when its value is set to all. An element that spans more than one column is called a spanning element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/column-span">MDN</a>
   */
  final val columnSpan = Attr.simple("column-span")

  /**
   * The column-width CSS property suggests an optimal column width. This is not a absolute value but a mere hint. Browser will adjust the width of the column around that suggested value, allowing to achieve scalable designs that fit different screen size. Especially in presence of the column-count CSS property which has precedence, to set an exact column width, all length values must be specified. In horizontal text these are width, column-width, column-gap, and column-rule-width.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/column-width">MDN</a>
   */
  final val columnWidth = Attr.simple("column-width")

  /**
   * The content CSS property is used with the ::before and ::after pseudo-elements to generate content in an element. Objects inserted using the content property are anonymous replaced elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/content">MDN</a>
   */
  final val content = Attr.simple("content")

  /**
   * The counter-increment CSS property is used to increase the value of CSS Counters by a given value. The counter's value can be reset using the counter-reset CSS property.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/counter-increment">MDN</a>
   */
  final val counterIncrement = Attr.simple("counter-increment")

  /**
   * The counter-reset CSS property is used to reset CSS Counters to a given value.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/counter-reset">MDN</a>
   */
  final val counterReset = Attr.simple("counter-reset")

  /**
   * The cursor CSS property specifies the mouse cursor displayed when the mouse pointer is over an element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/cursor">MDN</a>
   */
  final val cursor = Attr.simple("cursor")

  /**
   * Set the direction CSS property to match the direction of the text: rtl for languages written from right-to-left (like Hebrew or Arabic) text and ltr for other scripts. This is typically done as part of the document (e.g., using the dir attribute in HTML) rather than through direct use of CSS.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/direction">MDN</a>
   */
  final val direction = Attr.simple("direction")

  /**
   * The display CSS property specifies the type of rendering box used for an element. In HTML, default display property values are taken from behaviors described in the HTML specifications or from the browser/user default stylesheet. The default value in XML is inline.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/display">MDN</a>
   */
  final val display = Attr.simple("display")

  /**
   * The empty-cells CSS property specifies how user agents should render borders and backgrounds around cells that have no visible content.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/empty-cells">MDN</a>
   */
  final val emptyCells = Attr.simple("empty-cells")

  /**
   * The CSS filter property provides for effects like blurring or color shifting on an element’s rendering before the element is displayed. Filters are commonly used to adjust the rendering of an image, a background, or a border.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/filter">MDN</a>
   */
  final val filter = Attr.simple("filter")

  /**
   * The CSS flex-basis property specifies the flex basis which is the initial main size of a flex item. The property determines the size of the content-box unless specified otherwise using box-sizing.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/flex-basis">MDN</a>
   */
  final val flexBasis = Attr.simple("flex-basis")

  /**
   * The CSS flex-direction property specifies how flex items are placed in the flex container defining the main axis and the direction (normal or reversed).
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/flex-direction">MDN</a>
   */
  final val flexDirection = Attr.simple("flex-direction")

  /**
   * The CSS flex-grow property specifies the flex grow factor of a flex item.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/flex-grow">MDN</a>
   */
  final val flexGrow = Attr.simple("flex-grow")

  /**
   * The CSS flex-shrink property specifies the flex shrink factor of a flex item.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/flex-shrink">MDN</a>
   */
  final val flexShrink = Attr.simple("flex-shrink")

  /**
   * The CSS flex-wrap property specifies whether the children are forced into a single line or if the items can be flowed on multiple lines.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/flex-wrap">MDN</a>
   */
  final val flexWrap = Attr.simple("flex-wrap")

  /**
   * The float CSS property specifies that an element should be taken from the normal flow and placed along the left or right side of its container, where text and inline elements will wrap around it. A floating element is one where the computed value of float is not none.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/float">MDN</a>
   */
  final val float = Attr.simple("float")

  /**
   * The font-family CSS property allows for a prioritized list of font family names and/or generic family names to be specified for the selected element. Unlike most other CSS properties, values are separated by a comma to indicate that they are alternatives. The browser will select the first font on the list that is installed on the computer, or that can be downloaded using the information provided by a @font-face at-rule.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-family">MDN</a>
   */
  final val fontFamily = Attr.simple("font-family")

  /**
   * The font-feature-settings CSS property allows control over advanced typographic features in OpenType fonts.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-feature-settings">MDN</a>
   */
  final val fontFeatureSettings = Attr.simple("font-feature-settings")

  /**
   * The font-kerning CSS property controls the usage of the kerning information; that is, it controls how letters are spaced. The kerning information is stored in the font, and if the font is well-kerned, this feature allows spacing between characters to be very similar, whatever the characters are.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-kerning">MDN</a>
   */
  final val fontKerning = Attr.simple("font-kerning")

  /**
   * The font-language-override CSS property controls the usage of language-specific glyphs in a typeface.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-language-override">MDN</a>
   */
  final val fontLanguageOverride = Attr.simple("font-language-override")

  /**
   * The font-size CSS property specifies the size of the font – specifically the desired height of glyphs from the font. Setting the font size may, in turn, change the size of other items, since it is used to compute the value of em and ex length units.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-size">MDN</a>
   */
  final val fontSize = Attr.simple("font-size")

  /**
   * The font-size-adjust CSS property specifies that font size should be chosen based on the height of lowercase letters rather than the height of capital letters.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-size-adjust">MDN</a>
   */
  final val fontSizeAdjust = Attr.simple("font-size-adjust")

  /**
   * The font-stretch CSS property selects a normal, condensed, or expanded face from a font.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-stretch">MDN</a>
   */
  final val fontStretch = Attr.simple("font-stretch")

  /**
   * The font-style CSS property allows italic or oblique faces to be selected within a font-family.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-style">MDN</a>
   */
  final val fontStyle = Attr.simple("font-style")

  /**
   * The font-synthesis CSS property controls which missing typefaces, bold or italic, may be synthesized by the browser.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-synthesis">MDN</a>
   */
  final val fontSynthesis = Attr.simple("font-synthesis")

  /**
   * The font-variant-alternates CSS property controls the usage of alternate glyphs associated to alternative names defined in @font-feature-values.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-alternates">MDN</a>
   */
  final val fontVariantAlternates = Attr.simple("font-variant-alternates")

  /**
   * The font-variant-caps CSS property controls the usage of alternate glyphs for capital letters. Scripts can have capital letter glyphs of different sizes, the normal uppercase glyphs, small capital glyphs, and petite capital glyphs. This property controls which alternate glyphs to use.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-caps">MDN</a>
   */
  final val fontVariantCaps = Attr.simple("font-variant-caps")

  /**
   * The font-variant-east-asian CSS property controls the usage of alternate glyphs for East Asian scripts, like Japanese and Chinese.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-east-asian">MDN</a>
   */
  final val fontVariantEastAsian = Attr.simple("font-variant-east-asian")

  /**
   * The font-variant-ligatures CSS property controls which ligatures and contextual forms are used in textual content of the elements it applies to. This leads to more harmonized forms in the resulting text.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-ligatures">MDN</a>
   */
  final val fontVariantLigatures = Attr.simple("font-variant-ligatures")

  /**
   * The font-variant-numeric CSS property controls the usage of alternate glyphs for numbers, fractions, and ordinal markers.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-numeric">MDN</a>
   */
  final val fontVariantNumeric = Attr.simple("font-variant-numeric")

  /**
   * The font-variant-position CSS property controls the usage of alternate glyphs of smaller size positioned as superscript or subscript regarding the baseline of the font, which is set unchanged. These glyphs are likely to be used in &lt;sub> and &lt;sup> elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant-position">MDN</a>
   */
  final val fontVariantPosition = Attr.simple("font-variant-position")

  /**
   * The font-weight CSS property specifies the weight or boldness of the font. However, some fonts are not available in all weights; some are available only on normal and bold.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-weight">MDN</a>
   */
  final val fontWeight = Attr.simple("font-weight")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid">MDN</a>
   */
  final val grid = Attr.simple("grid")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-area">MDN</a>
   */
  final val gridArea = Attr.simple("grid-area")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-auto-columns">MDN</a>
   */
  final val gridAutoColumns = Attr.simple("grid-auto-columns")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-auto-flow">MDN</a>
   */
  final val gridAutoFlow = Attr.simple("grid-auto-flow")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-auto-position">MDN</a>
   */
  final val gridAutoPosition = Attr.simple("grid-auto-position")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-auto-rows">MDN</a>
   */
  final val gridAutoRows = Attr.simple("grid-auto-rows")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-column">MDN</a>
   */
  final val gridColumn = Attr.simple("grid-column")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-column-start">MDN</a>
   */
  final val gridColumnStart = Attr.simple("grid-column-start")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-column-end">MDN</a>
   */
  final val gridColumnEnd = Attr.simple("grid-column-end")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-row">MDN</a>
   */
  final val gridRow = Attr.simple("grid-row")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-row-start">MDN</a>
   */
  final val gridRowStart = Attr.simple("grid-row-start")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-row-end">MDN</a>
   */
  final val gridRowEnd = Attr.simple("grid-row-end")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-template">MDN</a>
   */
  final val gridTemplate = Attr.simple("grid-template")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-template-areas">MDN</a>
   */
  final val gridTemplateAreas = Attr.simple("grid-template-areas")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-template-rows">MDN</a>
   */
  final val gridTemplateRows = Attr.simple("grid-template-rows")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/grid-template-columns">MDN</a>
   */
  final val gridTemplateColumns = Attr.simple("grid-template-columns")

  /**
   * The height CSS property specifies the height of the content area of an element. The content area is inside the padding, border, and margin of the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/height">MDN</a>
   */
  final val height = Attr.simple("height")

  /**
   * The hyphens CSS property tells the browser how to go about splitting words to improve the layout of text when line-wrapping. On HTML, the language is determined by the lang attribute: browsers will hyphenate only if this attribute is present and if an appropriate hyphenation dictionary is available. On XML, the xml:lang attribute must be used.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/hyphens">MDN</a>
   */
  final val hyphens = Attr.simple("hyphens")

  /**
   * The image-rendering CSS property provides a hint to the user agent about how to handle its image rendering.  It applies to any images appearing on the element properties, but has no effect on non-scaled images.. For example, if the natural size of the image is 100×100px but the page author specifies the dimensions to 200×200px (or 50×50px), then the image will be upscaled (or downscaled) to the new dimensions using the specified algorithm. Scaling may also apply due to user interaction (zooming).
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/image-rendering">MDN</a>
   */
  final val imageRendering = Attr.simple("image-rendering")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/image-resolution">MDN</a>
   */
  final val imageResolution = Attr.simple("image-resolution")

  /**
   * The image-orientation CSS property describes how to correct the default orientation of an image.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/image-orientation">MDN</a>
   */
  final val imageOrientation = Attr.simple("image-orientation")

  /**
   * The ime-mode CSS property controls the state of the input method editor for text fields.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/ime-mode">MDN</a>
   */
  final val imeMode = Attr.simple("ime-mode")

  /**
   * The inherit CSS-value causes the element for which it is specified to take the computed value of the property from its parent element. It is allowed on every CSS property.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/inherit">MDN</a>
   */
  final val inherit = Attr.simple("inherit")

  /**
   * The initial CSS keyword applies the initial value of a property to an element. It is allowed on every CSS property and causes the element for which it is specified to use the initial value of the property.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/initial">MDN</a>
   */
  final val initial = Attr.simple("initial")

  /**
   * The inline-size CSS property defines the horizontal or vertical size of an element's block depending on it's writing mode. It corresponds to the width or the height property depending on the value defined for writing-mode.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/inline-size">MDN</a>
   */
  final val inlineSize = Attr.simple("inline-size")

  /**
   * The isolation CSS property defines if the element must create a new stacking context.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/isolation">MDN</a>
   */
  final val isolation = Attr.simple("isolation")

  /**
   * The CSS justify-content property defines how a browser distributes available space between and around elements when aligning flex items in the main-axis of the current line. The alignment is done after the lengths and auto margins are applied, meaning that, if there is at least one flexible element, with flex-grow different than 0, it will have no effect as there won't be any available space.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/justify-content">MDN</a>
   */
  final val justifyContent = Attr.simple("justify-content")

  /**
   * The left CSS property specifies part of the position of positioned elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/left">MDN</a>
   */
  final val left = Attr.simple("left")

  /**
   * The letter-spacing CSS property specifies spacing behavior between text characters.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/letter-spacing">MDN</a>
   */
  final val letterSpacing = Attr.simple("letter-spacing")

  /**
   * The line-break CSS property is used to specify how (or if) to break lines.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/line-break">MDN</a>
   */
  final val lineBreak = Attr.simple("line-break")

  /**
   * On block level elements, the line-height CSS property specifies the minimal height of line boxes within the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/line-height">MDN</a>
   */
  final val lineHeight = Attr.simple("line-height")

  /**
   * The list-style-image CSS property sets the image that will be used as the list item marker.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/list-style-image">MDN</a>
   */
  final val listStyleImage = Attr.simple("list-style-image")

  /**
   * The list-style-position CSS property specifies the position of the marker box in the principal block box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/list-style-position">MDN</a>
   */
  final val listStylePosition = Attr.simple("list-style-position")

  /**
   * The list-style-type CSS property specifies appearance of a list item element. As it is the only one which defaults to display:list-item, this is usually a &lt;li> element, but can be any element with this display value.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/list-style-type">MDN</a>
   */
  final val listStyleType = Attr.simple("list-style-type")

  /**
   * The margin-block-end CSS property defines the logical block end margin of an element, which maps to a physical margin depending on the element's writing mode, directionality, and text orientation. It corresponds to the margin-top, margin-right, margin-bottom, or margin-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/margin-block-end">MDN</a>
   */
  final val marginBlockEnd = Attr.simple("margin-block-end")

  /**
   * The margin-block-start CSS property defines the logical block start margin of an element, which maps to a physical margin depending on the element's writing mode, directionality, and text orientation. It corresponds to the margin-top, margin-right, margin-bottom, or margin-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/margin-block-start">MDN</a>
   */
  final val marginBlockStart = Attr.simple("margin-block-start")

  /**
   * The margin-bottom CSS property of an element sets the margin space required on the bottom of an element. A negative value is also allowed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/margin-bottom">MDN</a>
   */
  final val marginBottom = Attr.simple("margin-bottom")

  /**
   * The margin-inline-end CSS property defines the logical inline end margin of an element, which maps to a physical margin depending on the element's writing mode, directionality, and text orientation. In other words, it corresponds to the margin-top, margin-right, margin-bottom or margin-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/margin-inline-end">MDN</a>
   */
  final val marginInlineEnd = Attr.simple("margin-inline-end")

  /**
   * The margin-inline-start CSS property defines the logical inline end margin of an element, which maps to a physical margin depending on the element's writing mode, directionality, and text orientation. It corresponds to the margin-top, margin-right, margin-bottom, or margin-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/margin-inline-start">MDN</a>
   */
  final val marginInlineStart = Attr.simple("margin-inline-start")

  /**
   * The margin-left CSS property of an element sets the margin space required on the left side of a box associated with an element. A negative value is also allowed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/margin-left">MDN</a>
   */
  final val marginLeft = Attr.simple("margin-left")

  /**
   * The margin-right CSS property of an element sets the margin space required on the right side of an element. A negative value is also allowed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/margin-right">MDN</a>
   */
  final val marginRight = Attr.simple("margin-right")

  /**
   * The margin-top CSS property of an element sets the margin space required on the top of an element. A negative value is also allowed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/margin-top">MDN</a>
   */
  final val marginTop = Attr.simple("margin-top")

  /**
   * The marks CSS property adds crop and/or cross marks to the presentation of the document. Crop marks indicate where the page should be cut. Cross marks are used to align sheets.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/marks">MDN</a>
   */
  final val marks = Attr.simple("marks")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/mask">MDN</a>
   */
  final val mask = Attr.simple("mask")

  /**
   * The CSS mask-type properties defines if a SVG &lt;mask> element is a luminance or an alpha mask.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/mask-type">MDN</a>
   */
  final val maskType = Attr.simple("mask-type")

  /**
   * The max-block-size CSS property defines the horizontal or vertical maximal size of an element's block depending on its writing mode. It corresponds to the max-width or the max-height property, depending on the value defined for writing-mode. If the writing mode is vertically oriented, the value of max-block-size relates to the maximal width of the element, otherwise it relates to the maximal height of the element. It relates to max-inline-size, which defines the other dimension of the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/max-block-size">MDN</a>
   */
  final val maxBlockSize = Attr.simple("max-block-size")

  /**
   * The max-height CSS property is used to set the maximum height of a given element. It prevents the used value of the height property from becoming larger than the value specified for max-height.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/max-height">MDN</a>
   */
  final val maxHeight = Attr.simple("max-height")

  /**
   * The max-inline-size CSS property defines the horizontal or vertical maximal size of an element's block depending on its writing mode. It corresponds to the max-width or the max-height property depending on the value defined for writing-mode. If the writing mode is vertically oriented, the value of max-inline-size relates to the maximal height of the element, otherwise it relates to the maximal width of the element. It relates to max-block-size, which defines the other dimension of the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/max-inline-size">MDN</a>
   */
  final val maxInlineSize = Attr.simple("max-inline-size")

  /**
   * The max-width CSS property is used to set the maximum width of a given element. It prevents the used value of the width property from becoming larger than the value specified for max-width.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/max-width">MDN</a>
   */
  final val maxWidth = Attr.simple("max-width")

  /**
   * The min-block-size CSS property defines the horizontal or vertical minimal size of an element's block depending on its writing mode. It corresponds to the min-width or the min-height property, depending on the value defined for writing-mode. If the writing mode is vertically oriented, the value of min-block-size relates to the minimal width of the element, otherwise it relates to the minimal height of the element. It relates to min-inline-size, which defines the other dimension of the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/min-block-size">MDN</a>
   */
  final val minBlockSize = Attr.simple("min-block-size")

  /**
   * The min-height CSS property is used to set the minimum height of a given element. It prevents the used value of the height property from becoming smaller than the value specified for min-height.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/min-height">MDN</a>
   */
  final val minHeight = Attr.simple("min-height")

  /**
   * The min-inline-size CSS property defines the horizontal or vertical minimal size of an element's block depending on its writing mode. It corresponds to the min-width or the min-height property, depending on the value defined for writing-mode. If the writing mode is vertically oriented, the value of min-inline-size relates to the minimal height of the element, otherwise it relates to the minimal width of the element. It relates to min-block-size, which defines the other dimension of the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/min-inline-size">MDN</a>
   */
  final val minInlineSize = Attr.simple("min-inline-size")

  /**
   * The min-width CSS property is used to set the minimum width of a given element. It prevents the used value of the width property from becoming smaller than the value specified for min-width.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/min-width">MDN</a>
   */
  final val minWidth = Attr.simple("min-width")

  /**
   * The mix-blend-mode CSS property describes how an element content should blend with the content of the element that is below it and the element's background.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/mix-blend-mode">MDN</a>
   */
  final val mixBlendMode = Attr.simple("mix-blend-mode")

  /**
   * The object-fit CSS property specifies how the contents of a replaced element should be fitted to the box established by its used height and width.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/object-fit">MDN</a>
   */
  final val objectFit = Attr.simple("object-fit")

  /**
   * The object-position property determines the alignment of the replaced element inside its box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/object-position">MDN</a>
   */
  final val objectPosition = Attr.simple("object-position")

  /**
   * The offset-block-end CSS property defines the logical block end offset of an element, which maps to a physical offset depending on the element's writing mode, directionality, and text orientation. It corresponds to the top, right, bottom, or left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/offset-block-end">MDN</a>
   */
  final val offsetBlockEnd = Attr.simple("offset-block-end")

  /**
   * The offset-block-start CSS property defines the logical block start offset of an element, which maps to a physical offset depending on the element's writing mode, directionality, and text orientation. It corresponds to the top, right, bottom, or left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/offset-block-start">MDN</a>
   */
  final val offsetBlockStart = Attr.simple("offset-block-start")

  /**
   * The offset-inline-end CSS property defines the logical inline end offset of an element, which maps to a physical offset depending on the element's writing mode, directionality, and text orientation. It corresponds to the top, right, bottom, or left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/offset-inline-end">MDN</a>
   */
  final val offsetInlineEnd = Attr.simple("offset-inline-end")

  /**
   * The offset-inline-start CSS property defines the logical inline start offset of an element, which maps to a physical offset depending on the element's writing mode, directionality, and text orientation. It corresponds to the top, right, bottom, or left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/offset-inline-start">MDN</a>
   */
  final val offsetInlineStart = Attr.simple("offset-inline-start")

  /**
   * The opacity CSS property specifies the transparency of an element, that is, the degree to which the background behind the element is overlaid.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/opacity">MDN</a>
   */
  final val opacity = Attr.simple("opacity")

  /**
   * The CSS order property specifies the order used to lay out flex items in their flex container. Elements are laid out by ascending order of the order value. Elements with the same order value are laid out in the order they appear in the source code.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/order">MDN</a>
   */
  final val order = Attr.simple("order")

  /**
   * The orphans CSS property refers to the minimum number of lines in a block container that must be left at the bottom of the page. This property is normally used to control how page breaks occur.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/orphans">MDN</a>
   */
  final val orphans = Attr.simple("orphans")

  /**
   * The outline-color CSS property sets the color of the outline of an element. An outline is a line that is drawn around elements, outside the border edge, to make the element stand out.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/outline-color">MDN</a>
   */
  final val outlineColor = Attr.simple("outline-color")

  /**
   * The outline-offset CSS property is used to set space between an outline and the edge or border of an element. An outline is a line that is drawn around elements, outside the border edge.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/outline-offset">MDN</a>
   */
  final val outlineOffset = Attr.simple("outline-offset")

  /**
   * The outline-style CSS property is used to set the style of the outline of an element. An outline is a line that is drawn around elements, outside the border edge, to make the element stand out.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/outline-style">MDN</a>
   */
  final val outlineStyle = Attr.simple("outline-style")

  /**
   * The outline-width CSS property is used to set the width of the outline of an element. An outline is a line that is drawn around elements, outside the border edge, to make the element stand out:
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/outline-width">MDN</a>
   */
  final val outlineWidth = Attr.simple("outline-width")

  /**
   * The overflow CSS property specifies whether to clip content, render scrollbars or just display content when it overflows its block level container.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/overflow">MDN</a>
   */
  final val overflow = Attr.simple("overflow")

  /**
   * REDIRECT https://developer.mozilla.org/en-US/docs/CSS/word-wrap
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/overflow-wrap">MDN</a>
   */
  final val overflowWrap = Attr.simple("overflow-wrap")

  /**
   * The overflow-x CSS property specifies whether to clip content, render a scroll bar or display overflow content of a block-level element, when it overflows at the left and right edges.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/overflow-x">MDN</a>
   */
  final val overflowX = Attr.simple("overflow-x")

  /**
   * The overflow-y CSS property specifies whether to clip content, render a scroll bar, or display overflow content of a block-level element, when it overflows at the top and bottom edges.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/overflow-y">MDN</a>
   */
  final val overflowY = Attr.simple("overflow-y")

  /**
   * The padding CSS property sets the required padding space on all sides of an element. The padding area is the space between the content of the element and its border. Negative values are not allowed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/padding">MDN</a>
   */
  final val padding = Attr.simple("padding")

  /**
   * The padding-block-end CSS property defines the logical block end padding of an element, which maps to a physical padding depending on the element's writing mode, directionality, and text orientation. It corresponds to the padding-top, padding-right, padding-bottom, or padding-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/padding-block-end">MDN</a>
   */
  final val paddingBlockEnd = Attr.simple("padding-block-end")

  /**
   * The padding-block-start CSS property defines the logical block start padding of an element, which maps to a physical padding depending on the element's writing mode, directionality, and text orientation. It corresponds to the padding-top, padding-right, padding-bottom, or padding-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/padding-block-start">MDN</a>
   */
  final val paddingBlockStart = Attr.simple("padding-block-start")

  /**
   * The padding-bottom CSS property of an element sets the height of the padding area at the bottom of an element. The padding area is the space between the content of the element and it's border. Contrary to margin-bottom values, negative values of padding-bottom are invalid.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/padding-bottom">MDN</a>
   */
  final val paddingBottom = Attr.simple("padding-bottom")

  /**
   * The padding-inline-end CSS property defines the logical inline end padding of an element, which maps to a physical padding depending on the element's writing mode, directionality, and text orientation. It corresponds to the padding-top, padding-right, padding-bottom, or padding-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/padding-inline-end">MDN</a>
   */
  final val paddingInlineEnd = Attr.simple("padding-inline-end")

  /**
   * The padding-inline-start CSS property defines the logical inline start padding of an element, which maps to a physical padding depending on the element's writing mode, directionality, and text orientation. It corresponds to the padding-top, padding-right, padding-bottom, or padding-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/padding-inline-start">MDN</a>
   */
  final val paddingInlineStart = Attr.simple("padding-inline-start")

  /**
   * The padding-left CSS property of an element sets the padding space required on the left side of an element. The padding area is the space between the content of the element and it's border. A negative value is not allowed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/padding-left">MDN</a>
   */
  final val paddingLeft = Attr.simple("padding-left")

  /**
   * The padding-right CSS property of an element sets the padding space required on the right side of an element. The padding area is the space between the content of the element and its border. Negative values are not allowed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/padding-right">MDN</a>
   */
  final val paddingRight = Attr.simple("padding-right")

  /**
   * The padding-top CSS property of an element sets the padding space required on the top of an element. The padding area is the space between the content of the element and its border. Contrary to margin-top values, negative values of padding-top are invalid.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/padding-top">MDN</a>
   */
  final val paddingTop = Attr.simple("padding-top")

  /**
   * The page-break-after CSS property adjusts page breaks after the current element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/page-break-after">MDN</a>
   */
  final val pageBreakAfter = Attr.simple("page-break-after")

  /**
   * The page-break-before CSS property adjusts page breaks before the current element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/page-break-before">MDN</a>
   */
  final val pageBreakBefore = Attr.simple("page-break-before")

  /**
   * The page-break-inside CSS property adjusts page breaks inside the current element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/page-break-inside">MDN</a>
   */
  final val pageBreakInside = Attr.simple("page-break-inside")

  /**
   * The perspective CSS property determines the distance between the z=0 plane and the user in order to give to the 3D-positioned element some perspective. Each 3D element with z>0 becomes larger; each 3D-element with z&lt;0 becomes smaller. The strength of the effect is determined by the value of this property.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/perspective">MDN</a>
   */
  final val perspective = Attr.simple("perspective")

  /**
   * The perspective-origin CSS property determines the position the viewer is looking at. It is used as the vanishing point by the perspective property.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/perspective-origin">MDN</a>
   */
  final val perspectiveOrigin = Attr.simple("perspective-origin")

  /**
   * The CSS property pointer-events allows authors to control under what circumstances (if any) a particular graphic element can become the target of mouse events. When this property is unspecified, the same characteristics of the visiblePainted value apply to SVG content.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/pointer-events">MDN</a>
   */
  final val pointerEvents = Attr.simple("pointer-events")

  /**
   * The position CSS property chooses alternative rules for positioning elements, designed to be useful for scripted animation effects.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/position">MDN</a>
   */
  final val position = Attr.simple("position")

  /**
   * The quotes CSS property indicates how user agents should render quotation marks.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/quotes">MDN</a>
   */
  final val quotes = Attr.simple("quotes")

  /**
   * The resize CSS property lets you control the resizability of an element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/resize">MDN</a>
   */
  final val resize = Attr.simple("resize")

  /**
   * The right CSS property specifies part of the position of positioned elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/right">MDN</a>
   */
  final val right = Attr.simple("right")

  /**
   * The ruby-align CSS property defines the distribution of the different ruby elements over the base.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/ruby-align">MDN</a>
   */
  final val rubyAlign = Attr.simple("ruby-align")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/ruby-merge">MDN</a>
   */
  final val rubyMerge = Attr.simple("ruby-merge")

  /**
   * The ruby-position CSS property defines the position of a ruby element relatives to its base element. It can be position over the element (over), under it (under), or between the characters, on their right side (inter-character).
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/ruby-position">MDN</a>
   */
  final val rubyPosition = Attr.simple("ruby-position")

  /**
   * The scroll-behavior CSS property specifies the scrolling behavior for a scrolling box, when scrolling happens due to navigation or CSSOM scrolling APIs. Any other scrolls, e.g. those that are performed by the user, are not affected by this property. When this property is specified on the root element, it applies to the viewport instead.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/scroll-behavior">MDN</a>
   */
  final val scrollBehavior = Attr.simple("scroll-behavior")

  /**
   * The shape-image-threshold CSS property defines the alpha channel threshold used to extract the shape using an image as the value for shape-outside. A value of 0.5 means that the shape will enclose all the pixels that are more than 50% opaque.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/shape-image-threshold">MDN</a>
   */
  final val shapeImageThreshold = Attr.simple("shape-image-threshold")

  /**
   * The shape-margin CSS property adds a margin to shape-outside.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/shape-margin">MDN</a>
   */
  final val shapeMargin = Attr.simple("shape-margin")

  /**
   * The shape-outside CSS property uses shape values to define the float area for a float and will cause inline content to wrap around the shape instead of the float's bounding box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/shape-outside">MDN</a>
   */
  final val shapeOutside = Attr.simple("shape-outside")

  /**
   * The table-layout CSS property defines the algorithm to be used to layout the table cells, rows, and columns.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/table-layout">MDN</a>
   */
  final val tableLayout = Attr.simple("table-layout")

  /**
   * The tab-size CSS property is used to customize the width of a tab (U+0009) character.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/tab-size">MDN</a>
   */
  final val tabSize = Attr.simple("tab-size")

  /**
   * The text-align CSS property describes how inline content like text is aligned in its parent block element. text-align does not control the alignment of block elements itself, only their inline content.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-align">MDN</a>
   */
  final val textAlign = Attr.simple("text-align")

  /**
   * The text-align-last CSS property describes how the last line of a block or a line, right before a forced line break, is aligned.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-align-last">MDN</a>
   */
  final val textAlignLast = Attr.simple("text-align-last")

  /**
   * The documentation about this has not yet been written; please consider contributing!
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-combine-upright">MDN</a>
   */
  final val textCombineUpright = Attr.simple("text-combine-upright")

  /**
   * The text-decoration CSS property is used to set the text formatting to underline, overline, line-through or blink. Underline and overline decorations are positioned under the text, line-through over it.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-decoration">MDN</a>
   */
  final val textDecoration = Attr.simple("text-decoration")

  /**
   * The text-decoration-color CSS property sets the color used when drawing underlines, overlines, or strike-throughs specified by text-decoration-line. This is the preferred way to color these text decorations, rather than using combinations of other HTML elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-decoration-color">MDN</a>
   */
  final val textDecorationColor = Attr.simple("text-decoration-color")

  /**
   * The text-decoration-line CSS property sets what kind of line decorations are added to an element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-decoration-line">MDN</a>
   */
  final val textDecorationLine = Attr.simple("text-decoration-line")

  /**
   * The text-decoration-style CSS property defines the style of the lines specified by text-decoration-line. The style applies to all lines, there is no way to define different style for each of the line defined by text-decoration-line.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-decoration-style">MDN</a>
   */
  final val textDecorationStyle = Attr.simple("text-decoration-style")

  /**
   * The text-indent CSS property specifies how much horizontal space should be left before the beginning of the first line of the text content of an element. Horizontal spacing is with respect to the left (or right, for right-to-left layout) edge of the containing block element's box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-indent">MDN</a>
   */
  final val textIndent = Attr.simple("text-indent")

  /**
   * The text-orientation CSS property defines the orientation of the text in a line. This property only has an effect in vertical mode, that is when writing-mode is not horizontal-tb. It is useful to control the display of writing in languages using vertical script, but also to deal with vertical table headers.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-orientation">MDN</a>
   */
  final val textOrientation = Attr.simple("text-orientation")

  /**
   * The text-overflow CSS property determines how overflowed content that is not displayed is signaled to the users. It can be clipped, or display an ellipsis ('…', U+2026 Horizontal Ellipsis) or a Web author-defined string.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-overflow">MDN</a>
   */
  final val textOverflow = Attr.simple("text-overflow")

  /**
   * The text-rendering CSS property provides information to the rendering engine about what to optimize for when rendering text. The browser makes trade-offs among speed, legibility, and geometric precision. The text-rendering property is an SVG property that is not defined in any CSS standard. However, Gecko and WebKit browsers let you apply this property to HTML and XML content on Windows, Mac OS X and Linux.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-rendering">MDN</a>
   */
  final val textRendering = Attr.simple("text-rendering")

  /**
   * The text-shadow CSS property adds shadows to text. It accepts a comma-separated list of shadows to be applied to the text and text-decorations of the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-shadow">MDN</a>
   */
  final val textShadow = Attr.simple("text-shadow")

  /**
   * The text-transform CSS property specifies how to capitalize an element's text. It can be used to make text appear in all-uppercase or all-lowercase, or with each word capitalized.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-transform">MDN</a>
   */
  final val textTransform = Attr.simple("text-transform")

  /**
   * The CSS text-underline-position property specifies the position of the underline which is set using the text-decoration property underline value.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/text-underline-position">MDN</a>
   */
  final val textUnderlinePosition = Attr.simple("text-underline-position")

  /**
   * The top CSS property specifies part of the position of positioned elements. It has no effect on non-positioned elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/top">MDN</a>
   */
  final val top = Attr.simple("top")

  /**
   * The touch-action CSS property specifies whether and how a given region can be manipulated by the user (for instance, by panning or zooming).
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/touch-action">MDN</a>
   */
  final val touchAction = Attr.simple("touch-action")

  /**
   * The CSS transform property lets you modify the coordinate space of the CSS visual formatting model. Using it, elements can be translated, rotated, scaled, and skewed according to the values set.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/transform">MDN</a>
   */
  final val transform = Attr.simple("transform")

  /**
   * The transform-origin CSS property lets you modify the origin for transformations of an element. For example, the transform-origin of the rotate() function is the centre of rotation. (This property is applied by first translating the element by the negated value of the property, then applying the element's transform, then translating by the property value.)
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/transform-origin">MDN</a>
   */
  final val transformOrigin = Attr.simple("transform-origin")

  /**
   * The transform-style CSS property determines if the children of the element are positioned in the 3D-space or are flattened in the plane of the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/transform-style">MDN</a>
   */
  final val transformStyle = Attr.simple("transform-style")

  /**
   * The transition-delay CSS property specifies the amount of time to wait between a change being requested to a property that is to be transitioned and the start of the transition effect.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/transition-delay">MDN</a>
   */
  final val transitionDelay = Attr.simple("transition-delay")

  /**
   * The transition-duration CSS property specifies the number of seconds or milliseconds a transition animation should take to complete. By default, the value is 0s, meaning that no animation will occur.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/transition-duration">MDN</a>
   */
  final val transitionDuration = Attr.simple("transition-duration")

  /**
   * The transition-property CSS property is used to specify the names of CSS properties to which a transition effect should be applied.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/transition-property">MDN</a>
   */
  final val transitionProperty = Attr.simple("transition-property")

  /**
   * The CSS transition-timing-function property is used to describe how the intermediate values of the CSS properties being affected by a transition effect are calculated. This in essence lets you establish an acceleration curve, so that the speed of the transition can vary over its duration.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/transition-timing-function">MDN</a>
   */
  final val transitionTimingFunction = Attr.simple("transition-timing-function")

  /**
   * The unicode-bidi CSS property together with the direction property relates to the handling of bidirectional text in a document. For example, if a block of text contains both left-to-right and right-to-left text then the user-agent uses a complex Unicode algorithm to decide how to display the text. This property overrides this algorithm and allows the developer to control the text embedding.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/unicode-bidi">MDN</a>
   */
  final val unicodeBidi = Attr.simple("unicode-bidi")

  /**
   * The unicode-range CSS descriptor sets the specific range of characters to be downloaded from a font defined by @font-face and made available for use on the current page.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/unicode-range">MDN</a>
   */
  final val unicodeRange = Attr.simple("unicode-range")

  /**
   * The vertical-align CSS property specifies the vertical alignment of an inline or table-cell box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/vertical-align">MDN</a>
   */
  final val verticalAlign = Attr.simple("vertical-align")

  /**
   * The visibility CSS property has two purposes:
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/visibility">MDN</a>
   */
  final val visibility = Attr.simple("visibility")

  /**
   * The white-space CSS property is used to to describe how white spaces inside the element is handled.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/white-space">MDN</a>
   */
  final val whiteSpace = Attr.simple("white-space")

  /**
   * The widows CSS property defines how many minimum lines must be left on top of a new page, on a paged media. In typography, a widow is the last line of a paragraph appearing alone at the top of a page. Setting the widows property allows to prevent widows to be left.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/widows">MDN</a>
   */
  final val widows = Attr.simple("widows")

  /**
   * The width CSS property specifies the width of the content area of an element. The content area is inside the padding, border, and margin of the element.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/width">MDN</a>
   */
  final val width = Attr.simple("width")

  /**
   * The will-change CSS property provides a way for authors to hint browsers about the kind of changes to be expected on an element, so that the browser can setup appropriate optimizations ahead of time before the element is actually changed.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/will-change">MDN</a>
   */
  final val willChange = Attr.simple("will-change")

  /**
   * The word-break CSS property is used to specify how (or if) to break lines within words.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/word-break">MDN</a>
   */
  final val wordBreak = Attr.simple("word-break")

  /**
   * The word-spacing CSS property specifies spacing behavior between tags and words.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/word-spacing">MDN</a>
   */
  final val wordSpacing = Attr.simple("word-spacing")

  /**
   * The word-wrap CSS property is used to specify whether or not the browser may break lines within words in order to prevent overflow (in other words, force wrapping) when an otherwise unbreakable string is too long to fit in its containing box.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/word-wrap">MDN</a>
   */
  final val wordWrap = Attr.simple("word-wrap")

  /**
   * The writing-mode CSS property defines whether lines of text are laid out horizontally or vertically and the direction in which blocks progress.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/writing-mode">MDN</a>
   */
  final val writingMode = Attr.simple("writing-mode")

  /**
   * The z-index CSS property specifies the z-order of an element and its descendants. When elements overlap, z-order determines which one covers the other. An element with a larger z-index generally covers an element with a lower one.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/z-index">MDN</a>
   */
  final val zIndex = Attr.simple("z-index")

  // -------------------------------------------------------------------------------------------------------------------
  // Overriding Attributes
  // -------------------------------------------------------------------------------------------------------------------

  /**
   * The CSS all shorthand property resets all properties, but unicode-bidi and direction to their initial or inherited value.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/all">MDN</a>
   */
  final val all = Attr.simpleG("all",
    AttrComparison.set(unicodeBidi, direction)(Unrelated, FullOverride))

  /**
   * The animation CSS property is a shorthand property for animation-name, animation-duration, animation-timing-function, animation-delay, animation-iteration-count, animation-direction, animation-fill-mode and animation-play-state.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/animation">MDN</a>
   */
  final val animation = Attr.simpleFO("animation",
    animationName, animationDuration, animationTimingFunction, animationDelay, animationIterationCount,
    animationDirection, animationFillMode, animationPlayState)

  /**
   * The background CSS property is a shorthand for setting the individual background values in a single place in the style sheet. background can be used to set the values for one or more of: background-clip, background-color, background-image, background-origin, background-position, background-repeat, background-size, and background-attachment.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/background">MDN</a>
   */
  final val background = Attr.simpleFO("background",
    backgroundClip, backgroundColor, backgroundImage, backgroundOrigin, backgroundPosition, backgroundRepeat,
    backgroundSize, backgroundAttachment)

  /**
   * The border-bottom CSS property is a shorthand that sets the values of border-bottom-color, border-bottom-style, and border-bottom-width. These properties describe the bottom border of elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-bottom">MDN</a>
   */
  final val borderBottom = Attr.simpleFO("border-bottom",
    borderBottomColor, borderBottomStyle, borderBottomWidth)

  /**
   * The border-left CSS property is a shorthand that sets the values of border-left-color, border-left-style, and border-left-width. These properties describe the left border of elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-left">MDN</a>
   */
  final val borderLeft = Attr.simpleFO("border-left",
    borderLeftColor, borderLeftStyle, borderLeftWidth)

  /**
   * The border-right CSS property is a shorthand that sets the values of border-right-color, border-right-style, and border-right-width. These properties describe the right border of elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-right">MDN</a>
   */
  final val borderRight = Attr.simpleFO("border-right",
    borderRightColor, borderRightStyle, borderRightWidth)

  /**
   * The border-top CSS property is a shorthand that sets the values of border-top-color, border-top-style, and border-top-width. These properties describe the top border of elements.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-top">MDN</a>
   */
  final val borderTop = Attr.simpleFO("border-top",
    borderTopColor, borderTopStyle, borderTopWidth)

  /**
   * The border-block-start CSS property is a shorthand property for setting the individual logical block start border property values in a single place in the style sheet. border-block-start can be used to set the values for one or more of: border-block-start-width, border-block-start-style, border-block-start-color. It maps to a physical border depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top, border-right, border-bottom, or border-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-start">MDN</a>
   */
  final val borderBlockStart = Attr.simpleFO("border-block-start",
    borderBlockStartWidth, borderBlockStartStyle, borderBlockStartColor,
    borderTop, borderRight, borderBottom, borderLeft)

  /**
   * The border-block-end CSS property is a shorthand property for setting the individual logical block end border property values in a single place in the style sheet. border-block-end can be used to set the values for one or more of: border-block-end-width, border-block-end-style, border-block-end-color. It maps to a physical border depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top, border-right, border-bottom, or border-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-block-end">MDN</a>
   */
  final val borderBlockEnd = Attr.simpleFO("border-block-end",
    borderBlockEndWidth, borderBlockEndStyle, borderBlockEndColor,
    borderTop, borderRight, borderBottom, borderLeft)

  /**
   * The border-color CSS property is a shorthand for setting the color of the four sides of an element's border: border-top-color, border-right-color, border-bottom-color, border-left-color
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-color">MDN</a>
   */
  final val borderColor = Attr.simpleFO("border-color",
    borderTopColor, borderRightColor, borderBottomColor, borderLeftColor)

  /**
   * The border-inline-end CSS property is a shorthand property for setting the individual logical inline end border property values in a single place in the style sheet. border-inline-end can be used to set the values for one or more of: border-inline-end-width, border-inline-end-style, border-inline-end-color. It maps to a physical border depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top, border-right, border-bottom, or border-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-end">MDN</a>
   */
  final val borderInlineEnd = Attr.simpleFO("border-inline-end",
    borderInlineEndWidth, borderInlineEndStyle, borderInlineEndColor)

  /**
   * The border-inline-start CSS property is a shorthand property for setting the individual logical inline start border property values in a single place in the style sheet. border-inline-start can be used to set the values for one or more of: border-inline-start-width, border-inline-start-style, border-inline-start-color. It maps to a physical border depending on the element's writing mode, directionality, and text orientation. It corresponds to the border-top, border-right, border-bottom, or border-left property depending on the values defined for writing-mode, direction, and text-orientation.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border-inline-start">MDN</a>
   */
  final val borderInlineStart = Attr.simpleFO("border-inline-start",
    borderInlineStartWidth, borderInlineStartStyle, borderInlineStartColor)

  /**
   * The border CSS property is a shorthand property for setting the individual border property values in a single place in the style sheet. border can be used to set the values for one or more of: border-width, border-style, border-color.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/border">MDN</a>
   */
  final val border = Attr.simpleFO("border",
    borderWidth, borderStyle, borderColor)

  /**
   * The columns CSS property is a shorthand property allowing to set both the column-width and the column-count properties at the same time.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/columns">MDN</a>
   */
  final val columns = Attr.simpleFO("columns",
    columnWidth, columnCount)

  /**
   * In multi-column layouts, the column-rule CSS property specifies a straight line, or "rule", to be drawn between each column. It is a convenient shorthand to avoid setting each of the individual column-rule-* properties separately : column-rule-width, column-rule-style and column-rule-color.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/column-rule">MDN</a>
   */
  final val columnRule = Attr.simpleFO("column-rule",
    columnRuleWidth, columnRuleStyle, columnRuleColor)

  /**
   * The flex CSS property is a shorthand property specifying the ability of a flex item to alter its dimensions to fill available space. Flex items can be stretched to use available space proportional to their flex grow factor or their flex shrink factor to prevent overflow.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/flex">MDN</a>
   */
  final val flex = Attr.simpleFO("flex",
    flexGrow, flexShrink, flexBasis)

  /**
   * The CSS flex-flow property is a shorthand property for flex-direction and flex-wrap individual properties.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/flex-flow">MDN</a>
   */
  final val flexFlow = Attr.simpleFO("flex-flow",
    flexDirection, flexWrap)

  /**
   * The font-variant CSS property selects a normal, or small-caps face from a font family. Setting the CSS Level 2 (Revision 1) values of the  font-variant property, that is normal or small-caps, is also possible by using the font shorthand.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font-variant">MDN</a>
   */
  final val fontVariant = Attr.simpleFO("font-variant",
    fontVariantAlternates, fontVariantCaps, fontVariantEastAsian, fontVariantLigatures, fontVariantNumeric,
    fontVariantPosition)

  /**
   * The font CSS property is either a shorthand property for setting font-style, font-variant, font-weight, font-size, line-height and font-family, or a way to set the element's font to a system font, using specific keywords.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/font">MDN</a>
   */
  final val font = Attr.simpleFO("font",
    fontStyle, fontVariant, fontWeight, fontSize, lineHeight, fontFamily)

  /**
   * The list-style CSS property is a shorthand property for setting list-style-type, list-style-image and list-style-position.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/list-style">MDN</a>
   */
  final val listStyle = Attr.simpleFO("list-style",
    listStyleType, listStyleImage, listStylePosition)

  /**
   * The margin CSS property sets the margin for all four sides. It is a shorthand to avoid setting each side separately with the other margin properties: margin-top, margin-right, margin-bottom and margin-left.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/margin">MDN</a>
   */
  final val margin = Attr.simpleFO("margin",
    marginTop, marginRight, marginBottom, marginLeft)

  /**
   * The CSS outline property is a shorthand property for setting one or more of the individual outline properties outline-style, outline-width and outline-color in a single declaration. In most cases the use of this shortcut is preferable and more convenient.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/outline">MDN</a>
   */
  final val outline = Attr.simpleFO("outline",
    outlineStyle, outlineWidth, outlineColor)

  /**
   * The CSS transition property is a shorthand property for transition-property, transition-duration, transition-timing-function, and transition-delay. It allows to define the transition between two states of an element. Different states may be defined using pseudo-classes like :hover or :active or dynamically set using JavaScript.
   *
   * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/transition">MDN</a>
   */
  final val transition = Attr.simpleFO("transition",
    transitionProperty, transitionDuration, transitionTimingFunction, transitionDelay)
}