package scalacss

import nyaya.gen._
import nyaya.prop._
import nyaya.test.PropTest._
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
    NDomain.ofValues(Attrs.values.whole: _*)

  val builtInAttrPairs: NDomain[(Attr, Attr)] =
    builtInAttrs.pair

  val builtInAttrTriplets: Gen[(Attr, Attr, Attr)] =
    RandomData.attr.triple

  def px(n: Int) = Length(n, LengthUnit.px)
  val length = px(3)
  val style = Literal.Typed.dashed
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
      test(cursor.zoomIn, "-moz-zoom-in", "-o-zoom-in", "-webkit-zoom-in", "zoom-in")
    }

    'border {
      assertEq(border(length).value, "3px")
      assertEq(border(length, style).value, "3px dashed")
      assertEq(border(length, style, colour).value, "3px dashed green")
    }

    'textIndent {
      def test(av: AV, exp: String): Unit = assertEq(av.value, exp)
      import Literal.Typed._
      test(textIndent(length)                   , "3px")
      test(textIndent(length, hanging)          , "3px hanging")
      test(textIndent(length, eachLine)         , "3px each-line")
      test(textIndent(length, hanging, eachLine), "3px hanging each-line")
    }

    'borderRadius{
      @inline implicit def ToAVToAV(x: ToAV): AV = x.av
      def test(av: AV, exp: String): Unit = assertEq(av.value, exp)
      test(borderRadius(px(3)), "3px")
      test(borderRadius(px(3))(px(5)), "3px / 5px")
      test(borderRadius(px(1), px(2), px(3), px(4))(px(9), px(8), px(7), px(6)), "1px 2px 3px 4px / 9px 8px 7px 6px")
    }

    'PrefixApplyWords {
      val pa = CanIUse2.PrefixApply.keywords("abc", "def")
      def test(i: String, eo: String = null): Unit = {
        val o = pa(i).fold(i)(_(CanIUse.Prefix.o))
        assertEq(o, Option(eo) getOrElse i)
      }
      test("abc", "-o-abc")
      test("def", "-o-def")
      test("abc-")
      test("abc0")
      test("abcd")
      test("-abc")
      test("0abc")
      test("aabc")
      test(" abc", " -o-abc")
      test("abc ", "-o-abc ")
      test("abc 123", "-o-abc 123")
      test("qwe abc", "qwe -o-abc")
      test("abc abc 123 def", "-o-abc -o-abc 123 -o-def")
      test("-o-abc")
      test("def(12)", "-o-def(12)")
    }

    'envDepPrefixes1 {
      def test(name: String)(exp: String*): Unit = {
        val env = Env.empty.copy(platform = Env.Platform.empty(None).copy(name = Some(name)))
        val a = cursor.zoomIn(env).map(_.value).sorted
        assertEq(a, exp.toVector.sorted)
      }
      'chrome  - test("Chrome") ("-webkit-zoom-in",               "zoom-in")
      'firefox - test("Firefox")("-moz-zoom-in",                  "zoom-in")
      'opera   - test("Opera")  ("-o-zoom-in", "-webkit-zoom-in", "zoom-in")
      'ie      - test("IE")     (/*"-ms-zoom-in", Unsupported */  "zoom-in")
      'safari  - test("Safari") ("-webkit-zoom-in",               "zoom-in")
    }

    'envDepPrefixes2 {
      def test(name: String)(exp: String*): Unit = {
        val env = Env.empty.copy(platform = Env.Platform.empty(None).copy(name = Some(name)))
        val a = AV(flex, "")(env).map(_.key).sorted
        assertEq(a, exp.toVector.sorted)
      }
      'chrome  - test("Chrome") ("-webkit-flex",            "flex")
      'firefox - test("Firefox")("-moz-flex",               "flex")
      'opera   - test("Opera")  ("-o-flex", "-webkit-flex", "flex")
      'ie      - test("IE")     ("-ms-flex",                "flex")
      'safari  - test("Safari") ("-webkit-flex",            "flex")
    }
  }
}
