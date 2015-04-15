## Global Registry

[mutable.GlobalRegistry](https://github.com/japgolly/scalacss/blob/master/core/src/main/scala/scalacss/mutable/GlobalRegistry.scala)
is an optional convenience for working with inline stylesheets.

Ideally inline stylesheet modules should either be merged into a single app-wide object,
or passed around like normal dependencies.

If that is unpalatable to your circumstances or preferences,
you can instead register style modules with the global registry,
and have client-code retrieve modules from the global registry by type.

There are three operations:

#### 1. `register`
This stores stylesheets in the registry.

```scala
class BoxStyles extends StyleSheet.Inline {
  import dsl._
  val mainBox = style(
    margin(2 px, 1 ex),
    padding(1 ex),
    border(1 px, solid, black))
}
```

```scala
import scalacss.mutable.GlobalRegistry

def onStartup(): Unit = {
  // Register styles
  GlobalRegistry.register(new BoxStyles)
}
```

#### 2. `onRegistration`
This specifies code to be executed once per each stylesheet added to the registry.
It will be applied retroactively to all styles already registered,
as well as new ones when they're added.
This can be called multiple times to have multiple actions applied.

```scala
// For scalajs-react, add each stylesheet to the document DOM.
import scalacss.ScalaCssReact._

GlobalRegistry.onRegistration(_.addToDocument())
```

#### 3. `apply`
This is used to retrieve a stylesheet from the registry.
If it hasn't been registered, the result will be `None`.

```scala
// Calling Option.get will throw an exception if BoxStyles hasn't been registered
val boxStyles = GlobalRegistry[BoxStyles].get

<.div(boxStyles.mainBox, "Yay")
```
