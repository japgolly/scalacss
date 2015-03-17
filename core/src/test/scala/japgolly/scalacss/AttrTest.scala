package japgolly.scalacss

import japgolly.nyaya._
import japgolly.nyaya.test._
import japgolly.nyaya.test.PropTest._
import utest._
import TestUtil._
import AttrCmp.{Overlap, Unrelated}
import Attrs._
import ValueT.Rules._

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

  val length = ValueT.Length(3, ValueT.LengthUnit.px)
  val style = Literal.dashed
  val colour = Color.green

  def testGen(l: CssKV.Lens)(a: Attr, v: Value, exp: String*): Unit = {
    val x = a.gen(Env.empty)(v).map(l.get).sorted
    val y = exp.toVector.sorted
    assertEq(x, y)
  }

  override val tests = TestSuite {
    'laws1 - laws1.mustBeProvedBy(builtInAttrs)
    'laws2 - laws2.mustBeProvedBy(builtInAttrPairs)
//    'laws3 - Attr.laws3.mustBeSatisfiedBy(builtInAttrTriplets)

    'overlap {
      def test(e: AttrCmp, a: Attr, b: Attr): Unit =
        assertEq(s"$a cmp $b", e, a cmp b)
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

    'keyPrefixes {
      def test(a: Attr, exp: String*): Unit = testGen(CssKV.key)(a, "x", exp: _*)
      test(textAlign,    "text-align")
      test(borderRadius, "border-radius", "-moz-border-radius", "-webkit-border-radius")
      test(flexWrap,     "flex-wrap", "-moz-flex-wrap", "-ms-flex-wrap", "-o-flex-wrap", "-webkit-flex-wrap")
    }

    'valuePrefixes {
      def test(av: AV, exp: String*): Unit = testGen(CssKV.value)(av.attr, av.value, exp: _*)
      test(textAlign.left, "left")
      test(cursor.pointer, "pointer")
      test(cursor.zoom_in, "-moz-zoom-in", "-o-zoom-in", "-webkit-zoom-in", "zoom-in")
    }

    'border {
      assertEq(border(length).value, "3px")
      assertEq(border(length, style).value, "3px dashed")
      assertEq(border(length, style, colour).value, "3px dashed green")
    }
  }
}
