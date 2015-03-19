# scalacss

[![Build Status](https://travis-ci.org/japgolly/scalacss.svg?branch=master)](https://travis-ci.org/japgolly/scalacss)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/japgolly/scalacss?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

> Server code, client JS and HTML are all compiler-verified in Scala, leaving CSS as the last part of my webapp mega-project where I feel unsafe that it always works and is maintainable.

The first thoughts about the conception of this project are gathered in two places:
* [scalajs-react#77](https://github.com/japgolly/scalajs-react/issues/77).
* [scala-js ML](https://groups.google.com/forum/#!folder/Scala/scala-js/IzCn1xfoFWs)


# Status

##### Requirements

Analysis and the Requirements first draft are done.
See [doc/reqs.md](https://github.com/japgolly/scalacss/blob/master/doc/reqs.md).

Please feel free to review and discuss in Gitter chat.

##### Types & Design

Low-level types are done in [experiment/main.scala](https://github.com/japgolly/scalacss/blob/master/experiment/main.scala).
(Be prepared for ugliness - it's experimentation.)

It's been decided that compile-time key conflict resolution will be defered. Resolution will be performed at runtime for now.

There will be a high-level interface on top to make usage as nice, easy and concise as LESS/SASS.

##### Implementation

In progress.

# Features / Progress

##### Week #1

* Attributes.
* Pseudo selectors.
* Automatic browser prefixes for properties (using caniuse.com data).
* Attribute conflict detection. (Transitive using reachability of underlying properties. Eg. `grid-area` affects `grid-row` which affects `grid-row-start`.)
* Various (customisable) composition behaviours.
* Static styles.
* Functional styles.
* Safe composite styles. (style families, style groups)
* Concise DSL.
* CSS generation (for external CSS files) in minified format, pretty-printed or custom.
* Anonymous CSS generation (for internal styling like in Scala.JS).
* Reusable styles (modules).
* Ability to dynamically tailor CSS to environment. (Able but as yet, unused.)
* Path-dependent children like in LESS/SASS.

##### Week #2

* Add more attrs discovered by caniuse.com data.
* Type-safe CSS values (not all but most oft used are done)
* Automatic browser prefixes for _values_ (using caniuse.com data).
* Styles can add class names, for example to apply Bootstrap classes like `btn btn-default`.
* CSS Reset.
* More DSL for composite styles.
* Standalone styles (as opposed to Inline styles).
* Default settings for JVM ⊗ {Dev,Prod}.
* In Scala.JS, omit unnecessary browser prefixes.
* Default `Env` for JS.

Upcoming...

* Media query AST.

* Add more psuedo selectors discovered by caniuse.com data.
* Detect more problems, add more warnings.
* Incorporate known CSS hacks like [this](https://github.com/postcss/autoprefixer-core/blob/master/lib/hacks/border-radius.coffee).

