package scalacss

import utest._
import scalacss.TestUtil._
import Defaults._
import ScalatagsCss._

object ScalatagsTest extends TestSuite {

  object MyStyles extends StyleSheet.Inline {
    import dsl._

    val input = style(
      fontWeight.bold,
      padding(0.3 ex, 2 ex)
    )

    val other = style(
      margin(1 ex)
    )

    val bootstrappy = style(addClassName("btn btn-default"))
  }

  type T = scalatags.Text.TypedTag[String]
  import scalatags.Text.all._

  override val tests = TestSuite {

    'styleTag {
      val html = MyStyles.render[T].toString()
      assertEq(html, """<style type="text/css">.ScalatagsTest_MyStyles-input {
                       |  font-weight: bold;
                       |  padding: 0.3ex 2ex;
                       |}
                       |
                       |.ScalatagsTest_MyStyles-other {
                       |  margin: 1ex;
                       |}
                       |
                       |</style>""".stripMargin)
    }

    'simple {
      val el = input(`type` := "text", MyStyles.input, value := "ah")
      val html = el.toString()
      assertEq(html, """<input type="text" class=" ScalatagsTest_MyStyles-input" value="ah" />""")
    }

    'addClassName {
      val el = button(MyStyles.bootstrappy)
      val html = el.toString()
      assertEq(html, """<button class=" btn btn-default"></button>""")
    }
    'multipleStyles {
      val el = input(`type` := "text", MyStyles.input, MyStyles.other, value := "ah")
      val html = el.toString()
      assertEq(html, """<input type="text" class=" ScalatagsTest_MyStyles-input ScalatagsTest_MyStyles-other" value="ah" />""")
    }

  }
}
