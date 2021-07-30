package scalacss.internal

import scala.annotation.nowarn
import scala.concurrent.duration._
import scalacss.internal.ValueT.Rules._
import scalacss.internal.ValueT._
import utest._

object ValueTTest extends TestSuite {

  val len = Length(12, LengthUnit.px)
  val %%  = Percentage(25)
  val sec = 3 seconds
  val ms  = 3 milliseconds
  import Dsl.inherit

  override def tests = Tests {

    "valueClasses" - {
      @nowarn("cat=unused")
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
                      test[WidStyCol](%%)
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

    }
  }
}
