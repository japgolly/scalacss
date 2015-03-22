## Functional Styles

Sometimes you want a style that depends on input.

For standalone stylesheets, it's not an issue.
```scala
object MyStyles extends StyleSheet.Standalone {

  for (i <- 0 to 3)
    s".indent-$i" -
      paddingLeft(i * 2.ex)
}
```

For all other purposes, you want an `A => Style`.
That's what `StyleF` is.

A `StyleF[A]` has two arguments:

1. `A => StyleS`. A function producing a static style.
2. `Domain[A]`. A set of all legal inputs.
  CSS is generated ahead-of-time, before styles are used so its inputs must also be provided ahead-of-time.

Domains can be generated as follows:
```scala
Domain.boolean                                   // Domain[Boolean]
Domain.ofValues(1, 5, 7, 9)                      // Domain[Int]
Domain.ofRange(0 to 4)                           // Domain[Int]
Domain.ofRange(0 to 4).option *** Domain.boolean // Domain[(Option[Int], Boolean)]
```

The most common domains are `Int` and `Boolean` and so convenience methods
`intStyle` and `boolStyle` exist. Otherwise, call `styleF`.

Examples:

```scala
object MyInline extends StyleSheet.Inline {

  // Convenience method: boolStyle
  val everythingOk =
    boolStyle(ok =>
      styleS(backgroundColor(if (ok) green else red)))

  // Convenience method: intStyle
  val indent =
    intStyle(1 to 3)(i =>
      styleS(paddingLeft(i * 4.ex)))

  // Full control
  sealed trait Blah
  case object Blah1 extends Blah
  case object Blah2 extends Blah
  case object Blah3 extends Blah
  val blahStyle =
    styleF(Domain.ofValues(Blah1, Blah2, Blah3))(b =>
      styleS(...))
}

```

