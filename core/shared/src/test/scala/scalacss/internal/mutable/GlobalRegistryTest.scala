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
  implicit val eqS2: Eq[S2]                = _ eq _
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

    "reentrantRegisterInsideCallback" - {
      val r = new StyleSheetRegistry()
      val a = new S1
      val b = new S2
      r.register(a)
      var seen = Vector.empty[StyleSheet.Inline]
      r.onRegistrationN { ss =>
        seen ++= ss
        if (ss.contains(a))
          r.register(b)
      }
      assertEq(seen, Vector[StyleSheet.Inline](a, b))
    }

    "deduplicatesRegistrations" - {
      val r = new StyleSheetRegistry()
      val a = new S1
      val b = new S2
      var seen = Vector.empty[StyleSheet.Inline]
      r.onRegistration(seen :+= _)
      r.register(a, a)
      r.register(a, b)
      r.register(b)
      assertEq(seen, Vector[StyleSheet.Inline](a, b))
    }

    "manyListenersNoStackOverflow" - {
      val r = new StyleSheetRegistry()
      var count = 0
      for (_ <- 1 to 10000)
        r.onRegistration(_ => count += 1)
      r.register(new S1)
      assertEq(count, 10000)
    }

    "unregisterAndClear" - {
      val r = new StyleSheetRegistry()
      val a = new S1
      val b = new S2
      var seen = Vector.empty[StyleSheet.Inline]
      val unsub = r.registerListener(seen :+= _)
      r.register(a, b)
      assertEq(r[S1], Some(a))
      assertEq(r[S2], Some(b))

      r.unregister(a)
      assertEq(r[S1], None)
      assertEq(r[S2], Some(b))

      // Re-registering an unregistered sheet notifies listeners again
      r.register(a)
      assertEq(r[S1], Some(a))
      assertEq(seen, Vector[StyleSheet.Inline](a, b, a))

      unsub()
      r.clear()
      assertEq(r[S1], None)
      assertEq(r[S2], None)
      r.register(a, b)
      assertEq(seen, Vector[StyleSheet.Inline](a, b, a))
    }
  }
}
