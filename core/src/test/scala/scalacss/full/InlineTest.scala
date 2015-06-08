package scalacss.full

import scala.concurrent.duration._
import scalacss.Defaults._

//object CopyDefaultsForInline extends Defaults
//import CopyDefaultsForInline._

object MyInline extends StyleSheet.Inline {
  import dsl._

  val noMacrosOrClassnameHintHere =
    style("manual")(
      margin(12 px),
      padding(0.5 ex),
      cursor.pointer,
      textDecorationLine.underline.overline,
      backgroundImage := "radial-gradient(5em circle at top left, yellow, blue)",

      &.hover(
        fontWeight.normal,
        lineHeight(1 em),
        padding.`0`,
        cursor.zoomIn
      ),

      &.visited.not(_.firstChild)(
        animationDelay(1 minute, 50 millis),
        fontWeight.bold,
        font := ^.inherit
      ),

      &(media.tv.minDeviceAspectRatio(4 :/: 3) & media.all.resolution(300 dpi))(
        margin.vertical(10 em)
      ),

      &(media.not.handheld.landscape.color)(
        padding.horizontal(500 px)
      ),

      unsafeChild("nav.debug")(
        backgroundColor("#f88"),
        color.black.important,

        unsafeChild("h1")(
          lineHeight(97.5 %%),
          fontSize(150 %%)
        )
      ),

      unsafeRoot("blockquote:before, blockquote:after")(
        content := "''",
        content := none
      ),
      unsafeRoot(".DEBUG")(
        borderColor("#080".color)
      )
    )

  val `it's a mixin!` = mixin(color.brown)

  val medianess = style(
    media.maxWidth(100 px)(
      // https://github.com/japgolly/scalacss/issues/41
      unsafeChild("span")(backgroundColor(yellow)),
      // https://github.com/japgolly/scalacss/issues/38
      `it's a mixin!`)
  )

  val condMixins = mixin(
    display.block,
    media.maxWidth(100 px)(margin.auto),
    &.hover(color.red))

  val condMixinP = style(&.hover(condMixins))
  val condMixinQ = style(media.maxWidth(100 px)(condMixins))

  /** Style requiring boolean */
  val everythingOk =
    boolStyle(ok => styleS(
      backgroundColor(if (ok) green else red),
      maxWidth(80.ex)
    ))

  /** Style requiring int */
  val indent: Int => StyleA =
    intStyle(1 to 3)(i =>
      styleS(
        paddingLeft(i * 4.ex),
        mixinIf(i == 2)(color.red),
        mixinIfElse(i == 1)(color.blue)(marginTop(1 em))
    ))

  /** Style applying Bootstrap */
  val sb1 = style(addClassNames("btn", "btn-default"))

  /** Style extending into Bootstrap */
  val sb2 = style(
    addClassNames("btn", "btn-default"),
    marginTop.inherit
  )

  val empty = style(null: String)()

  /** Composite style */
  val sc = styleC {
    val o = styleS(border(1 px, solid, black), padding(1 ex))
    val l = styleS(fontWeight.bold)
    val c = styleS(margin(4 ex), backgroundColor("#eee"))
    o.named('outer) :*: l.named('label) :*: c.named('checkbox)
  }
}

object InlineTest extends utest.TestSuite {
  import utest._
  import scalacss.TestUtil._

  def norm(css: String) = css.trim

  override val tests = TestSuite {
    'css - assertEq(norm(MyInline.render), norm(
      """
        |@media not handheld and (orientation:landscape) and (color) {
        |  .manual {
        |    padding-left: 500px;
        |    padding-right: 500px;
        |  }
        |}
        |
        |@media tv and (min-device-aspect-ratio:3/4), all and (resolution:300dpi) {
        |  .manual {
        |    margin-top: 10em;
        |    margin-bottom: 10em;
        |  }
        |}
        |
        |@media (max-width:100px) {
        |  .MyInline-medianess {
        |    color: brown;
        |  }
        |  .MyInline-medianess span {
        |    background-color: yellow;
        |  }
        |  .MyInline-condMixinP:hover {
        |    margin: auto;
        |  }
        |  .MyInline-condMixinQ {
        |    display: block;
        |    margin: auto;
        |  }
        |  .MyInline-condMixinQ:hover {
        |    color: red;
        |  }
        |}
        |
        |.manual:not(:first-child):visited {
        |  -o-animation-delay: 60s,50ms;
        |  -webkit-animation-delay: 60s,50ms;
        |  -moz-animation-delay: 60s,50ms;
        |  animation-delay: 60s,50ms;
        |  font-weight: bold;
        |  font: inherit;
        |}
        |
        |.manual:hover {
        |  font-weight: normal;
        |  line-height: 1em;
        |  padding: 0;
        |  cursor: -webkit-zoom-in;
        |  cursor: -moz-zoom-in;
        |  cursor: -o-zoom-in;
        |  cursor: zoom-in;
        |}
        |
        |.manual {
        |  margin: 12px;
        |  padding: 0.5ex;
        |  cursor: pointer;
        |  -webkit-text-decoration-line: underline overline;
        |  -moz-text-decoration-line: underline overline;
        |  text-decoration-line: underline overline;
        |  background-image: -o-radial-gradient(5em circle at top left, yellow, blue);
        |  background-image: -webkit-radial-gradient(5em circle at top left, yellow, blue);
        |  background-image: -moz-radial-gradient(5em circle at top left, yellow, blue);
        |  background-image: radial-gradient(5em circle at top left, yellow, blue);
        |}
        |
        |.manual nav.debug {
        |  background-color: #f88;
        |  color: black !important;
        |}
        |
        |.manual nav.debug h1 {
        |  line-height: 97.5%;
        |  font-size: 150%;
        |}
        |
        |blockquote:before, blockquote:after {
        |  content: '';
        |  content: none;
        |}
        |
        |.DEBUG {
        |  border-color: #080;
        |}
        |
        |.MyInline-condMixinP:hover {
        |  display: block;
        |  color: red;
        |}
        |
        |.MyInline-everythingOk-t {
        |  background-color: green;
        |  max-width: 80ex;
        |}
        |
        |.MyInline-everythingOk-f {
        |  background-color: red;
        |  max-width: 80ex;
        |}
        |
        |.MyInline-indent-1 {
        |  padding-left: 4ex;
        |  color: blue;
        |}
        |
        |.MyInline-indent-2 {
        |  padding-left: 8ex;
        |  color: red;
        |  margin-top: 1em;
        |}
        |
        |.MyInline-indent-3 {
        |  padding-left: 12ex;
        |  margin-top: 1em;
        |}
        |
        |.MyInline-sb2 {
        |  margin-top: inherit;
        |}
        |
        |.MyInline-0002 {
        |  border: 1px solid black;
        |  padding: 1ex;
        |}
        |
        |.MyInline-0003 {
        |  font-weight: bold;
        |}
        |
        |.MyInline-0004 {
        |  margin: 4ex;
        |  background-color: #eee;
        |}
      """.stripMargin))

    'classnames {
      assertEq(MyInline.noMacrosOrClassnameHintHere.htmlClass, "manual")

      assertEq(MyInline.everythingOk(true) .htmlClass, "MyInline-everythingOk-t")
      assertEq(MyInline.everythingOk(false).htmlClass, "MyInline-everythingOk-f")

      assertEq(MyInline.indent(1).htmlClass, "MyInline-indent-1")
      assertEq(MyInline.indent(2).htmlClass, "MyInline-indent-2")
      assertEq(MyInline.indent(3).htmlClass, "MyInline-indent-3")

      assertEq(MyInline.sb1.htmlClass, "btn btn-default")
      assertEq(MyInline.sb2.htmlClass, "MyInline-sb2 btn btn-default")

      assertEq(MyInline.empty.htmlClass, "MyInline-0001")

      import shapeless.syntax.singleton._ // TODO
      val classNames =
        MyInline.sc('outer)(o =>
                    _('label)(l =>
                      _('checkbox)(c =>
                        List(o, l, c).map(_.htmlClass))))
      assertEq(classNames, List("MyInline-0002", "MyInline-0003", "MyInline-0004"))
    }
  }
}
