package japgolly.scalacss.mutable

import scala.concurrent.duration._
import utest._
import japgolly.scalacss._
import TestUtil._
import Register.{NameGen, ErrorHandler}

object UsageTest extends TestSuite {

  def newRegister = new Register(NameGen.numbered("demo-"), ErrorHandler.noisy)

  object SS extends StyleSheet(newRegister) {
    import Attrs._
    import Pseudo._
    implicit def composition = Compose.safe

    // TODO TODO TODO TODO TODO TODO
    import Literal._ // Conflicts with some attrs! DSL should correct in trait Literals
    import Color._
    // TODO TODO TODO TODO TODO TODO


    val s1: StyleA =
      style(
        margin(12 px),
        padding(0.5 ex),
        cursor.pointer,
        backgroundImage := "radial-gradient(5em circle at top left, yellow, blue)",

        Hover(
          fontWeight.normal,
          lineHeight(1.em),
          padding.`0`,
          cursor.zoom_in
        ),

        Visited.not(FirstChild)(
          animationDelay(1 minute, 50 millis),
          fontWeight.bold,
          font := inherit
        ),

        unsafeChild("nav.debug")(
          backgroundColor("#f88"),
          color.black.important,

          unsafeChild("h1")(
            lineHeight(97.5 %%),
            fontSize(150 %%)
          )
        )
      )

    /** Style requiring boolean */
    val everythingOk: Boolean => StyleA =
      boolStyle(ok => styleS(
        backgroundColor(if (ok) green else red),
        maxWidth(80.ex)
      ))

    /** Style requiring int */
    val indent: Int => StyleA =
      styleF(Domain.ofRange(1 to 3))(i =>
        styleS(paddingLeft(i * 4.ex)))


    /** Style hooking into Bootstrap */
    val sb = style(
      addClassNames("btn", "btn-default"),
      marginTop.inherit
    )
  }

  def norm(css: String) = css.trim

  override val tests = TestSuite {
    val css = SS.render(StringRenderer.defaultPretty, Env.empty)
    //  println(norm(css))
    assertEq(norm(css), norm(
      """
        |.demo-0001 {
        |  cursor: pointer;
        |  margin: 12px;
        |  padding: 0.5ex;
        |  background-image: -o-radial-gradient(5em circle at top left, yellow, blue);
        |  background-image: -webkit-radial-gradient(5em circle at top left, yellow, blue);
        |  background-image: -moz-radial-gradient(5em circle at top left, yellow, blue);
        |  background-image: radial-gradient(5em circle at top left, yellow, blue);
        |}
        |
        |.demo-0001:hover {
        |  font-weight: normal;
        |  line-height: 1em;
        |  padding: 0;
        |  cursor: -webkit-zoom-in;
        |  cursor: -moz-zoom-in;
        |  cursor: -o-zoom-in;
        |  cursor: zoom-in;
        |}
        |
        |.demo-0001:not(:first-child):visited {
        |  -o-animation-delay: 60s,50ms;
        |  -webkit-animation-delay: 60s,50ms;
        |  -moz-animation-delay: 60s,50ms;
        |  animation-delay: 60s,50ms;
        |  font-weight: bold;
        |  font: inherit;
        |}
        |
        |.demo-0001 nav.debug {
        |  background-color: #f88;
        |  color: black !important;
        |}
        |
        |.demo-0001 nav.debug h1 {
        |  line-height: 97.5%;
        |  font-size: 150%;
        |}
        |
        |.demo-0002 {
        |  background-color: green;
        |  max-width: 80ex;
        |}
        |
        |.demo-0003 {
        |  background-color: red;
        |  max-width: 80ex;
        |}
        |
        |.demo-0004 {
        |  padding-left: 4ex;
        |}
        |
        |.demo-0005 {
        |  padding-left: 8ex;
        |}
        |
        |.demo-0006 {
        |  padding-left: 12ex;
        |}
        |
        |.demo-0007 {
        |  margin-top: inherit;
        |}
      """.stripMargin))

    assertEq(SS.everythingOk(true) .htmlClass, "demo-0002")
    assertEq(SS.everythingOk(false).htmlClass, "demo-0003")

    assertEq(SS.indent(1).htmlClass, "demo-0004")
    assertEq(SS.indent(2).htmlClass, "demo-0005")
    assertEq(SS.indent(3).htmlClass, "demo-0006")

    assertEq(SS.sb.htmlClass, "demo-0007 btn btn-default")
  }
}
