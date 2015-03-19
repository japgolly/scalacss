# Quick Start

Add ScalaCSS to your project by adding this dependency to your SBT build:
```scala
libraryDependencies += "com.github.japgolly.scalacss" %%% "core" % "0.1.0"
```

Then to start using it, decide which of the following best suits your needs.

StyleSheets come in two flavours.

#### Standalone.

Produces static CSS for external consumption.

Like SCSS and LESS.

[Example.](standalone.md)


#### Inline.

Styles are values that can be applied directly in Scala & Scala.JS to HTML-like _stuff_.

[Example.](inline.md)

