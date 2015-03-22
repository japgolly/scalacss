## Quick Start: Inline stylesheets

Create styles as follows:

```scala
import japgolly.scalacss.Defaults._

object MyStyles extends StyleSheet.Inline {
  import dsl._

  val myStyle1 = style(
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
    addClassNames("btn", "btn-default"),
    textAlign.center
  )
}
```

To apply a style, you apply the style's class names to some inline HTML.

You can see styles' class names like this:

```scala
MyStyles.myStyle1.htmlClass  // Returns "scalacss-0001"
MyStyles.indent(1).htmlClass // Returns "scalacss-0003"
MyStyles.indent(2).htmlClass // Returns "scalacss-0004"
MyStyles.button.htmlClass    // Returns "scalacss-0006 btn btn-default"
```

And to see the generated CSS:
```scala
println( MyStyles.render )
```

Which prints:
```css
@media not handheld and (orientation:landscape) and (max-width:640px) {
  .scalacss-0001 {
    width: 400px;
  }
}

.scalacss-0001 {
  text-align: left;
  margin: 12px auto;
  cursor: pointer;
}

.scalacss-0001:hover {
  cursor: -webkit-zoom-in;
  cursor: -moz-zoom-in;
  cursor: -o-zoom-in;
  cursor: zoom-in;
}

.scalacss-0002 {
  padding-left: 0;
}

.scalacss-0003 {
  padding-left: 2ex;
}

.scalacss-0004 {
  padding-left: 4ex;
}

.scalacss-0005 {
  padding-left: 6ex;
}

.scalacss-0006 {
  text-align: center;
}
```

Or if you're running in production-mode, you'll see:
```css
@media not handheld and (orientation:landscape) and (max-width:640px){._0{width:400px}}._0{text-align:left;margin:12px auto;cursor:pointer}._0:hover{cursor:-webkit-zoom-in;cursor:-moz-zoom-in;cursor:-o-zoom-in;cursor:zoom-in}._1{padding-left:0}._2{padding-left:2ex}._3{padding-left:4ex}._4{padding-left:6ex}._5{text-align:center}
```
