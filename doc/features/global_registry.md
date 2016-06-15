## Global Registry

[mutable.GlobalRegistry](https://github.com/japgolly/scalacss/blob/master/core/src/main/scala/scalacss/internal/mutable/GlobalRegistry.scala)
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
import scalacss.internal.mutable.GlobalRegistry

def onStartup(): Unit = {
  // Register styles
  GlobalRegistry.register(new BoxStyles)
}
```

#### 2. `onRegistration` & `onRegistrationN`
`onRegistration` allows you to specify code to be executed once per stylesheet
added to the registry.
`onRegistrationN` does the same but in bulk (i.e. takes a `Vector` of stylesheets).

Specified functions will be applied retroactively to all styles already registered,
and applied to new styles when they're added.

`onRegistration` can be called called multiple times to have multiple actions applied.

```scala
GlobalRegistry.onRegistration { s =>
  val styleCount = s.styles.size
  println(s"Registered $styleCount styles.")
}

GlobalRegistry.onRegistrationN { ss =>
  val sheetCount = ss.size
  val styleCount = ss.map(_.styles.size).sum
  println(s"Registered $sheetCount sheets with a total of $styleCount styles.")
}
```

#### 3. `apply`
This is used to retrieve a stylesheet from the registry.
If it hasn't been registered, the result will be `None`.

```scala
// Calling Option.get will throw an exception if BoxStyles hasn't been registered
val boxStyles = GlobalRegistry[BoxStyles].get

<.div(boxStyles.mainBox, "Yay")
```
