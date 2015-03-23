## [Scalatags](https://github.com/lihaoyi/scalatags)

ScalaCSS integrates with Scalatags in both `Text` and `JsDom` modes.

1. Add this to your SBT build:

  <pre><code class="lang-scala">libraryDependencies += <span class="hljs-string">&quot;com.github.japgolly.scalacss&quot;</span> %%% <span class="hljs-string">&quot;ext-scalatags&quot;</span> % <span class="hljs-string">&quot;{{ book.ver }}&quot;</span></code></pre>

1. Add this to your Scala code:
  ```scala
  import japgolly.scalacss.ScalatagsCss._
  ```

1. Create your [inline styles](../quickstart/inline.md) as normal.

1. To generate a `<style>` tag of all of your styles, call `.render[T]` where
   `T` is the type of output you want.
  * `MyStyles.render[String]`
  * `MyStyles.render[scalatags.Text.TypedTag[String]]`
  * `MyStyles.render[scalatags.JsDom.TypedTag[HTMLStyleElement]]`
  <br/>
  <br/>

1. Finally, to apply styles, just reference them in your tags.
  ```scala
  h1( MyStyles.heading, "This is a heading" )
  ```

## Example

MyStyles.scala
```scala
import japgolly.scalacss.Defaults._

object MyStyles extends StyleSheet.Inline {
  import dsl._

  val bootstrapButton = style(
    addClassName("btn btn-default"),
    fontSize(200 %%)
  )
}
```

MyApp.scala
```scala
import japgolly.scalacss.Defaults._
import japgolly.scalacss.ScalatagsCss._
import scalatags.Text._
import scalatags.Text.all._

object MyApp {

  def myPage =
    html(
      MyStyles.render[TypedTag[String]],
      body(
        button(
          MyStyles.bootstrapButton,
          "I am a button!"
        )
      )
    )
}
```
