package downstream

import japgolly.microlibs.testutil.TestUtil._
import japgolly.microlibs.testutil.TestUtilInternals._
import scala.Console._
import sourcecode.Line
import utest._

object JsOutputTest extends TestSuite {
  import Props._

  private def fgrep(term: String): Unit = {
    println(s"> fgrep '$term'")
    content
      .linesIterator
      .zipWithIndex
      .filter(_._1.contains(term))
      .map { case (s, l) => s"$GREEN$l:$RESET " + s.replace(term, MAGENTA_B + WHITE + term + RESET) }
      .foreach(println)
  }

  private def contentTest(substrToExpect: (String, Boolean)*)(implicit l: Line): Unit = {
    System.out.flush()
    System.err.flush()
    var errors = List.empty[String]
    for ((substr, expect) <- substrToExpect) {
      val actual = content.contains(substr)
      val pass   = actual == expect
      val result = if (pass) s"${GREEN}pass$RESET" else s"${RED_B}${WHITE}FAIL$RESET"
      val should = if (expect) "should" else "shouldn't"
      val strCol = if (expect) (GREEN + BRIGHT_GREEN) else BRIGHT_BLACK
      println(s"[$result] JS $should contain $strCol$substr$RESET")
      if (!pass) errors ::= s"JS $should contain $substr"
    }
    System.out.flush()
    if (errors.nonEmpty) {
      for ((substr, _) <- substrToExpect)
        fgrep(substr)
      fail(errors.sorted.mkString(", "))
    }
  }

  override def tests = Tests {

    "size" - "%,d bytes".format(content.length)

    "defaults" - contentTest(
      "DevDefaults"  -> !expectProd,
      "ProdDefaults" -> expectProd,
    )

  }
}
