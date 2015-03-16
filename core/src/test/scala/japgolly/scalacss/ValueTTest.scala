package japgolly.scalacss

import scala.language.reflectiveCalls
import shapeless.test.illTyped
import utest._
import ValueT._
import ValueT.Rules._

object ValueTTest extends TestSuite {

  val len = Length(12, LengthUnit.px)
  val %% = Percentage(25)

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
      }

      'Integer {
                  test[Integer](1)
        illTyped("test[Integer](1.5)")
        illTyped("test[Integer](len)")
        illTyped("test[Integer](%%)")
        illTyped("test[Integer](Literal.thick)")
        illTyped("test[Integer](Literal.dashed)")
      }

      'Number {
                  test[Number](1)
                  test[Number](1.5)
        illTyped("test[Number](len)")
        illTyped("test[Number](%%)")
        illTyped("test[Number](Literal.thick)")
        illTyped("test[Number](Literal.dashed)")
      }

      'LenPct {
        illTyped("test[LenPct](1)")
        illTyped("test[LenPct](1.5)")
                  test[LenPct](len)
                  test[LenPct](%%)
        illTyped("test[LenPct](Literal.thick)")
        illTyped("test[LenPct](Literal.dashed)")
      }

      'LenPctNum {
                  test[LenPctNum](1)
                  test[LenPctNum](1.5)
                  test[LenPctNum](len)
                  test[LenPctNum](%%)
        illTyped("test[LenPctNum](Literal.thick)")
        illTyped("test[LenPctNum](Literal.dashed)")
      }
      
      'BrWidth {
        illTyped("test[BrWidth](1)")
        illTyped("test[BrWidth](1.5)")
                  test[BrWidth](len)
        illTyped("test[BrWidth](%%)")
                  test[BrWidth](Literal.thick)
        illTyped("test[BrWidth](Literal.dashed)")
      }

      'BrStyle {
        illTyped("test[BrStyle](1)")
        illTyped("test[BrStyle](1.5)")
        illTyped("test[BrStyle](len)")
        illTyped("test[BrStyle](%%)")
        illTyped("test[BrStyle](Literal.thick)")
        test[BrStyle](Literal.dashed)
      }
    }
  }
}
