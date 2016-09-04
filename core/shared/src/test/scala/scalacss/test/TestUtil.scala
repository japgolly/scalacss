package scalacss.test

import scala.io.AnsiColor._
import scalaz.Equal
import scalaz.syntax.equal._
import scalacss.internal.Warning

object TestUtil extends TestUtil

trait TestUtil extends japgolly.univeq.UnivEqExports {

  implicit def equalFromUnivEq[A: UnivEq]: Equal[A] = Equal.equalA

  implicit def warningEquality: Equal[Warning] = Equal.equalA

  def NDomain = nyaya.test.Domain
  type NDomain[A] = nyaya.test.Domain[A]

  def assertEq[A: Equal](actual: A, expect: A): Unit =
    assertEq(None, actual, expect)

  def assertEq[A: Equal](name: String, actual: A, expect: A): Unit =
    assertEq(Some(name), actual, expect)

  def assertEq[A: Equal](name: Option[String], actual: A, expect: A): Unit =
    if (actual ≠ expect) {
      println()
      name.foreach(n => println(s">>>>>>> $n"))

      val toString: Any => String = {
        case s: Stream[_] => s.force.toString() // SI-9266
        case a            => a.toString
      }

      var as = toString(actual)
      var es = toString(expect)
      var pre = "["
      var post = "]"
      if ((as + es) contains "\n") {
        pre = "↙[\n"
      }
      println(s"expect: $pre$BOLD$BLUE$es$RESET$post")
      println(s"actual: $pre$BOLD$RED$as$RESET$post")
      println()
      assert(false)
    }

  def fail(msg: String): Nothing =
    throw new AssertionError(msg)
}