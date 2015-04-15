package scalacss.js

import utest._
import scalacss._
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
