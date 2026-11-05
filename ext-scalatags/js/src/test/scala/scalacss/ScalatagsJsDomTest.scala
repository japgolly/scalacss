package scalacss

import japgolly.microlibs.testutil.TestUtil._
import org.scalajs.dom.HTMLStyleElement
import scalacss.DevDefaults._
import scalacss.ScalatagsCss._
import scalatags.JsDom._
import scalatags.JsDom.all._
import utest._

object ScalatagsJsDomTest extends TestSuite {

  object MyStyles extends StyleSheet.Inline {
    import dsl._

    val plain = style(
      color.red
    )

    val bootstrappy = style(
      addClassName("btn btn-default")
    )

    val multiple = style(
      addClassNames("c1 c2", "c3")
    )

    val withEmpty = style(
      color.blue,
      addClassName("")
    )

    val emptyAndValid = style(
      addClassNames("", "valid-1", "   ", "valid-2")
    )
  }

  override def tests = Tests {
    "plain" - {
      val btn = button(MyStyles.plain).render
      assert(btn.classList.contains("ScalatagsJsDomTest_MyStyles-plain"))
    }

    "addClassNameWithSpaces" - {
      val btn = button(MyStyles.bootstrappy).render
      assert(btn.classList.contains("btn"))
      assert(btn.classList.contains("btn-default"))
    }

    "addClassNamesMultiple" - {
      val btn = button(MyStyles.multiple).render
      assert(btn.classList.contains("c1"))
      assert(btn.classList.contains("c2"))
      assert(btn.classList.contains("c3"))
    }

    "emptyClassName" - {
      val btn = button(MyStyles.withEmpty).render
      assert(btn.classList.contains("ScalatagsJsDomTest_MyStyles-withEmpty"))
      assertEq(btn.classList.length, 1)
    }

    "emptyAndValid" - {
      val btn = button(MyStyles.emptyAndValid).render
      assert(btn.classList.contains("valid-1"))
      assert(btn.classList.contains("valid-2"))
      assertEq(btn.classList.length, 2)
    }

    "renderToTag" - {
      val styleTag = MyStyles.render[TypedTag[HTMLStyleElement]].render
      assertEq(styleTag.tagName, "STYLE")
      assertMultiline(styleTag.innerHTML.trim, """
        |.ScalatagsJsDomTest_MyStyles-plain {
        |  color: red;
        |}
        |
        |.ScalatagsJsDomTest_MyStyles-withEmpty {
        |  color: blue;
        |}""".stripMargin.trim)
    }
  }
}
