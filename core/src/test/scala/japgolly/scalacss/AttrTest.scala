package japgolly.scalacss

import japgolly.nyaya.test._
import japgolly.nyaya.test.PropTest._
import utest._
import TestUtil._

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
    'laws3 - Attr.laws3.mustBeSatisfiedBy(builtInAttrTriplets)

    /*
    'overlapTransitivity {
      // A ← B → C → D
      val a = Attr.simple("a")
      val d = Attr.simple("d")
      val c = Attr.simpleO("c")(d)
      val b = Attr.simpleO("b")(a, c) // not even referenced
      assertEq("a cmp d", a cmp d, Overlap)
      assertEq("d cmp a", d cmp a, Overlap)
    }
    */
  }
}
