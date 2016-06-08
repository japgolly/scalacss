package scalacss

import org.scalajs.dom.document
import org.scalajs.dom.raw.HTMLStyleElement
import scalacss.mutable.{StyleSheet, StyleSheetRegistry}

package object js {

  def createStyle(styleStr: String): HTMLStyleElement = {
    val e = document.createElement("style").asInstanceOf[HTMLStyleElement]
    e.`type` = "text/css"
    e appendChild document.createTextNode(styleStr)
    e
  }

  def installStyle(style: HTMLStyleElement): Unit =
    document.head appendChild style

  implicit final class InlineSSReactExt(private val ss: StyleSheet.Inline) extends AnyVal {
    /** Turns this StyleSheet into a `&lt;style&gt;` and adds it to the document DOM. */
    def addToDocument()(implicit s: Renderer[HTMLStyleElement], e: Env): Unit =
    installStyle(ss.render[HTMLStyleElement])
  }

  implicit final class StyleSheetRegistryReactExt(private val r: StyleSheetRegistry) extends AnyVal {
    /** Registered StyleSheets are turned into a `&lt;style&gt;` and added to the document DOM. */
    def addToDocumentOnRegistration()(implicit s: Renderer[String], e: Env): Unit = {
      r.onRegistrationN { ss =>
        val styleStr = ss.map(_.render[String]).mkString("\n")
        val style = createStyle(styleStr)
        installStyle(style)
      }
    }
  }

  class StyleElementRenderer(s: Renderer[String]) extends Renderer[HTMLStyleElement] {
    override def apply(css: Css) = createStyle(s(css))
  }

  implicit final def cssStyleElementRenderer(implicit s: Renderer[String]): Renderer[HTMLStyleElement] =
    new StyleElementRenderer(s)
}
