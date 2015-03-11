package japgolly.scalacss

import japgolly.nyaya.test._
import japgolly.nyaya.test.PropTest._
import utest._
import TestUtil._
import AttrCmp.{Overlap, Unrelated}

object AttrTest extends TestSuite {

  val builtInAttrs: Domain[Attr] =
    Domain.ofValues(Attrs.values.list: _*)

  // TODO Add to Nyaya: Domain.pair
  val builtInAttrPairs: Domain[(Attr, Attr)] =
    builtInAttrs *** builtInAttrs

  val builtInAttrTriplets: Gen[(Attr, Attr, Attr)] =
    Gen.oneofL(Attrs.values).triple

  override val tests = TestSuite {
    'laws1 - Attr.laws1.mustBeProvedBy(builtInAttrs)
    'laws2 - Attr.laws2.mustBeProvedBy(builtInAttrPairs)
//    'laws3 - Attr.laws3.mustBeSatisfiedBy(builtInAttrTriplets)

    'overlap {
      def test(e: AttrCmp, a: Attr, b: Attr): Unit =
        assertEq(s"$a cmp $b", e, a cmp b)
      import Attrs._
      test(Unrelated, padding,     margin)
      test(Overlap,   padding,     paddingLeft)
      test(Overlap,   all,         padding)
      test(Overlap,   all,         paddingLeft)
      test(Overlap,   border,      borderLeft)
      test(Overlap,   border,      borderColor)
      test(Overlap,   borderLeft,  borderColor)
      test(Unrelated, borderStyle, borderColor)
      test(Unrelated, borderLeft,  borderRightColor)
      test(Unrelated, borderLeft,  borderRight)
    }
  }
}
