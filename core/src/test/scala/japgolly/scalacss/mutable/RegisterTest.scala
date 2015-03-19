package japgolly.scalacss.mutable

import shapeless._
import shapeless.syntax.singleton._ // TODO It would be nice to avoid the need for this import at client site
import utest._
import japgolly.scalacss._
import Attrs._
import Register._
import TestUtil._

object RegisterTest extends TestSuite {

  def styleS(av: AV, avs: AV*) =
    StyleS.data(Map(Cond.empty -> NonEmptyVector(av, avs: _*)))

  val ss1 = styleS(AV(marginTop, "1px"))
  val ss2 = styleS(AV(marginBottom, "2px"))
  val ss3 = styleS(AV(marginRight, "3px"))
  val ss4 = styleS(AV(marginLeft, "4px"))

  val sfid = Domain.ofRange(0 until 4)
  val sfi = StyleF[Int](i => styleS(AV(paddingLeft, s"${i * 4}ex")))(sfid)
  val sfb = StyleF.bool(b => styleS(AV(fontWeight, if (b) "bold" else "normal")))

  val sc1 = ss1.named('a) :*: ss2.named('b)
  val sc2 = ss1.named('a) :*: ss2.named('b) :*: ss3.named('c) :*: ss4.named('d)
  val sc3 = sfb.named('b) :*: ss1.named('s) :*: sfi.named('i)

  def assertDistinctClasses(as: StyleA*): Unit =
    assertDistinctClassNames(as.map(_.className): _*)

  def assertDistinctClassNames(ns: ClassName*): Unit = {
    val l = ns.map(_.value).toList
    assertEq(l, l.distinct)
  }

  override val tests = TestSuite {
    val reg = new Register(NameGen.short(), ErrorHandler.noisy)
    implicit def env = Env.empty

    'registerS {
      val a1 = reg register ss1
      val a2 = reg register ss2
      val a3 = reg register ss3
      val a4 = reg register ss4
      assertDistinctClasses(a1, a2, a3, a4)
      val css = Css(reg.styles).toMap
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

      val fb: Boolean => StyleA = reg register sfb
      val fi: Int     => StyleA = reg register sfi
      val css = Css(reg.styles).toMap
      assertEq(css.size, 2 + 4)
      test(fb, Domain.boolean.toStream)
      test(fi, sfid.toStream)
    }

    'registerC {
      val c1 = reg register sc1
      val c2 = reg register sc2
      val c3 = reg register sc3

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
