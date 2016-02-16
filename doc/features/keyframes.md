## Keyframe animations

The following DSL is available for specifying keyframe animations.

| DSL | Description |
|-----|-----|
| `keyframe(...)` | It is equivalent of `style(...)`, but will not produce an indiviual class/entry in the rendered CSS. |
| `keyframes((Percentage[Int], StyleA)*)` | Specify a group of keyframes for animation. |


##### Example:

```scala
object Demo extends StyleSheet.Inline {
  import dsl._

  val hello = style(
    height(100 px),
    width(30 px))

  val hello2 = keyframe(
    height(150 px),
    width(30 px))

  val kf1 = keyframes(
    (0 %%) -> hello,
    (20 %%) -> hello2,
    (100 %%) -> keyframe(
      height(200 px),
      width(60 px))
  )

  val anim1 = style(
    animationName(kf1))
}
```

This code will not produce separate CSS for value `hello2`; its style will only be rendered inside the `@keyframes` block.

Rendered CSS gonna look like following:

```css
@keyframes Demo-kf1 {
  0% {
    height: 100px;
    width: 30px;
  }

  20% {
    height: 150px;
    width: 30px;
  }

  100% {
    height: 200px;
    width: 60px;
  }
}

.Demo-hello {
  height: 100px;
  width: 30px;
}

.Demo-anim1 {
  -o-animation-name: Keyframes-kf1;
  -webkit-animation-name: Keyframes-kf1;
  -moz-animation-name: Keyframes-kf1;
  animation-name: Keyframes-kf1;
}
```
