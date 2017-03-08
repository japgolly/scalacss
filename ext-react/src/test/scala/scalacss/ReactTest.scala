package scalacss

import japgolly.scalajs.react._, vdom.html_<^._
import org.scalajs.dom.raw.HTMLStyleElement
import org.scalajs.dom.document
import utest._
import scalacss.TestUtil._
import scalacss.internal.mutable.StyleSheetRegistry
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

  object MyStyles2 extends StyleSheet.Inline {
    import dsl._
    style(display.inline)
  }

  val expectedMyStyles =
    """
      |.ReactTest_MyStyles-input {
      |  font-weight: bold;
      |  padding: 0.3ex 2ex;
      |}
    """.stripMargin.trim

  val expectedMyStyles2 =
    """
      |.ReactTest_MyStyles2-0001 {
      |  display: inline;
      |}
    """.stripMargin.trim

  val expectedStyleTag1 =
    s"""<style type="text/css">$expectedMyStyles</style>"""

  val expectedStyleTag12 =
    s"""
       |<style type="text/css">$expectedMyStyles
       |$expectedMyStyles2
       |</style>
    """.stripMargin.trim

  def assertStyle(actual: String, expect: String) = {
    def fix(s: String) = s.replaceAll("\n{2,}", "\n").replaceFirst("\n+(?=</style>)", "")
    assertEq(fix(actual), fix(expect))
  }

  override val tests = TestSuite {

    'styleReactElement {
      val html = ReactDOMServer.renderToStaticMarkup(MyStyles.render[VdomElement])
      assertStyle(html, expectedStyleTag1)
    }

    'styleHtmlElement {
      val html = MyStyles.render[HTMLStyleElement].outerHTML
      assertStyle(html, expectedStyleTag1)
    }

    'simple {
      val el = <.input(^.`type` := "text", MyStyles.input, ^.defaultValue := "ah")
      val html = ReactDOMServer.renderToStaticMarkup(el)
      assertEq(html, """<input type="text" class="ReactTest_MyStyles-input" value="ah"/>""")
    }

    'addClassName {
      val el = <.button(MyStyles.bootstrappy)
      val html = ReactDOMServer.renderToStaticMarkup(el)
      assertEq(html, """<button class="btn btn-default"></button>""")
    }

    'addToDocumentOnRegistration {
      val registry = new StyleSheetRegistry
      registry.register(MyStyles, MyStyles2)
      def count = document.head.childElementCount
      val before = count
      registry.addToDocumentOnRegistration()
      assertEq("Elements added", count, before + 1)
      assertStyle(document.head.lastElementChild.outerHTML, expectedStyleTag12)
    }

  }
}
