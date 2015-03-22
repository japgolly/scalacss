## It's all automatic

For some CSS properties, browsers require an engine-specific prefix.

Prefixes can be required on CSS attributes:
```css
   -moz-animation-delay: 60s,50ms;
   -moz-animation-delay: 60s,50ms;
     -o-animation-delay: 60s,50ms;
-webkit-animation-delay: 60s,50ms;
        animation-delay: 60s,50ms;
```

Prefixes can also be required on CSS values and functions:
```css
cursor:    -moz-zoom-in;
cursor:      -o-zoom-in;
cursor: -webkit-zoom-in;
cursor:         zoom-in;

```

ScalaCSS uses (offline) data from [caniuse.com](http://caniuse.com/) to
automatically add necessary prefixes for you.

It's taken care of under-the-hood.

## But wait, there's more

You can configure which prefixes to apply or omit.
CSS attributes ([`Attr`](https://github.com/japgolly/scalacss/blob/master/core/src/main/scala/japgolly/scalacss/Attr.scala))
aren't just a `Value => CssKV`; they have a ` Env => Value => Vector[CssKV]`
which allows them to customise what they produce depending on an environment
you feed in.

When you're generating CSS in the browser (ie. a Scala.JS project)
you don't need to generate different prefixes because you know what
rendering engine to target.
The Scala.JS version of `Defaults` provides an implicit `Env` that uses
[platform.js](https://github.com/bestiejs/platform.js/) (embedded)
to determine which prefixes you need according to [caniuse.com](http://caniuse.com/) data.
