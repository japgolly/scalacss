# StyleSheet.Inline

Create styles as follows:

```scala
import japgolly.scalacss.Defaults._

object MyStyles extends StyleSheet.Inline {

}
```

To see the generated CSS:
```scala
println( MyStyles.renderA )
```

prints:
```css
```
