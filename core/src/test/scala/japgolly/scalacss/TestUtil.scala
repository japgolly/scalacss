package japgolly.scalacss

import scalaz.Equal
import scalaz.syntax.equal._

object TestUtil extends TestUtil

trait TestUtil
  extends scalaz.std.StringInstances
     with scalaz.std.StreamInstances
     with scalaz.std.VectorInstances
     with scalaz.std.TupleInstances
     with scalaz.std.AnyValInstances
     with scalaz.std.ListInstances {

  @inline def NDomain = japgolly.nyaya.test.Domain
  type NDomain[A] = japgolly.nyaya.test.Domain[A]

  def assertEq[A: Equal](actual: A, expect: A): Unit =
    assertEq(None, actual, expect)

  def assertEq[A: Equal](name: String, actual: A, expect: A): Unit =
    assertEq(Some(name), actual, expect)

  def assertEq[A: Equal](name: Option[String], actual: A, expect: A): Unit =
    if (actual ≠ expect) {
      println()
      name.foreach(n => println(s">>>>>>> $n"))
      val as = actual.toString
      val es = expect.toString
      if ((as + es) contains "\n")
        println(s"actual: ↙[\n$as]\nexpect: ↙[\n$es]")
      else
        println(s"actual: [$as]\nexpect: [$es]")
      println()
      assert(false)
    }

  def fail(msg: String): Nothing =
    throw new AssertionError(msg)
}