package scalacss.elision

import scala.annotation.elidable
import utest._
import scalacss._
import scalacss.internal.Platform
import scala.annotation.nowarn

object DefaultsTest extends TestSuite {

  def assert(ok: Boolean, msg: String) =
    if (!ok) fail(msg)

  def fail(msg: String) = {
    val e = new java.lang.AssertionError(msg)
    e.setStackTrace(Array.empty)
    throw e
  }

  override def tests = Tests {

    "elision" - {
      var on = false

      @elidable(elidable.ASSERTION)
      @nowarn("cat=unused")
      def test(): Unit = on = true

      assert(!on, "ELISION")
    }

    "platform" - assert(!Platform.DevMode, "PLATFORM")
    "defaults" - assert(devOrProdDefaults eq ProdDefaults, "DEFAULTS")
  }
}
