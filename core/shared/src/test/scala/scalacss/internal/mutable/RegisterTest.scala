package scalacss.internal.mutable

import japgolly.microlibs.testutil.TestUtil._
import scalacss.internal._
import sourcecode.Line
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

  def assertDistinctClasses(as: StyleA*)(implicit q: Line): Unit =
    assertDistinctClassNames(as.map(_.className): _*)

  def assertDistinctClassNames(ns: ClassName*)(implicit q: Line): Unit = {
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
        assertEq(a1.htmlClasses, "btn btn-default")
        assertEq(a2.htmlClasses, "btn btn-primary")
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

      "keyframesUnsupportedFeatures" - {
        val (reg, getWarns) = newTestReg()
        val & = Cond.empty
        val kfStyle = Dsl.style(
          color.red,
          &.hover(color.blue),
          media.minWidth(500 px)(margin(10 px)),
          unsafeChild("span")(padding(5 px))
        )
        val kf = Keyframes(ClassName("myAnim"), Seq((Percentage(0), StyleA(ClassName("k0"), Vector.empty, kfStyle))))
        reg.registerKeyframes(kf)
        val warns = getWarns()
        assertEq(warns.count(_._2.cond.pseudo.contains(Pseudo.Hover)), 1)
        assertEq(warns.count(_._2.cond.mediaQueries.nonEmpty), 1)
        assertEq(warns.count(_._2.msg.contains("unsafe")), 1)
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

    "macroNameUse" - {
      "cssIdentStart" - {
        assertEq(MacroName.Use(ClassNameHint(""), "1foo"), Some(ClassName("_1foo")))
        assertEq(MacroName.Use(ClassNameHint(""), "-1foo"), Some(ClassName("_-1foo")))
        assertEq(MacroName.Use(ClassNameHint(""), "-"), Some(ClassName("_-")))
        assertEq(MacroName.Use(ClassNameHint(""), "-foo"), Some(ClassName("-foo")))
        assertEq(MacroName.Use(ClassNameHint("1abc"), "foo"), Some(ClassName("_1abc-foo")))
        assertEq(MacroName.Use(ClassNameHint("SS"), "1foo"), Some(ClassName("SS-1foo")))
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

      "preservesExplicitClassName" - {
        implicit val reg: Register = new Register(NameGen.numbered(), MacroName.Use, ErrorHandler.silent)
        class SS extends StyleSheet.Inline {
          override protected implicit val classNameHint: ClassNameHint = ClassNameHint("SS")
          import dsl._
          val f = styleF("manual").bool { b =>
            if (b) dsl.styleS(color.red).copy(className = Some(ClassName("custom-name")))
            else   dsl.styleS(color.blue)
          }
        }
        val ss = new SS
        assertEq(ss.f(true).className, ClassName("custom-name"))
        assertEq(ss.f(false).className, ClassName("manual-f"))
      }

      "addClassNamesOnly" - {
        implicit val reg: Register = new Register(NameGen.numbered(), MacroName.Use, ErrorHandler.silent)
        class SS extends StyleSheet.Inline {
          override protected implicit val classNameHint: ClassNameHint = ClassNameHint("SS")
          import dsl._
          val btn = styleF("mybtn").bool { b =>
            if (b) addClassNames("btn", "btn-lg")
            else   addClassNames("btn", "btn-sm")
          }
          val mixed = styleF("mymixed").bool { b =>
            if (b) addClassNames("btn", "btn-lg")
            else   dsl.styleS(addClassNames("btn"), color.red)
          }
        }
        val ss = new SS
        assertEq(ss.btn(true).htmlClasses, "btn btn-lg")
        assertEq(ss.btn(false).htmlClasses, "btn btn-sm")
        assertEq(ss.mixed(true).htmlClasses, "btn btn-lg")
        assertEq(ss.mixed(false).htmlClasses, "mymixed-f btn")
      }

      "ensureUniqueOnCollision" - {
        var warnings = Vector.empty[String]
        val errHandler = new ErrorHandler {
          override def warn = Some((_, w) => warnings :+= w.msg)
          override def badInput[I](s: StyleF[I], i: I) = ???
        }
        implicit val reg: Register = new Register(NameGen.numbered(), MacroName.Use, errHandler)
        class SS extends StyleSheet.Inline {
          override protected implicit val classNameHint: ClassNameHint = ClassNameHint("SS")
          import dsl._
          val pre     = style("dup-f")(color.black)
          val byBool  = styleF("dup").bool(_ => dsl.styleS(color.red))
          val byInput = styleF("suf")(Domain.ofValues(0, 1, 2, 3))(
            _ => dsl.styleS(color.red),
            (i, _) => i match {
              case 0 => "a b"
              case 1 => "a_b"
              case _ => ""
            }
          )
        }
        val ss = new SS
        assertEq(ss.pre.className, ClassName("dup-f"))
        assertEq(ss.byBool(true).className, ClassName("dup-t"))
        assertEq(ss.byBool(false).className, ClassName("dup-f-2"))
        assertEq(ss.byInput(0).className, ClassName("suf-a_b"))
        assertEq(ss.byInput(1).className, ClassName("suf-a_b-2"))
        assertEq(ss.byInput(2).className, ClassName("suf"))
        assertEq(ss.byInput(3).className, ClassName("suf-2"))
        assertEq(warnings, Vector.empty)
      }

      "ensuresValidCssIdentStart" - {
        implicit val reg: Register = new Register(NameGen.numbered(), MacroName.Use, ErrorHandler.silent)
        class SS extends StyleSheet.Inline {
          override protected implicit val classNameHint: ClassNameHint = ClassNameHint("SS")
          import dsl._
          val d  = styleF("1foo").bool(_ => dsl.styleS(color.red))
          val hd = styleF("-1foo").bool(_ => dsl.styleS(color.red))
          val h  = styleF("-").bool(_ => dsl.styleS(color.red))
          val hv = styleF("-foo").bool(_ => dsl.styleS(color.red))
        }
        val ss = new SS
        assertEq(ss.d(true).className, ClassName("_1foo-t"))
        assertEq(ss.hd(true).className, ClassName("_-1foo-t"))
        assertEq(ss.h(true).className, ClassName("_--t"))
        assertEq(ss.hv(true).className, ClassName("-foo-t"))
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
        assertEq(ss.btn(true).htmlClasses, "btn btn-lg")
        assertEq(ss.btn(false).htmlClasses, "btn btn-sm")
        assertEq(ss.mixed(true).htmlClasses, "btn btn-lg")
        assertEq(ss.mixed(false).htmlClasses, "SS-mixed-f btn")
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

      "dollarInIdentifiers" - {
        val reg = new Register(NameGen.numbered(), MacroName.Use, ErrorHandler.silent)
        val ss = new DslMacroNameDollarSS(reg)
        assertEq(ss.`$btn`.className, ClassName("SS-_btn"))
        assertEq(ss.btn.className, ClassName("SS-btn"))
        assertDistinctClasses(ss.`$btn`, ss.btn)
        assertEq(ss.`a$b`.className, ClassName("SS-a_b"))
        assertEq(ss.`foo$`.className, ClassName("SS-foo_"))
        assertEq(ss.`sub$group`.`x$y`.className, ClassName("SS-sub_group-x_y"))
        assertEq(`Obj$SS`.s.className, ClassName("Obj_SS-s"))
        assertEq(`Obj$SS`.inner.t.className, ClassName("Obj_SS-inner-t"))
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

    "emptyStyleReservation" - {
      def newWarnReg(mn: MacroName): (Register, () => Vector[String]) = {
        var warnings = Vector.empty[String]
        val errHandler = new ErrorHandler {
          override def warn = Some((_, w) => warnings :+= w.msg)
          override def badInput[I](s: StyleF[I], i: I) = ???
        }
        val reg = new Register(NameGen.numbered(), mn, errHandler)
        (reg, () => warnings)
      }

      "macroNameCollision" - {
        val (reg, getWarns) = newWarnReg(MacroName.Use)
        implicit val cnh: ClassNameHint = ClassNameHint("SS")
        val a1 = reg.registerS("same", StyleS.empty)
        val a2 = reg.registerS("same", ss1)
        assertEq(a1.className, ClassName("SS-same"))
        assertEq(a2.className, ClassName("SS-same-2"))
        assertEq(getWarns(), Vector.empty)
      }

      "nextNameCollision" - {
        val (reg, getWarns) = newWarnReg(MacroName.Ignore)
        implicit val cnh: ClassNameHint = ClassNameHint("SS")
        val manual = reg.registerS(StyleS.empty.copy(className = Some(ClassName("SS-0001"))))
        val auto   = reg.registerS(ss1)
        val kf     = reg.registerKeyframes("fade", Seq((Percentage(0), StyleA(ClassName("k0"), Vector.empty, ss2))))
        assertEq(manual.className, ClassName("SS-0001"))
        assertEq(auto.className, ClassName("SS-0002"))
        assertEq(kf.name, ClassName("SS-0003"))
        assertEq(getWarns(), Vector.empty)
      }

      "duplicateManualWarning" - {
        val (reg, getWarns) = newWarnReg(MacroName.Use)
        implicit val cnh: ClassNameHint = ClassNameHint("SS")
        reg.registerS(StyleS.empty.copy(className = Some(ClassName("manual"))))
        reg.registerS(ss1.copy(className = Some(ClassName("manual"))))
        assertEq(getWarns(), Vector("Another style in the register has the same classname."))
      }

      "explicitClassNameWithAddClassNames" - {
        val (reg, getWarns) = newWarnReg(MacroName.Use)
        implicit val cnh: ClassNameHint = ClassNameHint("SS")
        val s1 = StyleS.empty.copy(className = Some(ClassName("SS-same")), addClassNames = Vector(ClassName("btn")))
        val a1 = reg.registerS(s1)
        val a2 = reg.registerS("same", ss1)
        assertEq(a1.className, ClassName("SS-same"))
        assertEq(a2.className, ClassName("SS-same-2"))
        assertEq(getWarns(), Vector.empty)
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

private class DslMacroNameDollarSS(reg: Register) extends StyleSheet.Inline()(reg) {
  override protected implicit val classNameHint: ClassNameHint = ClassNameHint("SS")
  import dsl._
  private implicit val compose: Compose = Compose.trust
  val `$btn` = style(color.red)
  val btn    = style(color.blue)
  val `a$b`  = style(color.green)
  val `foo$` = style(color.yellow)
  object `sub$group` {
    val `x$y` = style(color.black)
  }
  initInnerObjects(`sub$group`.`x$y`)
}

private object `Obj$SS` extends StyleSheet.Inline()(new Register(Register.NameGen.numbered(), Register.MacroName.Use, Register.ErrorHandler.silent)) {
  import dsl._
  private implicit val compose: Compose = Compose.trust
  val s = style(color.red)
  object inner {
    val t = style(color.blue)
  }
  initInnerObjects(inner.t)
}
