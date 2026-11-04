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
          fontFamily = "\"My Font\"",
          src = NonEmptyVector("url(font.woff)"),
          fontStretch = None,
          fontStyle = None,
          fontWeight = None,
          unicodeRange = None
        )
        val rendered = StringRenderer.formatTiny(Vector(ff))
        assert(!rendered.contains("\"\""))
        assertEq(rendered, """@font-face {font-family:"My Font";src:url(font.woff)}""")
      }

      "consistencyWithStyle" - {
        val ff = FontFace[String](
          "My Font",
          NonEmptyVector("url(font.woff)")
        )
        val styleWithFont = Dsl.style(fontFamily(ff))
        val styleEntry = Css.styleA(StyleA(ClassName("c"), Vector.empty, styleWithFont))(Env.empty)
        val fontEntry = Css.fontFaces(ff)
        val renderedStyle = StringRenderer.formatTiny(styleEntry)
        val renderedFont = StringRenderer.formatTiny(Vector(fontEntry))

        assertEq(renderedFont, """@font-face {font-family:"My Font";src:url(font.woff)}""")
        assertEq(renderedStyle, """.c{font-family:"My Font"}""")
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

  }
}
