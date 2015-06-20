In CSS each attribute has its own syntax describing its legal value(s).
In ScalaCSS most CSS attributes have their value syntaxes encoded, but not all.

You can browse defined attributes in [Attrs.scala](https://github.com/japgolly/scalacss/blob/master/core/src/main/scala/scalacss/Attrs.scala).

## Untyped

To use an attribute that doesn't have a typed value syntax yet,
use the `:=` operator.

Example:
```scala
font := "monochrome"
font := "'Times New Roman', Times, serif"
font := ^.auto  // The DSL provides ^ as a shortcut for Literal.
```

That's all there is to it.

## Typed

Most attributes have their value syntax encoded.
These attributes are `objects` that extend `TypedAttrBase`.
They have methods that facilitate legal values in a type-safe manner.
To use, simply explore their methods.

Example:
```scala
margin.auto
margin.inherit
margin.`0`
margin(12 px)
margin(12 px, auto)
margin(12 px, auto, 16 px, 4 ex)

textDecorationLine.overline
textDecorationLine.underline
textDecorationLine.underline.overline
```


#### Colours

The following DSL is available for specifying colours.

| DSL | Example | Description |
|-----|---------|-------------|
| `c"<literal>"` | `c"#37b"` <br> `c"#ffcc99"` | Specify a literal colour and have the compiler verify that it is a valid `#RGB`, `#RRGGBB`, or `{rgb,hsl}{,a}` function with valid arguments. |
| `<name>` | `red` <br> `black` | A colour literal, specified in the CSS spec. |
| `rgb(int x 3)` | `rgb(0, 128, 0)` | Combines red, green and blue values [0-255]. |
| `rgb(percentage x 3)` | `rgb(0.%%, 50.%%, 0.%%)` | Red, green and blue percentages. |
| `rgba(int x 3, double)` | `rgba(0, 128, 0, 0.5)` | Red, green and blue values [0-255] with an alpha component [0-1]. |
| `hsl(int, percentage x 2)` | `hsl(300, 50.%%, 10.%%)` | Hue [0-360], saturation and lightness. |
| `hsla(int, percentage x 2, double)` | `hsla(300, 50.%%, 10.%%, 0.5)` | Hue [0-360], saturation, lightness and alpha [0-1]. |
| `grey(int)` | `grey(224)` | A greyscale colour [0-255] with the specified amount of red, green and blue. |
| `<keyword>` | `transparent` <br> `currentColor` <br> `inherit` | Any other keywords that are legal as, or in place of colours in the CSS spec can be used as is. |
| `Color("<literal>")` | `Color("i break you css!")`| Specify a literal colour without any compiler validation. |

Using the `c"<literal>"` syntax, invalid colours will result in compiler errors.

Example:
```scala
backgroundColor(red) // ok

backgroundColor(c"#ffcc99") // ok
backgroundColor(c"#ffcc9o") // compiler error: o is not hex
backgroundColor(c"#ffcc9")  // compiler error: 5 digits

backgroundColor(c"hsl(200, 20%, 10%)") // ok
backgroundColor(c"rgb(0%, 20%, 10%)")  // ok
backgroundColor(c"rgba(0, 0, 0, 0.5)") // ok
backgroundColor(c"rgb(30, 30, 300)")   // compiler error: 300 is too large
backgroundColor(c"rbg(32, 128, 32)")   // compiler error: typo in rbg
```


#### Bypassing

You can bypass type-safety and provide a manual value.
Similar to the untyped attribute syntax of `attr := value`,
for typed attributes the syntax is
```scala
attr :=! value
```

You _can_ use `:=` but it will generate compiler warnings.
This mechanism means that when an untyped attribute becomes typed in a ScalaCSS
upgrade, you'll be alerted to all the cases where you're accidently bypassing
type-safety.

Example:
```scala
margin :=  "12px auto"  // Compile warns there's a type-safe alternative
margin :=! "auto\\9"    // The ! means you know what you're doing
```


## Contributing

If your favourite attribute doesn't have typed values, please patch it and
submit a pull request.

Typing all values is a lot of work for one person and automation only gets one
so far. Most are done but not all.
If different users contribute a small amount of time to get a few
attributes done then they'll all be typed in no time.

This is what an untyped attribute looks like:
```scala
val alignItems =
  Attr.real("align-items", Transform keys CanIUse.flexbox)
```

This is what a typed attribute looks like:
```scala
object alignItems extends TypedAttrBase {
  override val attr = Attr.real("align-items", Transform keys CanIUse.flexbox)
  def baseline  = av(L.baseline)
  def center    = av(L.center)
  def flexEnd   = av(L.flexEnd)
  def flexStart = av(L.flexStart)
  def stretch   = av(L.stretch)
}
```

There are classes that help with common value syntaxes.
This example uses `TypedAttrT1[Len]` to get a 1-arg `apply()` method accepting
anything that be considered a length (eg. `12 px`), and `ZeroLit` to gain a
<code>&#96;0&#96;</code> method to indicate a length of 0.
```scala
object letterSpacing extends TypedAttrT1[Len] with ZeroLit {
  override val attr = Attr.real("letter-spacing")
  def normal = av(L.normal)
}
```
