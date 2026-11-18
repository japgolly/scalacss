package scalacss.internal

import japgolly.microlibs.testutil.TestUtil._
import scala.annotation.nowarn
import utest._

@nowarn("cat=deprecation")
object CondTest extends TestSuite {

  override def tests = Tests {
    "pseudo" - {
      import Pseudo._
      "not" - {
        assertEq(Not("div").cssValue, ":not(div)")
        assertEq(Not(Link).cssValue, ":not(:link)")
      }

      "is" - {
        assertEq(Is("h1, h2").cssValue, ":is(h1, h2)")
        assertEq(Is(Hover).cssValue, ":is(:hover)")
        assertEq(Is(Hover, Focus).cssValue, ":is(:hover, :focus)")
        assertEq(Is(_.hover).cssValue, ":is(:hover)")
        assertEq(Cond.empty.is(Hover, Focus).pseudo.map(_.cssValue), Some(":is(:hover, :focus)"))
      }

      "where" - {
        assertEq(Where("p, div").cssValue, ":where(p, div)")
        assertEq(Where(FocusVisible, FocusWithin).cssValue, ":where(:focus-visible, :focus-within)")
        assertEq(Where(_.focus).cssValue, ":where(:focus)")
        assertEq(Cond.empty.where("span").pseudo.map(_.cssValue), Some(":where(span)"))
      }

      "root" - {
        assertEq(Root.cssValue, ":root")
        assertEq(Cond.empty.root.pseudo.map(_.cssValue), Some(":root"))
      }

      "focusVisible" - {
        assertEq(FocusVisible.cssValue, ":focus-visible")
        assertEq(Cond.empty.focusVisible.pseudo.map(_.cssValue), Some(":focus-visible"))
      }

      "focusWithin" - {
        assertEq(FocusWithin.cssValue, ":focus-within")
        assertEq(Cond.empty.focusWithin.pseudo.map(_.cssValue), Some(":focus-within"))
      }

      "placeholder" - {
        assertEq(Placeholder.cssValue, "::placeholder")
        assertEq((Hover & Placeholder).cssValue, ":hover::placeholder")
        assertEq(Cond.empty.focusVisible.placeholder.pseudo.map(_.cssValue), Some(":focus-visible::placeholder"))
      }

      "attrSelectors" - {
        assertEq(AttrExists("custom-attr").cssValue, "[custom-attr]")
        assertEq(Attr("custom-attr", "bla").cssValue, "[custom-attr=\"bla\"]")
        assertEq(AttrContains("custom-attr", "bla").cssValue, "[custom-attr~=\"bla\"]")
        assertEq(AttrStartsWith("custom-attr", "bla").cssValue, "[custom-attr^=\"bla\"]")
        assertEq(AttrEndsWith("custom-attr", "bla").cssValue, "[custom-attr$=\"bla\"]")
        assertEq(AttrDashMatch("lang", "en").cssValue, "[lang|=\"en\"]")
        assertEq(AttrSubstring("href", "example").cssValue, "[href*=\"example\"]")
        assertEq(Cond.empty.attrDashMatch("lang", "en").pseudo.map(_.cssValue), Some("[lang|=\"en\"]"))
        assertEq(Cond.empty.attrSubstring("href", "example").pseudo.map(_.cssValue), Some("[href*=\"example\"]"))
        assertEq(Attr("title", """ok " ' \ Δ cool""").cssValue, """[title="ok \000022 ' \00005C \000394 cool"]""")
        assertEq(Attr("a", "\"a").cssValue, """[a="\000022a"]""")
        assertEq(Attr("a", "\\a").cssValue, """[a="\00005Ca"]""")
        assertEq(Attr("a", "hello\nworld").cssValue, """[a="hello\00000Aworld"]""")
        assertEq(Attr("a", "🎉").cssValue, """[a="\01F389"]""")
        assertEq(AttrContains("data-x", """a"b\c""").cssValue, """[data-x~="a\000022b\00005Cc"]""")
      }

      "nthChild" - {
        assertEq(NthChild("ODD").cssValue, ":nth-child(ODD)")
        assertEq(NthChild("EVEN").cssValue, ":nth-child(EVEN)")
        assertEq(NthChild("Odd").cssValue, ":nth-child(Odd)")
        assertEq(NthChild("2N+1").cssValue, ":nth-child(2N+1)")
        assertEq(NthChild("-N+3").cssValue, ":nth-child(-N+3)")
        assertEq(NthChild("+2N - 1").cssValue, ":nth-child(+2N - 1)")
        assertEq(NthChild("2n + 1").cssValue, ":nth-child(2n + 1)")
        assertEq(NthChild("2n - 1").cssValue, ":nth-child(2n - 1)")
        assertEq(NthChild("2n+ 1").cssValue, ":nth-child(2n+ 1)")
        assertEq(NthChild("2n +1").cssValue, ":nth-child(2n +1)")
        assertEq(NthChild("  n + 2  ").cssValue, ":nth-child(n + 2)")
        assertEq(NthChild("-n + 3 of li.important").cssValue, ":nth-child(-n + 3 of li.important)")
        assertEq(NthChild("2n+1 OF .foo, .bar").cssValue, ":nth-child(2n+1 OF .foo, .bar)")
        assertEq(NthLastChild("odd of li").cssValue, ":nth-last-child(odd of li)")
        assertEq(NthChild("  2n + 1 of .item  ").cssValue, ":nth-child(2n + 1 of .item)")
      }

      "elementClassOrder" - {
        assertEq(Before.&(Hover).attrExists("custom-attr").cssValue, "[custom-attr]:hover::before")
        assertEq(Hover.attr("custom-attr", "bla").&(Before).cssValue, "[custom-attr=\"bla\"]:hover::before")
        assertEq(AttrEndsWith("custom-attr", "bla").&(Hover).&(Before).cssValue, "[custom-attr$=\"bla\"]:hover::before")
        assertEq(Before.&(Hover).nthChild("2n+1").cssValue, ":hover:nth-child(2n+1)::before")
        assertEq(Before.&(Hover).nthChild("n+4").cssValue, ":hover:nth-child(n+4)::before")
        assertEq(Before.&(Hover).nthChild("n").cssValue, ":hover:nth-child(n)::before")
        assertEq(Before.&(Hover).nthChild("4").cssValue, ":hover:nth-child(4)::before")
        assertEq(Before.&(Hover).nthChild("odd").cssValue, ":hover:nth-child(odd)::before")
        assertEq(Before.&(Hover).nthChild("even").cssValue, ":hover:nth-child(even)::before")
        assertEq(Hover.&(Before).cssValue, ":hover::before")
      }

      "brokenNthChildQueries" - {
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2n+k").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2x+1").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2n+-1").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("--2n+1").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2n+1+3").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2-n+1").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2*n+1").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2n*1").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("n/3").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("1+4").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("nn").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("-").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("+").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2 n").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("+ 2n").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("- n").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("+ 4").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("- 4").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2n1").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2n+").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2n-").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("   ").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2n+1 of").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2n+1 of   ").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2nofli").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("- n + 3 of li.important").cssValue }
        intercept[IllegalArgumentException] { NthOfType("2n of li").cssValue }
        intercept[IllegalArgumentException] { NthLastOfType("2n of li").cssValue }
      }

      "composite" - {
        "commutativity" - {
          assertEq(Hover & Focus, Focus & Hover)
          assertEq((Hover & Focus).hashCode, (Focus & Hover).hashCode)
          assertEq((Hover & Focus).cssValue, (Focus & Hover).cssValue)
        }

        "associativity" - {
          val a = (Hover & Focus) & Active
          val b = Hover & (Focus & Active)
          assertEq(a, b)
          assertEq(a.hashCode, b.hashCode)
        }

        "idempotence" - {
          val x = Hover & Focus
          assertEq(x & x, x)
          assertEq(x & Hover, x)
          assertEq(Hover & x, x)
          assertEq(x & Focus, x)
          assertEq(Focus & x, x)
          val y = Hover & Focus & Active
          assertEq(y & x, y)
          assertEq(x & y, y)
        }

        "condConsistency" - {
          val c1 = Cond.empty.hover.focus
          val c2 = Cond.empty.focus.hover
          assertEq(c1, c2)
          assertEq(c1.hashCode, c2.hashCode)
          assertEq(Cond.ordering.compare(c1, c2), 0)
          assertEq(Set(c1, c2).size, 1)
        }
      }
    }

    "applyToStyle" - {
      import Dsl._
      implicit val composition: Compose = Compose.safe
      val & = Cond.empty

      "warningsCondition" - {
        val s = style(
          &.hover(
            color.red,
            color.blue
          )
        )
        assertEq(s.warnings.map(_.cond), Vector(&.hover))
      }

      "mergeSameConditionConflict" - {
        val inner = style(
          color.red,
          &.hover(color.blue)
        )
        val s = style(
          &.hover(inner)
        )
        assert(s.warnings.nonEmpty)
      }
    }

    "media" - {
      import Dsl._
      "or" - {
        val q = media.screen or media.handheld
        assertEq(q.mediaQueries.length, 2)
        assertEq(q.toString.trim, "@media screen, handheld")
      }
      "and" - {
        "basic" - {
          val q1 = media.minWidth(500 px) and media.maxWidth(1000 px)
          assertEq(q1.cssSuffix, "(min-width:500px) and (max-width:1000px)")
          val q2 = media.screen and media.minWidth(500 px)
          assertEq(q2.cssSuffix, "screen and (min-width:500px)")
          val q3 = media.minWidth(500 px) and media.screen
          assertEq(q3.cssSuffix, "screen and (min-width:500px)")
        }

        "featureOrder" - {
          val q1 = media.minWidth(500 px) and media.screen.maxWidth(1000 px)
          assertEq(q1.cssSuffix, "screen and (min-width:500px) and (max-width:1000px)")
          val q2 = media.minWidth(500 px).orientation(Media.Portrait) and media.screen.maxWidth(1000 px).color
          assertEq(q2.cssSuffix, "screen and (min-width:500px) and (orientation:portrait) and (max-width:1000px) and (color)")
        }

        "compatibleTypes" - {
          assertEq((media.screen and media.screen).cssSuffix, "screen")
          assertEq((media.all and media.screen).cssSuffix, "screen")
          assertEq((media.screen and media.all).cssSuffix, "screen")
          assertEq((media.all and media.not.screen).cssSuffix, "not screen")
          assertEq((media.not.screen and media.all).cssSuffix, "not screen")
          assertEq((media.only.all and media.screen).cssSuffix, "only screen")
          assertEq((media.screen and media.only.all).cssSuffix, "only screen")
          assertEq((media.screen and media.only.screen).cssSuffix, "only screen")
          assertEq((media.only.screen and media.screen).cssSuffix, "only screen")
        }

        "incompatibleTypes" - {
          "screenAndPrint"       - intercept[IllegalArgumentException](media.screen and media.print)
          "screenAndNotScreen"   - intercept[IllegalArgumentException](media.screen and media.not.screen)
          "notScreenAndScreen"   - intercept[IllegalArgumentException](media.not.screen and media.screen)
          "notScreenAndNotPrint" - intercept[IllegalArgumentException](media.not.screen and media.not.print)
          "notScreenAndPrint"    - intercept[IllegalArgumentException](media.not.screen and media.print)
          "onlyScreenAndNot"     - intercept[IllegalArgumentException](media.only.screen and media.not.screen)
        }
      }
      "nested" - {
        import Dsl._
        implicit val composition: Compose = Compose.safe
        val s = style(
          media.minWidth(500 px)(
            media.maxWidth(1000 px)(
              color.red
            )
          )
        )
        val expected = Vector(media.minWidth(500 px) and media.maxWidth(1000 px))
        assertEq(s.data.keys.flatMap(_.mediaQueries).toVector, expected)
        val css = StringRenderer.defaultPretty(Css.style(".a", s)(Env.empty)).trim
        assertMultiline(css,
          """
            |@media (min-width:500px) and (max-width:1000px) {
            |  .a {
            |    color: red;
            |  }
            |}
          """.stripMargin.trim)
      }
      "deprecatedAmpersand" - {
        val q = media.screen & media.handheld
        assertEq(q.toString.trim, "@media screen, handheld")
      }
    }
  }
}
