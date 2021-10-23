package scalacss

import org.scalajs.dom
import org.scalajs.dom.HTMLStyleElement
import scalacss.internal.{Css, Renderer}
import scalatags.JsDom._
import scalatags.JsDom.all._

trait ScalatagsJsDomImplicits {

  implicit final def styleaToJsDomTag(s: StyleA): Modifier = new Modifier {
    def applyTo(t: dom.Element) =
      for (cn <- s.classNameIterator)
        t.classList.add(cn.value)
  }

  implicit final def styleJsDomTagRenderer(implicit s: Renderer[String]): Renderer[TypedTag[HTMLStyleElement]] =
    new ScalatagsJsDomRenderer(s)
}

class ScalatagsJsDomRenderer(s: Renderer[String]) extends Renderer[TypedTag[HTMLStyleElement]] {
  override def apply(css: Css) =
    tags2.style(`type` := "text/css", s(css))
}
