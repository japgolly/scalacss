# ScalaCSS Improvement, Fix & Optimisation Suggestions

## Bugs & Correctness

11. **Fix `Domain.IntoSeq`, `Pair`, and `OptionT` missing bounds checks and `IntoSeq` overflow/negative-range bugs (`core/shared/src/main/scala/scalacss/internal/Domain.scala`)**
    - `IntoSeq.apply(i0)` (lines 101-120) never checks `i0 < 0 || i0 >= size` when `as > 0` (out-of-bounds indices silently wrap or index the last bucket; negative indices pass negative modulo values to `a`).
    - `IntoSeq` (line 98) computes `math.pow(as, i).toInt`, silently overflowing `Int` for large domains or turning negative `r` values into `0`.
    - `Pair.apply` (lines 79-83) only guards `as == 0` and `OptionT.apply` (line 67) doesn't guard `i < 0`.

12. **Fix `Pseudo.AttrSelector` escaping and `Pseudo.NthChildBase` `An+B` syntax support (`core/shared/src/main/scala/scalacss/internal/Pseudo.scala`)**
    - `AttrSelector` (lines 239-240) interpolates `value` raw into `[$name$op"$value"]` without escaping `"` or `\`, producing broken CSS if `value` contains quotes.
    - `NthChildBase` (`queryPattern` / `fQueryPattern`, lines 165-166) rejects valid CSS `An+B` expressions with spaces around `+`/`-` (e.g. `"2n + 1"`) or uppercase (`"ODD"`, `"EVEN"`, `"2N+1"`).

13. **Fix `Media.Query.and` silently dropping `TypeExpr` and reordering features (`core/shared/src/main/scala/scalacss/internal/Media.scala`)**
    - In `Query.and` (lines 114-126), `(Left(t1), Left(t2))` does `if (t1 ==* Just(All)) t2 else t1`, silently discarding `t2` (e.g. `media.screen and media.print` drops `print`, and `media.not.screen and media.all` vs `media.all and media.not.screen` behaves inconsistently).
    - `(Right(f), Left(t))` puts `f +: this.tail` after `that.tail`, whereas `(Left(t), Right(f))` puts `this.tail` before `f +: that.tail`.

14. **Fix implicit ambiguity when `DevDefaults._` and `ScalaCssReact._` are imported together (`ext-react/src/main/scala/scalacss/React.scala`)**
    - `ScalaCssReactImplicits` (line 25) extends `PlatformExports`, and `Exports` (`core/shared/src/main/scala/scalacss/defaults/Exports.scala:4`, mixed into `DevDefaults`/`ProdDefaults`) also extends `PlatformExports`.
    - Importing both `scalacss.DevDefaults._` and `scalacss.ScalaCssReact._` in the same scope causes ambiguous implicit errors for `.addToDocument()`, `.addToDocumentOnRegistration()`, and `.render[HTMLStyleElement]`.

15. **Fix `ScalatagsTextRenderer` RAWTEXT `<style>` injection hazard (`ext-scalatags/shared/src/main/scala/scalacss/ScalatagsText.scala`)**
    - `ScalatagsTextRenderer.apply` (line 21) passes `raw(s(css))` directly inside `tags2.style(...)`. Because `Util.quote` (`Util.scala:15-19`) does not escape `<` or `>`, a CSS string value containing `</style` prematurely closes the `<style>` tag in HTML5 parsers (XSS vector if untrusted strings reach `content.string` or `url`).

---

## Thread-Safety & Concurrency

16. **Close TOCTOU race, public `var` exposure, and torn snapshots in `Register` (`core/shared/src/main/scala/scalacss/internal/mutable/Register.scala`, `StyleSheet.scala`)**
    - `StyleSheet.Inline.MStyle.apply` (`StyleSheet.scala:141-142`) calls `applyMacroName` (which checks `ensureUnique` and releases `mutex`) and then `registerS` (which re-acquires `mutex`), allowing concurrent registrations with the same macro name to collide.
    - `_nameGen`, `_styles`, `_keyframes`, `_fontFaces`, and `_rendered` (`Register.scala:17-21`) are public `var`s instead of `private[this]`.
    - `Register.css` (`Register.scala:157-158`) calls `styles`, `keyframes`, `fontFaces` in three separate `mutex` blocks, allowing a torn snapshot.
    - `nextName` (`Register.scala:67, 125, 134`) does not check `isTaken`, so auto-generated names can collide with manual names.

17. **Fix JVM `Mutex.mutex` allocating a fresh lock per implicit resolution (`core/jvm/src/main/scala/scalacss/internal/mutable/Mutex.scala`)**
    - `Mutex.mutex` (line 15) is `implicit def mutex: Mutex = new Mutex` (unlike JS's `implicit val mutex`). Wait-free/shared coordination across callers that summon `Mutex` implicitly (e.g., multiple `StyleSheetRegistry` instances or default `Register`s) gets separate lock instances and extra allocations.

18. **Fix `StyleSheetRegistry` (`GlobalRegistry`) callback ordering, re-entrancy, and unbounded growth (`core/shared/src/main/scala/scalacss/internal/mutable/GlobalRegistry.scala`)**
    - `onRegistrationN` (lines 43-52) invokes `f(_register)` **while holding `mutex`** and *before* updating `_onRegistration`. Re-entrant `register(s2)` inside `f` misses the new listener, and external side effects risk deadlock.
    - `register` (lines 56-60) invokes `_onRegistration(v)` *outside* `mutex`, allowing concurrent `register` calls to deliver callbacks out of order.
    - `_register` has no deduplication or `unregister`/`clear`, and `_onRegistration = s => { g(s); f(s) }` chains closures indefinitely (no unsubscribe, linear stack depth on `N` listeners).

---

## Performance & Memory Optimisations

19. **Eliminate per-`AV` `Subject` filtering and `Map` allocations in `Transform.prefix` / `Attrs` / `CanIUse` (`Attr.scala`, `Attrs.scala`, `CanIUse.scala`, `misc/caniuse.scala`)**
    - In `Attr.scala:123-128`, `Transform.prefix` runs `subject.filter(kv => e.agents.contains(kv._1))` and `CanIUse2.prefixPlan(subectSubset)` on **every single `AV` evaluation** (and 5 times per `display` property via `Transform.*` in `Attrs.scala:949-954`). For `Env.empty` (all 16 agents), this needlessly copies the full 16-entry `Map`, hashes it, and hits `memo`'s synchronised global `Map`.
    - In `Attrs.scala`, dozens of attributes with transforms (e.g., `boxShadow`, `clipPath`, `filter`, `grid*`, `backgroundImage`, etc.) are `final def` instead of `val`/`lazy val`, allocating a fresh `RealAttr` and `Transform` on every usage.
    - In `CanIUse.scala`, ~110 of ~122 `Subject`s are generated as `def` (because `misc/caniuse.scala:257-269` only lists 12 in `valAttrs`), re-allocating a 16-entry `Map` and 16 `Set`s every time an `Attrs` `def` evaluates.
    - **Fix:** Compute `PrefixPlan` directly from `subject.iterator` + `e.agents` without building an intermediate `Map`; precompute the full-agent `PrefixPlan` on `Transform.prefix` (for `e.agents.size == Agent.values.length`) or cache per `Env`; and make `CanIUse` subjects and `Attrs` definitions `val` / `lazy val` (note that `CanIUse2.prefixed` also runs two regexes per transform step).

20. **Make `Register.isTaken` O(1) instead of O(N) (`core/shared/src/main/scala/scalacss/internal/mutable/Register.scala`)**
    - `isTaken` (lines 34-37) linearly scans `_styles` and `_keyframes`. Under `DevDefaults`, `ensureUnique` and `emitRegistrationWarnings` both call `isTaken`, making `N` style registrations O(N²) (or O(N³) on collisions).
    - **Fix:** Maintain a `Set[ClassName]` (or `mutable.HashSet[String]`) of taken names inside `Register`.

21. **Avoid O(n log n) `Cond.toString` allocations in `Cond.ordering` (`core/shared/src/main/scala/scalacss/internal/Cond.scala`)**
    - `Cond.ordering` (lines 65-66) is `Ordering.by[Cond, String](_.toString)(NaturalOrdering)`.
    - `StyleS.dataSorted` (`Style.scala:57-61`) sorts using this ordering, calling `Cond.toString`—which renders `Media.css` and `Css.selector`—repeatedly on every comparison.
    - **Fix:** Cache the sort key (e.g. `private[scalacss] lazy val sortKey: String = toString`) or compare structurally before falling back to string comparison.

22. **Replace repeated `Vector ++`, `ListMap.updated`, and `Set` allocations in `Css`, `Compose`, and `AVs` (`Css.scala`, `Compose.scala`, `AVs.scala`)**
    - `Css.style` (`Css.scala:57`): `avs.avIterator.map(_(env)).foldLeft(Vector.empty[CssKV])(_ ++ _)` repeatedly concatenates `Vector`s; use `avs.avIterator.flatMap(_(env)).toVector` (or a `Vector.newBuilder`).
    - `Css.mapByMediaQuery` (`Css.scala:91-98`) and `Css.keyframes` (`Css.scala:40-43`): repeatedly call `ListMap.updated` (O(n) per update) and `NonEmptyVector.endO` (`whole :+ a`); building with a `mutable.LinkedHashMap` internally is O(n) total.
    - `Compose.mergeAVs` (`Compose.scala:29-31, 44`): calls `kvs.toSet` / `ovs.toSet` on every conflict check and duplicate filter. Also, `b.data.foldLeft` (line 63) iterates a `HashMap`, making warning order non-deterministic when `b.data` has >4 conditions.
    - `AVs.++(avs: AVs)` (`AVs.scala:34-35`): rebuilds `order` and `data` per attribute instead of merging in batch.

23. **Cache `Platform.env` on JS (`core/js/src/main/scala/scalacss/internal/Platform.scala`, `PlatformJsEnv.scala`, `Settings.scala`)**
    - `Platform.env`, `PlatformJsEnv.value`, and `Settings.cssEnv` are all `def`s. Every `.render`, `.css`, or `.addToDocument()` re-parses `PlatformJs` properties and allocates fresh `OS`, `Platform`, `NonEmptyVector`, `Media`, and `Env` objects, defeating `Env.agents: lazy val`.
    - **Fix:** Make `Platform.env` (and `PlatformJsEnv.value`) a `lazy val`.

24. **Optimise hot-path string & wrapper allocations (`Util.scala`, `Data.scala`, `Renderer.scala`, `Dsl.scala`, `ValueT.scala`, `Pseudo.scala`, `Scalatags*.scala`)**
    - `Util.quote` (`Util.scala:7-32`) uses `newStr += add` in a loop and `"\\%06X".format(cp)` instead of a `StringBuilder` and hex digit appending.
    - `Data.Length.value` (`Data.scala:144-147`) allocates two intermediate strings via `s.stripPrefix("-").stripSuffix(".0")` on every call; `ClassName.parse` (`Data.scala:15`) compiles/splits `\\s+` regex on every call.
    - `StringRenderer.formatTiny` (`Renderer.scala:155-158`) emits `"@font-face {"` with an unnecessary space (unlike all other rules in `formatTiny`, though note `ProdRenderInlineTest` and `RendererTest` currently assert this space) and allocates `CssKV(key, value)` per `@font-face` property.
    - `Dsl.style` (`Dsl.scala:306`) calls `t.map(_.s).reduce(_ compose _)` allocating an intermediate collection instead of iterating/folding `t.iterator`. `DslBase.DslCond` (`Dsl.scala:144`) is not `AnyVal`.
    - `ValueT` (`ValueT.scala:15`) allocates a case class wrapper per typed CSS value (commented-out zero-cost cast at lines 21-28; on Scala 3 this could be an `opaque type`, and on Scala 2 `extends AnyVal` or tagged type). `ValueT.concat` (lines 177-184) uses chained `+` instead of a single interpolation/`StringBuilder`.
    - `Pseudo.addPseudo` / `Composite` (`Pseudo.scala:42-65, 90-92`) builds a `SortedSet`, passes `(s.head, s.tail)` to `Composite`, and `Composite` immediately recomputes `val singles = t + h`.
    - `ScalatagsTextImplicits.styleaToTextTag` (`ScalatagsText.scala:11-13`) allocates `" " + s.htmlClass` (and produces leading/double spaces in `class="..."`), and `ScalatagsJsDomImplicits.styleaToJsDomTag` (`ScalatagsJsDom.scala:11-14`) allocates a composite iterator even for single-class `StyleA`s.

---

## JS / DOM & Platform Detection

25. **Replace `js.eval` of embedded `platform.js` with `navigator.userAgent` / feature detection (`core/js/src/main/scala/scalacss/js/PlatformJs.scala`, `PlatformJsEnv.scala`)**
    - `PlatformJs.value` (lines 19-27) embeds ~9 KB of an old (~2015) `platform.js` snapshot and executes it via `js.eval(sourceCode)`.
    - This fails under strict CSP (`'unsafe-eval'` disabled), pollutes `window.platform`, and breaks under AMD/CommonJS bundlers (where the UMD epilogue writes to `define(...)` or `exports` instead of `window.platform`, leaving `js.Dynamic.global.platform` `undefined`).
    - Also, `PlatformJsEnv.value` (`PlatformJsEnv.scala:22`) does not guard against `null` (`UndefOr` only checks `!= undefined`), and `PlatformJs.sourceCode` is out of date for modern browsers (Chromium Edge, iPadOS, Samsung Internet).
    - **Fix:** Detect browser/OS directly in Scala.js from `window.navigator.userAgent` / `userAgentData` without `eval`, or make `PlatformJs` opt-in.

26. **Update `CanIUse2.agentsForPlatform` matching (`core/shared/src/main/scala/scalacss/internal/CanIUse2.scala`)**
    - Line 127 inspects `p.toString.toLowerCase contains "android"`, which allocates the full `Platform.toString` and can misfire if `desc`/`userAgent` contains `"android"` unexpectedly (e.g. `p.os.exists(_.family.exists(_.toLowerCase.contains("android")))` is cleaner).
    - `CanIUse.Agent.values` includes `Samsung` and `BlackberryBrowser`, neither of which is matched in `agentsForPlatform` (lines 124-146), and Safari matching only checks exact `"Safari"` (missing mobile/iOS variants).

27. **Harden `PlatformExports.installStyle` and `addToDocumentOnRegistration` (`core/js/src/main/scala/scalacss/defaults/PlatformExports.scala`)**
    - `installStyle` (line 23) assumes `document.head` is non-null (throws in SVG/XML docs or early head scripts; fallback to `Option(document.head).getOrElse(document.documentElement)`).
    - `addToDocumentOnRegistration` (lines 37-45) creates empty `<style>` elements when `ss` renders to `""` (e.g., `addClassNames`-only sheets or empty `register()` calls), ignores custom `Renderer[HTMLStyleElement]` instances, and joins sheets with `"\n"` even under `formatTiny`.

---

## CSS Spec Completeness, `CanIUse` Wiring & API Polish

28. **Wire unused `CanIUse` subjects and clean up obsolete CSS properties (`Attrs.scala`, `Values.scala`, `ValueT.scala`, `Media.scala`, `Pseudo.scala`)**
    - 62 `CanIUse` subjects are unused, including `sticky` (not wired to `position`), `backgroundblendmode` (`backgroundBlendMode`), `backgroundClipText` (`backgroundClip` lacks `text`), `flexboxGap` (`gap`/`rowGap`/`columnGap`), `backgroundRepeatRoundSpace` (commented out at `Attrs.scala:320`), `css3CursorsGrab`, `prefersColorScheme`, `prefersReducedMotion`, `pointer`, `appearance`, `calc`, `viewportUnits` (several already listed in `TODO`).
    - Remove/deprecate obsolete draft properties (`offsetBlockStart`/`End`, `offsetInlineStart`/`End` renamed to `inset-*`; `gridAutoPosition`; `boxSizing.paddingBox`).
    - Fix public typo `RorderRadiusOps` (`Attrs.scala:2848, 2853, 2857`) -> `BorderRadiusOps` (with a deprecated type alias for binary/source compat).
    - Add missing CSS features/overloads: `ValueT.TypedAttrBase.revert` (`ValueT.scala:151-170`), `animationFillMode.none` (`Attrs.scala:180`), multi-value/comma-separated support on `animation*`/`transitionTimingFunction` and fractional `animationIterationCount`, `overflow.clip` and 2-arg `overflow(x, y)`, `Typed.TimingFunction` rule in `ValueT.Rules`, `TypedLiteralAliases.count`, decimal/angle-unit support in `ColorLiteral` modern `rgb`/`hsl`, `:root` / `:focus-visible` / `:focus-within` / `:is` / `:where` / `::placeholder` / `|=` / `*=` in `Pseudo.scala`, and `prefers-color-scheme` / `prefers-reduced-motion` / `pointer` / `hover` media features in `Media.scala`.
    - Resolve explicit code TODOs (`Pseudo.scala:6`, `Attrs.scala:788`, `Attrs.scala:1651`, `Data.scala:169`).

29. **Clean up `ModeMacros` duplication and config bugs (`core/{js,jvm}/src/main/scala-{2,3}/scalacss/internal/ModeMacros.scala`)**
    - `readConfig` is duplicated across all 4 files and places `.filter(_.nonEmpty)` *after* `orElse`, so an empty `-Dscalacss.mode=` prevents falling back to env vars.
    - `System.getenv("scalacss.mode")` uses a dot (not exportable in POSIX shells; consider also supporting `SCALACSS_MODE`).
    - Fix `"Unrecognise option..."` typo and use `_root_.scalacss.*` consistently in Scala 3 quotes (`scala-3/.../ModeMacros.scala:44,47` and `jvm/.../scala-3/...:36,39`).

---

## Build, Tooling & Tests

30. **Fix broken scripts in `bin/*` and `downstream-tests/run`, plus `misc/caniuse.scala`**
    - `bin/stats:11,16` and `bin/untyped:4` point to non-existent `core/src/main/scala/scalacss/Attrs.scala` (should be `core/shared/src/main/scala/scalacss/internal/Attrs.scala`) and use `grep -P` (fails on macOS BSD `grep`); `bin/stats:6-8` and `bin/todo-list:6` use `*/src` which misses `core/{shared,js,jvm}/src` and `ext-scalatags/{shared,js,jvm}/src`.
    - `bin/generate-platformjs:15` points to non-existent `core/src/main/scala-js/scalacss/js/PlatformJs.scala` (should be `core/js/src/main/scala/scalacss/js/PlatformJs.scala`).
    - `bin/ci:36` runs `free -h` under `set -euo pipefail`, which fails on macOS; `downstream-tests/run:18` uses Bash 4+ `declare -A`, which fails on macOS's default Bash 3.2.
    - `bin/generate-aliases:24-27` splits blocks on `\n[ \t]*\n+`, silently dropping consecutive declarations without a blank line (currently omits `fontVariantionSettings` at `Attrs.scala:1247`).
    - `misc/caniuse.scala:110-111` lists `"variables"` twice in `dataToIgnore`.

31. **Improve build configuration and test hygiene (`project/Build.scala`, `build.sbt`, `scalafix.sbt`, tests)**
    - `project/Build.scala:36` has `-Ywarn-value-discard` commented out (allowing bugs like #8 in `Macros.scala:237` to go unnoticed).
    - `build.sbt:11` disables `parallelExecution` globally (`ThisBuild / parallelExecution := false`) due to global mutable state in `NameGen.short` (`_nextShortPrefix`); isolating `NameGen.short` state per test or `Register` would allow parallel test execution.
    - `core/js/src/test/scala/scalacss/js/PlatformJsTest.scala:14-16` only calls `println(...)` and asserts nothing (`PlatformJs` / `PlatformJsEnv` behaviour is effectively untested).
    - `ErrorHandler.Noisy.badInput` (`Register.scala:299`) calls `s.domain.iterator.mkString(",")`, which can allocate huge strings / OOM on large ranges (truncating to e.g. `.take(20)` avoids this).
