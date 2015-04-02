package japgolly.scalacss

import org.scalajs.dom.raw.HTMLStyleElement
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

  val expectedStyleTag =
    """
      |<style type="text/css">.ReactTest_MyStyles-0001 {
      |  font-weight: bold;
      |  padding: 0.3ex 2ex;
      |}
      |
      |</style>
    """.stripMargin.trim

  override val tests = TestSuite {

    'styleReactElement {
      val html = React.renderToStaticMarkup(MyStyles.render[ReactElement])
      assertEq(html, expectedStyleTag)
    }

    'styleHtmlElement {
      val html = MyStyles.render[HTMLStyleElement].outerHTML
      assertEq(html, expectedStyleTag)
    }

    'simple {
      val el = <.input(^.`type` := "text", MyStyles.input, ^.defaultValue := "ah")
      val html = React.renderToStaticMarkup(el)
      assertEq(html, """<input type="text" class="ReactTest_MyStyles-0001" value="ah">""")
    }

    'addClassName {
      val el = <.button(MyStyles.bootstrappy)
      val html = React.renderToStaticMarkup(el)
      assertEq(html, """<button class="ReactTest_MyStyles-0002 btn btn-default"></button>""")
    }

  }
}
