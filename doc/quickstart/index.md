# Quick Start

Add [ScalaCSS](https://github.com/japgolly/scalacss/) to your project by adding this dependency to your SBT build:

<pre><code class="lang-scala">libraryDependencies += <span class="hljs-string">&quot;com.github.japgolly.scalacss&quot;</span> %%% <span class="hljs-string">&quot;core&quot;</span> % <span class="hljs-string">&quot;{{ book.ver }}&quot;</span></code></pre>

Then to start using it, decide which of the following best suits your needs.

StyleSheets come in two flavours.

#### Standalone.

Produces static CSS for external consumption.

Like SCSS and LESS.

[Example.](standalone.md)


#### Inline.

Styles are values that can be applied directly in Scala & Scala.JS to HTML-like _stuff_.

[Example.](inline.md)

