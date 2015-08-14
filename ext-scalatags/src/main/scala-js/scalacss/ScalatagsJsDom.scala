package scalacss

import org.scalajs.dom.raw.HTMLStyleElement
import scalatags.JsDom._
import all._

trait ScalatagsJsDomImplicits {

  implicit def styleaToJsDomTag(s: scalacss.StyleA): Modifier = new Modifier {
    def applyTo(t: org.scalajs.dom.Element) = t.classList.add(s.className.value)
  }

  implicit final def styleJsDomTagRenderer(implicit s: Renderer[String]): Renderer[TypedTag[HTMLStyleElement]] =
    new ScalatagsJsDomRenderer(s)
}

class ScalatagsJsDomRenderer(s: Renderer[String]) extends Renderer[TypedTag[HTMLStyleElement]] {
  override def apply(css: Css) =
    tags2.style(`type` := "text/css", s(css))
}
