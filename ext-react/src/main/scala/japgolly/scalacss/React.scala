package japgolly.scalacss

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.document
import org.scalajs.dom.raw.HTMLStyleElement
import mutable.StyleSheet

/**
 * Usage:
 *
 *   1. `import ScalaCssReact._`
 *   2. Call `.addToDocument()` on your stylesheet to create the inline &lt;style&gt; tag.
 *   3. Reference styles in React tags to apply them.
 */
object ScalaCssReact extends ScalaCssReactImplicits {

  final class InlineSSReactExt(val ss: StyleSheet.Inline) extends AnyVal {

    /** Turns this StyleSheet into a `&lt;style&gt;` and adds it to the document DOM. */
    def addToDocument()(implicit s: Renderer[HTMLStyleElement], e: Env): Unit =
      document.head.appendChild(ss.render[HTMLStyleElement])
  }

  class ReactElementRenderer(s: Renderer[String]) extends Renderer[ReactElement] {
    override def apply(css: Css) =
      <.styleTag(^.`type` := "text/css", s(css))
  }

  class StyleElementRenderer(s: Renderer[String]) extends Renderer[HTMLStyleElement] {
    override def apply(css: Css) = {
      val e = document.createElement("style").asInstanceOf[HTMLStyleElement]
      e.`type` = "text/css"
      e appendChild document.createTextNode(s(css))
      e
    }
  }
}

trait ScalaCssReactImplicits {

  implicit final def styleaToTagMod(s: StyleA): TagMod =
    ^.className := s.htmlClass

  implicit final def inlineSSReactExt(ss: mutable.StyleSheet.Inline) =
    new ScalaCssReact.InlineSSReactExt(ss)

  implicit final def cssReactElementRenderer(implicit s: Renderer[String]): Renderer[ReactElement] =
    new ScalaCssReact.ReactElementRenderer(s)

  implicit final def cssStyleElementRenderer(implicit s: Renderer[String]): Renderer[HTMLStyleElement] =
    new ScalaCssReact.StyleElementRenderer(s)
}
