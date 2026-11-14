package scalacss.js

import scala.scalajs.js.annotation.JSExportTopLevel
import utest._

object PlatformJsTest extends TestSuite {

  @JSExportTopLevel("testPlatformJs")
  def test(): Unit = {
    println(PlatformJs.value)
    println(PlatformJsEnv.value.map(_.platform))
  }

  override def tests = Tests {
    test()
  }
}
