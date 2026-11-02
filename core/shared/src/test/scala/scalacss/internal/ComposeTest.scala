package scalacss.internal

import japgolly.microlibs.testutil.TestUtil._
import nyaya.prop._
import nyaya.test.PropTest._
import utest._

object ComposeTest extends TestSuite {
  import Compose.{Rules => R}

  type TT = (StyleS, StyleS)

//  val associativity =
//    Prop.equal[TTT]("Associativity")(
//      { case (a,b,c) => x((a compose b) compose c) },
//      { case (a,b,c) => x(a compose (b compose c)) })

  def flat(s: StyleS) =
    Css.flatten4(s.copy(className = None).inspectCss)

  case class PropTest(a: StyleS, b: StyleS) {
    val E = EvalOver(this.toString.take(200))

    def appendTest = {
      implicit val impc = new Compose(R(R.append, R.silent))
      E.equal("sort(a) + sort(b) = sort(a + b)",
        (flat(a) ++ flat(b)).sorted.toVector,
        flat(a compose b).sorted.toVector)
    }

    def replaceTest = {
      implicit val impc = new Compose(R(R.replace, R.silent))
      E.allPresent("a∘b ⊇ b",
        flat(b).toSet,
        flat(a compose b))
    }

    def all = "Props" rename_: (appendTest & replaceTest)
  }

  val propTest =
    RandomData.styleS.pair.map((PropTest.apply _).tupled)

  object Issue25 {
    import scalacss.defaults.Exports._
    import scalacss.defaults.DefaultSettings.Dev._
    object SampleStyles extends StyleSheet.Inline {
      override implicit val classNameHint: ClassNameHint = ClassNameHint("TEST")
      import dsl._
      val other = style(borderCollapse.collapse, &.hover(fontWeight._200), fontWeight._100)
      val outer = style(fontWeight.bold)
      val inner = style(color.red, outer)
    }
    def test(): Unit = {
      val css = SampleStyles.renderA[String].trim
      assertEq(SampleStyles.outer.htmlClass, "TEST-outer")
      assertEq(SampleStyles.inner.htmlClass, "TEST-inner")
      assertMultiline(css,
        """
          |.TEST-other {
          |  border-collapse: collapse;
          |  font-weight: 100;
          |}
          |
          |.TEST-other:hover {
          |  font-weight: 200;
          |}
          |
          |.TEST-outer {
          |  font-weight: bold;
          |}
          |
          |.TEST-inner {
          |  color: red;
          |  font-weight: bold;
          |}
        """.stripMargin.trim)
    }
  }

  override def tests = Tests {
    "props"   - propTest.mustSatisfyE(_.all) //(defaultPropSettings.setSampleSize(2000))
    "issue25" - Issue25.test()

    "values" - {
      import Dsl._
      implicit def c: Compose = Compose.safe

      def test(s: StyleS)(avs: AV*)(ws: String*): Unit = {
        assertEq(s.data(Cond.empty).avIterator.toVector, avs.toVector)
        assertEq(s.warnings.map(_.msg), ws.toVector)
      }

      "idempotency" - {
        val a = style(display.block)
        test(a compose a)(AV(display, "block"))()
      }

      "sameKey" -
        test(style(display.block) compose style(display.inline))(
          AV(display, "block"), AV(display, "inline")
        )("{display: inline} conflicts with {display: block}")

      "marginN1" -
        test(style(margin.auto) compose style(marginLeft.`0`))(
          AV(margin, "auto"), AV(marginLeft, "0")
        )("{margin-left: 0} conflicts with {margin: auto}")

      "margin1N" -
        test(style(marginLeft.`0`) compose style(margin.auto))(
          AV(marginLeft, "0"), AV(margin, "auto")
        )("{margin: auto} conflicts with {margin-left: 0}")

      "logicalProps" - {
        "blockSizeAndInlineSize" - {
          test(style(blockSize(10.px), inlineSize(20.px)))(
            AV(blockSize, "10px"), AV(inlineSize, "20px")
          )()
        }

        "marginBlockAndInline" - {
          test(style(marginBlockStart(10.px), marginBlockEnd(20.px)))(
            AV(marginBlockStart, "10px"), AV(marginBlockEnd, "20px")
          )()
        }

        "replaceMode" - {
          implicit val c: Compose = new Compose(Compose.Rules(Compose.Rules.replace, Compose.Rules.silent))
          val s = style(blockSize(10.px), inlineSize(20.px))
          assertEq(s.data(Cond.empty).avIterator.toVector, Vector(AV(blockSize, "10px"), AV(inlineSize, "20px")))
        }
      }

      "exactMatchConflict" - {
        "safe" - {
          val a = style(margin.auto, marginLeft.`0`)(Compose.trust)
          val b = style(margin.auto)
          test(a compose b)(
            AV(margin, "auto"), AV(marginLeft, "0")
          )("{margin: auto} conflicts with {margin-left: 0}")
        }

        "replace" - {
          implicit val c: Compose = new Compose(Compose.Rules(Compose.Rules.replace, Compose.Rules.silent))
          val a = style(margin.auto, marginLeft.`0`)(Compose.trust)
          val b = style(margin.auto)
          val res = a compose b
          assertEq(res.data(Cond.empty).avIterator.toVector, Vector(AV(margin, "auto")))
        }
      }

      "selfConflictInOperand" - {
        "replace" - {
          implicit val c: Compose = new Compose(Compose.Rules(Compose.Rules.replace, Compose.Rules.silent))
          val a = style(color.red)
          val b = style(margin.auto, marginLeft.`0`)(Compose.trust)
          val res = a compose b
          assertEq(res.data(Cond.empty).avIterator.toVector, Vector(AV(color, "red"), AV(margin, "auto"), AV(marginLeft, "0")))
        }

        "safe" - {
          val a = style(color.red)
          val b = style(margin.auto, marginLeft.`0`)(Compose.trust)
          test(a compose b)(
            AV(color, "red"), AV(margin, "auto"), AV(marginLeft, "0")
          )()
        }
      }

      "largeStyleOrder" - {
        implicit val c: Compose = new Compose(Compose.Rules(Compose.Rules.replace, Compose.Rules.silent))
        val a = style(color.red)
        val b = style(
          marginTop(1.px),
          marginRight(2.px),
          marginBottom(3.px),
          marginLeft(4.px),
          paddingTop(5.px),
          paddingRight(6.px),
        )
        val res = a compose b
        assertEq(
          res.data(Cond.empty).order.iterator.map(_.id).toVector,
          Vector("color", "margin-top", "margin-right", "margin-bottom", "margin-left", "padding-top", "padding-right")
        )
      }

      "replaceLaw" - {
        implicit val c: Compose = new Compose(Compose.Rules(Compose.Rules.replace, Compose.Rules.silent))
        val a = style(color.red)
        val b = style(margin(10.px), marginLeft(20.px))(Compose.trust)
        val flatB = flat(b).toSet
        val flatAB = flat(a compose b).toSet
        assert(flatB.subsetOf(flatAB))
      }

    }
  }
}
