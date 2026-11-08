package scalacss.internal.mutable

import japgolly.microlibs.nonempty.NonEmptyVector
import japgolly.microlibs.testutil.TestUtil._
import scalacss.internal._
import utest._

object RegisterTest extends TestSuite {
  import Attrs._
  import Register._

  def styleS(av: AV, avs: AV*) =
    StyleS.data(Map(Cond.empty -> AVs(av, avs: _*)))

  val ss1 = styleS(AV(marginTop, "1px"))
  val ss2 = styleS(AV(marginBottom, "2px"))
  val ss3 = styleS(AV(marginRight, "3px"))
  val ss4 = styleS(AV(marginLeft, "4px"))

  val sfid = Domain.ofRange(0 until 4)
  val sfi = StyleF[Int](i => styleS(AV(paddingLeft, s"${i * 4}ex")))(sfid)
  val sfb = StyleF[Boolean](b => styleS(AV(fontWeight, if (b) "bold" else "normal")))(Domain.boolean)

  def assertDistinctClasses(as: StyleA*): Unit =
    assertDistinctClassNames(as.map(_.className): _*)

  def assertDistinctClassNames(ns: ClassName*): Unit = {
    val l = ns.map(_.value).toList
    assertEq(l, l.distinct)
  }

  implicit def env: Env = Env.empty

  def stylesToCssMap(s: Vector[StyleA]) =
    Css.styles(s).map(e => (e.sel, e.content)).toMap

  override def tests = Tests {
    val reg = new Register(NameGen.numbered(), MacroName.Use, ErrorHandler.noisy)
    implicit def cnh: ClassNameHint = ClassNameHint("blah")

    "registerS" - {
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

    "registerF" - {
      def test[I](f: I => StyleA, d: List[I]): Unit = {
        d.foreach { i =>
          assert(f(i) eq f(i))
        }
        assertDistinctClasses(d.map(f): _*)
      }

      val fb: Boolean => StyleA = reg registerF sfb
      val fi: Int     => StyleA = reg registerF sfi
      val css = stylesToCssMap(reg.styles)
      assertEq(css.size, 2 + 4)
      test(fb, Domain.boolean.iterator.toList)
      test(fi, sfid.iterator.toList)
    }

    "addClassNames" - {
      "underlyingStyle" - {
        "multi" - {
          val reg = new Register(NameGen.numbered(), MacroName.Use, ErrorHandler.silent)
          implicit def cnh: ClassNameHint = ClassNameHint("blah")
          val a = reg registerS StyleS.empty.copy(addClassNames = Vector(ClassName("btn"), ClassName("btn-default")))
          assertEq(a.style.addClassNames.map(_.value), Vector("btn", "btn-default"))
        }

        "single" - {
          val reg = new Register(NameGen.numbered(), MacroName.Use, ErrorHandler.silent)
          implicit def cnh: ClassNameHint = ClassNameHint("blah")
          val a = reg registerS StyleS.empty.copy(addClassNames = Vector(ClassName("btn")))
          assertEq(a.style.addClassNames.map(_.value), Vector("btn"))
        }
      }

      "noDuplicateWarning" - {
        var warnings = Vector.empty[String]
        val errHandler = new ErrorHandler {
          override def warn = Some((_, w) => warnings :+= w.msg)
          override def badInput[I](s: StyleF[I], i: I) = ???
        }
        val reg = new Register(NameGen.numbered(), MacroName.Use, errHandler)
        implicit def cnh: ClassNameHint = ClassNameHint("blah")
        val s1 = StyleS.empty.copy(addClassNames = Vector(ClassName("btn"), ClassName("btn-default")))
        val s2 = StyleS.empty.copy(addClassNames = Vector(ClassName("btn"), ClassName("btn-primary")))
        val a1 = reg registerS s1
        val a2 = reg registerS s2
        assertEq(a1.htmlClass, "btn btn-default")
        assertEq(a2.htmlClass, "btn btn-primary")
        assertEq(warnings, Vector.empty)
      }
    }

    "warnings" - {
      import Dsl._
      implicit def composition: Compose = Compose.safe
      implicit def cnh: ClassNameHint = ClassNameHint("blah")

      def newTestReg(): (Register, () => Vector[(ClassName, Warning)]) = {
        var warnings = Vector.empty[(ClassName, Warning)]
        val errHandler = new ErrorHandler {
          override def warn = Some((cn, w) => warnings :+= ((cn, w)))
          override def badInput[I](s: StyleF[I], i: I) = ???
        }
        val reg = new Register(NameGen.numbered(), MacroName.Use, errHandler)
        (reg, () => warnings)
      }

      "unsafeChild" - {
        val (reg, getWarns) = newTestReg()
        val s = Dsl.style(
          unsafeChild("span")(
            color.red,
            color.blue
          )
        )
        reg.registerS(s)
        val warns = getWarns()
        assert(warns.exists(_._2.msg.contains("color")))
      }

      "unsafeRoot" - {
        val (reg, getWarns) = newTestReg()
        val s = Dsl.style(
          unsafeRoot("span")(
            color.red,
            color.blue
          )
        )
        reg.registerS(s)
        val warns = getWarns()
        assert(warns.exists(_._2.msg.contains("color")))
      }

      "keyframes" - {
        val (reg, getWarns) = newTestReg()
        val kfStyle = Dsl.style(color.red, color.blue)
        val kf = Keyframes(ClassName("myAnim"), Seq((Percentage(0), StyleA(ClassName("k0"), Vector.empty, kfStyle))))
        reg.registerKeyframes(kf)
        val warns = getWarns()
        assert(warns.exists(_._2.msg.contains("color")))
      }

      "standalone" - {
        val (reg, getWarns) = newTestReg()
        implicit val r: Register = reg
        class MySS extends StyleSheet.Standalone {
          import dsl._
          RootStringOps("div").-(
            color.red,
            color.blue
          )
        }
        new MySS
        val warns = getWarns()
        assert(warns.exists(_._2.msg.contains("color")))
      }

      "fontFacePostRender" - {
        val (reg, getWarns) = newTestReg()
        val _ = reg.fontFaces
        val ff = FontFace[Option[String]](Some("myFont"), NonEmptyVector("url(font.woff)"))
        reg.registerFontFace(ff)
        val warns = getWarns()
        assert(warns.exists(_._2.msg.contains("after stylesheet has been rendered")))
      }
    }

  }
}
