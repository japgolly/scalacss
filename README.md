# scalacss

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
