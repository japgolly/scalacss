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

  def assertMultiline(actual: String, expect: String): Unit =
    if (actual != expect) {
      println()
      val AE = List(actual, expect).map(_.split("\n"))
      val List(as, es) = AE
      val lim = as.length max es.length
      val List(maxA,_) = AE.map(x => (0 #:: x.map(_.length).toStream).max)
      val maxL = lim.toString.length
      println("A|E")
      val fmt = s"%s%${maxL}d: %-${maxA}s |%s| %s$RESET\n"
      for (i <- (0 until lim)) {
        val List(a, e) = AE.map(s => if (i >= s.length) "" else s(i))
        val ok = a == e
        val cmp = if (ok) " " else "≠"
        val col = if (ok) BOLD + BLACK else WHITE
        printf(fmt, col, i + 1, a, cmp, e)
      }
      println()
      fail("assertMultiline failed.")
    }

  def fail(msg: String): Nothing =
    throw new AssertionError(msg)
}