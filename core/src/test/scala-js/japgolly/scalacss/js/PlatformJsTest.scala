package japgolly.scalacss.js

import utest._
import japgolly.scalacss._
import TestUtil._

import scala.scalajs.js.annotation.JSExport

@JSExport("PlatformJsTest")
object PlatformJsTest extends TestSuite {

  @JSExport("test")
  def test: Unit = {
    println(PlatformJs.value)
    println(JsEnv.value.platform)
    println(JsEnv.value.media)
  }

  override val tests = TestSuite {
    test
  }
}
