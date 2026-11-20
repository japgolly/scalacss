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
        assertEq(Not(Hover, Focus).cssValue, ":not(:hover, :focus)")
        assertEq(Not(_.disabled).cssValue, ":not(:disabled)")
        assertEq(Cond.empty.not(Hover, Focus).pseudo.map(_.cssValue), Some(":not(:hover, :focus)"))
      }

      "has" - {
        assertEq(Has("> img").cssValue, ":has(> img)")
        assertEq(Has(Hover).cssValue, ":has(:hover)")
        assertEq(Has(Hover, Focus).cssValue, ":has(:hover, :focus)")
        assertEq(Has(_.checked).cssValue, ":has(:checked)")
        assertEq(Cond.empty.has("> img").pseudo.map(_.cssValue), Some(":has(> img)"))
        assertEq(Cond.empty.has(Hover, Focus).pseudo.map(_.cssValue), Some(":has(:hover, :focus)"))
      }

      "anyLink" - {
        assertEq(AnyLink.cssValue, ":any-link")
        assertEq(Cond.empty.anyLink.pseudo.map(_.cssValue), Some(":any-link"))
      }

      "placeholderShown" - {
        assertEq(PlaceholderShown.cssValue, ":placeholder-shown")
        assertEq(Cond.empty.placeholderShown.pseudo.map(_.cssValue), Some(":placeholder-shown"))
      }

      "marker" - {
        assertEq(Marker.cssValue, "::marker")
        assertEq((Hover & Marker).cssValue, ":hover::marker")
        assertEq(Cond.empty.marker.pseudo.map(_.cssValue), Some("::marker"))
      }

      "backdrop" - {
        assertEq(Backdrop.cssValue, "::backdrop")
        assertEq(Cond.empty.backdrop.pseudo.map(_.cssValue), Some("::backdrop"))
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
        assertEq(Attr("href", ".pdf", AttrCase.Insensitive).cssValue, """[href=".pdf" i]""")
        assertEq(AttrEndsWith("href", ".pdf", AttrCase.Insensitive).cssValue, """[href$=".pdf" i]""")
        assertEq(Attr("type", "text", AttrCase.Sensitive).cssValue, """[type="text" s]""")
        assertEq(Attr("href", ".pdf").caseInsensitive.cssValue, """[href=".pdf" i]""")
        assertEq(Attr("type", "text").caseSensitive.cssValue, """[type="text" s]""")
        assertEq(Cond.empty.attrEndsWith("href", ".pdf", AttrCase.Insensitive).pseudo.map(_.cssValue), Some("""[href$=".pdf" i]"""))
        assertEq(Cond.empty.attr("type", "text", AttrCase.Sensitive).pseudo.map(_.cssValue), Some("""[type="text" s]"""))
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

      "modSelector" - {
        assertEq(Hover.modSelector("h1"), "h1:hover")
        assertEq(Hover.modSelector("h1, h2"), "h1:hover, h2:hover")
        assertEq((Hover & Focus).modSelector("h1, h2, h3"), "h1:focus:hover, h2:focus:hover, h3:focus:hover")
        assertEq(Hover.modSelector("h1 , h2"), "h1:hover , h2:hover")
        assertEq(Hover.modSelector(":is(h1, h2)"), ":is(h1, h2):hover")
        assertEq(Hover.modSelector("a:not(.foo, .bar), button"), "a:not(.foo, .bar):hover, button:hover")
        assertEq(Hover.modSelector("li:nth-child(2n of .a, .b)"), "li:nth-child(2n of .a, .b):hover")
        assertEq(Hover.modSelector("""a[title="a, b"], button"""), """a[title="a, b"]:hover, button:hover""")
        assertEq(Hover.modSelector("""a[title='a, b'], button"""), """a[title='a, b']:hover, button:hover""")
        assertEq(Hover.modSelector("""a[title="a\", b"], button"""), """a[title="a\", b"]:hover, button:hover""")
        assertEq(Hover.modSelector("div:is(a[data-x='1,2'], b), p"), "div:is(a[data-x='1,2'], b):hover, p:hover")
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
          assertEq((media.all and media.not.screen.color).cssSuffix, "not screen and (color)")
          assertEq((media.not.screen.color and media.all).cssSuffix, "not screen and (color)")
          assertEq((media.not.screen and media.not.screen).cssSuffix, "not screen")
          assertEq((media.not.screen.color and media.not.screen.color).cssSuffix, "not screen and (color)")
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

        "negatedWithFeatures" - {
          "notScreenAndFeature"              - intercept[IllegalArgumentException](media.not.screen and media.minWidth(500 px))
          "featureAndNotScreen"              - intercept[IllegalArgumentException](media.minWidth(500 px) and media.not.screen)
          "notScreenAndAllWithFeature"       - intercept[IllegalArgumentException](media.not.screen and media.all.minWidth(500 px))
          "allWithFeatureAndNotScreen"       - intercept[IllegalArgumentException](media.all.minWidth(500 px) and media.not.screen)
          "notScreenDistinctFeatures"        - intercept[IllegalArgumentException](media.not.screen.color and media.not.screen.minWidth(500 px))
          "notScreenAndNotScreenWithFeature" - intercept[IllegalArgumentException](media.not.screen and media.not.screen.minWidth(500 px))
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
      "prefersColorScheme" - {
        assertEq(media.prefersColorSchemeLight.cssSuffix, "(prefers-color-scheme:light)")
        assertEq(media.prefersColorSchemeDark.cssSuffix, "(prefers-color-scheme:dark)")
        assertEq(media.prefersColorScheme(Media.ColorScheme.Dark).cssSuffix, "(prefers-color-scheme:dark)")
      }
      "prefersReducedMotion" - {
        assertEq(media.prefersReducedMotion.cssSuffix, "(prefers-reduced-motion)")
        assertEq(media.prefersReducedMotionNoPreference.cssSuffix, "(prefers-reduced-motion:no-preference)")
        assertEq(media.prefersReducedMotionReduce.cssSuffix, "(prefers-reduced-motion:reduce)")
        assertEq(media.prefersReducedMotion(Media.ReducedMotion.Reduce).cssSuffix, "(prefers-reduced-motion:reduce)")
      }
      "pointer" - {
        assertEq(media.pointer.cssSuffix, "(pointer)")
        assertEq(media.pointerNone.cssSuffix, "(pointer:none)")
        assertEq(media.pointerCoarse.cssSuffix, "(pointer:coarse)")
        assertEq(media.pointerFine.cssSuffix, "(pointer:fine)")
        assertEq(media.pointer(Media.PointerValue.Fine).cssSuffix, "(pointer:fine)")
      }
      "hover" - {
        assertEq(media.hover.cssSuffix, "(hover)")
        assertEq(media.hoverNone.cssSuffix, "(hover:none)")
        assertEq(media.hoverHover.cssSuffix, "(hover:hover)")
        assertEq(media.hover(Media.HoverValue.Hover).cssSuffix, "(hover:hover)")
        assertEq((media.screen.hoverHover and media.pointerFine).cssSuffix, "screen and (hover:hover) and (pointer:fine)")
      }
      "anyPointer" - {
        assertEq(media.anyPointer.cssSuffix, "(any-pointer)")
        assertEq(media.anyPointerNone.cssSuffix, "(any-pointer:none)")
        assertEq(media.anyPointerCoarse.cssSuffix, "(any-pointer:coarse)")
        assertEq(media.anyPointerFine.cssSuffix, "(any-pointer:fine)")
        assertEq(media.anyPointer(Media.PointerValue.Fine).cssSuffix, "(any-pointer:fine)")
      }
      "anyHover" - {
        assertEq(media.anyHover.cssSuffix, "(any-hover)")
        assertEq(media.anyHoverNone.cssSuffix, "(any-hover:none)")
        assertEq(media.anyHoverHover.cssSuffix, "(any-hover:hover)")
        assertEq(media.anyHover(Media.HoverValue.Hover).cssSuffix, "(any-hover:hover)")
      }
      "prefersContrast" - {
        assertEq(media.prefersContrast.cssSuffix, "(prefers-contrast)")
        assertEq(media.prefersContrastNoPreference.cssSuffix, "(prefers-contrast:no-preference)")
        assertEq(media.prefersContrastMore.cssSuffix, "(prefers-contrast:more)")
        assertEq(media.prefersContrastLess.cssSuffix, "(prefers-contrast:less)")
        assertEq(media.prefersContrastCustom.cssSuffix, "(prefers-contrast:custom)")
        assertEq(media.prefersContrast(Media.ContrastValue.More).cssSuffix, "(prefers-contrast:more)")
      }
      "forcedColors" - {
        assertEq(media.forcedColors.cssSuffix, "(forced-colors)")
        assertEq(media.forcedColorsNone.cssSuffix, "(forced-colors:none)")
        assertEq(media.forcedColorsActive.cssSuffix, "(forced-colors:active)")
        assertEq(media.forcedColors(Media.ForcedColorsValue.Active).cssSuffix, "(forced-colors:active)")
      }
      "rangeSyntax" - {
        assertEq((media.width < 600.px).cssSuffix, "(width < 600px)")
        assertEq((media.width <= 600.px).cssSuffix, "(width <= 600px)")
        assertEq((media.width > 600.px).cssSuffix, "(width > 600px)")
        assertEq((media.width >= 600.px).cssSuffix, "(width >= 600px)")
        assertEq((media.width === 600.px).cssSuffix, "(width = 600px)")
        assertEq(media.width.between(400.px, 800.px).cssSuffix, "(400px <= width <= 800px)")
        assertEq(media.width.betweenExclusive(400.px, 800.px).cssSuffix, "(400px < width < 800px)")
        assertEq(media.width.between(400.px, Media.RangeOp.Lte, Media.RangeOp.Lt, 800.px).cssSuffix, "(400px <= width < 800px)")
        assertEq(media.width.between(800.px, Media.RangeOp.Gte, Media.RangeOp.Gt, 400.px).cssSuffix, "(800px >= width > 400px)")
        assertEq((media.screen.width >= 500.px).cssSuffix, "screen and (width >= 500px)")
        assertEq((media.height < 500.px).cssSuffix, "(height < 500px)")
        assertEq((media.deviceWidth >= 1024.px).cssSuffix, "(device-width >= 1024px)")
        assertEq((media.deviceHeight <= 768.px).cssSuffix, "(device-height <= 768px)")
        assertEq((media.aspectRatio >= (16 :/: 9)).cssSuffix, "(aspect-ratio >= 16/9)")
        assertEq(media.aspectRatio.between(4 :/: 3, 16 :/: 9).cssSuffix, "(4/3 <= aspect-ratio <= 16/9)")
        assertEq((media.deviceAspectRatio > (4 :/: 3)).cssSuffix, "(device-aspect-ratio > 4/3)")
        assertEq((media.resolution >= 2.dppx).cssSuffix, "(resolution >= 2dppx)")
        assertEq(media.resolution.between(1.dppx, 3.dppx).cssSuffix, "(1dppx <= resolution <= 3dppx)")
        assertEq(media.width(500.px).cssSuffix, "(width:500px)")
        intercept[IllegalArgumentException](media.width.between(400.px, Media.RangeOp.Lte, Media.RangeOp.Gte, 800.px))
        intercept[IllegalArgumentException](media.width.between(400.px, Media.RangeOp.Eql, Media.RangeOp.Lte, 800.px))
      }
    }

    "applyToStyleDeterministicOrder" - {
      import Attrs.color
      import Dsl._

      "pseudoBaseBeforeHover" - {
        val hoverCond = Cond(Some(Pseudo.Hover), Vector.empty)
        val s = StyleS.data(Map(
          Cond.empty                               -> AVs(color, "black"),
          Cond(Some(Pseudo.Hover), Vector.empty)   -> AVs(color, "blue"),
          Cond(Some(Pseudo.Focus), Vector.empty)   -> AVs(color, "green"),
          Cond(Some(Pseudo.Active), Vector.empty)  -> AVs(color, "yellow"),
          Cond(Some(Pseudo.Visited), Vector.empty) -> AVs(color, "red"),
          Cond(Some(Pseudo.Link), Vector.empty)    -> AVs(color, "white"),
        ))
        val res = hoverCond.applyToStyle(s)(Compose.safe)
        assertEq(res.data(hoverCond).avIterator.toVector, Vector(AV(color, "black"), AV(color, "blue")))
      }

      "collapsingMediaQueriesSorted" - {
        val onlyScreen = Cond(None, Vector1(media.only.screen))
        val s = StyleS.data(Map(
          Cond.empty                          -> AVs(color, "c1"),
          Cond(None, Vector1(media.all))      -> AVs(color, "c2"),
          Cond(None, Vector1(media.only.all)) -> AVs(color, "c3"),
          onlyScreen                          -> AVs(color, "c4"),
          Cond(None, Vector1(media.screen))   -> AVs(color, "c5"),
        ))
        val res = onlyScreen.applyToStyle(s)(Compose.safe)
        assertEq(res.data.keySet, Set(onlyScreen))
        assertEq(
          res.data(onlyScreen).avIterator.toVector,
          Vector(AV(color, "c1"), AV(color, "c2"), AV(color, "c3"), AV(color, "c4"), AV(color, "c5"))
        )
      }
    }
  }
}
