package japgolly.scalacss.mutable

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
        margin(12.px),
        padding(2.ex),
        cursor.pointer,
        backgroundImage := "radial-gradient(5em circle at top left, yellow, blue)",

        Hover(
          fontWeight.normal,
          lineHeight(1.em),
          padding.`0`,
          cursor.zoom_in
        ),

        Visited.not(FirstChild)(
          fontWeight.bold
        ),

        unsafeChild("nav.debug")(
          backgroundColor("#f88"),
          color.black.important,

          unsafeChild("h1")(
            fontSize(150.%%)
          )
        )
      )

    val everythingOk: Boolean => StyleA =
      boolStyle(ok => styleS(
        backgroundColor(if (ok) green else red),
        maxWidth(80.ex)
      ))

    val indent: Int => StyleA =
      styleF(Domain.ofRange(1 to 3))(i =>
        styleS(paddingLeft(i * 4.ex)))
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
        |  padding: 2ex;
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
        |  font-weight: bold;
        |}
        |
        |.demo-0001 nav.debug {
        |  background-color: #f88;
        |  color: black !important;
        |}
        |
        |.demo-0001 nav.debug h1 {
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
      """.stripMargin))

    assertEq(SS.everythingOk(true) .className.value, "demo-0002")
    assertEq(SS.everythingOk(false).className.value, "demo-0003")

    assertEq(SS.indent(1).className.value, "demo-0004")
    assertEq(SS.indent(2).className.value, "demo-0005")
    assertEq(SS.indent(3).className.value, "demo-0006")
  }
}
