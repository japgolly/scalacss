package scalacss

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import scalacss.defaults.PlatformExports
import scalacss.internal.{Css, Renderer}

object ScalaCssReactFns {
  def createStyleTag(styleStr: String): ReactElement =
    <.styleTag(^.`type` := "text/css", styleStr)

  class ReactElementRenderer(s: Renderer[String]) extends Renderer[ReactElement] {
    override def apply(css: Css) = createStyleTag(s(css))
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

trait ScalaCssReactImplicits extends PlatformExports {
  import ScalaCssReactFns._

  implicit final def styleaToTagMod(s: StyleA): TagMod =
    ^.className := s.htmlClass

  implicit final def cssReactElementRenderer(implicit s: Renderer[String]): Renderer[ReactElement] =
    new ReactElementRenderer(s)
}
