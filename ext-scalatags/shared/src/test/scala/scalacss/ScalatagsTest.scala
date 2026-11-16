package scalacss

import japgolly.microlibs.testutil.TestUtil._
import scalacss.DevDefaults._
import scalacss.ScalatagsCss._
import utest._

object ScalatagsTest extends TestSuite {

  object MyStyles extends StyleSheet.Inline {
    import dsl._

    val input = style(
      fontWeight.bold,
      padding(0.3 ex, 2 ex)
    )

    val required = style(
      border(thin, solid, red)
    )

    val bootstrappy = style(addClassName("btn btn-default"))
  }

  object MyStyles2 extends StyleSheet.Inline {
    import dsl._

    val child = style(
      unsafeChild("> span")(
        color.red
      )
    )

    val quote = style(
      content.string("he\"llo")
    )
  }

  object MyStylesInjection extends StyleSheet.Inline {
    import dsl._

    val lower = style(
      content.string("</style><script>alert(1)</script>")
    )

    val upper = style(
      content.string("</STYLE><script>alert(1)</script>")
    )

    val rawUrl = style(
      backgroundImage := "url('https://example.com/</style><script>alert(1)</script>')"
    )
  }

  type T = scalatags.Text.TypedTag[String]
  import scalatags.Text.all._

  override def tests = Tests {

    "styleTag" - {
      val html = MyStyles.render[T].toString()
      assertMultiline(html, """<style type="text/css">.ScalatagsTest_MyStyles-input {
                               |  font-weight: bold;
                               |  padding: 0.3ex 2ex;
                               |}
                               |
                               |.ScalatagsTest_MyStyles-required {
                               |  border: thin solid red;
                               |}
                               |
                               |</style>""".stripMargin)
    }

    "noHtmlEscaping" - {
      val html = MyStyles2.render[T].toString()
      assertMultiline(html, """<style type="text/css">.ScalatagsTest_MyStyles2-child > span {
                              |  color: red;
                              |}
                              |
                              |.ScalatagsTest_MyStyles2-quote {
                              |  content: 'he"llo';
                              |}
                              |
                              |</style>""".stripMargin)
    }

    "simple" - {
      val el = input(`type` := "text", MyStyles.input, value := "ah")
      val html = el.toString()
      assertEq(html, """<input type="text" class=" ScalatagsTest_MyStyles-input" value="ah" />""")
    }

    "addClassName" - {
      val el = button(MyStyles.bootstrappy)
      val html = el.toString()
      assertEq(html, """<button class=" btn btn-default"></button>""")
    }

    "multipleStyles" - {
      val el = input(`type` := "text", MyStyles.input, MyStyles.required, value := "ah")
      val html = el.toString()
      assertEq(html, """<input type="text" class=" ScalatagsTest_MyStyles-input  ScalatagsTest_MyStyles-required" value="ah" />""")
    }

    "noStyleTagInjection" - {
      val html = MyStylesInjection.render[T].toString()
      val inner = html.stripPrefix("""<style type="text/css">""").stripSuffix("</style>")
      assertNotContains(inner.toLowerCase, "</style")
      assertContains(inner, """content: '\00003C/style><script>alert(1)</script>';""")
      assertContains(inner, """content: '\00003C/STYLE><script>alert(1)</script>';""")
      assertContains(inner, """url('https://example.com/\00003C/style><script>alert(1)</script>')""")
      inner
    }
  }
}
