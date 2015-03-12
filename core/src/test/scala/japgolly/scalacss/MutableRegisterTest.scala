package japgolly.scalacss

import scalaz.NonEmptyList
import utest._
import japgolly.TODO.Domain
import Attrs._
import MutableRegister._
import TestUtil._

object MutableRegisterTest extends TestSuite {

  def styleS(av: AV, avs: AV*) =
    new StyleS(Map(NoCond -> AVsAndWarnings(NonEmptyList(av, avs: _*), Nil)), Nil, None)

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

  def assertDistinct(ns: ClassName*): Unit = {
    val l = ns.map(_.value).toList
    assertEq(l, l.distinct)
  }

  override val tests = TestSuite {
    val reg = new MutableRegister(NameGen.short(), ErrorHandler.noisy)
    implicit def env = Env.empty

    'registerS {
      val a1 = reg register ss1
      val a2 = reg register ss2
      val a3 = reg register ss3
      val a4 = reg register ss4
      assertDistinct(a1.className, a2.className, a3.className, a4.className)
      val css = Css(reg.styles).toMap
      assertEq(css(Css className a1.className), NonEmptyList(CssKV("margin-top", "1px")))
      assertEq(css(Css className a2.className), NonEmptyList(CssKV("margin-bottom", "2px")))
      assertEq(css(Css className a3.className), NonEmptyList(CssKV("margin-right", "3px")))
      assertEq(css(Css className a4.className), NonEmptyList(CssKV("margin-left", "4px")))
      assertEq(css.size, 4)
    }

    'registerF {
      def test[I](f: I => StyleA, d: Stream[I]): Unit = {
        d.foreach { i =>
          assert(f(i) eq f(i))
        }
        assertDistinct(d.map(f).map(_.className): _*)
      }

      val fb: Boolean => StyleA = reg register sfb
      val fi: Int     => StyleA = reg register sfi
      val css = Css(reg.styles).toMap
      assertEq(css.size, 2 + 4)
      test(fb, Domain.boolean.toStream)
      test(fi, sfid.toStream)
    }
  }
}
