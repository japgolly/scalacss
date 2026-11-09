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

      "attrSelectors" - {
        assertEq(AttrExists("custom-attr").cssValue, "[custom-attr]")
        assertEq(Attr("custom-attr", "bla").cssValue, "[custom-attr=\"bla\"]")
        assertEq(AttrContains("custom-attr", "bla").cssValue, "[custom-attr~=\"bla\"]")
        assertEq(AttrStartsWith("custom-attr", "bla").cssValue, "[custom-attr^=\"bla\"]")
        assertEq(AttrEndsWith("custom-attr", "bla").cssValue, "[custom-attr$=\"bla\"]")
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
        assertEq(s.warnings.map(_.cond), ArraySeq(&.hover))
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
        val q1 = media.minWidth(500 px) and media.maxWidth(1000 px)
        assertEq(q1.cssSuffix, "(min-width:500px) and (max-width:1000px)")
        val q2 = media.screen and media.minWidth(500 px)
        assertEq(q2.cssSuffix, "screen and (min-width:500px)")
        val q3 = media.minWidth(500 px) and media.screen
        assertEq(q3.cssSuffix, "screen and (min-width:500px)")
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
        val expected = ArraySeq(media.minWidth(500 px) and media.maxWidth(1000 px))
        assertEq(s.data.keys.flatMap(_.mediaQueries).to(ArraySeq), expected)
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
