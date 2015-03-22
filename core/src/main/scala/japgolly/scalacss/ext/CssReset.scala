package japgolly.scalacss.ext

import japgolly.scalacss._
import Dsl.{unsafeRoot => $, _}

/**
 * A collection of HTML element and attribute style-normalizations.
 */
object CssReset {
  implicit def composition = Compose.trust

  val webkitAppearance = Attr.real("-webkit-appearance")

  // ===================================================================================================================

  /**
   * normalize.css v3.0.2 | MIT License | git.io/normalize
   *
   * https://github.com/necolas/normalize.css/blob/master/normalize.css
   */
  def normaliseCss = style(

    $("html")(
      fontFamily := "sans-serif",
      textSizeAdjust(100.%%)
    ),

    $("body")(
      margin.`0`
    ),

    $("article,aside,details,figcaption,figure,footer,header,hgroup,main,menu,nav,section,summary")(
      display.block
    ),

    $("audio,canvas,progress,video")(
      display.inlineBlock,
      verticalAlign.baseline
    ),

    $("audio:not([controls])")(
      display.none,
      height.`0`
    ),

    $("[hidden],template")(
      display.none
    ),

    $("a")(
      backgroundColor.transparent
    ),

    $("a:active,a:hover")(
      outline.`0`
    ),

    $("abbr[title]")(
      borderBottom(1 px, dotted)
    ),

    $("b,bold")(
      fontWeight.bold
    ),

    $("dfn")(
      fontStyle.italic
    ),

    $("h1")(
      fontSize(2 em),
      margin(0.67 em, `0`)
    ),

    $("mark")(
      background := "#ff0",
      color.black
    ),

    $("small")(
      fontSize(80 %%)
    ),

    $("sub,sup")(
      fontSize(75 %%),
      lineHeight.`0`,
      position.relative,
      verticalAlign.baseline
    ),

    $("sup")(
      top(-0.5 em)
    ),

    $("sub")(
      bottom(-0.25 em)
    ),

    $("img")(
      border.`0`
    ),

    $("svg:not(:root)")(
      overflow.hidden
    ),

    $("figure")(
      margin(1 em, 40 px)
    ),

    $("hr")(
      boxSizing.contentBox,
      height.`0`
    ),

    $("pre")(
      overflow.auto
    ),

    $("code,kbd,pre,samp")(
      // Hack that fixes the inheritance and scaling of font-size for preformated text.
      // The duplication of monospace is intentional.
      fontFamily := "monospace, monospace",
      fontSize(1 em)
    ),

    $("button, input, optgroup, select, textarea")(
      color.inherit,
      font := Literal.inherit,
      margin.`0`
    ),

    $("button")(
      overflow.visible
    ),

    $("button,select")(
      textTransform.none
    ),

    $("""button, html input[type="button"], input[type="reset"], input[type="submit"]""")(
      webkitAppearance := "button",
      cursor.pointer
    ),

    $("button[disabled], html input[disabled]")(
      cursor.default
    ),

    $("button::-moz-focus-inner, input::-moz-focus-inner")(
      border.`0`,
      padding.`0`
    ),

    $("input")(
      lineHeight.normal
    ),

    $("""input[type="checkbox"],input[type="radio"]""")(
      boxSizing.borderBox,
      padding.`0`
    ),

    $("""input[type="number"]::-webkit-inner-spin-button, input[type="number"]::-webkit-outer-spin-button""")(
      height.auto
    ),

    $("""input[type="search"]""")(
      webkitAppearance := "textfield",
      boxSizing.contentBox
    ),

    $("""input[type="search"]::-webkit-search-cancel-button,input[type="search"]::-webkit-search-decoration""")(
      webkitAppearance := "none"
    ),

    $("fieldset")(
      border(1 px, solid, Color("#c0c0c0")),
      padding(0.35 em, 0.625 em, 0.75 em),
      margin(`0`, 2 px)
    ),

    $("legend")(
      border.`0`,
      padding.`0`
    ),

    $("textarea")(
      overflow.auto
    ),

    $("optgroup")(
      fontWeight.bold
    ),

    $("table")(
      borderCollapse.collapse,
      borderSpacing.`0`
    ),

    $("td,th")(
      padding.`0`
    )
  )

  // ===================================================================================================================

  /**
   * Eric Meyer’s “Reset CSS” 2.0
   * http://meyerweb.com/eric/tools/css/reset/
   * License: none (public domain)
   */
  def meyer = style(
    $( """
         |html, body, div, span, applet, object, iframe,
         |h1, h2, h3, h4, h5, h6, p, blockquote, pre,
         |a, abbr, acronym, address, big, cite, code,
         |del, dfn, em, img, ins, kbd, q, s, samp,
         |small, strike, strong, sub, sup, tt, var,
         |b, u, i, center,
         |dl, dt, dd, ol, ul, li,
         |fieldset, form, label, legend,
         |table, caption, tbody, tfoot, thead, tr, th, td,
         |article, aside, canvas, details, embed,
         |figure, figcaption, footer, header, hgroup,
         |menu, nav, output, ruby, section, summary,
         |time, mark, audio, video
       """.stripMargin.replaceAll("\\s+", ""))(
        margin.`0`,
        padding.`0`,
        border.`0`,
        fontSize(100 %%),
        font := Literal.inherit,
        verticalAlign.baseline
      ),

    $("article, aside, details, figcaption, figure, footer, header, hgroup, menu, nav, section")(
      display.block
    ),

    $("body")(
      lineHeight(1)
    ),

    $("ol,ul")(
      listStyle := none // TODO
    ),

    $("blockquote, q")(
      quotes.none
    ),

    $("blockquote:before, blockquote:after, q:before, q:after")(
      content := "''",
      content := none
    ),

    $("table")(
      borderCollapse.collapse,
      borderSpacing.`0`
    )
  )
}