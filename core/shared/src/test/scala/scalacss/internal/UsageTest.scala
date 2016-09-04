package scalacss.internal

import utest._
import scalacss.test.TestUtil._
import Dsl._
import Pseudo._

object UsageTest extends TestSuite {
  implicit def composition = Compose.safe

  val thing = 2.ex

  val s1 =
    style(
      margin(12.px),
      paddingTop(-thing),
      paddingLeft(thing * 2),
      paddingRight(thing / 4),

      Hover(
        fontWeight.normal,
        lineHeight(1.em),
        padding.`0`),

      Visited.not(FirstChild)(
        fontWeight.bold),

      unsafeChild("nav.debug")(
        backgroundColor(c"#f88"),
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
          |  padding-top: -2ex;
          |  padding-left: 4ex;
          |  padding-right: 0.5ex;
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
