package scalacss.full

import scala.concurrent.duration._
import scalacss.Defaults._

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
        borderColor("#080".color)
      )
    )

  val `it's a mixin!` = mixin(color.brown)

  val medianess = style("medianess")(
    media.maxWidth(100 px)(
      // https://github.com/japgolly/scalacss/issues/41
      unsafeChild("span")(backgroundColor(yellow)),
      // https://github.com/japgolly/scalacss/issues/38
      `it's a mixin!`)
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

  val empty = style()

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
        |  .MyInline-0001 {
        |    padding-left: 500px;
        |    padding-right: 500px;
        |  }
        |}
        |
        |@media tv and (min-device-aspect-ratio:3/4), all and (resolution:300dpi) {
        |  .MyInline-0001 {
        |    margin-top: 10em;
        |    margin-bottom: 10em;
        |  }
        |}
        |
        |@media (max-width:100px) {
        |  .medianess {
        |    color: brown;
        |  }
        |  .medianess span {
        |    background-color: yellow;
        |  }
        |}
        |
        |.MyInline-0001:not(:first-child):visited {
        |  -o-animation-delay: 60s,50ms;
        |  -webkit-animation-delay: 60s,50ms;
        |  -moz-animation-delay: 60s,50ms;
        |  animation-delay: 60s,50ms;
        |  font-weight: bold;
        |  font: inherit;
        |}
        |
        |.MyInline-0001:hover {
        |  font-weight: normal;
        |  line-height: 1em;
        |  padding: 0;
        |  cursor: -webkit-zoom-in;
        |  cursor: -moz-zoom-in;
        |  cursor: -o-zoom-in;
        |  cursor: zoom-in;
        |}
        |
        |.MyInline-0001 {
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
        |.MyInline-0001 nav.debug {
        |  background-color: #f88;
        |  color: black !important;
        |}
        |
        |.MyInline-0001 nav.debug h1 {
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
        |.MyInline-0002 {
        |  background-color: green;
        |  max-width: 80ex;
        |}
        |
        |.MyInline-0003 {
        |  background-color: red;
        |  max-width: 80ex;
        |}
        |
        |.MyInline-0004 {
        |  padding-left: 4ex;
        |  color: blue;
        |}
        |
        |.MyInline-0005 {
        |  padding-left: 8ex;
        |  color: red;
        |  margin-top: 1em;
        |}
        |
        |.MyInline-0006 {
        |  padding-left: 12ex;
        |  margin-top: 1em;
        |}
        |
        |.MyInline-0007 {
        |  margin-top: inherit;
        |}
        |
        |.MyInline-0009 {
        |  border: 1px solid black;
        |  padding: 1ex;
        |}
        |
        |.MyInline-0010 {
        |  font-weight: bold;
        |}
        |
        |.MyInline-0011 {
        |  margin: 4ex;
        |  background-color: #eee;
        |}
      """.stripMargin))

    'classnames {
      assertEq(MyInline.everythingOk(true) .htmlClass, "MyInline-0002")
      assertEq(MyInline.everythingOk(false).htmlClass, "MyInline-0003")

      assertEq(MyInline.indent(1).htmlClass, "MyInline-0004")
      assertEq(MyInline.indent(2).htmlClass, "MyInline-0005")
      assertEq(MyInline.indent(3).htmlClass, "MyInline-0006")

      assertEq(MyInline.sb1.htmlClass, "btn btn-default")
      assertEq(MyInline.sb2.htmlClass, "MyInline-0007 btn btn-default")

      assertEq(MyInline.empty.htmlClass, "MyInline-0008")

      import shapeless.syntax.singleton._ // TODO
      val classNames =
        MyInline.sc('outer)(o =>
                    _('label)(l =>
                      _('checkbox)(c =>
                        List(o, l, c).map(_.htmlClass))))
      assertEq(classNames, List("MyInline-0009", "MyInline-0010", "MyInline-0011"))
    }
  }
}
