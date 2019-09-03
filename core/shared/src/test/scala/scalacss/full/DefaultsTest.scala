package scalacss.full

import japgolly.microlibs.testutil.TestUtil._
import scalacss._
import scalacss.internal.Platform
import utest._

object DefaultsTest extends TestSuite {

  override def tests = Tests {
    "platform" - assert(Platform.DevMode)
    "defaults" - assert(devOrProdDefaults eq DevDefaults)

    "dev"     - Dev .test()
    "prod"    - Prod.test()
  }

  class SharedStyles(implicit reg: StyleSheet.Register) extends StyleSheet.Inline {
    import dsl._
    implicit def compose = CssComposer.trust
    val header = style(backgroundColor(c"#333"))
    val footer = style(backgroundColor(c"#666"))
  }

  // ===================================================================================================================
  object Dev {
    import DevDefaults._

    object SS extends StyleSheet.Inline {
      import dsl._
      val style1 = style(
        margin(12 px),
        marginLeft(6 px)
      )
      val style2 = style(
        cursor.pointer,
        cursor.zoomIn
      )
      val shared = new SharedStyles
    }

    implicit def cssEnv = CssEnv.empty
    val css = SS.render[String]

    def norm(css: String) = css.trim

    def test(): Unit =
      assertMultiline(norm(css), norm(
        """
          |.DefaultsTest_Dev_SS-style1 {
          |  margin: 12px;
          |  margin-left: 6px;
          |}
          |
          |.DefaultsTest_Dev_SS-style2 {
          |  cursor: pointer;
          |  cursor: -moz-zoom-in;
          |  cursor: -o-zoom-in;
          |  cursor: -webkit-zoom-in;
          |  cursor: zoom-in;
          |}
          |
          |.DefaultsTest_SharedStyles-header {
          |  background-color: #333;
          |}
          |
          |.DefaultsTest_SharedStyles-footer {
          |  background-color: #666;
          |}
        """.stripMargin))
  }

  // ===================================================================================================================
  object Prod {
    import ProdDefaults._

    object SS1 extends StyleSheet.Inline {
      import dsl._
      val style1 = style(
        margin(12 px),
        marginLeft(6 px)
      )
      val style2 = style(
        cursor.pointer,
        cursor.zoomIn
      )
      val shared = new SharedStyles
    }

    object SS2 extends StyleSheet.Inline {
      import dsl._
      val blah = style(width.inherit)
    }

    implicit def cssEnv = CssEnv.empty
    val css1 = SS1.render[String]
    val css2 = SS2.render[String]

    def test(): Unit =
      assertEq(css1 +  css2,
        "._a0{margin:12px;margin-left:6px}" +
        "._a1{cursor:pointer;cursor:-moz-zoom-in;cursor:-o-zoom-in;cursor:-webkit-zoom-in;cursor:zoom-in}" +
        "._a2{background-color:#333}" +
        "._a3{background-color:#666}" +
        "._b0{width:inherit}"
      )
  }
}
