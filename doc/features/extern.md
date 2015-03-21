## Status Quo

CSS libraries, like [Bootstrap](http://getbootstrap.com/),
often require that you add special class names to everything in your code.

This is
1. Tedious to do.
2. Error-prone.  Mistakes are easy.
3. Hard to switch from, to a different CSS library.
4. Invasive and disruptive. It makes code very hard to read when it's littered with foreign class names.

## Tame it!

ScalaCSS isn't magic that makes everything taste like rainbows but
_it can_ mitigate much of the problem-space dealing with external CSS
and the issues mentioned above.

When writing [inline stylesheets](../quickstart/inline.md),
you have a new property at your fingertips: `addClassNames`.

```scala
object MyStyles extends StyleSheet.Inline {

  val button = style(
    addClassNames("btn", "btn-default"), // Bootstrap classes
    textAlign.center                     // Optional customisation
  )
}
```

You can declare that when your style is applied, additional class names
should also be applied. This means that you can put all of your Bootstrap
(for example) class names in a single file with all your other style
properties and have it be inivisble to the rest of your app.
If you later decide to drop Bootstrap for
[Materialize](http://materializecss.com/) or [Groundwork](https://groundworkcss.github.io/),
then you change the `addClassNames` attributes in a single file and you're about done.
