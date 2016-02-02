## Font faces

ScalaCSS provides support for CSS3 `@font-face` rule.

```scala
def fontFace(fontFamily: String)(config: FontSrcSelector => FontFace): FontFace
```

`fontFace` method takes two arguments:
* `fontFamily` - name of the font face.
* `config` - additional configuration.
  At a minimum the font sources `.src(…)` must be specified.
  Next you can specify the following optional attributes:
  * `fontStretch`
  * `fontStyle`
  * `fontWeight`
  * `unicodeRange`

##### Example:

```scala
object Demo extends StyleSheet.Inline {
  import dsl._

  val ff = fontFace("myFont")(
    _.src("url(font.woff)")
      .fontStretch.expanded
      .fontStyle.italic
      .unicodeRange(0, 5))

  val ff2 = fontFace("myFont2")(
    _.src("url(font2.woff)")
      .fontStyle.oblique
      .fontWeight._200)

  val ff3 = fontFace("myFont3")(
    _.src("local(HelveticaNeue)", "url(font2.woff)")
      .fontStretch.ultraCondensed
      .fontWeight._200)

  val ff4 = fontFace("myFont3")(
    _.src("local(HelveticaNeue)", "url(font2.woff)"))

  val myFontText = style(
    fontFamily(ff))
}
```

which produces this CSS:

```css
@font-face {
  font-family: MyInlineWithFontFace-myFont;
  src: url(font.woff);
  font-stretch: expanded;
  font-style: italic;
  unicode-range: U+0-5;
}

@font-face {
  font-family: MyInlineWithFontFace-myFont2;
  src: url(font2.woff);
  font-style: oblique;
  font-weight: 200;
}

@font-face {
  font-family: MyInlineWithFontFace-myFont3;
  src: local(HelveticaNeue),url(font2.woff);
  font-stretch: ultra-condensed;
  font-weight: 200;
}

@font-face {
  font-family: MyInlineWithFontFace-myFont3-2;
  src: local(HelveticaNeue),url(font2.woff);
}

.MyInlineWithFontFace-myFontText {
  font-family: MyInlineWithFontFace-myFont;
}
```
