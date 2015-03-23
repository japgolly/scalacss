package japgolly.scalacss

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

/**
 * Usage:
 *
 *   1. `import ScalaCssReact._`
 *   2. Call `.render[ReactElement]` on your styles to create the inline &lt;style&gt; tag.
 */
object ScalaCssReact extends ScalaCssReactImplicits

trait ScalaCssReactImplicits {

  implicit final def styleaToTagMod(s: StyleA): TagMod =
    ^.className := s.htmlClass

  implicit final def styleTagRenderer(implicit s: Renderer[String]): Renderer[ReactElement] =
    new ReactStyleRenderer(s)
}

class ReactStyleRenderer(s: Renderer[String]) extends Renderer[ReactElement] {
  override def apply(css: Css): ReactElement =
    <.styleTag(^.`type` := "text/css", s(css))
}
