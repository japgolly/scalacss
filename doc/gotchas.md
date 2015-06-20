## Inner style objects

Objects in Scala are lazy.
If you put styles in inner objects you need to make sure they're initialised before your styles are rendered.
To do so, call this at the end of your stylesheet with one style from each inner object.

Example:
```scala
object MyStyles extends StyleSheet.Inline {
  import dsl._

  val hello = style(display.inline)

  object innerObject {
    val blah1 = style(borderCollapse.collapse)
    val blah2 = style(borderCollapse.separate)

    object andAgain {
      val hello = style(display.block)
    }
  }

  // Put this last
  initInnerObjects(
    innerObject.blah1,
    innerObject.andAgain.hello)
}
```
