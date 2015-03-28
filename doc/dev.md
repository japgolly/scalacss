## Adding attributes

1. Add to [Attrs.scala](https://github.com/japgolly/scalacss/blob/master/core/src/main/scala/japgolly/scalacss/Attrs.scala)
1. Copy some ScalaDoc from MDN or similar.
1. Add it to `Attrs.valuesForAllAttr`
1. Run `bin/generate-aliases`

## Updaing CanIUse data

1. `cd misc`
1. Update caniuse (how? lol)
1. `sbt run`
1. Check-in changes.

## Updaing platform.js

1. `bin/generate-platformjs`
1. Check-in changes.

## Editing the documentation

* It's all under `doc/` in markdown format.
  The sidebar is in `SUMMARY.md`
* Run `bin/book-dev`. It will split your terminal into with the bottom half
  running `bin/book-serve` which generates HTML with live-refresh.
* Open your browser to `localhost:4000`.
* Edit markdown.