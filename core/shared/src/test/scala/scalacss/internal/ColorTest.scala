package scalacss.internal

import japgolly.microlibs.testutil.TestUtil._
import utest._

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
      "hexU" - test(c"#ABC"     , "#abc")

      "legacy" - {
        "rgbI"       - test(c"rgb(0,128,255)",    "rgb(0,128,255)")
        "rgbP"       - test(c"rgb(0%,50%,100%)",  "rgb(0%,50%,100%)")
        "rgbaI"      - test(c"rgba(255,128,0,0)", "rgba(255,128,0,0)")
        "rgbaP"      - test(c"rgba(0%,50%,100%,0.8)", "rgba(0%,50%,100%,0.8)")
        "hsl"        - test(c"hsl(360,0%,100%)",  "hsl(360,0%,100%)")
        "hsla"       - test(c"hsla(0,100%,0%,1)", "hsla(0,100%,0%,1)")
        "rgbU"       - test(c"RGB(0,128,255)", "rgb(0,128,255)")
        "alphaDec0"  - test(c"hsla(0,100%,0%,0.918)", "hsla(0,100%,0%,0.918)")
        "alphaDecZ"  - test(c"hsla(0,100%,0%,.543)", "hsla(0,100%,0%,.543)")
        "whitespace" - test(c"  rgba  (  255  ,  128  ,  0  ,  0  )  ", "rgba(255,128,0,0)")
      }

      "modern" - {
        "rgb"     - test(c"rgb(1 2% none)", "rgb(1 2% none)")
        "rgbNoneU" - test(c"rgb(NONE 0 0)", "rgb(none 0 0)")
        "rgbWS"   - test(c" rgb ( 1  2%  none ) ", "rgb(1 2% none)")
        "rgbaD"   - test(c"rgb(1 2 3/0.5)", "rgb(1 2 3/0.5)")
        "rgbaDWS" - test(c" rgb ( 1  2  3 / 0.5 ) ", "rgb(1 2 3/0.5)")
        "rgbaP"   - test(c"rgb(1 2 3/25%)", "rgb(1 2 3/25%)")
        "rgbaPWS" - test(c" rgb ( 1  2  3 / 25% ) ", "rgb(1 2 3/25%)")
        "hsl"     - test(c"hsl(1 2% 3)", "hsl(1 2% 3)")
        "hslN"    - test(c"hsl(none 2% 3)", "hsl(none 2% 3)")
        "hslNU"   - test(c"hsl(NONE 2% 3)", "hsl(none 2% 3)")
        "hslDeg"  - test(c"hsl(360deg 2% 3)", "hsl(360 2% 3)")
        "hslDegU" - test(c"hsl(120DEG 50% 50%)", "hsl(120 50% 50%)")
        "hslWS"   - test(c" hsl ( 1  2%  3 ) ", "hsl(1 2% 3)")
        "hsla"    - test(c"hsl(1 2% 3)", "hsl(1 2% 3)")
        "hslaDeg" - test(c"hsl(360deg 2% 3/50%)", "hsl(360 2% 3/50%)")
        "hslaDegU" - test(c"hsl(120Deg 50% 50%/50%)", "hsl(120 50% 50%/50%)")
        "hslaWS"  - test(c" hsl ( 1  2%  3  / 0.5 ) ", "hsl(1 2% 3/0.5)")
        "rgbDec"  - test(c"rgb(127.5 0.5 255)", "rgb(127.5 0.5 255)")
        "rgbDecZ" - test(c"rgb(.5 12.25% none)", "rgb(.5 12.25% none)")
        "rgbaDec" - test(c"rgb(127.5 0.5 255 / 50.5%)", "rgb(127.5 0.5 255/50.5%)")
        "hslDec"  - test(c"hsl(180.5 50.5% 25.5)", "hsl(180.5 50.5% 25.5)")
        "hslDegD" - test(c"hsl(120.5deg 50% 50%)", "hsl(120.5 50% 50%)")
        "hslGrad" - test(c"hsl(200grad 50% 50%)", "hsl(200grad 50% 50%)")
        "hslGradU"- test(c"hsl(200.5GRAD 50% 50%)", "hsl(200.5grad 50% 50%)")
        "hslTurn" - test(c"hsl(0.5turn 50% 50%)", "hsl(0.5turn 50% 50%)")
        "hslTurnU"- test(c"hsl(.25TURN 50% 50% / 0.5)", "hsl(.25turn 50% 50%/0.5)")
        "hslRad"  - test(c"hsl(3.14rad 50% 50%)", "hsl(3.14rad 50% 50%)")
        "hslRadU" - test(c"hsl(3.14RAD 50% 50%)", "hsl(3.14rad 50% 50%)")
      }

    }

    "invalid" - {
      def assertErrorContains(e: CompileError, frag: String) = {
        assertContains(e.msg, frag)
        e.msg
      }
      def assertFailure(e: CompileError) =
        assertErrorContains(e, "Invalid colour literal")

      "hex" - {
        def test(e: CompileError) = assertErrorContains(e, "Hex notation must be")
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

      "legacy" - {
        "r-1"   - assertErrorContains(compileError(""" c"rgb(-1,0,0)" """), "Invalid red value")
        "r256"  - assertErrorContains(compileError(""" c"rgb(256,0,0)" """), "Invalid red value")
        "rOver" - assertErrorContains(compileError(""" c"rgb(99999999999999999999999999,0,0)" """), "Invalid red value")
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

      "modern" - {
        "r256"  - assertErrorContains(compileError(""" c"rgb(256 0 0)" """), "Invalid red value")
        "g256"  - assertErrorContains(compileError(""" c"rgb(0 256 0)" """), "Invalid green value")
        "b256"  - assertErrorContains(compileError(""" c"rgb(0 0 256)" """), "Invalid blue value")
        "r101%" - assertErrorContains(compileError(""" c"rgb(101% 0% none)" """), "Invalid red value")
        "g101%" - assertErrorContains(compileError(""" c"rgb(none 101% none)" """), "Invalid green value")
        "b101%" - assertErrorContains(compileError(""" c"rgb(none none 101%)" """), "Invalid blue value")
        "rgbaD" - assertErrorContains(compileError(""" c"rgb(1 2 3/1.5)" """), "Invalid alpha value")
        "rgbaP" - assertErrorContains(compileError(""" c"rgb(1 2 3/125%)" """), "Invalid alpha value")
        "h361"  - assertErrorContains(compileError(""" c"hsl(361 0 0)" """), "Invalid hue value")
        "s101"  - assertErrorContains(compileError(""" c"hsl(0 101% 0)" """), "Invalid saturation value")
        "l101"  - assertErrorContains(compileError(""" c"hsl(0 0 101%)" """), "Invalid lightness value")
        "multi3" - assertErrorContains(compileError(""" c"rgb(256 256 0)" """), "Invalid red value")
        "multi4" - assertErrorContains(compileError(""" c"rgb(1 256 256/2)" """), "Invalid green value")
        "rDecOver" - assertErrorContains(compileError(""" c"rgb(255.1 0 0)" """), "Invalid red value")
        "aDecOver" - assertErrorContains(compileError(""" c"rgb(1 2 3 / 100.5%)" """), "Invalid alpha value")
        "hDecOver" - assertErrorContains(compileError(""" c"hsl(360.1 0 0)" """), "Invalid hue value")
        "hGradOver"- assertErrorContains(compileError(""" c"hsl(401grad 0 0)" """), "Invalid hue value")
        "hTurnOver"- assertErrorContains(compileError(""" c"hsl(1.1turn 0 0)" """), "Invalid hue value")
        "hRadOver" - assertErrorContains(compileError(""" c"hsl(7rad 0 0)" """), "Invalid hue value")
        "sDecOver" - assertErrorContains(compileError(""" c"hsl(0 100.5 0)" """), "Invalid saturation value")
        "lDecOver" - assertErrorContains(compileError(""" c"hsl(0 0 100.5%)" """), "Invalid lightness value")
      }

      "multiLegacy" - assertErrorContains(compileError(""" c"rgb(256, 256, 0)" """), "Invalid red value")
    }
  }
}
