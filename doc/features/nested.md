## A common problem, rarely discussed

Styles are not always individual properties pertaining to an individual subject.
Often there are times when what the user perceives as a style or feature,
must be implemented as multiple styles applied to multiple subjects.

<p style="color:#811">
If one of the sub-styles is not applied, or applied incorrectly,
then <em><strong>the total style fails</strong></em>.
</p>

## StyleC

ScalaCSS has a feature to address this.
It's called a composite style, `StyleC`.
It is a composite of sub-styles, each given unique names.

In order to apply the total style, ScalaCSS uses the compiler to enforce
that developers…

##### 1. Receive all sub-styles.

If a total style has 3 sub-styles, the developer must receive all 3 styles.

If a 3-style composite is modified to add a fourth, all code using the style
will break until the fourth, new style is received everywhere the style is used.

##### 2. Acknowledge the purpose of each style.

In a type-safe world, it wouldn't feel very safe if you used a composite style
and were handed a `(StyleA, StyleA, StyleA)`.
Which one is which? If the order changes in the style definition its going to be
a problem.

This problem is addressed by the style definition attaching names
(like tags/labels) to each substyle, then the developer providing the same names
in order to use it.

This adds a little tedium but it provides an unprecedented level of safety and
error prevention.

##### Example (using {{book.scalajsReact}}):

Definition:
```scala
object Styles extends StyleSheet.Inline {
  import dsl._

  // This is a style for a container
  // with a <label> and an <input type=checkbox> in it
  val example = styleC {
    val top = styleS(display.block)
    val lbl = styleS(fontWeight.bold)
    val chk = styleS(backgroundColor.red, marginLeft(1 ex))
    top.named('outer) :*: lbl.named('label) :*: chk.named('checkbox)
  }
}
```

Usage:
```scala
def featureActiveCheckbox(on: Boolean): ReactElement =
  Styles.example('outer   )(outerStyle =>
               _('label   )(labelStyle =>
               _('checkbox)(inputStyle =>
    <.div(outerStyle,
      <.label(labelStyle, "Feature Active:"),
      <.input(inputStyle, ^.typ := "checkbox", ^.value := on))
  )))
```

Error prevention: arity
```scala
// Forgot outerStyle. Compile error!
def featureActiveCheckbox(on: Boolean): ReactElement =
  Styles.example('label   )(labelStyle =>
               _('checkbox)(inputStyle =>
    <.div(
      <.label(labelStyle, "Feature Active:"),
      <.input(inputStyle, ^.typ := "checkbox", ^.value := on))
  ))
```

Error prevention: order
```scala
// The second style is for the label, not the checkbox. Compile error!
def featureActiveCheckbox(on: Boolean): ReactElement =
  Styles.example('outer   )(outerStyle =>
               _('checkbox)(inputStyle =>
               _('label   )(labelStyle =>
    <.div(outerStyle,
      <.label(labelStyle, "Feature Active:"),
      <.input(inputStyle, ^.typ := "checkbox", ^.value := on))
  )))
```

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
