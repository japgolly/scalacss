## A common problem, rarely discussed

Styles are not always individual properties pertaining to an individual subject.
Often there are times when what the user perceives as a style or feature,
must be implemented as multiple styles applied to multiple subjects.

<p style="color:#811">
If one of the sub-styles is not applied, or applied incorrectly,
then <em><strong>the total style fails</strong></em>.
</p>

## StyleC

~~ScalaCSS has a feature to address this.
It's called a composite style, `StyleC`.
It is a composite of sub-styles, each given unique names.~~

`StyleC` was removed in 0.4.0. *(Why? See [#48](https://github.com/japgolly/scalacss/issues/48).)*

There is currently no replacement.

Ideas for an appropriate replacement can be discussed in [#71](https://github.com/japgolly/scalacss/issues/71).


# Unsafety

ScalaCSS's philosophy is to provide tools, not force decisions upon you.
If you want or need to style children without type-safety, you can do so
using `unsafeChild`. It works just like how you'd expect it to if you were
using SASS or LESS.

```scala
object YOLO extends StyleSheet.Inline {
  import dsl._

  val example = style(
    ...,

    unsafeChild("label")(
       ...
    ),

    unsafeChild("input")(
       ...
    )
  )
}
```

This is named `unsafeChild` to highlight that you're opting out of type-safety
and that care should be taken during refactoring and style management.
It is not unsafe in other ways — your other styles will be completely
unaffected, servers won't catch fire, your mother won't fall down stairs.
