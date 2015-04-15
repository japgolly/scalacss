package scalacss

import scala.concurrent.duration._
import scala.language.reflectiveCalls
import shapeless.test.illTyped
import utest._
import ValueT._
import ValueT.Rules._

object ValueTTest extends TestSuite {

  val len = Length(12, LengthUnit.px)
  val %%  = Percentage(25)
  val sec = 3 seconds
  val ms  = 3 milliseconds
  import Dsl.inherit

  override val tests = TestSuite {

    'valueClasses {
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

      'Len {
                  test[Len](inherit)
        illTyped("test[Len](1)")
        illTyped("test[Len](1.5)")
                  test[Len](len)
        illTyped("test[Len](%%)")
        illTyped("test[Len](Literal.Typed.thick)")
        illTyped("test[Len](Literal.Typed.dashed)")
        illTyped("test[Len](Color.black)")
        illTyped("test[Len](Literal.Typed.auto)")
        illTyped("test[Len](Literal.rtl)")
        illTyped("test[Len](sec)")
        illTyped("test[Len](ms)")
      }

      'Pct {
                  test[Pct](inherit)
        illTyped("test[Pct](1)")
        illTyped("test[Pct](1.5)")
        illTyped("test[Pct](len)")
                  test[Pct](%%)
        illTyped("test[Pct](Literal.Typed.thick)")
        illTyped("test[Pct](Literal.Typed.dashed)")
        illTyped("test[Pct](Color.black)")
        illTyped("test[Pct](Literal.Typed.auto)")
        illTyped("test[Pct](Literal.rtl)")
        illTyped("test[Pct](sec)")
        illTyped("test[Pct](ms)")
      }

      'Integer {
                  test[Integer](inherit)
                  test[Integer](1)
        illTyped("test[Integer](1.5)")
        illTyped("test[Integer](len)")
        illTyped("test[Integer](%%)")
        illTyped("test[Integer](Literal.Typed.thick)")
        illTyped("test[Integer](Literal.Typed.dashed)")
        illTyped("test[Integer](Color.black)")
        illTyped("test[Integer](Literal.Typed.auto)")
        illTyped("test[Integer](Literal.rtl)")
        illTyped("test[Integer](sec)")
        illTyped("test[Integer](ms)")
      }

      'Number {
                  test[Number](inherit)
                  test[Number](1)
                  test[Number](1.5)
        illTyped("test[Number](len)")
        illTyped("test[Number](%%)")
        illTyped("test[Number](Literal.Typed.thick)")
        illTyped("test[Number](Literal.Typed.dashed)")
        illTyped("test[Number](Color.black)")
        illTyped("test[Number](Literal.Typed.auto)")
        illTyped("test[Number](Literal.rtl)")
        illTyped("test[Number](sec)")
        illTyped("test[Number](ms)")
      }

      'LenPct {
                  test[LenPct](inherit)
        illTyped("test[LenPct](1)")
        illTyped("test[LenPct](1.5)")
                  test[LenPct](len)
                  test[LenPct](%%)
        illTyped("test[LenPct](Literal.Typed.thick)")
        illTyped("test[LenPct](Literal.Typed.dashed)")
        illTyped("test[LenPct](Color.black)")
        illTyped("test[LenPct](Literal.Typed.auto)")
        illTyped("test[LenPct](Literal.rtl)")
        illTyped("test[LenPct](sec)")
        illTyped("test[LenPct](ms)")
      }

      'LenPctAuto {
                  test[LenPctAuto](inherit)
        illTyped("test[LenPctAuto](1)")
        illTyped("test[LenPctAuto](1.5)")
                  test[LenPctAuto](len)
                  test[LenPctAuto](%%)
        illTyped("test[LenPctAuto](Literal.Typed.thick)")
        illTyped("test[LenPctAuto](Literal.Typed.dashed)")
        illTyped("test[LenPctAuto](Color.black)")
                  test[LenPctAuto](Literal.Typed.auto)
        illTyped("test[LenPctAuto](Literal.rtl)")
        illTyped("test[LenPctAuto](sec)")
        illTyped("test[LenPctAuto](ms)")
      }

      'LenPctNum {
                  test[LenPctNum](inherit)
                  test[LenPctNum](1)
                  test[LenPctNum](1.5)
                  test[LenPctNum](len)
                  test[LenPctNum](%%)
        illTyped("test[LenPctNum](Literal.Typed.thick)")
        illTyped("test[LenPctNum](Literal.Typed.dashed)")
        illTyped("test[LenPctNum](Color.black)")
        illTyped("test[LenPctNum](Literal.Typed.auto)")
        illTyped("test[LenPctNum](Literal.rtl)")
        illTyped("test[LenPctNum](sec)")
        illTyped("test[LenPctNum](ms)")
      }

      'BrWidth {
                  test[BrWidth](inherit)
        illTyped("test[BrWidth](1)")
        illTyped("test[BrWidth](1.5)")
                  test[BrWidth](len)
        illTyped("test[BrWidth](%%)")
                  test[BrWidth](Literal.Typed.thick)
        illTyped("test[BrWidth](Literal.Typed.dashed)")
        illTyped("test[BrWidth](Color.black)")
        illTyped("test[BrWidth](Literal.Typed.auto)")
        illTyped("test[BrWidth](Literal.rtl)")
        illTyped("test[BrWidth](sec)")
        illTyped("test[BrWidth](ms)")
      }

      'BrStyle {
                  test[BrStyle](inherit)
        illTyped("test[BrStyle](1)")
        illTyped("test[BrStyle](1.5)")
        illTyped("test[BrStyle](len)")
        illTyped("test[BrStyle](%%)")
        illTyped("test[BrStyle](Literal.Typed.thick)")
                  test[BrStyle](Literal.Typed.dashed)
        illTyped("test[BrStyle](Color.black)")
        illTyped("test[BrStyle](Literal.Typed.auto)")
        illTyped("test[BrStyle](Literal.rtl)")
        illTyped("test[BrStyle](sec)")
        illTyped("test[BrStyle](ms)")
      }

      'WidStyCol {
                  test[WidStyCol](inherit)
        illTyped("test[WidStyCol](1)")
        illTyped("test[WidStyCol](1.5)")
                  test[WidStyCol](len)
                  test[WidStyCol](%%)
                  test[WidStyCol](Literal.Typed.thick)
                  test[WidStyCol](Literal.Typed.dashed)
                  test[WidStyCol](Color.black)
        illTyped("test[WidStyCol](Literal.Typed.auto)")
        illTyped("test[WidStyCol](Literal.rtl)")
        illTyped("test[WidStyCol](sec)")
        illTyped("test[WidStyCol](ms)")
      }

      'Time {
                  test[Time](inherit)
        illTyped("test[Time](1)")
        illTyped("test[Time](1.5)")
        illTyped("test[Time](len)")
        illTyped("test[Time](%%)")
        illTyped("test[Time](Literal.Typed.thick)")
        illTyped("test[Time](Literal.Typed.dashed)")
        illTyped("test[Time](Color.black)")
        illTyped("test[Time](Literal.Typed.auto)")
        illTyped("test[Time](Literal.rtl)")
                  test[Time](sec)
                  test[Time](ms)
      }

    }
  }
}
