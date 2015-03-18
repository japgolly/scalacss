package japgolly.scalacss

import utest._
import TestUtil._

object DefaultsTest extends TestSuite {

  object DevDefaults extends Defaults {
    override def devMode = true
  }

  object ProdDefaults extends Defaults {
    override def devMode = false
  }

  override val tests = TestSuite {
    'dev  - Dev .test()
    'prod - Prod.test()
  }

  class SharedStyleModule(implicit reg: mutable.Register) extends mutable.StyleSheet {
    implicit def compose = Compose.trust
    val header = style(backgroundColor("#333"))
    val footer = style(backgroundColor("#666"))
  }

  // ===================================================================================================================
  object Dev {
    import DevDefaults._

    object SS extends StyleSheet {
      val style1 = style(
        margin(12 px),
        marginLeft(6 px)
      )
      val style2 = style(
        cursor.pointer,
        cursor.zoom_in
      )
      val shared = new SharedStyleModule
    }

    implicit def env = Env.empty
    val css = SS.render

    def norm(css: String) = css.trim

    def test(): Unit =
      assertEq(norm(css), norm(
        """
          |.scalacss-0001 {
          |  margin: 12px;
          |  margin-left: 6px;
          |}
          |
          |.scalacss-0002 {
          |  cursor: pointer;
          |  cursor: -webkit-zoom-in;
          |  cursor: -moz-zoom-in;
          |  cursor: -o-zoom-in;
          |  cursor: zoom-in;
          |}
          |
          |.scalacss-0003 {
          |  background-color: #333;
          |}
          |
          |.scalacss-0004 {
          |  background-color: #666;
          |}
        """.stripMargin))
  }

  // ===================================================================================================================
  object Prod {
    import ProdDefaults._

    object SS extends StyleSheet {
      val style1 = style(
        margin(12 px),
        marginLeft(6 px)
      )
      val style2 = style(
        cursor.pointer,
        cursor.zoom_in
      )
      val shared = new SharedStyleModule
    }

    implicit def env = Env.empty
    val css = SS.render

    def test(): Unit =
      assertEq(css,
        ".\u00a2\u00a0{margin:12px;margin-left:6px}" +
        ".\u00a2\u00a1{cursor:pointer;cursor:-webkit-zoom-in;cursor:-moz-zoom-in;cursor:-o-zoom-in;cursor:zoom-in}" +
        ".\u00a2\u00a2{background-color:#333}" +
        ".\u00a2\u00a3{background-color:#666}"
      )
  }
}
