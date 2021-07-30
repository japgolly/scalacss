package scalacss.internal

import japgolly.microlibs.testutil.TestUtil._
import utest._
import scala.annotation.nowarn

object ColorTest extends TestSuite {
  import Macros.Color
  import Dsl._

  override def tests = Tests {

    "valid" - {
      def test(c: Color, expect: String) =
        assertEq(c.value, expect)

      "hex3" - test(c"#f09"     , "#f09")
      "hex4" - test(c"#f0a9"    , "#f0a9")
      "hex6" - test(c"#abc105"  , "#abc105")
      "hex8" - test(c"#abc105f3", "#abc105f3")

      "rgbI" - test(c"rgb(0,128,255)",    "rgb(0,128,255)")
      "rgbP" - test(c"rgb(0%,50%,100%)",  "rgb(0%,50%,100%)")
      "rgba" - test(c"rgba(255,128,0,0)", "rgba(255,128,0,0)")
      "hsl"  - test(c"hsl(359,0%,100%)",  "hsl(359,0%,100%)")
      "hsla" - test(c"hsla(0,100%,0%,1)", "hsla(0,100%,0%,1)")

      "hexU" - test(c"#ABC",           "#abc")
      "rgbU" - test(c"RGB(0,128,255)", "rgb(0,128,255)")

      "whitespace" - test(c"  rgba  (  255  ,  128  ,  0  ,  0  )  ", "rgba(255,128,0,0)")

      "alphaDec0" - test(c"hsla(0,100%,0%,0.918)", "hsla(0,100%,0%,0.918)")
      "alphaDecZ" - test(c"hsla(0,100%,0%,.543)",  "hsla(0,100%,0%,.543)")
    }

    "invalid" - {
      @nowarn("cat=unused")
      def assertFailure(e: CompileError) = ()
      def assertErrorContains(e: CompileError, frag: String): Unit = {
        val err = e.msg
        assert(err contains frag)
      }

      "hex" - {
        def test(e: CompileError): Unit = assertErrorContains(e, "Hex notation must be")
        "0" - test(compileError(""" c"#" """))
        "1" - test(compileError(""" c"#f" """))
        "2" - test(compileError(""" c"#00" """))
        "5" - test(compileError(""" c"#12345" """))
        "7" - test(compileError(""" c"#1234567" """))
        "9" - test(compileError(""" c"#123456789" """))
        "g" - test(compileError(""" c"#00g" """))
        "G" - test(compileError(""" c"#G00" """))
      }

      "empty" - assertFailure(compileError( """ c"" """))
      "blank" - assertFailure(compileError( """ c"   " """))
      "badFn" - assertFailure(compileError( """ c"rbg(0,0,0)" """))
      "two"   - assertFailure(compileError( """ c"#fed #fed" """))

      "numbers" - {
        "r-1"   - assertErrorContains(compileError(""" c"rgb(-1,0,0)" """), "Invalid red value")
        "r256"  - assertErrorContains(compileError(""" c"rgb(256,0,0)" """), "Invalid red value")
        "g256"  - assertErrorContains(compileError(""" c"rgb(0,256,0)" """), "Invalid green value")
        "b256"  - assertErrorContains(compileError(""" c"rgb(0,0,256)" """), "Invalid blue value")
        "a2"    - assertErrorContains(compileError(""" c"rgba(0,0,0,2)" """), "Invalid alpha value")
        "a1.1"  - assertErrorContains(compileError(""" c"rgba(0,0,0,1.1)" """), "Invalid alpha value")
        "r101%" - assertErrorContains(compileError(""" c"rgb(101%,0%,0%)" """), "Invalid red value")
        "g101%" - assertErrorContains(compileError(""" c"rgb(0%,101%,0%)" """), "Invalid green value")
        "b101%" - assertErrorContains(compileError(""" c"rgb(0%,0%,101%)" """), "Invalid blue value")
        "dbl"   - assertFailure      (compileError(""" c"rgb(2.5,0,0)" """))
        "str"   - assertFailure      (compileError(""" c"rgb(x,0,0)" """))
        "empty" - assertFailure      (compileError(""" c"rgb(0,,0)" """))
        "mixed" - assertFailure      (compileError(""" c"rbg(0,0%,0)" """))
      }
    }
  }
}
