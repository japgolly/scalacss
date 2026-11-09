package scalacss.internal

import japgolly.microlibs.nonempty.NonEmptyArraySeq
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
          Percentage(0) -> ArraySeq(
            CssEntry.Style(None, "0%", NonEmptyArraySeq(CssKV("color", "red"))),
            CssEntry.Style(None, "0%", NonEmptyArraySeq(CssKV("width", "10px")))
          )
        )
      )
      val rendered = StringRenderer.formatTiny(ArraySeq(kfEntry))
      assertEq(rendered, "@keyframes anim{0%{color:red;width:10px}}")
    }

    "rawRendererDone" - {
      var doneCalled = false
      val customFormat: StringRenderer.Format = sb =>
        StringRenderer.formatTiny(sb).copy(done = () => { doneCalled = true; sb.append("/*done*/") })
      val renderer = new StringRenderer.Raw(customFormat)
      val out = renderer(ArraySeq.empty)
      assert(doneCalled)
      assertEq(out, "/*done*/")
    }

    "fontFamilyQuoting" - {
      import Dsl._

      "noDoubleQuotesInFontFace" - {
        val ff = CssEntry.FontFace(
          fontFamily = "\"My Font\"",
          src = NonEmptyArraySeq("url(font.woff)"),
          fontStretch = None,
          fontStyle = None,
          fontWeight = None,
          unicodeRange = None
        )
        val rendered = StringRenderer.formatTiny(ArraySeq(ff))
        assert(!rendered.contains("\"\""))
        assertEq(rendered, """@font-face {font-family:"My Font";src:url(font.woff)}""")
      }

      "consistencyWithStyle" - {
        val ff = FontFace[String](
          "My Font",
          NonEmptyArraySeq("url(font.woff)")
        )
        val styleWithFont = Dsl.style(fontFamily(ff))
        val styleEntry = Css.styleA(StyleA(ClassName("c"), ArraySeq.empty, styleWithFont))(Env.empty)
        val fontEntry = Css.fontFaces(ff)
        val renderedStyle = StringRenderer.formatTiny(styleEntry)
        val renderedFont = StringRenderer.formatTiny(ArraySeq(fontEntry))

        assertEq(renderedFont, """@font-face {font-family:'My Font';src:url(font.woff)}""")
        assertEq(renderedStyle, """.c{font-family:'My Font'}""")
      }
    }

    "duplicateKeyframeSelectorsMerged" - {
      import Dsl._
      val kf = Keyframes(
        ClassName("anim"),
        Seq(
          (Percentage(0), StyleA(ClassName("k0"), ArraySeq.empty, Dsl.style(color.red))),
          (Percentage(0), StyleA(ClassName("k1"), ArraySeq.empty, Dsl.style(width(10 px)))),
          (Percentage(100), StyleA(ClassName("k2"), ArraySeq.empty, Dsl.style(color.blue)))
        )
      )
      val cssKf = Css.keyframes(kf)(Env.empty)
      val frame0 = cssKf.frames(Percentage(0))
      val props0 = frame0.flatMap(_.content.whole.map(_.key))
      assertEq(props0, ArraySeq("color", "width"))
    }

  }
}
