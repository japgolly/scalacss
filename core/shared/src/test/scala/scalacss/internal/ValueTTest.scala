package scalacss.internal

import japgolly.microlibs.testutil.TestUtil._
import scala.concurrent.duration._
import scalacss.internal.ValueT.Rules._
import scalacss.internal.ValueT._
import sourcecode.Line
import utest._

object ValueTTest extends TestSuite {

  val len = Length(12, LengthUnit.px)
  val %%  = Percentage(25)
  val sec = 3 seconds
  val ms  = 3 milliseconds
  import Dsl.inherit

  override def tests = Tests {

    "valueClasses" - {
      def test[C <: ValueClass](v: ValueT[C]): Unit = ()
//      def test[C <: ValueClass] = new {
//        def apply[A](v: A)(implicit ev: A => ValueT[C]): Unit = ()
//      }
//      def test[C <: ValueClass] = new {
//        def apply[A](v: A)(implicit ev: Lazy[A => ValueT[C]]): Unit = ()
//      }
//      def test[C <: ValueClass] = new {
//        def apply[From](f: From)(implicit r: From ==> C): Unit = ()
//      }

      "Len" - {
                      test[Len](inherit)
        compileError("test[Len](1)")
        compileError("test[Len](1.5)")
                      test[Len](len)
        compileError("test[Len](%%)")
        compileError("test[Len](Literal.Typed.thick)")
        compileError("test[Len](Literal.Typed.dashed)")
        compileError("test[Len](Color.black)")
        compileError("test[Len](Literal.Typed.auto)")
        compileError("test[Len](Literal.rtl)")
        compileError("test[Len](sec)")
        compileError("test[Len](ms)")
        ()
      }

      "Pct" - {
                      test[Pct](inherit)
        compileError("test[Pct](1)")
        compileError("test[Pct](1.5)")
        compileError("test[Pct](len)")
                      test[Pct](%%)
        compileError("test[Pct](Literal.Typed.thick)")
        compileError("test[Pct](Literal.Typed.dashed)")
        compileError("test[Pct](Color.black)")
        compileError("test[Pct](Literal.Typed.auto)")
        compileError("test[Pct](Literal.rtl)")
        compileError("test[Pct](sec)")
        compileError("test[Pct](ms)")
        ()
      }

      "Integer" - {
                      test[Integer](inherit)
                      test[Integer](1)
        compileError("test[Integer](1.5)")
        compileError("test[Integer](len)")
        compileError("test[Integer](%%)")
        compileError("test[Integer](Literal.Typed.thick)")
        compileError("test[Integer](Literal.Typed.dashed)")
        compileError("test[Integer](Color.black)")
        compileError("test[Integer](Literal.Typed.auto)")
        compileError("test[Integer](Literal.rtl)")
        compileError("test[Integer](sec)")
        compileError("test[Integer](ms)")
        ()
      }

      "Number" - {
                      test[Number](inherit)
                      test[Number](1)
                      test[Number](1.5)
        compileError("test[Number](len)")
        compileError("test[Number](%%)")
        compileError("test[Number](Literal.Typed.thick)")
        compileError("test[Number](Literal.Typed.dashed)")
        compileError("test[Number](Color.black)")
        compileError("test[Number](Literal.Typed.auto)")
        compileError("test[Number](Literal.rtl)")
        compileError("test[Number](sec)")
        compileError("test[Number](ms)")
        ()
      }

      "LenPct" - {
                      test[LenPct](inherit)
        compileError("test[LenPct](1)")
        compileError("test[LenPct](1.5)")
                      test[LenPct](len)
                      test[LenPct](%%)
        compileError("test[LenPct](Literal.Typed.thick)")
        compileError("test[LenPct](Literal.Typed.dashed)")
        compileError("test[LenPct](Color.black)")
        compileError("test[LenPct](Literal.Typed.auto)")
        compileError("test[LenPct](Literal.rtl)")
        compileError("test[LenPct](sec)")
        compileError("test[LenPct](ms)")
        ()
      }

      "LenPctAuto" - {
                      test[LenPctAuto](inherit)
        compileError("test[LenPctAuto](1)")
        compileError("test[LenPctAuto](1.5)")
                      test[LenPctAuto](len)
                      test[LenPctAuto](%%)
        compileError("test[LenPctAuto](Literal.Typed.thick)")
        compileError("test[LenPctAuto](Literal.Typed.dashed)")
        compileError("test[LenPctAuto](Color.black)")
                      test[LenPctAuto](Literal.Typed.auto)
        compileError("test[LenPctAuto](Literal.rtl)")
        compileError("test[LenPctAuto](sec)")
        compileError("test[LenPctAuto](ms)")
        ()
      }

      "LenPctNum" - {
                      test[LenPctNum](inherit)
                      test[LenPctNum](1)
                      test[LenPctNum](1.5)
                      test[LenPctNum](len)
                      test[LenPctNum](%%)
        compileError("test[LenPctNum](Literal.Typed.thick)")
        compileError("test[LenPctNum](Literal.Typed.dashed)")
        compileError("test[LenPctNum](Color.black)")
        compileError("test[LenPctNum](Literal.Typed.auto)")
        compileError("test[LenPctNum](Literal.rtl)")
        compileError("test[LenPctNum](sec)")
        compileError("test[LenPctNum](ms)")
        ()
      }

      "BrWidth" - {
                      test[BrWidth](inherit)
        compileError("test[BrWidth](1)")
        compileError("test[BrWidth](1.5)")
                      test[BrWidth](len)
        compileError("test[BrWidth](%%)")
                      test[BrWidth](Literal.Typed.thick)
        compileError("test[BrWidth](Literal.Typed.dashed)")
        compileError("test[BrWidth](Color.black)")
        compileError("test[BrWidth](Literal.Typed.auto)")
        compileError("test[BrWidth](Literal.rtl)")
        compileError("test[BrWidth](sec)")
        compileError("test[BrWidth](ms)")
        ()
      }

      "BrStyle" - {
                      test[BrStyle](inherit)
        compileError("test[BrStyle](1)")
        compileError("test[BrStyle](1.5)")
        compileError("test[BrStyle](len)")
        compileError("test[BrStyle](%%)")
        compileError("test[BrStyle](Literal.Typed.thick)")
                      test[BrStyle](Literal.Typed.dashed)
        compileError("test[BrStyle](Color.black)")
        compileError("test[BrStyle](Literal.Typed.auto)")
        compileError("test[BrStyle](Literal.rtl)")
        compileError("test[BrStyle](sec)")
        compileError("test[BrStyle](ms)")
        ()
      }

      "WidStyCol" - {
                      test[WidStyCol](inherit)
        compileError("test[WidStyCol](1)")
        compileError("test[WidStyCol](1.5)")
                      test[WidStyCol](len)
        compileError("test[WidStyCol](%%)")
                      test[WidStyCol](Literal.Typed.thick)
                      test[WidStyCol](Literal.Typed.dashed)
                      test[WidStyCol](Color.black)
        compileError("test[WidStyCol](Literal.Typed.auto)")
        compileError("test[WidStyCol](Literal.rtl)")
        compileError("test[WidStyCol](sec)")
        compileError("test[WidStyCol](ms)")
        ()
      }

      "Time" - {
                      test[Time](inherit)
        compileError("test[Time](1)")
        compileError("test[Time](1.5)")
        compileError("test[Time](len)")
        compileError("test[Time](%%)")
        compileError("test[Time](Literal.Typed.thick)")
        compileError("test[Time](Literal.Typed.dashed)")
        compileError("test[Time](Color.black)")
        compileError("test[Time](Literal.Typed.auto)")
        compileError("test[Time](Literal.rtl)")
                      test[Time](sec)
                      test[Time](ms)
        ()
      }

      "Overflow" - {
                      test[Overflow](inherit)
                      test[Overflow](Dsl.revert)
                      test[Overflow](Literal.Typed.auto)
                      test[Overflow](Literal.Typed.hidden)
                      test[Overflow](Literal.Typed.clip)
                      test[Overflow](Literal.Typed.scroll)
                      test[Overflow](Literal.Typed.visible)
        compileError("test[Overflow](1)")
        compileError("test[Overflow](len)")
        compileError("test[Overflow](Color.black)")
        compileError("test[Overflow](Literal.Typed.solid)")
        ()
      }

      "Flex" - {
        val f = Fr(1)
                      test[Flex](inherit)
                      test[Flex](f)
        compileError("test[Flex](len)")
        compileError("test[Flex](1)")
        compileError("test[Len](f)")
        ()
      }

      "Ang" - {
        val a = Angle(90, AngleUnit.deg)
                      test[Ang](inherit)
                      test[Ang](a)
        compileError("test[Ang](len)")
        compileError("test[Ang](1)")
        compileError("test[Len](a)")
        ()
      }

    }

    "timeValues" - {
      def test(d: FiniteDuration, expected: String)(implicit q: Line): Unit =
        assertEq((d: ValueT[Time]).value, expected)

      "nanos"   - test(500000.nanos,  "0.5ms")
      "micros"  - test(500.micros,    "0.5ms")
      "millis"  - test(250.millis,    "250ms")
      "seconds" - test(3.seconds,     "3s")
      "minutes" - test(2.minutes,     "120s")
    }

    "concat" - {
      val s: ValueT[Time] = sec
      val m: ValueT[Time] = ms
      assertEq(ValueT.concat(" ", len, %%), "12px 25%")
      assertEq(ValueT.concat(" ", len, %%, s), "12px 25% 3s")
      assertEq(ValueT.concat(",", len, %%, s, m), "12px,25%,3s,3ms")
    }

    "revert" - {
      import Dsl.revert
      assertEq((revert: ValueT[Len]).value,    "revert")
      assertEq((revert: ValueT[Pct]).value,    "revert")
      assertEq((revert: ValueT[Number]).value, "revert")
      assertEq((revert: ValueT[Color]).value,  "revert")
      assertEq((revert: ValueT[Time]).value,   "revert")
    }

    "doubleFormatting" - {
      import Dsl._
      assertEq((1.0: ValueT[Number]).value,                 "1")
      assertEq((0.0: ValueT[Number]).value,                 "0")
      assertEq((2.5: ValueT[Number]).value,                 "2.5")
      assertEq((2.0: ValueT[LenPctNum]).value,              "2")
      assertEq((3.0: ValueT[IterationCount]).value,         "3")
      assertEq(Attrs.opacity(1.0).value,                    "1")
      assertEq(Length(2.0, LengthUnit.px).value,            "2px")
      assertEq(Length(2.5, LengthUnit.em).value,            "2.5em")
      assertEq(Length(0.0, LengthUnit.px).value,            "0")
      assertEq(Length(-0.0, LengthUnit.px).value,           "0")
      assertEq((2.0 px).value,                              "2px")
      assertEq(Percentage(50.0).value,                      "50%")
      assertEq(Percentage(33.3).value,                      "33.3%")
      assertEq((50.0 %%).value,                             "50%")
      assertEq(Resolution(300.0, ResolutionUnit.dpi).value, "300dpi")
      assertEq(Resolution(1.5, ResolutionUnit.dppx).value,  "1.5dppx")
      assertEq((300.0 dpi).value,                           "300dpi")
    }

    "viewportUnits" - {
      import Dsl._
      "vi" - assertEq(10.vi.value,    "10vi")
      "vb" - assertEq(10.vb.value,    "10vb")
      "svw" - assertEq(50.svw.value,   "50svw")
      "svh" - assertEq(100.svh.value,  "100svh")
      "svi" - assertEq(10.svi.value,   "10svi")
      "svb" - assertEq(10.svb.value,   "10svb")
      "svmin" - assertEq(10.svmin.value, "10svmin")
      "svmax" - assertEq(10.svmax.value, "10svmax")
      "lvw" - assertEq(50.lvw.value,   "50lvw")
      "lvh" - assertEq(100.lvh.value,  "100lvh")
      "lvi" - assertEq(10.lvi.value,   "10lvi")
      "lvb" - assertEq(10.lvb.value,   "10lvb")
      "lvmin" - assertEq(10.lvmin.value, "10lvmin")
      "lvmax" - assertEq(10.lvmax.value, "10lvmax")
      "dvw" - assertEq(50.dvw.value,   "50dvw")
      "dvh" - assertEq(100.dvh.value,  "100dvh")
      "dvi" - assertEq(10.dvi.value,   "10dvi")
      "dvb" - assertEq(10.dvb.value,   "10dvb")
      "dvmin" - assertEq(10.dvmin.value, "10dvmin")
      "dvmax" - assertEq(10.dvmax.value, "10dvmax")
      "height" - assertEq(height(100.dvh).value, "100dvh")
    }

    "containerQueryUnits" - {
      import Dsl._
      "cqw" - assertEq(10.cqw.value,   "10cqw")
      "cqh" - assertEq(20.cqh.value,   "20cqh")
      "cqi" - assertEq(5.cqi.value,    "5cqi")
      "cqb" - assertEq(5.cqb.value,    "5cqb")
      "cqmin" - assertEq(5.cqmin.value,  "5cqmin")
      "cqmax" - assertEq(5.cqmax.value,  "5cqmax")
      "fontSize" - assertEq(fontSize(5.cqi).value, "5cqi")
    }

    "frUnit" - {
      import Dsl._
      assertEq(1.fr.value,           "1fr")
      assertEq(1.5.fr.value,         "1.5fr")
      assertEq(2.0.fr.value,         "2fr")
      assertEq(0.fr.value,           "0fr")
      assertEq((-0.0).fr.value,      "0fr")
      assertEq((2.fr * 3).value,     "6fr")
      assertEq((3 * 2.fr).value,     "6fr")
      assertEq((3.fr / 2).value,     "1.5fr")
      assertEq((gridTemplateColumns := 1.fr).value, "1fr")
    }

    "angleUnits" - {
      import Dsl._
      assertEq(90.deg.value,         "90deg")
      assertEq(100.grad.value,       "100grad")
      assertEq(1.5.rad.value,        "1.5rad")
      assertEq(0.5.turn.value,       "0.5turn")
      assertEq(90.0.deg.value,       "90deg")
      assertEq(0.deg.value,          "0deg")
      assertEq((-0.0).deg.value,     "0deg")
      assertEq((-(45.deg)).value,    "-45deg")
      assertEq((45.deg * 2).value,   "90deg")
      assertEq((2 * 45.deg).value,   "90deg")
      assertEq((90.deg / 2).value,   "45deg")
      assertEq((imageOrientation := 90.deg).value, "90deg")
    }
  }
}
