package downstream

import japgolly.microlibs.compiletime.CompileTimeInfo
import japgolly.microlibs.testutil.TestUtil._
import scalacss._
import utest._

object RuntimeTest extends TestSuite {

  val mode       = CompileTimeInfo.sysProp("scalacss.mode")
  val expectProd = CompileTimeInfo.sysProp("downstream_tests.expect.prod").contains("1")

  override def tests = Tests {

    "settings" - {

      "isDev" - assertEq(
        actual = (Main.CssSettings: AnyRef) eq DevDefaults,
        expect = !expectProd)

      "isProd" - assertEq(
        actual = (Main.CssSettings: AnyRef) eq ProdDefaults,
        expect = expectProd)
    }

  }
}
