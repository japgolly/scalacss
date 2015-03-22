## Quick Start: Standalone stylesheets

Create styles as follows:

```scala
import japgolly.scalacss.Defaults._

object MyStyles extends StyleSheet.Standalone {
  import dsl._

  "div.std" - (
    margin(12 px, auto),
    textAlign.left,
    cursor.pointer,

    &.hover -
      cursor.zoomIn,

    &(media.not.handheld.landscape.maxWidth(640 px)) -
      width(400 px),

    &("span") -
      color.red
  )

  "h1".firstChild -
    fontWeight.bold

  for (i <- 0 to 3)
    s".indent-$i" -
      paddingLeft(i * 2.ex)
}
```

To see the generated CSS:
```scala
println( MyStyles.render )
```

prints:
```css
@media not handheld and (orientation:landscape) and (max-width:640px) {
  div.std {
    width: 400px;
  }
}

div.std {
  text-align: left;
  margin: 12px auto;
  cursor: pointer;
}

div.std:hover {
  cursor: -webkit-zoom-in;
  cursor: -moz-zoom-in;
  cursor: -o-zoom-in;
  cursor: zoom-in;
}

div.std span {
  color: red;
}

h1:first-child {
  font-weight: bold;
}

.indent-0 {
  padding-left: 0;
}

.indent-1 {
  padding-left: 2ex;
}

.indent-2 {
  padding-left: 4ex;
}

.indent-3 {
  padding-left: 6ex;
}
```

Or if you're running in production-mode, you'll see:
```css
@media not handheld and (orientation:landscape) and (max-width:640px){div.std{width:400px}}div.std{text-align:left;margin:12px auto;cursor:pointer}div.std:hover{cursor:-webkit-zoom-in;cursor:-moz-zoom-in;cursor:-o-zoom-in;cursor:zoom-in}div.std span{color:red}h1:first-child{font-weight:bold}.indent-0{padding-left:0}.indent-1{padding-left:2ex}.indent-2{padding-left:4ex}.indent-3{padding-left:6ex}
```
