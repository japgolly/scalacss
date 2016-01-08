package scalacss.mutable

import shapeless._
import shapeless.syntax.singleton._ // TODO It would be nice to avoid the need for this import at client site
import utest._
import scalacss._
import Attrs._
import Register._
import TestUtil._

object RegisterTest extends TestSuite {

  def styleS(av: AV, avs: AV*) =
    StyleS.data(Map(Cond.empty -> AVs(av, avs: _*)))

  val ss1 = styleS(AV(marginTop, "1px"))
  val ss2 = styleS(AV(marginBottom, "2px"))
  val ss3 = styleS(AV(marginRight, "3px"))
  val ss4 = styleS(AV(marginLeft, "4px"))

  val sfid = Domain.ofRange(0 until 4)
  val sfi = StyleF[Int](i => styleS(AV(paddingLeft, s"${i * 4}ex")))(sfid)
  val sfb = StyleF[Boolean](b => styleS(AV(fontWeight, if (b) "bold" else "normal")))(Domain.boolean)

  val sc1 = ss1.named('a) :*: ss2.named('b)
  val sc2 = ss1.named('a) :*: ss2.named('b) :*: ss3.named('c) :*: ss4.named('d)
  val sc3 = sfb.named('b) :*: ss1.named('s) :*: sfi.named('i)

  def assertDistinctClasses(as: StyleA*): Unit =
    assertDistinctClassNames(as.map(_.className): _*)

  def assertDistinctClassNames(ns: ClassName*): Unit = {
    val l = ns.map(_.value).toList
    assertEq(l, l.distinct)
  }

  implicit def env = Env.empty

  def stylesToCssMap(s: Vector[StyleA]) =
    Css.styles(s).map(e => (e.sel, e.content)).toMap

  override val tests = TestSuite {
    val reg = new Register(NameGen.numbered(), MacroName.Use, ErrorHandler.noisy)
    implicit def cnh = ClassNameHint("blah")

    'registerS {
      val a1 = reg registerS ss1
      val a2 = reg registerS ss2
      val a3 = reg registerS ss3
      val a4 = reg registerS ss4
      assertDistinctClasses(a1, a2, a3, a4)
      val css = stylesToCssMap(reg.styles)
      assertEq(css(Css className a1.className), NonEmptyVector(CssKV("margin-top", "1px")))
      assertEq(css(Css className a2.className), NonEmptyVector(CssKV("margin-bottom", "2px")))
      assertEq(css(Css className a3.className), NonEmptyVector(CssKV("margin-right", "3px")))
      assertEq(css(Css className a4.className), NonEmptyVector(CssKV("margin-left", "4px")))
      assertEq(css.size, 4)
    }

    'registerF {
      def test[I](f: I => StyleA, d: Stream[I]): Unit = {
        d.foreach { i =>
          assert(f(i) eq f(i))
        }
        assertDistinctClasses(d.map(f): _*)
      }

      val fb: Boolean => StyleA = reg registerF sfb
      val fi: Int     => StyleA = reg registerF sfi
      val css = stylesToCssMap(reg.styles)
      assertEq(css.size, 2 + 4)
      test(fb, Domain.boolean.toStream)
      test(fi, sfid.toStream)
    }

    'registerC {
      val c1 = reg registerC sc1
      val c2 = reg registerC sc2
      val c3 = reg registerC sc3

      val result =
        c1('a)(a => _('b)(b => {
          assertDistinctClasses(a, b)
          123
        }))
      assertEq(result, 123)

      c2('a)(a =>
        _('b)(b =>
          _('c)(c =>
            _('d)(d =>
              assertDistinctClasses(a, b, c, d)))))

      c3('b)(b =>
        _('s)(s =>
          _('i)(i =>
            assertDistinctClasses(b(true), b(false), s, i(0), i(1), i(2), i(3)))))
    }
  }
}
