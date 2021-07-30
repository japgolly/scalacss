package scalacss

import scalaz.Equal
import scalaz.syntax.equal._
import scala.io.AnsiColor._
import scala.annotation.nowarn

object TestUtil extends TestUtil

trait TestUtil extends japgolly.univeq.UnivEqExports {

  @nowarn("cat=unused")
  implicit def equalFromUnivEq[A: UnivEq]: Equal[A] = Equal.equalA

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
      var pre = "["
      val post = "]"
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
