# Defaults

The first thing you'll want to do is import the default settings.

```scala
import japgolly.scalacss.Defaults._
```

This provides two sets of sensible settings: development and production.

Property | Dev | Prod
--- | --- | ---
Style conflicts       | Warn and append | Silently append
Error handling        | Noisy | Silent
Class name generation | `.scalacss-0001` <br/> `.scalacss-0002` <br/> etc | `.¢¢` <br/> `.¢¥` <br/> etc
CSS output            | Pretty-print. <br/> Indents, spaces, newlines. | Tiny. <br/> No whitespace.

The defaults automatically determine which mode to use based on
[elision](http://www.scala-lang.org/api/current/scala/annotation/elidable.html).
For instance, in my production SBT settings I generally have:

```scala
// Prod settings
scalacOptions ++= Seq("-Xelide-below", "OFF")
```

_Note: `-Xelide-below OFF` doesn't turn eliding off, it means "elide everything". There must've been painting going on in the building and no windows open when that was named._

Defaults aren't mandatory, you're free to customise as needed.
(See [Defaults.scala](https://github.com/japgolly/scalacss/blob/master/core/src/main/scala/japgolly/scalacss/Defaults.scala).)

# StyleSheets

Styles, attributes, values, nearly everything in ScalaCSS is immutable.
When you're declaring a large module of styles, it's very inconvenient to have
to manually collate all styles you declare.
`mutable.StyleSheet` exists for this purpose. Subclass it, create your styles
and they will be registered automatically so that you can turn them all into
CSS at once.

Stylesheets come in two different flavours: standalone and inline.

Property | Standalone | Inline
--- | --- | ---
Purpose                     | Generating a CSS file. | Styling in a Scala or Scala.JS webapp.
CSS Selectors (class names) | Mandatory. <br/> You need to specify. | Optional. <br/> Generated when not provided.
Usable style types          | Static only: `StyleS` | All types: `StyleS`, `StyleF`, `StyleC`.
Return type after creation  | Unit | `StyleA` (`A` = Applicable)
Declaration style           | `"div.box" - ...` | `val box = style(...)`

To create a style module, create an `object` that extends
`StyleSheet.Standalone` or `StyleSheet.Inline`.
A DSL will be available inside. Following the example, just start typing and
if you're using an IDE with auto-complete, you'll be guided towards type-safe
styles.

##### Full examples:

* [Standalone](https://github.com/japgolly/scalacss/blob/master/core/src/test/scala/japgolly/scalacss/full/StandaloneTest.scala)
* [Inline](https://github.com/japgolly/scalacss/blob/master/core/src/test/scala/japgolly/scalacss/full/InlineTest.scala)

