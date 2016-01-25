package scalacss

import scalatags.Text._
import scalatags.text
import all._

trait ScalatagsTextImplicits {

  implicit final def styleaToTextTag(s: StyleA): Modifier = new Modifier {
    def applyTo(t: text.Builder) = t.appendAttr("class", " " + s.htmlClass)
  }

  implicit final def styleTextTagRenderer(implicit s: Renderer[String]): Renderer[TypedTag[String]] =
    new ScalatagsTextRenderer(s)
}

class ScalatagsTextRenderer(s: Renderer[String]) extends Renderer[TypedTag[String]] {
  override def apply(css: Css) =
    tags2.style(`type` := "text/css", s(css))
}
