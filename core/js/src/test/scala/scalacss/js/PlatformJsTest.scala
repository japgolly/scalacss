package scalacss.js

import scala.scalajs.js.annotation.JSExport
import utest._

@JSExport("PlatformJsTest")
object PlatformJsTest extends TestSuite {

  @JSExport("test")
  def test(): Unit = {
    println(PlatformJs.value)
    println(PlatformJsEnv.value.map(_.platform))
    println(PlatformJsEnv.value.map(_.media))
  }

  override val tests = TestSuite {
    test()
  }
}
