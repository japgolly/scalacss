## Conditional CSS

CSS allows styling that is applicable only under specific under conditions.
Conditions can be:
* Pseudo selectors
* Media queries

In ScalaCSS, conditions are…
* defined using a DSL starting with `&` for pseudo selectors and `media` for media queries.
* applied by nesting the declaration inside the style that it affects.
* composable with any other conditions and styles. (In fact `Cond` is a monoid.)


## Pseudo Selectors

Very straight-forward. Examples:

```scala
&.visited                   // :visited
&.nthChild(3).not(".debug") // :nth-child(3):not(.debug)
```


## Media Queries

Similarly straight-forward except media queries are actually a list of queries.
Use `&` to compose separate ones.

Examples:

```scala
media.color                         // @media (color)
media.screen                        // @media screen
media.screen.landscape.color        // @media screen and (orientation:landscape) and (color)
media.not.handheld.minWidth(320 px) // @media not handheld and (min-width:320px)

// Use & to compose
media.screen & media.not.handheld   // @media screen, not handheld
```
