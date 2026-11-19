# ScalaCSS Codebase Suggestions

A prioritized, numbered list of bugs, optimisations, CSS/API gaps, build/tooling fixes, and test improvements found across the repository.

---

## Bugs & Correctness

1. **`NameGen.short` can collide across stylesheets**
   - **Where:** `core/shared/src/main/scala/scalacss/internal/mutable/Register.scala:295-330`
   - **Issue:** `NameGen.short` builds `fullPrefix = "_" + nextShortPrefix()` (which emits `"b"`, `"c"`, ..., `"z"`, `"aa"`, ..., `"ba"` at index 52) and directly concatenates `Alphabet.code(Nil, n)` using `nmchar7` (`'0'..'9'`, `'a'..'z'`, `'-'`, `'_'`). Because there is no delimiter between `prefix2` and the generated code and their alphabets overlap on `'a'..'z'`, stylesheet #1 (`prefix2 = "b"`, `n = 418` -> `code = "a0"`) and stylesheet #52 (`prefix2 = "ba"`, `n = 0` -> `code = "0"`) both generate `._ba0`. Each stylesheet owns an independent `Register._taken` set, so the collision goes undetected.
   - **Fix:** Add a delimiter between `prefix2` and the suffix that cannot appear in `prefix2` (e.g. `s"$p$p2-"` when `p2.nonEmpty`, or encode `prefix2` using uppercase `'A'..'Z'`).

4. **`MacroName.Use`, `registerF2`, and `NameGen.IncFmt` can emit invalid CSS class identifiers**
   - **Where:** `core/shared/src/main/scala/scalacss/internal/mutable/Register.scala:123, 220-234, 253-259`
   - **Issue:** `NameGen.IncFmt` with `ClassNameHint("")` and default format `"%s-%04d"` generates `-0001`, and `MacroName.Use.apply(ClassNameHint(""), "1foo")` / `registerF2` (`manualName = "1foo"`) only replace illegal characters without ensuring a valid CSS identifier start (unlike `StyleSheet.mkClassNameHint` at `StyleSheet.scala:23-35`). Leading digits or `-` followed by a digit produce invalid CSS selectors (`.1foo`, `.-0001`).
   - **Fix:** Reuse the leading-character validation/prefixing logic (e.g. prepend `"_"` if starting with `[0-9]` or `-[0-9]`, or `"-"`) across `MacroName.Use`, `registerF2`, and `IncFmt`.

5. **`Compose.apply` drops overriding values when they appear inside an existing fallback list**
   - **Where:** `core/shared/src/main/scala/scalacss/internal/Compose.scala:32-49`
   - **Issue:** For `k ==* a`, `isConflict` returns `false` whenever every value in `nvs` is already contained anywhere in `kvs`. If `into` has `color -> [red, blue]` (`blue` wins) and `newAVs` has `color -> [red]` (or `[blue, red]`), `isConflict` returns `false`, `case None` filters out `nvs` because all items are already in `ovs`, and `blue` wrongly stays the winning declaration instead of `red`.
   - **Fix:** Treat `k ==* a` as non-conflicting only when `kvs` and `nvs` have identical values in order (or at minimum end with the same winning sequence), and otherwise delegate to `rules.mergeAVs`.

6. **React SSR breaks CSS containing `>`, `&`, or quotes, and lacks `</style` breakout protection**
   - **Where:** `ext-react/src/main/scala/scalacss/React.scala:7-10`
   - **Issue:** `ScalaCssReactFns.createStyleTag` renders `<.styleTag(^.`type` := "text/css", styleStr)` as a normal React string child. During `ReactDOMServer` SSR, React HTML-escapes text children (`>` -> `&gt;`, `"` -> `&quot;`, `'` -> `&#x27;`, `&` -> `&amp;`), which browsers do **not** decode inside `<style>`, breaking child selectors (`> span`) and quoted strings (`content`, `font-family`). It also lacks `</style` escaping that `ScalatagsTextRenderer` has (`ScalatagsText.scala:22`).
   - **Fix:** Render via `^.dangerouslySetInnerHtml := safeCss` with case-insensitive `</(?=style)` escaped to `\00003C/` matching `ScalatagsTextRenderer`.

7. **`ScalatagsTextImplicits.styleaToTextTag` adds a spurious leading space to `class` attributes**
   - **Where:** `ext-scalatags/shared/src/main/scala/scalacss/ScalatagsText.scala:12-14`
   - **Issue:** `t.appendAttr("class", Builder.GenericAttrValueSource(" " + s.htmlClasses))` prepends `" "` even though Scalatags' `Builder.appendAttr` already joins multiple `class` modifiers with `" "`, producing `class=" Foo-a"` (or double spaces when chaining styles, currently asserted in `ScalatagsTest.scala:90, 96, 102`).
   - **Fix:** Pass `s.htmlClasses` directly without `" " +` (and update `ScalatagsTest.scala`).

8. **`PlatformExports.installStyle` crashes when `document.head` is `null` or `undefined`**
   - **Where:** `core/js/src/main/scala/scalacss/defaults/PlatformExports.scala:22-23`
   - **Issue:** `document.head appendChild style` throws a `TypeError` in SVG/XML documents (where `document.head` is `undefined`) or minimal/early HTML documents without `<head>`.
   - **Fix:** Check `!js.isUndefined(head) && (head ne null)`, falling back to `document.documentElement`.

9. **`StyleSheetRegistryJsOps.addToDocumentOnRegistration` ignores `Renderer[HTMLStyleElement]` and inserts empty `<style>` elements**
   - **Where:** `core/js/src/main/scala/scalacss/defaults/PlatformExports.scala:31-45`
   - **Issue:** `addToDocumentOnRegistration` requires only `Renderer[String]`, bypassing custom `Renderer[HTMLStyleElement]` instances (e.g. adding a CSP `nonce` attribute) that `StyleSheetInlineJsOps.addToDocument` respects. It also creates and appends `<style>` tags even when stylesheets produce empty CSS (or `"\n"`-joined empty strings across `addClassName`-only stylesheets), and renders each sheet separately rather than merging shared media queries across the batch.
   - **Fix:** Skip empty CSS output before `installStyle` in both `addToDocument` and `addToDocumentOnRegistration`, and accept/use `Renderer[HTMLStyleElement]` (or combine `Css` before rendering in `addToDocumentOnRegistration`).

10. **`PlatformJsEnv.readUserAgent` / `parseUserAgent` unsafe null/`undefined` handling and UA matching gaps**
    - **Where:** `core/js/src/main/scala/scalacss/js/PlatformJsEnv.scala:26-40, 49, 58`
    - **Issue:**
      - Line 30 uses `Option(nav.userAgent)` on `dom.Navigator.userAgent`; if a partial/mocked `navigator` has `userAgent === undefined`, `Option(undefined)` is `Some(undefined)` in Scala.js and `ua.nonEmpty` throws. Calling `parseUserAgent(null)` / `fromUserAgent(null)` throws `NullPointerException` on `ua.isEmpty`.
      - Line 49 (`\b(?:BB10|PlayBook|BlackBerry)\b`) misses legacy `BlackBerry9700/...` UAs (no word boundary after `BlackBerry`), and line 58 (`\bOpera Mobi\b`) misses `Opera Mobile/...` UAs (misclassifying them as desktop `Opera` at line 62).
    - **Fix:** Guard `nav.userAgent` as `UndefOr[String]` (and `(ua eq null) || ua.isEmpty`), and update the regexes to `\b(?:BB10\b|PlayBook\b|BlackBerry)` and `\bOpera Mobi(?:le)?\b`.

11. **`CanIUse2.PrefixApply.keywords` can throw on `$`/`\`, rewrites `url(...)`/strings, and drops combined prefixes**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/CanIUse2.scala:17-22, 137-148`
    - **Issue:**
      - `r2.replaceAllIn(in, m => m.group(1) + p.prefix + m.group(2))` does not quote `m.group(1)` with `Regex.quoteReplacement`. Since `[^a-zA-Z0-9_-]` matches `$` and `\`, values like `"$linear-gradient(...)"` or `"\\linear-gradient(...)"` trigger `IllegalArgumentException` on JVM or drop `\`.
      - Unlike `CanIUse2.prefixed` (line 110), `keywords` does not strip/skip `url(...)` or quoted strings, rewriting e.g. `url(linear-gradient.png)` into `url(-webkit-linear-gradient.png)`.
      - Chaining `a * b * c` in `backgroundImageTransforms` cannot emit a single declaration with both functions prefixed when a `background-image` uses two separately-prefixed functions (e.g. `linear-gradient` + `image-set`), because the first prefixed output short-circuits subsequent steps via `prefixed(tgt)`.
    - **Fix:** Escape `m.group(1)` with `Regex.quoteReplacement` (or use index slicing), ignore `url(...)` and quoted spans in `keywords`, and rethink multi-keyword chaining so multiple prefixable functions in one value compose cleanly.

12. **`StringRenderer.Default` / `Css.mapByMediaQuery` can invert `@media` cascade order**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/Renderer.scala:18-33`, `core/shared/src/main/scala/scalacss/internal/Css.scala:91-98`
    - **Issue:** `Css.mapByMediaQuery` groups rules by `CssMediaQueryO` into a `ListMap` ordered by *first* appearance. If rule `.a` uses `@media (min-width:900px)` and rule `.b` uses `min-width:600px` followed by `min-width:900px`, `.b`'s `900px` rule is hoisted before its `600px` rule, reversing responsive override order.
    - **Fix:** Document this behavior clearly (pointing to `StringRenderer.Raw` when relative `@media` order matters), or merge only adjacent rules with matching media queries.

13. **`Css.keyframes` / `FormatSB` silently drop `Cond` and `unsafeExts` selectors**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/Css.scala:39-45`, `core/shared/src/main/scala/scalacss/internal/Renderer.scala:83-93`
    - **Issue:** `FormatSB` flattens `styles.flatMap(_.content.whole)` into the keyframe stop, silently stripping any media query, pseudo-selector, or `unsafeChild` selector on a keyframe `StyleA` while dumping its declarations directly into the keyframe.
    - **Fix:** Warn or reject non-empty `mq`, pseudos, and `unsafeExts` inside `keyframes` (or narrow keyframe DSL builders to static declarations).

14. **`FontFace.quoteIfNeeded` misses non-whitespace special characters and multi-quoted inputs**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/Data.scala:85-92`
    - **Issue:** `quoteIfNeeded` only quotes when `s.exists(_.isWhitespace) && !isQuoted`, and `isQuoted` only checks matching leading/trailing `'` or `"`. Unquoted names like `""`, `"123font"`, `"Font,Name"`, or `"inherit"` are emitted unquoted, while `"'Foo' 'Bar'"` passes `isQuoted` and emits invalid CSS.
    - **Fix:** Emit unquoted only if `s` is a valid single CSS `<custom-ident>` (and not a CSS-wide keyword) or a properly single-quoted/double-quoted CSS string without unescaped internal quotes.

15. **`Cond.applyToStyle` iterates unordered `s.data`**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/Cond.scala:37`
    - **Issue:** `s.data.foldLeft(init)` iterates `Map[Cond, AVs]` in hash order rather than `s.dataSorted`. When two distinct `oldCond` entries collapse to the same `this & oldCond` (e.g. `Cond.empty` and `Hover` under `hover(...)`) and `s.data.size >= 5`, property override order is non-deterministic.
    - **Fix:** Iterate `s.dataSorted`.

16. **`Domain.IntoSeq.apply` and `Domain.Pair.apply` lack proper bounds checks**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/Domain.scala:76-84, 91-121`
    - **Issue:** When `as > 0`, `IntoSeq.apply(i0)` never checks `0 <= i0 && i0 < size` (silently wrapping out-of-range indices via `i % as`, or indexing `r(-1)` if `r` is empty). `Pair.apply(i)` only checks `as == 0`, not `size == 0` (when `b.size == 0`).
    - **Fix:** Check `if (i0 < 0 || i0 >= size) throw new IndexOutOfBoundsException(...)` in `IntoSeq.apply` and check `size == 0` (or bounds) in `Pair.apply`.

17. **`NaturalOrdering` uses `Character.isDigit` (matching non-ASCII Unicode digits)**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/NaturalOrdering.scala:16, 29-30, 51-52`
    - **Issue:** `Character.isDigit` matches non-ASCII decimal digits (such as Arabic-Indic `\u0660`–`\u0669`) whose numeric values aren't handled consistently with `'0'` leading-zero checks and raw UTF-16 comparisons, and in Scala.js pulling `Character.isDigit` bloats the output with Unicode category tables.
    - **Fix:** Replace `Character.isDigit(c)` with `c >= '0' && c <= '9'`.

18. **`Need.value` clears `_thunk` before invoking `t()`**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/Need.scala:5-9`
    - **Issue:** `_thunk = null` happens before `t()` runs. If `t()` throws once, retrying `lazy val value` throws `NullPointerException` instead of re-invoking/preserving the failure.
    - **Fix:** Evaluate `val res = t()` before setting `_thunk = null` (or wrap cleanly if one-shot failure is desired).

19. **Scala 3 `Dsl.nameQuoted` strips all `$` characters indiscriminately**
    - **Where:** `core/shared/src/main/scala-3/scalacss/internal/Macros.scala:5, 57-58` (compare with `core/shared/src/main/scala-2/scalacss/internal/Macros.scala:16-35`)
    - **Issue:** Scala 3's `nameQuoted` has an unused `import scala.reflect.NameTransformer` and applies `s.replace("$", "")`, stripping literal `$` from identifiers like ``val `$btn` = style(...)`` instead of decoding / trimming only trailing module-class `$` markers.
    - **Fix:** Remove the unused import and strip `$` only at symbol segment boundaries (e.g. `s.replaceAll("\\$(?=\\.|$)", "")`).

20. **`backgroundOrigin` and `backgroundSize` omit `CanIUse.backgroundImgOpts`**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/Attrs.scala:371, 397` (compare with `backgroundClip` at `316-326`)
    - **Issue:** `CanIUse.backgroundImgOpts` covers `background-clip`, `background-origin`, and `background-size`, but only `backgroundClip` wires it up; `backgroundOrigin` and `backgroundSize` omit the transform. Also `overflowX`/`overflowY` (`1920`, `1929`) omit `Transform keys CanIUse.overflow`, and `alignContent` (`84`) omits `CanIUse.justifyContentSpaceEvenly` (unlike `justifyContent` at `1537`).
    - **Fix:** Add the corresponding `Transform`s to `backgroundOrigin`, `backgroundSize`, `overflowX`, `overflowY`, and `alignContent`.

21. **`columnCount` accepts `Number` (floats) instead of `Integer`; `outlineStyle` allows `hidden`**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/Attrs.scala:768-771, 1847-1850`, `core/shared/src/main/scala/scalacss/internal/ValueT.scala:355-363`
    - **Issue:** `columnCount` extends `TypedAttrT1[Number]` (allowing invalid fractional `column-count: 2.5`), and `outlineStyle` extends `TypedAttr_BrStyle` which inherits `def hidden` from `BrStyleOps` even though `hidden` is invalid for CSS `outline-style`.
    - **Fix:** Change `columnCount` to `TypedAttrT1[Integer]`, and separate an `OutlineStyle` value trait/class from `BrStyleOps` without `hidden`.

22. **`Double` formatting differs between JVM and Scala.js in `ruleNumber_D` and `Length.value`**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/ValueT.scala:60-63, 112`, `core/shared/src/main/scala/scalacss/internal/Data.scala:154-157, 174, 195`
    - **Issue:** `ruleNumber_D` uses `Rule(_.toString)` instead of `ValueT.fmtDouble`, and `Length.value` only drops `.0` for `0` (while `Percentage.value` and `Resolution.value` use `n.toString`). On JVM `(2.0).toString` is `"2.0"`, whereas on JS it is `"2"`.
    - **Fix:** Normalize integer-valued `Double`s via `ValueT.fmtDouble` across `ruleNumber_D`, `Length.value`, `Percentage.value`, and `Resolution.value` (avoiding `stripSuffix` / `dropRight` in `fmtDouble` / `ruleTime_FD` where `substring(0, s.length - 2)` suffices).

---

## Performance, Memory & Scala.js Bundle Size

23. **`Transform.prefix` recomputes `PrefixPlan` on every `AV` evaluation**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/Attr.scala:122-130`
    - **Issue:** `var pp: CanIUse2.PrefixPlan = null` is allocated **inside** the `e => ...` lambda returned to `Transform.attrGen`. Every `AV.apply(env)` during CSS rendering calls `run(e)`, allocating a fresh `pp` and re-scanning all 16 `Agent`s in `subject` per declaration instead of caching across `AV`s sharing the same `Env.agents`.
    - **Fix:** Cache the last `(agents, PrefixPlan)` pair outside `e => ...` on the `Transform` closure (or key a small cache on `subject`/`e.agents`), keeping retained memory minimal while avoiding repeated scans during stylesheet rendering.

24. **`CanIUse` `val`s vs `def`s and repeated `Attr.real` allocation on untyped properties**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/CanIUse.scala` (12 eager `val`s e.g. lines `100, 325, 350, 650, 975, 1325, 1625, 1727, 1827, 2353, 2478, 2828`), `misc/caniuse.scala:261-273`, `core/shared/src/main/scala/scalacss/internal/Attrs.scala` (e.g. `304, 311, 313-326, 626, 633`)
    - **Issue:**
      - `object Attrs` initializes `backgroundClipTextKeyword` and `backgroundClipTransform` eagerly even when `backgroundClip` is never used, and 12 `CanIUse` subjects are eager `val`s (retaining 12 browser-support maps and preventing Scala.js DCE when unused).
      - Meanwhile, untyped attributes defined as `final def` with `Transform keys CanIUse.foo` (e.g. `boxShadow`, `backgroundBlendMode`, `boxReflect`) rebuild the `CanIUse` `Map` + `Set`s + `Transform` + `Attr` every time they are referenced in a style.
    - **Fix:** Make all `CanIUse` subjects `def`s (for DCE and zero retained heap on `CanIUse$`), move `backgroundClipTextKeyword`/`backgroundClipTransform` into `object backgroundClip`, and back transformed attributes that are called repeatedly with `private lazy val` (or `object`) if initialization cost matters, without keeping unused subjects alive.

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

28. **Small avoidable allocations & quadratic folds in rendering/composition**
    - **Where & Issues:**
      - `core/shared/src/main/scala/scalacss/internal/AVs.scala:69-71`: `filterKeys` runs predicate `f` twice per entry (once on `order.whole`, once on `data.iterator`), doubling `Attr.cmp` conflict checks in `Compose.mergeAVs`.
      - `core/shared/src/main/scala/scalacss/internal/Compose.scala:66`: `Compose.apply` calls `b.dataSorted` only to fold into an unordered `Map` (sorting unnecessarily, especially in `Compose.trust`).
      - `core/shared/src/main/scala/scalacss/internal/Css.scala:39-45, 91-98`: `keyframes` and `mapByMediaQuery` update `ListMap` in a fold ($O(N \cdot M)$); using a local `mutable.LinkedHashMap` inside those methods would be linear.
      - `core/shared/src/main/scala/scalacss/internal/StyleA.scala:24-25`: `htmlClasses` builds iterators and a `StringBuilder` even when `addClassNames.isEmpty` (fast-path `if (addClassNames.isEmpty) className.value`).
      - `core/shared/src/main/scala/scalacss/internal/NonEmptyVector.scala:39-40`: `++(b: NonEmptyVector[B])` allocates `b.whole` instead of appending `b.head` and `b.tail`.
      - `core/shared/src/main/scala/scalacss/internal/StyleLookup.scala:31-38`: `implicit def scalaMap[I: UnivEq]` allocates a new `StyleLookup` instance at each call site instead of reusing a cached singleton cast via `.asInstanceOf`.
      - `core/shared/src/main/scala/scalacss/internal/Pseudo.scala:40-62, 88-90`: `addPseudo` constructs `Composite(s.head, s.tail)` which immediately re-executes `t + h` on the already-sorted set.
      - `core/shared/src/main/scala/scalacss/internal/Renderer.scala:156-157, 207-208`: `printFontFace` allocates temporary `CssKV` objects for every descriptor.
      - `core/shared/src/main/scala/scalacss/internal/mutable/StyleSheet.scala:42-43`: `StyleSheet.Base` eagerly computes `classNameHint` (3 regexes + initializing `Register.MacroName.Use$` with its regex) even in `ProdDefaults` where it is unused.
      - `ext-scalatags/js/src/main/scala/scalacss/ScalatagsJsDom.scala:11-15`: `styleaToJsDomTag` allocates `classNameIterator` and a closure per element instead of calling `t.classList.add(s.className.value)` plus a `while` loop over `s.addClassNames`.
      - `core/js/src/main/scala/scalacss/defaults/PlatformExports.scala:50-55` & `ext-react/src/main/scala/scalacss/React.scala:28-29`: `toStyleSheetInlineJsOps`, `toStyleSheetRegistryJsOps`, and `scalacssStyleaToTagMod` are missing `@inline`.

29. **JVM `Mutex.apply` closure overhead and lock-ordering / listener retention in `GlobalRegistry`**
    - **Where:** `core/jvm/src/main/scala/scalacss/internal/mutable/Mutex.scala:9-12`, `core/shared/src/main/scala/scalacss/internal/mutable/GlobalRegistry.scala:45-75`
    - **Issue:** Unlike JS `Mutex`, JVM `Mutex.apply` is not `@inline` (allocating `Function0` closures on nested `Register` calls and using `synchronized(this)`, which pins JDK 21+ virtual threads during callbacks). In `StyleSheetRegistry`, `registerListenerN` invokes `f(existing)` before returning the unregister function (preventing self-unregistration during replay), `onRegistration`/`onRegistrationN` discard the unregister handle forever, and both `GlobalRegistry` and `Register` retain all stylesheet definitions indefinitely after startup CSS injection with no optional release/reset mechanism.
    - **Fix:** Mark JVM `Mutex.apply` `@inline` (or use `ReentrantLock` if virtual-thread safety matters), document the `callbackMutex -> mutex` lock order, and consider exposing a post-render release/dispose hook if users wish to reclaim `Register` / `StyleA.style` memory after SSR/startup injection.

---

## CSS Spec Compliance & API / Macro Gaps

30. **`ToAV` subtypes lack `.important` and `&` combinator support**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/ValueT.scala:8-10`, `core/shared/src/main/scala/scalacss/internal/Attrs.scala:337-340, 2071-2074, 2268-2271, 2915-2918`, `core/shared/src/main/scala/scalacss/internal/Dsl.scala:187, 192`
    - **Issue:** `backgroundClip.Accum`, `quotes.Accum`, `textDecorationLine.Accum`, and `BorderRadiusNext` implement `ToAV` without `.important` (forcing workarounds like `.av.important` in `AttrTest.scala:371`), and `ToAV` values cannot be combined with `&` (`borderRadius(4 px) & color.red`) without calling `.av`.
    - **Fix:** Add `def important: AV = av.important` directly to `trait ToAV` and add `implicit def autoDslToAV(a: ToAV): DslAV = new DslAV(a.av)` in `DslBase`.

31. **`StyleA.+` silently drops `Compose` warnings and can duplicate class names**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/StyleA.scala:27-28`
    - **Issue:** `a + b` prepends `s.className` unconditionally to `addClassNames` (allowing duplicates if composed repeatedly) and computes `style.compose(s.style)` into an unregistered `StyleS`, silently discarding any `Compose.safe` warnings while actual HTML styling follows CSS stylesheet declaration order rather than `a + b` order.
    - **Fix:** Deduplicate `addClassNames` in `StyleA.+` and document the warning/cascade caveat.

32. **`StyleSheet.Standalone` lacks `keyframes`/`fontFace` and misapplies pseudos to comma-separated selectors**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/mutable/StyleSheet.scala:95-130`
    - **Issue:** `Standalone` provides no `keyframes` or `fontFace` DSL helpers (forcing raw `cssRegister` calls), and `("h1, h2").hover - (...)` produces `"h1, h2:hover"` (attaching `:hover` only to the last selector).
    - **Fix:** Expose `keyframes` / `fontFace` on `Standalone` (or `Base`), and apply pseudos across top-level comma-separated selectors in `RootStringOps` / `NestedStringOps`.

33. **`ColorLiteral` CSS Color 4 gaps (both Scala 2 and Scala 3)**
    - **Where:** `core/shared/src/main/scala-2/scalacss/internal/Macros.scala:84-99`, `core/shared/src/main/scala-3/scalacss/internal/Macros.scala:120-135` (plus Scaladoc typo `"validates Color"` at Scala 2 line 270 / Scala 3 line 14)
    - **Issue:** `modernA` and `modernSL` don't accept `none` (unlike `modernNPN`/`modernH`), `rgbM`/`hslM` reject `rgba(...)` and `hsla(...)` with space-separated syntax, legacy `hsl`/`hsla` require integer hue and decimal alpha, and CSS Color 4 functions (`oklch`, `oklab`, `lch`, `lab`, `hwb`, `color(...)`) are unsupported.
    - **Fix:** Allow `none` in `modernA`/`modernSL`, `(?:rgb|rgba)` and `(?:hsl|hsla)` in modern syntax, decimal/angle hue and `%` alpha in legacy syntax, and add validators for modern color functions.

34. **Attribute DSL gaps (`gridTemplateAreas.none`, `placeSelf`, `textIndent`, `GapStyle`, `fontFamily`, `FontFace`)**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/Attrs.scala:1153-1157, 1402-1405, 2223, 2321-2327, 2381, 2388, 3338-3357, 3416, 3488`, `ValueT.scala:144-146`, `Data.scala:70-81`
    - **Issues:**
      - `gridTemplateAreas` lacks `.none` (`gridTemplateAreas("none")` quotes it into `"\"none\""`).
      - `placeSelf` lacks `apply` and single-keyword values (`auto`, `normal`, `center`, `stretch`, `start`, `end`, etc.).
      - `textIndent` allows `(LenPct, hanging, eachLine)` but not `(LenPct, eachLine, hanging)`.
      - `GapStyle` in `ValueT.Rules` omits `GapStyle <=< LenPct` (despite having `Len` and `Pct`).
      - `textAlign.startEnd` emits obsolete `"start end"` while `justify-all` is missing.
      - `flexBasis`, `svgFill`, `svgFloodColor`, `textStrokeColor`, `textStrokeWidth` are untyped `final def`s.
      - `fontFamily` only accepts a single `FontFace` (no fallback stack / standard generic families like `serif`, `sans-serif`, `monospace`).
      - `FontFace` lacks `font-display`, multi-range `unicode-range`, and variable-font descriptor ranges.

35. **`Media.Query.and` with `Not(...)` and modern CSS features / units / pseudos**
    - **Where:** `core/shared/src/main/scala/scalacss/internal/Media.scala:170-183`, `Data.scala:134-151`, `Pseudo.scala`
    - **Issues:**
      - Combining `media.not.screen and media.minWidth(500 px)` emits `@media not screen and (min-width:500px)`, which in CSS negates the entire conjunction rather than just `screen`.
      - Remaining modern CSS features/units to consider: viewport units (`dvh`/`svh`/`lvh`/`dvw`/etc.), container query units (`cqw`/`cqh`/`cqi`/`cqb`), `fr` & angle units, `Percentage` arithmetic (`*`, `/`, `-`), `Pseudo` (`:has`, `:any-link` for `CanIUse.anyLink`, `:placeholder-shown`, `::marker`, `::backdrop`, multi-arg `Not`, case-insensitive attribute flags), `Media` (`any-hover`, `any-pointer`, `prefers-contrast`, `forced-colors`, range syntax for `CanIUse.mediaRangeSyntax`), `Literal` (`revert-layer`, `jump-start`/`jump-end`/`jump-none`/`jump-both`, `break-spaces`), and layout/transform attrs (`aspect-ratio`, `place-content`, `place-items`, `contain`, `container*`, `translate`/`rotate`/`scale`, `backdrop-filter`, `accent-color`, `color-scheme`, `text-wrap`, `mask-*` noted in `Attrs.scala:1723`, logical shorthands `inset`/`marginBlock`/`paddingBlock`/`marginInline`/`paddingInline` — remembering to update `AttrCmp.logicalDirection` in `Attr.scala:175-189`).
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
