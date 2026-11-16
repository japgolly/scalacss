package scalacss.internal.mutable

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

      def newTestReg(): (Register, () => Vector[(Either[FontFamily, ClassName], Warning)]) = {
        var warnings = Vector.empty[(Either[FontFamily, ClassName], Warning)]
        val errHandler = new ErrorHandler {
          override def warn = Some((src, w) => warnings :+= ((src, w)))
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
        val ff = FontFace[Option[FontFamily]](Some(FontFamily("myFont")), NonEmptyVector("url(font.woff)"))
        reg.registerFontFace(ff)
        val warns = getWarns()
        assert(warns.exists(_._2.msg.contains("after stylesheet has been rendered")))
      }
    }

    "keyframesMacroName" - {
      implicit val compose: Compose = Compose.trust

      "backtickedWithWhitespace" - {
        "macroNameUse" - {
          implicit val reg: Register = new Register(NameGen.numbered(), MacroName.Use, ErrorHandler.silent)
          class SS extends StyleSheet.Inline {
            override protected implicit val classNameHint: ClassNameHint = ClassNameHint("SS")
            import dsl._
            val `fade in` = keyframes((0 %%) -> keyframe(color.red))
          }
          val ss = new SS
          assertEq(ss.`fade in`.name.value, "SS-fade_in")
        }

        "macroNameIgnore" - {
          implicit val reg: Register = new Register(NameGen.numbered(), MacroName.Ignore, ErrorHandler.silent)
          class SS extends StyleSheet.Inline {
            override protected implicit val classNameHint: ClassNameHint = ClassNameHint("SS")
            import dsl._
            val `fade in` = keyframes((0 %%) -> keyframe(color.red))
          }
          val ss = new SS
          assertEq(ss.`fade in`.name.value, "SS-0001")
        }
      }
    }

    "keyframeManualClassName" - {
      implicit val compose: Compose = Compose.trust
      implicit val reg: Register = new Register(NameGen.numbered(), MacroName.Use, ErrorHandler.silent)
      class SS extends StyleSheet.Inline {
        import dsl._
        val kf = keyframe("custom")(color.red)
      }
      val ss = new SS
      assertEq(ss.kf.className, ClassName("custom"))
    }

    "applyMacroName" - {
      implicit val compose: Compose = Compose.trust

      "preserveExplicitClassName" - {
        "macroNameUse" - {
          implicit val reg: Register = new Register(NameGen.numbered(), MacroName.Use, ErrorHandler.silent)
          class SS extends StyleSheet.Inline {
            override protected implicit val classNameHint: ClassNameHint = ClassNameHint("SS")
            import dsl._
            private val explicit = Dsl.style("explicit")(color.red)
            val s = style(explicit)
          }
          val ss = new SS
          assertEq(ss.s.className, ClassName("explicit"))
        }

        "macroNameIgnore" - {
          implicit val reg: Register = new Register(NameGen.numbered(), MacroName.Ignore, ErrorHandler.silent)
          class SS extends StyleSheet.Inline {
            override protected implicit val classNameHint: ClassNameHint = ClassNameHint("SS")
            import dsl._
            private val explicit = Dsl.style("explicit")(color.red)
            val s = style(explicit)
          }
          val ss = new SS
          assertEq(ss.s.className, ClassName("explicit"))
        }
      }

      "emptyStyle" - {
        implicit val reg: Register = new Register(NameGen.numbered(), MacroName.Use, ErrorHandler.silent)
        class SS extends StyleSheet.Inline {
          override protected implicit val classNameHint: ClassNameHint = ClassNameHint("SS")
          import dsl._
          val empty = style()
          val btn = style(addClassNames("btn", "btn-default"))
        }
        val ss = new SS
        assertEq(ss.empty.className, ClassName("SS-empty"))
        assertEq(ss.btn.className, ClassName("btn"))
        assertEq(ss.btn.addClassNames, Vector(ClassName("btn-default")))
      }
    }

    "macroNameWithDomain" - {
      implicit val compose: Compose = Compose.trust

      "sanitisesDomain" - {
        assertEq(MacroName.Use(ClassNameHint("SS"), "f", "a b?"), Some(ClassName("SS-f-a_b_")))
        assertEq(MacroName.Ignore(ClassNameHint("SS"), "f", "a b?"), None)
      }

      "styleFCustomSuffix" - {
        implicit val reg: Register = new Register(NameGen.numbered(), MacroName.Use, ErrorHandler.silent)
        class SS extends StyleSheet.Inline {
          override protected implicit val classNameHint: ClassNameHint = ClassNameHint("SS")
          import dsl._
          val f = styleF(Domain.boolean)(_ => dsl.styleS(color.red), (b, _) => if (b) "yes!" else "no ?")
        }
        val ss = new SS
        assertEq(ss.f(true).className, ClassName("SS-f-yes_"))
        assertEq(ss.f(false).className, ClassName("SS-f-no__"))
      }
    }

    "registerF2" - {
      implicit val compose: Compose = Compose.trust

      "sanitisesNameAndSuffix" - {
        implicit val reg: Register = new Register(NameGen.numbered(), MacroName.Use, ErrorHandler.silent)
        class SS extends StyleSheet.Inline {
          override protected implicit val classNameHint: ClassNameHint = ClassNameHint("SS")
          import dsl._
          val f = styleF("my style")(Domain.boolean)(_ => dsl.styleS(color.red), (b, _) => if (b) "yes!" else "no ?")
        }
        val ss = new SS
        assertEq(ss.f(true).className, ClassName("my_style-yes_"))
        assertEq(ss.f(false).className, ClassName("my_style-no__"))
      }

      "emptyManualName" - {
        implicit val reg: Register = new Register(NameGen.numbered(), MacroName.Use, ErrorHandler.silent)
        class SS extends StyleSheet.Inline {
          override protected implicit val classNameHint: ClassNameHint = ClassNameHint("SS")
          import dsl._
          val f = styleF("")(Domain.boolean)(_ => dsl.styleS(color.red))
        }
        val ss = new SS
        assertEq(ss.f(true).className, ClassName("SS-0001"))
        assertEq(ss.f(false).className, ClassName("SS-0002"))
      }
    }

    "registerFM" - {
      implicit val compose: Compose = Compose.trust

      "addClassNamesOnly" - {
        implicit val reg: Register = new Register(NameGen.numbered(), MacroName.Use, ErrorHandler.silent)
        class SS extends StyleSheet.Inline {
          override protected implicit val classNameHint: ClassNameHint = ClassNameHint("SS")
          import dsl._
          val btn = styleF.bool { b =>
            if (b) addClassNames("btn", "btn-lg")
            else   addClassNames("btn", "btn-sm")
          }
          val mixed = styleF.bool { b =>
            if (b) addClassNames("btn", "btn-lg")
            else   dsl.styleS(addClassNames("btn"), color.red)
          }
        }
        val ss = new SS
        assertEq(ss.btn(true).htmlClass, "btn btn-lg")
        assertEq(ss.btn(false).htmlClass, "btn btn-sm")
        assertEq(ss.mixed(true).htmlClass, "btn btn-lg")
        assertEq(ss.mixed(false).htmlClass, "SS-mixed-f btn")
      }
    }

    "classNameHint" - {
      implicit val compose: Compose = Compose.trust

      "backtickedWhitespace" - {
        implicit val reg: Register = new Register(NameGen.numbered(), MacroName.Use, ErrorHandler.silent)
        class `my sheet` extends StyleSheet.Inline {
          import dsl._
          val s = style(color.red)
          def hint = classNameHint
        }
        val ss = new `my sheet`
        assert(!ss.hint.value.exists(_.isWhitespace))
        assertEq(ss.s.className.value, s"${ss.hint.value}-s")
      }

      "dollarObject" - {
        val cn = `$`.s.className.value
        assert(cn.matches("^[_a-zA-Z\u00a0-\u00ff][_a-zA-Z0-9\u00a0-\u00ff-]*$"))
      }

      "mkClassNameHint" - {
        assertEq(StyleSheet.mkClassNameHint("com.example.MyStyles$"), ClassNameHint("MyStyles"))
        assertEq(StyleSheet.mkClassNameHint("$line8.$read.$iw.$iw.MyStyles$"), ClassNameHint("MyStyles"))
        assertEq(StyleSheet.mkClassNameHint("pkg.my sheet!$"), ClassNameHint("my_sheet_"))
        assertEq(StyleSheet.mkClassNameHint("pkg.123$"), ClassNameHint("_123"))
        assertEq(StyleSheet.mkClassNameHint("pkg.$$"), ClassNameHint("StyleSheet"))
        assertEq(StyleSheet.mkClassNameHint("$line8.$read.$iw.$iw.$$"), ClassNameHint("StyleSheet"))
      }
    }

    "dslMacroName" - {
      "nestedObjectsAndDecodedNames" - {
        val reg = new Register(NameGen.numbered(), MacroName.Use, ErrorHandler.silent)
        val ss = new DslMacroNameSS(reg)
        assertEq(ss.`a+b`.className, ClassName("SS-a_b"))
        assertEq(ss.nested.depth1.className, ClassName("SS-nested-depth1"))
        assertEq(ss.nested.deeper.depth2.className, ClassName("SS-nested-deeper-depth2"))
        assertEq(ss.`sub+group`.`my style!`.className, ClassName("SS-sub_group-my_style_"))
      }

      "inheritedStyleSheetClass" - {
        val reg = new Register(NameGen.numbered(), MacroName.Use, ErrorHandler.silent)
        val ss = new DslMacroNameSubSS(reg)
        assertEq(ss.nested.s.className, ClassName("SS-nested-s"))
      }
    }

    "nextNameCollision" - {

      "registerS" - {
        var warnings = Vector.empty[String]
        val errHandler = new ErrorHandler {
          override def warn = Some((_, w) => warnings :+= w.msg)
          override def badInput[I](s: StyleF[I], i: I) = ???
        }
        val reg = new Register(NameGen.numbered(), MacroName.Ignore, errHandler)
        implicit val cnh: ClassNameHint = ClassNameHint("SS")
        val manual = reg.registerS(ss1.copy(className = Some(ClassName("SS-0001"))))
        val auto   = reg.registerS(ss2)
        assertEq(manual.className, ClassName("SS-0001"))
        assertEq(auto.className, ClassName("SS-0002"))
        assertEq(warnings, Vector.empty)
      }

      "registerKeyframes" - {
        var warnings = Vector.empty[String]
        val errHandler = new ErrorHandler {
          override def warn = Some((_, w) => warnings :+= w.msg)
          override def badInput[I](s: StyleF[I], i: I) = ???
        }
        val reg = new Register(NameGen.numbered(), MacroName.Ignore, errHandler)
        implicit val cnh: ClassNameHint = ClassNameHint("SS")
        val manual = reg.registerS(ss1.copy(className = Some(ClassName("SS-0001"))))
        val kf     = reg.registerKeyframes("fade", Seq((Percentage(0), StyleA(ClassName("k0"), Vector.empty, ss2))))
        assertEq(manual.className, ClassName("SS-0001"))
        assertEq(kf.name, ClassName("SS-0002"))
        assertEq(warnings, Vector.empty)
      }

      "constantNameGen" - {
        val constNG: NameGen = new NameGen {
          override def next(cnh: ClassNameHint) = (ClassName("dup"), this)
        }
        val reg = new Register(constNG, MacroName.Ignore, ErrorHandler.silent)
        implicit val cnh: ClassNameHint = ClassNameHint("SS")
        val a1 = reg.registerS(ss1)
        val a2 = reg.registerS(ss2)
        assertDistinctClasses(a1, a2)
      }
    }

  }
}

private object `$` extends StyleSheet.Inline()(new Register(Register.NameGen.numbered(), Register.MacroName.Use, Register.ErrorHandler.silent)) {
  import dsl._
  private implicit val compose: Compose = Compose.trust
  val s = style(color.red)
}

private class DslMacroNameSS(reg: Register) extends StyleSheet.Inline()(reg) {
  override protected implicit val classNameHint: ClassNameHint = ClassNameHint("SS")
  import dsl._
  private implicit val compose: Compose = Compose.trust
  val `a+b` = style(color.red)
  object nested {
    val depth1 = style(color.red)
    object deeper {
      val depth2 = style(color.red)
    }
  }
  object `sub+group` {
    val `my style!` = style(color.red)
  }
  initInnerObjects(nested.depth1, nested.deeper.depth2, `sub+group`.`my style!`)
}

private abstract class DslMacroNameBaseSS(reg: Register) extends StyleSheet.Inline()(reg) {
  override protected implicit val classNameHint: ClassNameHint = ClassNameHint("SS")
}

private class DslMacroNameSubSS(reg: Register) extends DslMacroNameBaseSS(reg) {
  import dsl._
  private implicit val compose: Compose = Compose.trust
  object nested {
    val s = style(color.red)
  }
  initInnerObjects(nested.s)
}
