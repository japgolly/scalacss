package scalacss

import scala.util.matching.Regex
import scalacss.internal.{Css, Renderer}
import scalatags.Text._
import scalatags.Text.all._
import scalatags.text
import scalatags.text.Builder

trait ScalatagsTextImplicits {

  implicit final def styleaToTextTag(s: StyleA): Modifier = new Modifier {
    def applyTo(t: text.Builder) = t.appendAttr("class", Builder.GenericAttrValueSource(" " + s.htmlClass))
  }

  implicit final def styleTextTagRenderer(implicit s: Renderer[String]): Renderer[TypedTag[String]] =
    new ScalatagsTextRenderer(s)
}

class ScalatagsTextRenderer(s: Renderer[String]) extends Renderer[TypedTag[String]] {
  override def apply(css: Css) = {
    val safeCss = ScalatagsTextRenderer.closeStyleTag.replaceAllIn(s(css), ScalatagsTextRenderer.escapedClosePrefix)
    tags2.style(`type` := "text/css", raw(safeCss))
  }
}

object ScalatagsTextRenderer {
  private val closeStyleTag      = "(?i)</(?=style)".r
  private val escapedClosePrefix = Regex.quoteReplacement("""\00003C/""")
}
