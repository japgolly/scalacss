package scalacss.internal

import japgolly.microlibs.testutil.TestUtil._
import scala.collection.immutable.ListMap
import scalacss.DevDefaults._
import utest._

object RendererTest extends TestSuite {

  override def tests = Tests {

    "keyframesFormatTinySemicolon" - {
      val kfEntry = CssEntry.Keyframes(
        ClassName("anim"),
        ListMap(
          Percentage(0) -> Vector(
            CssEntry.Style(None, "0%", NonEmptyVector(CssKV("color", "red"))),
            CssEntry.Style(None, "0%", NonEmptyVector(CssKV("width", "10px")))
          )
        )
      )
      val rendered = StringRenderer.formatTiny(Vector(kfEntry))
      assertEq(rendered, "@keyframes anim{0%{color:red;width:10px}}")
    }

    "rawRendererDone" - {
      var doneCalled = false
      val customFormat: StringRenderer.Format = sb =>
        StringRenderer.formatTiny(sb).copy(done = () => { doneCalled = true; sb.append("/*done*/") })
      val renderer = new StringRenderer.Raw(customFormat)
      val out = renderer(Vector.empty)
      assert(doneCalled)
      assertEq(out, "/*done*/")
    }

    "fontFamilyQuoting" - {
      import Dsl._

      "noDoubleQuotesInFontFace" - {
        val ff = CssEntry.FontFace(
          fontFamily = FontFamily("\"My Font\""),
          src = NonEmptyVector("url(font.woff)"),
          fontStretch = None,
          fontStyle = None,
          fontWeight = None,
          unicodeRange = None
        )
        val rendered = StringRenderer.formatTiny(Vector(ff))
        assert(!rendered.contains("\"\""))
        assertEq(rendered, """@font-face{font-family:"My Font";src:url(font.woff)}""")
      }

      "consistencyWithStyle" - {
        val ff = FontFace[FontFamily](
          FontFamily("My Font"),
          NonEmptyVector("url(font.woff)")
        )
        val styleWithFont = Dsl.style(fontFamily(ff))
        val styleEntry = Css.styleA(StyleA(ClassName("c"), Vector.empty, styleWithFont))(Env.empty)
        val fontEntry = Css.fontFaces(ff)
        val renderedStyle = StringRenderer.formatTiny(styleEntry)
        val renderedFont = StringRenderer.formatTiny(Vector(fontEntry))

        assertEq(renderedFont, """@font-face{font-family:'My Font';src:url(font.woff)}""")
        assertEq(renderedStyle, """.c{font-family:'My Font'}""")
      }

      "fontFaceDescriptors" - {
        val ff = FontFace[FontFamily](FontFamily("VarFont"), NonEmptyVector("url(var.woff2)"))
          .fontDisplay.swap
          .fontWeight(100, 900)
          .fontStretch(50.%%, 200.%%)
          .fontStyle.oblique("-10deg", "20deg")
          .unicodeRange(UnicodeRange(0x0000, 0x00FF), UnicodeRange(0x0131, 0x0131))
        val rendered = StringRenderer.formatTiny(Vector(Css.fontFaces(ff)))
        assertEq(
          rendered,
          "@font-face{font-family:VarFont;src:url(var.woff2);font-display:swap;font-stretch:50% 200%;font-style:oblique -10deg 20deg;font-weight:100 900;unicode-range:U+0-ff,U+131-131}"
        )

        val ffSingle = FontFace[FontFamily](FontFamily("SingleFont"), NonEmptyVector("url(s.woff2)"))
          .fontDisplay.optional
          .fontWeight(450)
          .fontStretch(75.%%)
          .fontStyle.oblique("14deg")
        val renderedSingle = StringRenderer.formatTiny(Vector(Css.fontFaces(ffSingle)))
        assertEq(
          renderedSingle,
          "@font-face{font-family:SingleFont;src:url(s.woff2);font-display:optional;font-stretch:75%;font-style:oblique 14deg;font-weight:450}"
        )

        val ffAngles = FontFace[FontFamily](FontFamily("AngleFont"), NonEmptyVector("url(a.woff2)"))
          .fontStyle.oblique((-10).deg, 20.deg)
        assertEq(
          StringRenderer.formatTiny(Vector(Css.fontFaces(ffAngles))),
          "@font-face{font-family:AngleFont;src:url(a.woff2);font-style:oblique -10deg 20deg}"
        )
      }
    }

    "duplicateKeyframeSelectorsMerged" - {
      import Dsl._
      val kf = Keyframes(
        ClassName("anim"),
        Seq(
          (Percentage(0), StyleA(ClassName("k0"), Vector.empty, Dsl.style(color.red))),
          (Percentage(0), StyleA(ClassName("k1"), Vector.empty, Dsl.style(width(10 px)))),
          (Percentage(100), StyleA(ClassName("k2"), Vector.empty, Dsl.style(color.blue)))
        )
      )
      val cssKf = Css.keyframes(kf)(Env.empty)
      val frame0 = cssKf.frames(Percentage(0))
      val props0 = frame0.flatMap(_.content.whole.map(_.key))
      assertEq(props0, Vector("color", "width"))
    }

    "keyframesIgnoresConditionsAndUnsafeExts" - {
      import Dsl._
      val & = Cond.empty
      val s0 = Dsl.style(
        color.red,
        &.hover(color.blue),
        media.minWidth(500 px)(margin(10 px)),
        unsafeChild("span")(padding(5 px))
      )
      val s1 = Dsl.style(&.hover(backgroundColor.green))
      val kf = Keyframes(
        ClassName("anim"),
        Seq(
          (Percentage(0), StyleA(ClassName("k0"), Vector.empty, s0)),
          (Percentage(100), StyleA(ClassName("k1"), Vector.empty, s1))
        )
      )
      val rendered = StringRenderer.formatTiny(Vector(Css.keyframes(kf)(Env.empty)))
      assertEq(rendered, "@keyframes anim{0%{color:red}100%{}}")
    }

  }
}
