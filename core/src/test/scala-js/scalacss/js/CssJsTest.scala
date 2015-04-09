package scalacss.js

import utest._
import scalajs.js.JSON
import scalacss._
import TestUtil._

object CssJsTest extends TestSuite {

  import DevDefaults._

  object SampleStyles extends StyleSheet.Inline {
    override implicit val classNameHint = ClassNameHint("TEST")
    import dsl._
    val other = style(borderCollapse.collapse, &.hover(fontWeight._200), fontWeight._100)
    val outer = style(fontWeight.bold)
    val inner = style(color.red, outer)
  }

  lazy val js = CssJs.cssToJsObject(SampleStyles.css)

  def jsonFor(s: StyleA) =
    JSON.stringify(js(s.className.value))

  override def tests = TestSuite {

    'outer -
      assertEq(jsonFor(SampleStyles.outer), """{"fontWeight":"bold"}""")

    'all -
      assertEq(JSON stringify js,
      """{"TEST-0001":{"borderCollapse":"collapse","fontWeight":"100"},
         |"TEST-0002":{"fontWeight":"bold"},
         |"TEST-0003":{"color":"red","fontWeight":"bold"}}""".stripMargin.replace("\n", ""))

  }
}
