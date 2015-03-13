package japgolly.scalacss

import japgolly.nyaya._
import japgolly.nyaya.test._
import japgolly.nyaya.test.PropTest._
import utest._
import TestUtil._
import AttrCmp.{Overlap, Unrelated}

object AttrTest extends TestSuite {

  def laws1: Prop[Attr] = (
    Prop.equal[Attr, AttrCmp]("cmp is reflexive: a.cmp(a) = Same", a => a cmp a, _ => AttrCmp.Same)
      & Prop.test("id is populated", _.id.nonEmpty)
    )

  def laws2: Prop[(Attr, Attr)] =
    Prop.equal("cmp is commutative: a.cmp(b) = b.cmp(a)", t => t._1 cmp t._2, t => t._2 cmp t._1)

  val builtInAttrs: NDomain[Attr] =
    NDomain.ofValues(Attrs.values.list: _*)

  // TODO Add to Nyaya: Domain.pair
  val builtInAttrPairs: NDomain[(Attr, Attr)] =
    builtInAttrs *** builtInAttrs

  val builtInAttrTriplets: Gen[(Attr, Attr, Attr)] =
    Gen.oneofL(Attrs.values).triple

  override val tests = TestSuite {
    'laws1 - laws1.mustBeProvedBy(builtInAttrs)
    'laws2 - laws2.mustBeProvedBy(builtInAttrPairs)
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
