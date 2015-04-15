## Quick Start: Inline stylesheets

Create styles as follows:

```scala
import scalacss.Defaults._

object MyStyles extends StyleSheet.Inline {
  import dsl._

  val common = mixin(
    backgroundColor.green
  )

  val myStyle1 = style(
    common, // Applying our mixin
    margin(12 px, auto),
    textAlign.left,
    cursor.pointer,

    &.hover(
      cursor.zoomIn
    ),

    &(media.not.handheld.landscape.maxWidth(640 px))(
      width(400 px)
    )
  )

  /** Style requiring an Int when applied. */
  val indent =
    intStyle(0 to 3)(i => styleS(
      paddingLeft(i * 2.ex)
    ))

  /** Style hooking into Bootstrap. */
  val button = style(
    addClassNames("btn", "btn-default")
  )
}
```

To apply a style, you apply the style's class names to some inline HTML.

You can see styles' class names like this:

```scala
MyStyles.myStyle1.htmlClass  // Returns "MyStyles-0001"
MyStyles.indent(1).htmlClass // Returns "MyStyles-0003"
MyStyles.indent(2).htmlClass // Returns "MyStyles-0004"
MyStyles.button.htmlClass    // Returns "btn btn-default"
```

And to see the generated CSS:
```scala
println( MyStyles.render )
```

Which prints:
```css
@media not handheld and (orientation:landscape) and (max-width:640px) {
  .MyStyles-0001 {
    width: 400px;
  }
}

.MyStyles-0001 {
  background-color: green;
  margin: 12px auto;
  text-align: left;
  cursor: pointer;
}

.MyStyles-0001:hover {
  cursor: -webkit-zoom-in;
  cursor: -moz-zoom-in;
  cursor: -o-zoom-in;
  cursor: zoom-in;
}

.MyStyles-0002 {
  padding-left: 0;
}

.MyStyles-0003 {
  padding-left: 2ex;
}

.MyStyles-0004 {
  padding-left: 4ex;
}

.MyStyles-0005 {
  padding-left: 6ex;
}
```

Or if you're running in production-mode, you'll see:
```css
@media not handheld and (orientation:landscape) and (max-width:640px){._a0{width:400px}}._a0{background-color:green;margin:12px auto;text-align:left;cursor:pointer}._a0:hover{cursor:-webkit-zoom-in;cursor:-moz-zoom-in;cursor:-o-zoom-in;cursor:zoom-in}._a1{padding-left:0}._a2{padding-left:2ex}._a3{padding-left:4ex}._a4{padding-left:6ex}
```
