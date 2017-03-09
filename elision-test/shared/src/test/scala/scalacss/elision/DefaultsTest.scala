package scalacss.elision

import scala.language.experimental.macros
import scala.annotation.elidable
import utest._
import scalacss._
import scalacss.internal.Platform

object DefaultsTest extends TestSuite {

  def assert(ok: Boolean, msg: String) =
    if (!ok) fail(msg)

  def fail(msg: String) = {
    val e = new java.lang.AssertionError(msg)
    e.setStackTrace(Array.empty)
    throw e
  }

  override def tests = TestSuite {

    'elision {
      var on = false
      @elidable(elidable.ASSERTION)
      def test(): Unit = on = true
      assert(!on, "ELISION")
    }

    'platform - assert(!Platform.DevMode, "PLATFORM")
    'defaults - assert(devOrProdDefaults eq ProdDefaults, "DEFAULTS")
  }
}
