## Detecting conflict

Whether you're creating a new style from scratch,
extending an existing one,
or composing a few together,
you can end up with multiple attributes stepping on each other's toes.

Example:
```scala
// In MyGenericTheme.scala
val button = style(margin(8 px, auto), ...)

// In UserStyles.scala
val userTitle = style(marginLeft(4 ex), ...)
...
val userButton = style(button, userTitle, ...)

```

Whoops. The `button` style thinks `margin-left` should be `auto`,
but the `userTitle` style thinks it should be `4ex`.
Something is probably going to ***look wrong in production***.
Worse, broken styles can cause UI to be inaccessible to the user.
It's often been the case that these kinds of errors slip by unnoticed.

It's not always obvious when there's a conflict either.
`grid-area` and `grid-row` conflict. Why? They both affect `grid-row-start`.

ScalaCSS allows you to catch them and handle them immediately.

##### Example warnings
```
[CSS WARNING] .MyStyles-navbar -- {margin-left: 6px} conflicts with {margin: 12px}
[CSS WARNING] .MyStyles-button -- {cursor: zoom-in} conflicts with {cursor: pointer}
```


## Handling conflict

Conflict is detected whenever styles and/or attributes are composed.
How conflict should be handled is determined by the `Compose` class.
It's implicitly required to build a style with more than one attribute
but often invisible because [Defaults.scala](https://github.com/japgolly/scalacss/blob/master/core/src/main/scala/scalacss/Defaults.scala)
provides an instance.

By customising your `Compose` instance you can
* Create warnings when conflict occurs.
* Discard the old value and use the new one (or vica versa).
* Define custom merge strategies.

Because this behaviour is provided by an implicit value,
you can provide specialised behaviours to limited scopes or to explicit attributes.
