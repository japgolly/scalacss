package scalacss.internal.mutable

import cats.Eq
import cats.instances.option._
import cats.instances.vector._
import japgolly.microlibs.testutil.TestUtil._
import scalacss.defaults.DefaultSettings.Dev._
import utest._

class BlarrrrStyle extends StyleSheet.Inline {
  import dsl._
  val s = style(borderCollapse.collapse)
}

object GlobalRegistryTest extends TestSuite {

  val gr = new StyleSheetRegistry()

  class S1 extends StyleSheet.Inline
  class S2 extends StyleSheet.Inline

  val s1 = new S1
  val blar = new BlarrrrStyle
  implicit val eqS1: Eq[S1]                = _ eq _
  implicit val eqBL: Eq[BlarrrrStyle]      = _ eq _
  implicit val eqSS: Eq[StyleSheet.Inline] = _ eq _

  var postreg = Vector.empty[StyleSheet.Inline]
  gr.register(s1)
  gr.onRegistration(postreg :+= _)
  gr.register(blar)

  override def tests = Tests {
    "get" - {
      assertEq(gr[S1], Some(s1))
      assertEq(gr[BlarrrrStyle], Some(blar))
    }
    "missing" - {
      assertEq(gr[S2].isEmpty, true)
    }
    "onRegistration" - {
      assertEq(postreg, Vector(s1, blar))
    }
  }
}
