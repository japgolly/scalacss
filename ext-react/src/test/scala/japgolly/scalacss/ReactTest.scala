package japgolly.scalacss

import utest._
import japgolly.scalacss.TestUtil._
import japgolly.scalajs.react._, vdom.prefix_<^._
import Defaults._
import ScalaCssReact._

object ReactTest extends TestSuite {

  object MyStyles extends StyleSheet.Inline {
    import dsl._

    val input = style(
      fontWeight.bold,
      padding(0.3 ex, 2 ex)
    )

    val bootstrappy = style(addClassName("btn btn-default"))
  }

  override val tests = TestSuite {

    'styleTag {
      val html = React.renderToStaticMarkup(MyStyles.render[ReactElement])
      assertEq(html, """<style type="text/css">.scalacss-0001 {
                       |  font-weight: bold;
                       |  padding: 0.3ex 2ex;
                       |}
                       |
                       |</style>""".stripMargin)
    }

    'simple {
      val el = <.input(^.`type` := "text", MyStyles.input, ^.defaultValue := "ah")
      val html = React.renderToStaticMarkup(el)
      assertEq(html, """<input type="text" class="scalacss-0001" value="ah">""")
    }

    'addClassName {
      val el = <.button(MyStyles.bootstrappy)
      val html = React.renderToStaticMarkup(el)
      assertEq(html, """<button class="scalacss-0002 btn btn-default"></button>""")
    }

  }
}
