## Conditional CSS

CSS allows styling that is applicable only under specific under conditions.
Conditions can be:
* Pseudo selectors
* Media queries


## Pseudo Selectors

To create a pseudo selector, the syntax as follows:
<pre><code>&amp;.&lt;psuedo1&gt;
&amp;.&lt;psuedo1&gt;.&lt;psuedo2&gt;…&lt;psuedo<sub>n</sub>&gt;</code></pre>

When specifying `:not(…)` you can provide the argument as a string,
or a function in the same format used to create conditions.

Examples:

```scala
&.hover                             // :hover
&.hover.visited                     // :hover:visited
&.hover.not(_.visited)              // :hover:not(:visited)
&.hover.not(_.firstChild.visited)   // :hover:not(:first-child:visited)
&.nthChild(3).not(".debug")         // :nth-child(3):not(.debug)
&.nthChild("3n+2")                  // :nth-child(3n+2)
&.attr("custom-attr", "bla").hover  // [custom-attr="bla"]:hover
&.attrExists("custom-attr").hover   // [custom-attr]:hover
```


## Media Queries

To create a media query, the syntax as follows:
<pre><code>// Creating a single query
media.&lt;attr1&gt;
media.&lt;attr1&gt;.&lt;attr2&gt;…&lt;attr<sub>n</sub>&gt;

// Composing queries
&lt;query1&gt; & &lt;query2&gt; [… & &lt;query<sub>n</sub>&gt;] </code></pre>


Examples:

```scala
// Single queries:
media.color                         // @media (color)
media.screen                        // @media screen
media.screen.landscape.color        // @media screen and (orientation:landscape) and (color)
media.not.handheld.minWidth(320 px) // @media not handheld and (min-width:320px)

// Multiple queries:

media.screen & media.not.handheld
// produces: @media screen, not handheld

media.screen.color & media.not.handheld & media.minHeight(240 px)
// produces: @media screen and (color), not handheld, (min-height:240px)

```

## Combinations

*Henceforth I will abbreviate psuedo selector to 'PS' and media query to 'MQ'.*

To add a MQ to a PS, use `&`.

To add a PS to a MQ, specify the PS as a method on the MQ.
If you have an existing PS, use `&`.

Examples:
```scala
// These are all equivalent. All produce:
//   @media screen { <css>:active {...} }

media.screen.active

media.screen & &.active

&.active & media.screen

```


## Applying Styles

The general syntax is: `<condition>(<styles>)`.

In some cases (media queries ending in bool-like clauses),
Scala gets confused and so the syntax must be adjusted to:
`<condition> - (<styles>)`.

Examples:
```scala
object CondExample extends StyleSheet.Inline {
  import dsl._

  val exampleStyle = style(

    // Simple pseudo selector
    &.hover(
      lineHeight(1 em)
    ),

    // Multiple pseudo selectors
    &.visited.not(_.firstChild)(
      fontWeight.bold
    ),

    // Simple media query
    media.landscape(
      margin.auto
    ),

    // Pseudo selector and media query
    (&.hover & media.landscape)(
      color.black.important
    ),

    // Multiple media queries
    (media.tv.minDeviceAspectRatio(4 :/: 3) & media.all.resolution(300 dpi))(
      margin.vertical(10 em)
    ),

    // Syntax #2: <condition> - (<styles>)
    // Scala gets confused because media.color and media.color(ColorBits).
    // Use second syntax to resolve.
    media.color - (
      padding.horizontal(500 px)
    )

    // Everything is immutable. You can DRY repeated conditions
    modeX(
      backgroundColor(c"#700")
    ),
    modeX.hover(
      backgroundColor(c"#f00")
    )
  )

  def modeX = media.maxWidth(320 px).maxHeight(240 px) & media.handheld
}
```
