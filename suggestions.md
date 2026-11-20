# ScalaCSS Codebase Suggestions

A prioritized, numbered list of bugs, optimisations, CSS/API gaps, build/tooling fixes, and test improvements found across the repository.

---

## Bugs & Correctness

6. **React SSR breaks CSS containing `>`, `&`, or quotes, and lacks `</style` breakout protection**
   - **Where:** `ext-react/src/main/scala/scalacss/React.scala:7-10`
   - **Issue:** `ScalaCssReactFns.createStyleTag` renders `<.styleTag(^.`type` := "text/css", styleStr)` as a normal React string child. During `ReactDOMServer` SSR, React HTML-escapes text children (`>` -> `&gt;`, `"` -> `&quot;`, `'` -> `&#x27;`, `&` -> `&amp;`), which browsers do **not** decode inside `<style>`, breaking child selectors (`> span`) and quoted strings (`content`, `font-family`). It also lacks `</style` escaping that `ScalatagsTextRenderer` has (`ScalatagsText.scala:22`).
   - **Fix:** Render via `^.dangerouslySetInnerHtml := safeCss` with case-insensitive `</(?=style)` escaped to `\00003C/` matching `ScalatagsTextRenderer`.

7. **`ScalatagsTextImplicits.styleaToTextTag` adds a spurious leading space to `class` attributes**
   - **Where:** `ext-scalatags/shared/src/main/scala/scalacss/ScalatagsText.scala:12-14`
   - **Issue:** `t.appendAttr("class", Builder.GenericAttrValueSource(" " + s.htmlClasses))` prepends `" "` even though Scalatags' `Builder.appendAttr` already joins multiple `class` modifiers with `" "`, producing `class=" Foo-a"` (or double spaces when chaining styles, currently asserted in `ScalatagsTest.scala:90, 96, 102`).
   - **Fix:** Pass `s.htmlClasses` directly without `" " +` (and update `ScalatagsTest.scala`).

9. **`StyleSheetRegistryJsOps.addToDocumentOnRegistration` ignores `Renderer[HTMLStyleElement]` and inserts empty `<style>` elements**
   - **Where:** `core/js/src/main/scala/scalacss/defaults/PlatformExports.scala:31-45`
   - **Issue:** `addToDocumentOnRegistration` requires only `Renderer[String]`, bypassing custom `Renderer[HTMLStyleElement]` instances (e.g. adding a CSP `nonce` attribute) that `StyleSheetInlineJsOps.addToDocument` respects. It also creates and appends `<style>` tags even when stylesheets produce empty CSS (or `"\n"`-joined empty strings across `addClassName`-only stylesheets), and renders each sheet separately rather than merging shared media queries across the batch.
   - **Fix:** Skip empty CSS output before `installStyle` in both `addToDocument` and `addToDocumentOnRegistration`, and accept/use `Renderer[HTMLStyleElement]` (or combine `Css` before rendering in `addToDocumentOnRegistration`).

12. **`StringRenderer.Default` / `Css.mapByMediaQuery` can invert `@media` cascade order**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/Renderer.scala:18-33`, `core/shared/src/main/scala/scalacss/internal/Css.scala:91-98`
    - **Issue:** `Css.mapByMediaQuery` groups rules by `CssMediaQueryO` into a `ListMap` ordered by *first* appearance. If rule `.a` uses `@media (min-width:900px)` and rule `.b` uses `min-width:600px` followed by `min-width:900px`, `.b`'s `900px` rule is hoisted before its `600px` rule, reversing responsive override order.
    - **Fix:** Document this behavior clearly (pointing to `StringRenderer.Raw` when relative `@media` order matters), or merge only adjacent rules with matching media queries.

14. **`FontFace.quoteIfNeeded` misses non-whitespace special characters and multi-quoted inputs**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/Data.scala:85-92`
    - **Issue:** `quoteIfNeeded` only quotes when `s.exists(_.isWhitespace) && !isQuoted`, and `isQuoted` only checks matching leading/trailing `'` or `"`. Unquoted names like `""`, `"123font"`, `"Font,Name"`, or `"inherit"` are emitted unquoted, while `"'Foo' 'Bar'"` passes `isQuoted` and emits invalid CSS.
    - **Fix:** Emit unquoted only if `s` is a valid single CSS `<custom-ident>` (and not a CSS-wide keyword) or a properly single-quoted/double-quoted CSS string without unescaped internal quotes.

16. **`Domain.IntoSeq.apply` and `Domain.Pair.apply` lack proper bounds checks**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/Domain.scala:76-84, 91-121`
    - **Issue:** When `as > 0`, `IntoSeq.apply(i0)` never checks `0 <= i0 && i0 < size` (silently wrapping out-of-range indices via `i % as`, or indexing `r(-1)` if `r` is empty). `Pair.apply(i)` only checks `as == 0`, not `size == 0` (when `b.size == 0`).
    - **Fix:** Check `if (i0 < 0 || i0 >= size) throw new IndexOutOfBoundsException(...)` in `IntoSeq.apply` and check `size == 0` (or bounds) in `Pair.apply`.

---

## Performance, Memory & Scala.js Bundle Size

25. **`Platform.env` and `PlatformJsEnv` recompile regexes and recompute `Env` on every render**
    - **Where:** `core/js/src/main/scala/scalacss/internal/Platform.scala:13-14`, `core/js/src/main/scala/scalacss/js/PlatformJsEnv.scala:13, 38-75`
    - **Issue:** `Platform.env` is a `def` calling `PlatformJsEnv.value` (also uncached), and `parseUserAgent` uses `re.r.findFirstIn(ua).isDefined` — compiling ~15–20 Scala `Regex`es and constructing a fresh `Env` (recomputing `Env.agents` `lazy val`) on every `.render` / `.css` call.
    - **Fix:** Precompile patterns as `private val` `js.RegExp` instances (zero per-call regex/match overhead and keeps `scala.util.matching.Regex` out of `PlatformJsEnv`) and memoize the last `(ua, Env)` pair (or cache `Platform.env`).

26. **`ValueT` boxing and per-call-site `Rule` allocations**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/ValueT.scala:15-28, 74-155`
    - **Issue:** `ValueT` is a heap-allocated `final case class` (note the commented-out zero-cost representation at lines 21–28), and `Rule.retype`, `Rule.literal`, `Rule.>>`, and primitives in `ValueT.Rules` allocate fresh `Rule` objects/closures at call sites. Similarly, `DslAttrT.:=!` (`Dsl.scala:122-124`) lacks `@inline`, and Scala 3 ignores `@specialized` on `Length`/`Percentage`/`Resolution` (`Data.scala:153, 173, 194`).
    - **Fix:** Represent `ValueT` as an unboxed tagged type / Scala 3 `opaque type` (or at least `final class ... extends AnyVal` without `case` boilerplate), cache singleton `Rule` instances for `retype`/`literal`/primitive rules, and add `@inline` to `DslAttrT.:=!`.

27. **Inefficient `String.format` in `Util.quote`, `UnicodeRange`, and `NameGen.IncFmt`**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/Util.scala:19`, `core/shared/src/main/scala/scalacss/internal/Data.scala:34`, `core/shared/src/main/scala/scalacss/internal/mutable/Register.scala:255`
    - **Issue:** `.format(...)` / `String.format` pulls `java.util.Formatter` into Scala.js bundles and adds formatting overhead in `Util.quote` (run on `@font-face` / attribute selectors) and `UnicodeRange.toString`.
    - **Fix:** Replace `"%06X".format(cp)` and `"U+%x-%x".format(...)` with direct hex character / `Integer.toHexString` formatting.

29. **JVM `Mutex.apply` closure overhead and lock-ordering / listener retention in `GlobalRegistry`**
    - **Where:** `core/jvm/src/main/scala/scalacss/internal/mutable/Mutex.scala:9-12`, `core/shared/src/main/scala/scalacss/internal/mutable/GlobalRegistry.scala:45-75`
    - **Issue:** Unlike JS `Mutex`, JVM `Mutex.apply` is not `@inline` (allocating `Function0` closures on nested `Register` calls and using `synchronized(this)`, which pins JDK 21+ virtual threads during callbacks). In `StyleSheetRegistry`, `registerListenerN` invokes `f(existing)` before returning the unregister function (preventing self-unregistration during replay), `onRegistration`/`onRegistrationN` discard the unregister handle forever, and both `GlobalRegistry` and `Register` retain all stylesheet definitions indefinitely after startup CSS injection with no optional release/reset mechanism.
    - **Fix:** Mark JVM `Mutex.apply` `@inline` (or use `ReentrantLock` if virtual-thread safety matters), document the `callbackMutex -> mutex` lock order, and consider exposing a post-render release/dispose hook if users wish to reclaim `Register` / `StyleA.style` memory after SSR/startup injection.

---

## CSS Spec Compliance & API / Macro Gaps

33. **`ColorLiteral` CSS Color 4 gaps (both Scala 2 and Scala 3)**
    - **Where:** `core/shared/src/main/scala-2/scalacss/internal/Macros.scala:84-99`, `core/shared/src/main/scala-3/scalacss/internal/Macros.scala:120-135` (plus Scaladoc typo `"validates Color"` at Scala 2 line 270 / Scala 3 line 14)
    - **Issue:** `modernA` and `modernSL` don't accept `none` (unlike `modernNPN`/`modernH`), `rgbM`/`hslM` reject `rgba(...)` and `hsla(...)` with space-separated syntax, legacy `hsl`/`hsla` require integer hue and decimal alpha, and CSS Color 4 functions (`oklch`, `oklab`, `lch`, `lab`, `hwb`, `color(...)`) are unsupported.
    - **Fix:** Allow `none` in `modernA`/`modernSL`, `(?:rgb|rgba)` and `(?:hsl|hsla)` in modern syntax, decimal/angle hue and `%` alpha in legacy syntax, and add validators for modern color functions.

35. **Modern CSS features / units / pseudos**
    - **Where:** `Data.scala:134-151`, `Pseudo.scala`
    - **Issues:**
      - Remaining modern CSS features/units to consider: `Percentage` arithmetic (`*`, `/`, `-`), and `Pseudo` (`:has`, `:any-link` for `CanIUse.anyLink`, `:placeholder-shown`, `::marker`, `::backdrop`, multi-arg `Not`, case-insensitive attribute flags).
      - Also `TODO:1-6` still tracks wiring `CanIUse.viewportUnits` (`CanIUse.scala:2953`), `CanIUse.mediaResolution` (`CanIUse.scala:1702`), and `calc` (`misc/caniuse.scala:55`).

---

## Build, Tooling & Scripts

36. **`sourceMapsToGithub` computes broken URLs for cross-projects (`coreJS`, `extScalatagsJS`)**
    - **Where:** `project/Lib.scala:56`
    - **Issue:** `p.base.toURI.toString.replaceFirst("[^/]+/?$", "")` strips only one path segment. For `coreJS` (`core/js/`) and `extScalatagsJS` (`ext-scalatags/js/`), it emits `.../core/` and `.../ext-scalatags/` instead of the repo root, dropping `core/` and `ext-scalatags/` from published GitHub source-map URLs (causing 404s).
    - **Fix:** Use `(LocalRootProject / baseDirectory).value.toURI.toString` as the source-map prefix `a`.

37. **`downstream-tests/run` fails on macOS Bash 3.2 and leaves `$args` unquoted**
    - **Where:** `downstream-tests/run:18-24, 114`
    - **Issue:** Uses Bash 4+ `declare -A all_tests`, which fails on macOS's default `/bin/bash` (Bash 3.2), breaking local runs of `downstream-tests/run` and `bin/ci:57`. Line 114 also relies on unquoted word-splitting for `$args`.
    - **Fix:** Replace the associative array with positional lookup into `all_tests_values` (`$((t - 1))`) and parse flags cleanly into an array.

38. **`bin/ci` portability, incomplete clean glob, and duplicate JVM test execution**
    - **Where:** `bin/ci:36, 40, 46-51`
    - **Issue:** `free -h` fails on macOS under `set -euo pipefail`; `rm -rf */target/...` skips `*/*/target/...` (`core/{js,jvm}`, `ext-scalatags/{js,jvm}`); and lines 48–49 run `test` twice across all projects even though `FullOptStage` only applies to `rootJS`.
    - **Fix:** Guard `free -h` (`command -v free >/dev/null && free -h || true`), expand clean globs to `{*,*/*}/target/scala-*/{,test-}classes`, and scope the `FullOptStage` run to `rootJS / test`.

39. **`bin/todo-list`, `bin/todo-count`, `bin/todo` miss cross-projects and use GNU `awk`**
    - **Where:** `bin/todo-list:6-9` (and sibling `bin/todo-count`, `bin/todo`)
    - **Issue:** `find project */src` only matches `project/` and `ext-react/src/`, skipping all `core/{shared,js,jvm}/src` and `ext-scalatags/{shared,js,jvm}/src` files (so all `TODO`s in `core` are missed and `xargs fgrep` exits `123`), and 3-arg `match($1, regex, a)` fails on macOS BSD `awk`.
    - **Fix:** Search `project */src */*/src` with `grep -rn` / POSIX `awk`.

40. **`bin/generate-aliases` Scala 3 / `scala-cli` compatibility and comment/whitespace bugs**
    - **Where:** `bin/generate-aliases:10, 19-21, 28-31, 42-51`
    - **Issue:** Uses `.toIterator` (removed in Scala 3 when invoked with `scala-cli`), leaves `Source.fromFile` unclosed, skips non-`[a-z]` identifiers on `r5`, and emits a leading blank line + extra space before `// Note:` comments in `generated.scala`.
    - **Fix:** Use `.iterator` and `scala.util.Using`, fix `commentr` / `stripPrefix`, and clean up the header formatting in `attrAliases`.

41. **`bin/generate-caniuse`, `misc/caniuse.scala`, and `misc/copy-nyaya-domain.sh` issues**
    - **Where:** `bin/generate-caniuse:1-21`, `misc/caniuse.scala:18, 22, 31-34, 293-294`, `misc/copy-nyaya-domain.sh:5, 11`
    - **Issue:**
      - `bin/generate-caniuse` lacks `set -euo pipefail` and leaves submodule `--init` commented out.
      - `misc/caniuse.scala` doesn't close `Source.fromFile`, only escapes `<`/`>` in `htmlsafe` (not `&` or `*/`), and uses deprecated `.toStream`/`.mapValues`.
      - `misc/copy-nyaya-domain.sh` targets the obsolete single-platform path `core/src/main/scala/scalacss/Domain.scala` (`package scalacss`).
    - **Fix:** Fix error handling/submodule init in `bin/generate-caniuse`, escape `&` and `*/` and close `Source` in `misc/caniuse.scala`, and update paths in `misc/copy-nyaya-domain.sh`.

42. **`bin/stats` & `bin/untyped` count test/downstream/deprecated lines inaccurately**
    - **Where:** `bin/stats:7-9, 17-19`
    - **Issue:** `*/src/main` matches `downstream-tests/js/src/main` while `*/*/src` / `*/src` in Total includes `bench/`, so `main + test != Total`. Both scripts also count `@deprecated` attributes (`fontVariantionSettings`, `gridAutoPosition`, `offsetBlock*`, `offsetInline*`) as active attributes.
    - **Fix:** Restrict globs to `{core,ext-scalatags,ext-react}` and filter `@deprecated` declarations.

43. **Build configuration & compiler warning hygiene (`Build.scala`, `build.sbt`, `Bench.scala`, `scalafix.sbt`, `ModeMacros`)**
    - **Where:** `project/Build.scala:20-51, 69`, `build.sbt:11`, `project/Bench.scala:3, 32-33, 44-45, 102-103`, `scalafix.sbt`, `core/{js,jvm}/src/main/scala-3/scalacss/internal/ModeMacros.scala`
    - **Issues:**
      - `Build.scala` comments out `-Ywarn-value-discard` (line 36), passes `-source 3.0-migration` (line 40, also `downstream-tests/build.sbt:52`), uses obsolete `withCachedResolution(true)` (line 51), misses `-Xlint`/`-Wunused:all`/`-Wvalue-discard`, and configures `rootJS` with `commonSettings.jvm` (line 69) instead of `.js`.
      - `build.sbt:11` disables `parallelExecution` across the whole build because `NameGenTest` mutates global `NameGen._nextShortPrefix` concurrently with `DefaultsTest.Prod` (`NameGenTest.scala:13, 19, 47-48`). Passing an explicit `prefix2` in `NameGenTest` / isolating `DefaultsTest` allows parallel test execution.
      - `Bench.scala` uses deprecated `fastOptJS`/`fullOptJS` tasks and unused `ScalaJSPluginInternal`/`Stage` imports.
      - `scalafix.sbt` adds `-Yrangepos` without enabling `semanticdbEnabled` or running `scalafixAll --check` in CI.
      - Cross-platform SSR pitfall in `ModeMacros`: when `scalacss.mode` is unset, JS `fullLinkJS` switches to `ProdDefaults` via `LinkingInfo.developmentMode` while JVM always defaults to `DevDefaults`, silently mismatching SSR HTML vs client JS class names unless `-Dscalacss.mode=prod` is passed on JVM. Also `scala-3` `ModeMacros` wrap expressions in a redundant `Inlined(None, Nil, ...)`, and `.toLowerCase` omits `Locale.ROOT`.

---

## Test Coverage & Hygiene

44. **Missing JVM-only tests and DOM injection tests**
    - **Where:** `core/jvm/src/test` (absent), `core/js/src/main/scala/scalacss/defaults/PlatformExports.scala`, `ext-react/src/test/scala/scalacss/ReactTest.scala`, `ext-scalatags/js/src/test/scala/scalacss/ScalatagsJsDomTest.scala`
    - **Issue:** There are zero tests in `core/jvm` (for JVM `Mutex`, `Platform.env`, and JVM `ModeMacros.devOrProdDefaults`). On JS, `addToDocument()`, `createStyleElement`, `installStyle`, `StyleElementRenderer`, and `addToDocumentOnRegistration` have no unit tests in `core/js`, `ReactTest` lacks SSR escaping (`>`, `"`, `&`) and `ProdDefaults`/`StyleF` coverage (and appends `<style>` elements without DOM cleanup), and `downstream-tests` never defines/renders an actual stylesheet or tests `SCALACSS_MODE` / `ext-react` / `ext-scalatags` / JVM `devOrProdDefaults`.
    - **Fix:** Add targeted tests for `PlatformExports` DOM installation/skipping-empty, JVM `ModeMacros`/`Mutex`, React SSR `<style>` escaping, `ScalatagsTest` `</style>` + `ProdDefaults`, and expand `downstream-tests` to exercise actual rendered styles on both JS and JVM.

45. **Untested `core/shared` components and commented-out property tests**
    - **Where:** `Domain.scala`, `NonEmptyVector.scala`, `Need.scala`, `StyleLookup.scala`, `GlobalRegistry.scala` (`clearListeners`), `Register.scala` (`ErrorHandler.noisy`/`silent`, post-render warnings), `Renderer.scala` (`StringRenderer.Raw` on non-empty CSS), `AttrTest.scala:49`, `ComposeTest.scala:13-16`, `RandomData.scala:62`
    - **Issue:** `Domain`, `Need`, `NonEmptyVector`, and `StyleLookup` have no dedicated unit suites; `AttrTest`'s triplet law, `ComposeTest`'s associativity law, and media queries in `RandomData` are commented out / marked `TODO`; and `StringRenderer.Raw` is only tested on `Vector.empty` (`RendererTest.scala:26-34`).
    - **Fix:** Re-enable the commented property tests (adding media queries to `RandomData`), add focused unit tests for `Domain` (edge cases like `size == 0` / out-of-bounds / `IntoSeq`), `StringRenderer.Raw`, `GlobalRegistry.clearListeners`, and `Register` error handlers.
