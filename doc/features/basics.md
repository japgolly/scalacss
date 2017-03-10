## Settings

The first thing you'll want to do is import some settings by using one of these `import` statements:

```scala
import scalacss.DevDefaults._  // Always use dev settings
import scalacss.ProdDefaults._ // Always use prod settings

// This will choose between dev/prod depending on your scalac `-Xelide-below` setting
val CssSettings = scalacss.devOrProdDefaults
import CssSettings._
```

This provides two sets of sensible settings: development and production.

Property | Dev | Prod
--- | --- | ---
Style conflicts       | Keep both sides of conflict. <br/> Issue warnings. | Keep both sides of conflict.
Error handling        | Print to `stderr` | Ignore
Class name generation | `.<class>-<val>` <br/> Eg. `.MyStyles-outer` | `._a0` <br/> `._a1` <br/> etc
CSS output            | Pretty-print. <br/> Indents, spaces, newlines. | Minified. <br/> No whitespace.

`Defaults` will choose a mode to use based on its `devMode: Boolean` method
which uses
[elision](http://www.scala-lang.org/api/current/scala/annotation/elidable.html)
to determine production-mode.

For instance, in my production SBT settings I generally have:

```scala
// Prod settings
scalacOptions ++= Seq("-Xelide-below", "OFF")
```

_Note: `-Xelide-below OFF` doesn't turn eliding off, it means "elide everything". There must've been painting going on in the building with no windows open when that was named._

Defaults aren't mandatory, you're free to customise as needed.
(See [DefaultSettings.scala](https://github.com/japgolly/scalacss/blob/master/core/shared/src/main/scala/scalacss/defaults/DefaultSettings.scala).)

## StyleSheets

Styles, attributes, values, nearly everything in ScalaCSS is immutable.
When you're declaring a large module of styles, it's very inconvenient to have
to manually collate all styles you declare.
`mutable.StyleSheet` exists for this purpose. Subclass it, create your styles
and they will be registered automatically so that you can turn them all into
CSS at once.

Stylesheets come in two different flavours: standalone and inline.

Property | Standalone | Inline
--- | --- | ---
Purpose                           | Generating a CSS file. | Styling in a Scala or Scala.JS webapp.
CSS Selectors <br/> (class names) | Mandatory. <br/> You need to specify. | Optional. <br/> Generated when not provided.
Usable style types                | Static only: `StyleS` | All types: `StyleS`, [`StyleF`](stylef.md), [`StyleC`](nested.md).
Return type after creation        | Unit | `StyleA` (`A` = Applicable)
Declaration style                 | `"div.box" - ...` | `val box = style(...)`

To create a style module,
1. Create an `object` that extends `StyleSheet.Standalone` or `StyleSheet.Inline`.
1. Inside the object, import the DSL by typing `import dsl._`.
1. Following the examples, just start typing and if you're using an IDE with auto-complete, you'll be guided towards type-safe styles.

##### Full examples:

* [StandaloneTest.scala](https://github.com/japgolly/scalacss/blob/master/core/shared/src/test/scala/scalacss/full/StandaloneTest.scala)
* [InlineTest.scala](https://github.com/japgolly/scalacss/blob/master/core/shared/src/test/scala/scalacss/full/InlineTest.scala)
