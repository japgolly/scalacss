package scalacss

import cats.Eq
import cats.syntax.eq._
import scala.annotation.nowarn
import scala.io.AnsiColor._

object TestUtil extends TestUtil

trait TestUtil extends japgolly.univeq.UnivEqExports {

  @nowarn("cat=unused")
  implicit def equalFromUnivEq[A: UnivEq]: Eq[A] = Eq.fromUniversalEquals

  def assertEq[A: Eq](actual: A, expect: A): Unit =
    assertEq(None, actual, expect)

  def assertEq[A: Eq](name: String, actual: A, expect: A): Unit =
    assertEq(Some(name), actual, expect)

  def assertEq[A: Eq](name: Option[String], actual: A, expect: A): Unit =
    if (actual =!= expect) {
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
