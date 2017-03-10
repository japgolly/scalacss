## [scalajs-react](https://github.com/japgolly/scalajs-react)

1. Add this to your SBT build:

  <pre><code class="lang-scala">libraryDependencies += "com.github.japgolly.scalacss" %%% "ext-react" % "{{ book.ver }}"</code></pre>

1. Add this to Scala where you want ScalaCSS to integrate with your React code.
  ```scala
  import scalacss.ScalaCssReact._
  ```

1. Create your [inline styles](../quickstart/inline.md) as normal.

1. At the beginning of your Scala.JS app, before using any React components,
   create the inline styles by calling `.addToDocument()` on your stylesheet.
  ```scala
  MyStyles.addToDocument()
  ```

  If you're using [`GlobalRegistry`](../features/global_registry.md), do this:
  ```scala
  GlobalRegistry.addToDocumentOnRegistration()
  ```

1. Finally, to apply styles, just reference them in your tags.
  ```scala
  <.h1( MyStyles.heading, "This is a heading" )
  ```

## Example

example/package.scala
```scala
package object example {
  val CssSettings = scalacss.devOrProdDefaults
}
```

example/MyStyles.scala
```scala
package example

import CssSettings._

object MyStyles extends StyleSheet.Inline {
  import dsl._

  val bootstrapButton = style(
    addClassName("btn btn-default"),
    fontSize(200 %%)
  )
}
```

example/MyApp.scala
```scala
package example

import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom.{alert, document}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import CssSettings._
import scalacss.ScalaCssReact._

object MyApp {

  val MyComponent =
    ReactComponentB[Unit]("blah")
      .render(_ =>
        <.button(
          ^.onClick --> alert("Hey! I didn't say you could click me!"),
          MyStyles.bootstrapButton,
          "I am a button!"))
      .buildU


  @JSExport("app")
  def main(): Unit = {
    MyStyles.addToDocument()
    MyComponent() render document.getElementById("demo")
  }
}
```
