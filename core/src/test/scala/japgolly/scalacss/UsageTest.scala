package japgolly.scalacss

import utest._
import TestUtil._
import Dsl._
import Pseudo._

object UsageTest extends TestSuite {
  implicit def composition = Compose.safe

  val s1 =
    style(
      margin(12.px),
      padding(2.ex),

      Hover(
        fontWeight.normal,
        lineHeight(1.em),
        padding.`0`),

      Visited.not(FirstChild)(
        fontWeight.bold),

      unsafeChild("nav.debug")(
        backgroundColor("#f88"),
        color.black,

        unsafeChild("q")(
          quotes("'", "'"),
          quotes("<", ">")("\"", "\"")
        )
      )
    )

  override val tests = TestSuite {
    'maintest {
      val css = StringRenderer defaultPretty Css.style(".a1", s1)(Env.empty)
      // println(css)
      assertEq(css.trim,
        """
          |.a1 {
          |  margin: 12px;
          |  padding: 2ex;
          |}
          |
          |.a1:hover {
          |  font-weight: normal;
          |  line-height: 1em;
          |  padding: 0;
          |}
          |
          |.a1:not(:first-child):visited {
          |  font-weight: bold;
          |}
          |
          |.a1 nav.debug {
          |  background-color: #f88;
          |  color: black;
          |}
          |
          |.a1 nav.debug q {
          |  quotes: "'" "'";
          |  quotes: '<' '>' '"' '"';
          |}
        """.stripMargin.trim)
    }
  }
}
