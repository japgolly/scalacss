package scalacss.elision

import scala.annotation.elidable
import utest._
import scalacss._

object DefaultsTest extends TestSuite {

  override def tests = TestSuite {

    'elision {
      var on = false
      @elidable(elidable.ASSERTION)
      def test(): Unit = on = true
      assert(!on)
    }

    'defaults -
      assert(Defaults.cssSettings eq defaults.DefaultSettings.Prod)
  }
}
