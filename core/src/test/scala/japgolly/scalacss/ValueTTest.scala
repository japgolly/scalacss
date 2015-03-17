package japgolly.scalacss

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
        illTyped("test[Len](1)")
        illTyped("test[Len](1.5)")
                  test[Len](len)
        illTyped("test[Len](%%)")
        illTyped("test[Len](Literal.thick)")
        illTyped("test[Len](Literal.dashed)")
        illTyped("test[Len](Color.black)")
        illTyped("test[Len](Literal.auto)")
        illTyped("test[Len](Literal.rtl)")
        illTyped("test[Len](sec)")
        illTyped("test[Len](ms)")
      }

      'Pct {
        illTyped("test[Pct](1)")
        illTyped("test[Pct](1.5)")
        illTyped("test[Pct](len)")
                  test[Pct](%%)
        illTyped("test[Pct](Literal.thick)")
        illTyped("test[Pct](Literal.dashed)")
        illTyped("test[Pct](Color.black)")
        illTyped("test[Pct](Literal.auto)")
        illTyped("test[Pct](Literal.rtl)")
        illTyped("test[Pct](sec)")
        illTyped("test[Pct](ms)")
      }

      'Integer {
                  test[Integer](1)
        illTyped("test[Integer](1.5)")
        illTyped("test[Integer](len)")
        illTyped("test[Integer](%%)")
        illTyped("test[Integer](Literal.thick)")
        illTyped("test[Integer](Literal.dashed)")
        illTyped("test[Integer](Color.black)")
        illTyped("test[Integer](Literal.auto)")
        illTyped("test[Integer](Literal.rtl)")
        illTyped("test[Integer](sec)")
        illTyped("test[Integer](ms)")
      }

      'Number {
                  test[Number](1)
                  test[Number](1.5)
        illTyped("test[Number](len)")
        illTyped("test[Number](%%)")
        illTyped("test[Number](Literal.thick)")
        illTyped("test[Number](Literal.dashed)")
        illTyped("test[Number](Color.black)")
        illTyped("test[Number](Literal.auto)")
        illTyped("test[Number](Literal.rtl)")
        illTyped("test[Number](sec)")
        illTyped("test[Number](ms)")
      }

      'LenPct {
        illTyped("test[LenPct](1)")
        illTyped("test[LenPct](1.5)")
                  test[LenPct](len)
                  test[LenPct](%%)
        illTyped("test[LenPct](Literal.thick)")
        illTyped("test[LenPct](Literal.dashed)")
        illTyped("test[LenPct](Color.black)")
        illTyped("test[LenPct](Literal.auto)")
        illTyped("test[LenPct](Literal.rtl)")
        illTyped("test[LenPct](sec)")
        illTyped("test[LenPct](ms)")
      }

      'LenPctAuto {
        illTyped("test[LenPctAuto](1)")
        illTyped("test[LenPctAuto](1.5)")
                  test[LenPctAuto](len)
                  test[LenPctAuto](%%)
        illTyped("test[LenPctAuto](Literal.thick)")
        illTyped("test[LenPctAuto](Literal.dashed)")
        illTyped("test[LenPctAuto](Color.black)")
                  test[LenPctAuto](Literal.auto)
        illTyped("test[LenPctAuto](Literal.rtl)")
        illTyped("test[LenPctAuto](sec)")
        illTyped("test[LenPctAuto](ms)")
      }

      'LenPctNum {
                  test[LenPctNum](1)
                  test[LenPctNum](1.5)
                  test[LenPctNum](len)
                  test[LenPctNum](%%)
        illTyped("test[LenPctNum](Literal.thick)")
        illTyped("test[LenPctNum](Literal.dashed)")
        illTyped("test[LenPctNum](Color.black)")
        illTyped("test[LenPctNum](Literal.auto)")
        illTyped("test[LenPctNum](Literal.rtl)")
        illTyped("test[LenPctNum](sec)")
        illTyped("test[LenPctNum](ms)")
      }
      
      'BrWidth {
        illTyped("test[BrWidth](1)")
        illTyped("test[BrWidth](1.5)")
                  test[BrWidth](len)
        illTyped("test[BrWidth](%%)")
                  test[BrWidth](Literal.thick)
        illTyped("test[BrWidth](Literal.dashed)")
        illTyped("test[BrWidth](Color.black)")
        illTyped("test[BrWidth](Literal.auto)")
        illTyped("test[BrWidth](Literal.rtl)")
        illTyped("test[BrWidth](sec)")
        illTyped("test[BrWidth](ms)")
      }

      'BrStyle {
        illTyped("test[BrStyle](1)")
        illTyped("test[BrStyle](1.5)")
        illTyped("test[BrStyle](len)")
        illTyped("test[BrStyle](%%)")
        illTyped("test[BrStyle](Literal.thick)")
                  test[BrStyle](Literal.dashed)
        illTyped("test[BrStyle](Color.black)")
        illTyped("test[BrStyle](Literal.auto)")
        illTyped("test[BrStyle](Literal.rtl)")
        illTyped("test[BrStyle](sec)")
        illTyped("test[BrStyle](ms)")
      }

      'WidStyCol {
        illTyped("test[WidStyCol](1)")
        illTyped("test[WidStyCol](1.5)")
                  test[WidStyCol](len)
                  test[WidStyCol](%%)
                  test[WidStyCol](Literal.thick)
                  test[WidStyCol](Literal.dashed)
                  test[WidStyCol](Color.black)
        illTyped("test[WidStyCol](Literal.auto)")
        illTyped("test[WidStyCol](Literal.rtl)")
        illTyped("test[WidStyCol](sec)")
        illTyped("test[WidStyCol](ms)")
      }

      'Time {
        illTyped("test[Time](1)")
        illTyped("test[Time](1.5)")
        illTyped("test[Time](len)")
        illTyped("test[Time](%%)")
        illTyped("test[Time](Literal.thick)")
        illTyped("test[Time](Literal.dashed)")
        illTyped("test[Time](Color.black)")
        illTyped("test[Time](Literal.auto)")
        illTyped("test[Time](Literal.rtl)")
                  test[Time](sec)
                  test[Time](ms)
      }

    }
  }
}
