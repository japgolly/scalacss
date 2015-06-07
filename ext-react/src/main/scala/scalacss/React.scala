package scalacss

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.document
import org.scalajs.dom.raw.HTMLStyleElement
import scalacss.mutable.{StyleSheetRegistry, StyleSheet}

object ScalaCssReactFns {

  def createStyleTag(styleStr: String): ReactElement =
    <.styleTag(^.`type` := "text/css", styleStr)

  def createStyle(styleStr: String): HTMLStyleElement = {
    val e = document.createElement("style").asInstanceOf[HTMLStyleElement]
    e.`type` = "text/css"
    e appendChild document.createTextNode(styleStr)
    e
  }

  def installStyle(style: HTMLStyleElement): Unit =
    document.head appendChild style

  final class InlineSSReactExt(val ss: StyleSheet.Inline) extends AnyVal {

    /** Turns this StyleSheet into a `&lt;style&gt;` and adds it to the document DOM. */
    def addToDocument()(implicit s: Renderer[HTMLStyleElement], e: Env): Unit =
      installStyle(ss.render[HTMLStyleElement])
  }

  final class StyleSheetRegistryReactExt(val r: StyleSheetRegistry) extends AnyVal {

    /** Registered StyleSheets are turned into a `&lt;style&gt;` and added to the document DOM. */
    def addToDocumentOnRegistration()(implicit s: Renderer[String], e: Env): Unit = {
      r.onRegistrationN { ss =>
        val styleStr = ss.map(_.render[String]).mkString("\n")
        val style = createStyle(styleStr)
        installStyle(style)
      }
    }
  }

  class ReactElementRenderer(s: Renderer[String]) extends Renderer[ReactElement] {
    override def apply(css: Css) = createStyleTag(s(css))
  }

  class StyleElementRenderer(s: Renderer[String]) extends Renderer[HTMLStyleElement] {
    override def apply(css: Css) = createStyle(s(css))
  }
}

/**
 * Usage:
 *
 *   1. `import ScalaCssReact._`
 *   2. Call `.addToDocument()` on your stylesheet to create the inline &lt;style&gt; tag.
 *   3. Reference styles in React tags to apply them.
 */
object ScalaCssReact extends ScalaCssReactImplicits

trait ScalaCssReactImplicits {
  import ScalaCssReactFns._

  implicit final def styleaToTagMod(s: StyleA): TagMod =
    ^.className := s.htmlClass

  implicit final def inlineSSReactExt(ss: StyleSheet.Inline) =
    new InlineSSReactExt(ss)

  implicit final def styleSheetRegistryReactExt(r: StyleSheetRegistry) =
    new StyleSheetRegistryReactExt(r)

  implicit final def cssReactElementRenderer(implicit s: Renderer[String]): Renderer[ReactElement] =
    new ReactElementRenderer(s)

  implicit final def cssStyleElementRenderer(implicit s: Renderer[String]): Renderer[HTMLStyleElement] =
    new StyleElementRenderer(s)
}
