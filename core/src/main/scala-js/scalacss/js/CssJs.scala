package scalacss.js

import scalajs.js.Dictionary
import scalacss._

object CssJs {

  private val plainClassSel = "^\\.([^ :]+)$".r.pattern

  private val camelCaseRegex = "[_-]+([a-z0-9])".r

  def camelCase(s: String) =
    camelCaseRegex.replaceAllIn(s, _.group(1).toUpperCase)

  type StylesObject = Dictionary[StyleObject]
  type StyleObject  = Dictionary[String]

  def cssToJsObject(css: Css): StylesObject = {
    val styles: StylesObject = Dictionary.empty
    css.foreach { entry =>

      // Ignore media queries and pseudo-selected
      val m = plainClassSel.matcher(entry.sel)
      if (m.matches() && entry.mq.isEmpty) {
        val className = m.group(1)

        // Get/create style
        val style = styles.get(className) getOrElse {
          val s: StyleObject = Dictionary.empty
          styles(className) = s
          s
        }

        // Add style CSS
        entry.content.foreach { kv =>
          val k = camelCase(kv.key)
          style(k) = kv.value
        }
      }
    }

    styles
  }
}
