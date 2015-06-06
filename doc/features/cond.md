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
&.hover                           // :hover
&.hover.visited                   // :hover:visited
&.hover.not(_.visited)            // :hover:not(:visited)
&.hover.not(_.firstChild.visited) // :hover:not(:first-child:visited)
&.nthChild(3).not(".debug")       // :nth-child(3):not(.debug)
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
// These are all equivalent.
// They all produce @media screen :active

media.screen.active

media.screen  &  &.active

&.active  &  media.screen

```
