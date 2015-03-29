package japgolly.scalacss.full

import scala.concurrent.duration._
import japgolly.scalacss.Defaults._

//object CopyDefaultsForInline extends Defaults
//import CopyDefaultsForInline._

object MyInline extends StyleSheet.Inline {
  import dsl._

  val s1 =
    style(
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

      &.visited.not(_.FirstChild)(
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
        borderColor.red
      )
    )

  /** Style requiring boolean */
  val everythingOk =
    boolStyle(ok => styleS(
      backgroundColor(if (ok) green else red),
      maxWidth(80.ex)
    ))

  /** Style requiring int */
  val indent: Int => StyleA =
    intStyle(1 to 3)(i =>
      styleS(paddingLeft(i * 4.ex)))

  /** Style hooking into Bootstrap */
  val sb = style(
    addClassNames("btn", "btn-default"),
    marginTop.inherit
  )

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
  import japgolly.scalacss.TestUtil._

  def norm(css: String) = css.trim

  override val tests = TestSuite {
    'css - assertEq(norm(MyInline.render), norm(
      """
        |@media not handheld and (orientation:landscape) and (color) {
        |  .scalacss-0001 {
        |    padding-left: 500px;
        |    padding-right: 500px;
        |  }
        |}
        |
        |@media tv and (min-device-aspect-ratio:3/4), all and (resolution:300dpi) {
        |  .scalacss-0001 {
        |    margin-top: 10em;
        |    margin-bottom: 10em;
        |  }
        |}
        |
        |.scalacss-0001:not(:first-child):visited {
        |  -o-animation-delay: 60s,50ms;
        |  -webkit-animation-delay: 60s,50ms;
        |  -moz-animation-delay: 60s,50ms;
        |  animation-delay: 60s,50ms;
        |  font-weight: bold;
        |  font: inherit;
        |}
        |
        |.scalacss-0001:hover {
        |  font-weight: normal;
        |  line-height: 1em;
        |  padding: 0;
        |  cursor: -webkit-zoom-in;
        |  cursor: -moz-zoom-in;
        |  cursor: -o-zoom-in;
        |  cursor: zoom-in;
        |}
        |
        |.scalacss-0001 {
        |  -webkit-text-decoration-line: underline overline;
        |  -moz-text-decoration-line: underline overline;
        |  text-decoration-line: underline overline;
        |  padding: 0.5ex;
        |  margin: 12px;
        |  cursor: pointer;
        |  background-image: -o-radial-gradient(5em circle at top left, yellow, blue);
        |  background-image: -webkit-radial-gradient(5em circle at top left, yellow, blue);
        |  background-image: -moz-radial-gradient(5em circle at top left, yellow, blue);
        |  background-image: radial-gradient(5em circle at top left, yellow, blue);
        |}
        |
        |.scalacss-0001 nav.debug {
        |  background-color: #f88;
        |  color: black !important;
        |}
        |
        |.scalacss-0001 nav.debug h1 {
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
        |  border-color: red;
        |}
        |
        |.scalacss-0002 {
        |  background-color: green;
        |  max-width: 80ex;
        |}
        |
        |.scalacss-0003 {
        |  background-color: red;
        |  max-width: 80ex;
        |}
        |
        |.scalacss-0004 {
        |  padding-left: 4ex;
        |}
        |
        |.scalacss-0005 {
        |  padding-left: 8ex;
        |}
        |
        |.scalacss-0006 {
        |  padding-left: 12ex;
        |}
        |
        |.scalacss-0007 {
        |  margin-top: inherit;
        |}
        |
        |.scalacss-0008 {
        |  border: 1px solid black;
        |  padding: 1ex;
        |}
        |
        |.scalacss-0009 {
        |  font-weight: bold;
        |}
        |
        |.scalacss-0010 {
        |  margin: 4ex;
        |  background-color: #eee;
        |}
      """.stripMargin))

    'classnames {
      assertEq(MyInline.everythingOk(true) .htmlClass, "scalacss-0002")
      assertEq(MyInline.everythingOk(false).htmlClass, "scalacss-0003")

      assertEq(MyInline.indent(1).htmlClass, "scalacss-0004")
      assertEq(MyInline.indent(2).htmlClass, "scalacss-0005")
      assertEq(MyInline.indent(3).htmlClass, "scalacss-0006")

      assertEq(MyInline.sb.htmlClass, "scalacss-0007 btn btn-default")

      import shapeless.syntax.singleton._ // TODO
      val classNames =
        MyInline.sc('outer)(o =>
                    _('label)(l =>
                      _('checkbox)(c =>
                        List(o, l, c).map(_.htmlClass))))
      assertEq(classNames, List("scalacss-0008", "scalacss-0009", "scalacss-0010"))
    }
  }
}
