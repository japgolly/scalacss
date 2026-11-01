# ScalaCSS Bug & Defect Report

This report documents **40** verified bugs, specification violations, broken invariants, concurrency hazards, and defects identified across the ScalaCSS codebase.

---

## Summary Table

| # | Severity | Category | Location | Summary |
|---|----------|----------|----------|---------|
| 1 | High | DSL / Operators | `Dsl.scala:22-24` | Right-associative `:/:` operator swaps numerator and denominator of `Ratio` (`4 :/: 3` produces `"3/4"`) |
| 2 | High | Ordering / Data Loss | `NaturalOrdering.scala:91-141`, `Cond.scala:56-57` | `NaturalOrdering` equates distinct strings (leading zeroes, spaces) and breaks `Ordering` laws, causing `Cond` collisions and overwriting rules in `StyleS.data` |
| 3 | High | CSS Escaping | `Attrs.scala:792-816` | `content.escape` emits 4-digit hex escapes (`\\%04X`) without a trailing delimiter and splits UTF-16 surrogate pairs, corrupting strings |
| 4 | High | Extensions (Scalatags) | `ScalatagsText.scala:19-22` | `ScalatagsTextRenderer` HTML-escapes CSS inside `<style>`, corrupting child combinators (`>`) and quotes (`"`, `'`) |
| 5 | High | Extensions (Scalatags) | `ScalatagsJsDom.scala:11-15` | `ScalatagsJsDom` throws `DOMException` when `addClassName` contains space-separated classes or an empty class |
| 6 | High | Vendor Prefixing | `AVs.scala:9-16`, `Attr.scala:137-140` | Calling `.important` on an `AV` silently breaks vendor prefixing for all `Transform.values` attributes (e.g., `display.flex.important`) |
| 7 | High | Logical Properties | `Attrs.scala:2654-3236`, `Attr.scala:174-179` | CSS Logical Properties share identical physical `RealAttr` sets, causing orthogonal/opposite logical properties to conflict with and overwrite each other |
| 8 | High | Style Composition | `Compose.scala:21-45`, `AVs.scala:43-45` | `Compose.mergeAVs` drops exact matches before conflict checks, self-conflicts within the right-hand operand under `Rules.replace`, and iterates over unordered `Map`s |
| 9 | High | Style Registration | `Register.scala:62-76`, `Compose.scala:58` | Composing or mixing an `addClassName`-only `StyleA` into another style silently drops its first class name |
| 10 | High | StyleF Lookup | `StyleLookup.scala:15-29` | `StyleLookup.boolean.get` wraps `null` in `Some(null)` for partial boolean domains, bypassing `ErrorHandler.badInput` and returning `null` |
| 11 | High | Macros (Color) | `scala-2/.../Macros.scala:107`, `scala-3/.../Macros.scala:176` | `c"..."` color literal macro crashes with `StringIndexOutOfBoundsException` on empty/blank strings and `NumberFormatException` on large integers |
| 12 | High | Macros (Scala 3) | `scala-3/.../Macros.scala:33-77` | Scala 3 `nameQuoted` macro fails to detect `StyleSheet.Base` when superclass constructor arguments are passed or across compilation units, and strips `$` from names |
| 13 | Medium | CSS Spec / Media | `Media.scala:18, 185` | `Media.ScanValue.Interface` / `media.interface` emits invalid `"interface"` instead of CSS `"interlace"` |
| 14 | Medium | CSS Spec / Values | `Values.scala:365`, `Attrs.scala:1157` | `fontSize.sSmall` / `Literal.sSmall` emits invalid `"s-small"` instead of CSS `"x-small"` |
| 15 | Medium | CSS Spec / Selectors | `Pseudo.scala:220-222` | `Pseudo.AttrStartsWith` emits `"|="` (hyphen-prefix match) instead of `"^="` (prefix match) |
| 16 | Medium | CSS Spec / Attrs | `Attrs.scala:273` | `backgroundClip.Accum` joins multiple values with spaces instead of commas |
| 17 | Medium | CSS Spec / Attrs | `Attrs.scala:3254-3255` | `placeSelf.leftAuto` and `placeSelf.rightNormal` emit invalid CSS (`left`/`right` are invalid for `<'align-self'>`) |
| 18 | Medium | CSS Spec / ValueT | `ValueT.scala:145` | `ruleWidStyCol_P` erroneously allows `Percentage` (`Pct`) in `border`, `outline`, and `columnRule` |
| 19 | Medium | Vendor Prefixing | `Attrs.scala:1843, 1852, 1861, 2074` | `overscrollBehavior*` uses `CanIUse.overflowAnchor` instead of `CanIUse.overscrollBehavior`; `rowGap` uses `CanIUse.multicolumn` |
| 20 | Medium | Vendor Prefixing | `Attrs.scala:636, 662, 1086, 2048-2050, 2170-2171`, `ValueT.scala:281` | Bogus vendor value transforms on `textAlign` (`-ms-justify`), `resize`, `float`, `clear`, `captionSide`, and untransformed `width.available` (`"available"` vs `"fill-available"`) |
| 21 | Medium | Vendor Prefixing | `CanIUse2.scala:23-28, 32-33, 45, 49-52, 75` | `CanIUse2` bugs: `np` is always `true`, `agentPrefixes` emits invalid `-o-` prefixes, `prefixed` matches substrings like `-o-` in URLs, and regexes fail on multi-line values |
| 22 | Medium | Platform / JS | `CanIUse2.scala:106-125`, `PlatformJs.scala:19-24`, `Platform.scala:11` | `CanIUse2.agentsForPlatform` fails to match mobile browser names from `PlatformJs`; `PlatformJs.value` fails under CommonJS/AMD; `Platform.env` ignores `PlatformJsEnv` |
| 23 | Medium | Code Generation / Attrs | `Attrs.scala:1143`, `bin/generate-aliases:8`, `generated.scala` | `fontLanguageOverride` is missing from `generated.scala` (`Dsl`) due to an unanchored `override` filter in `bin/generate-aliases` |
| 24 | Medium | Attr Metadata | `Attrs.scala:9-70` | 9 attributes (`justifyItems`, `overflowAnchor`, and 7 `svg*` attributes) are missing from `Attrs.valuesForAllAttr`, `Attrs.values`, and `Attrs.all` |
| 25 | Medium | Conflict Detection | `Attr.scala:187-198` | `AttrCmp.conflicts` fails to detect transitive/chained conflicts (e.g., `[marginLeft, margin, marginRight]`) |
| 26 | Medium | Equality / Ordering | `Pseudo.scala:37-47, 72-78`, `Cond.scala:56-57` | `Pseudo.Composite` violates commutativity, associativity, and idempotence of `equals`/`hashCode`, and makes `Cond.ordering` inconsistent with `Cond.equals` |
| 27 | Medium | Equality | `AVs.scala:25`, `Style.scala:38` | `AVs` does not override `equals` or `hashCode`, breaking value equality on `StyleS` and `StyleA` |
| 28 | Medium | Conditions & UnsafeExt | `Cond.scala:34-49`, `Css.scala:66-69`, `Dsl.scala:224` | `Cond.applyToStyle` drops condition prefixes from `warnings` and bypasses `Compose`; `unsafeRoot` drops outer pseudo-conditions |
| 29 | Medium | Warnings | `Register.scala:73, 130`, `StyleSheet.scala:100, 163`, `Dsl.scala:202-203` | Composition warnings in `keyframe`, `unsafeChild`, `unsafeRoot`, `unsafeExt`, and `StyleSheet.Standalone` are silently discarded |
| 30 | Medium | Renderer | `Renderer.scala:38-46, 82-90, 115, 124-130`, `Css.scala:39-40` | `StringRenderer` issues: missing `;` in `formatTiny` keyframes, missing `fmt.done()` in `Raw`, inconsistent `font-family` quoting, and overwritten duplicate keyframe selectors |
| 31 | Medium | Concurrency / Thread Safety | `Register.scala:135-141, 250-256`, `package.scala:64-71` | Thread-safety races in `Register.registerFontFace`, `internal.memo` (`CanIUse2.prefixPlan`), and `NameGen.nextShortPrefix()` |
| 32 | Low | Typo in Public API | `Attrs.scala:50, 1271`, `generated.scala:808` | Attribute named `fontVariantionSettings` instead of `fontVariationSettings` |
| 33 | Low | Missing DSL Alias | `Values.scala:11-51, 127` | `Literal.Typed.normal` is missing from `TypedLiteralAliases`, preventing `gap(10.px, normal)` in the DSL |
| 34 | Low | DSL / Media | `Media.scala:95-102, 253` | `Media.Query.+` returns `Vector[Query]` which cannot be used as a `Cond`, while `&` renders `, ` (logical OR) instead of `and` |
| 35 | Low | Data / Units | `Data.scala:126-131, 151-152, 167-178`, `ValueT.scala:21-26, 115-121` | `Length(0.0)` JVM/JS divergence (`"0.0px"` vs `"0"`), negative `Resolution`, unescaped quotes/backslashes in `ValueT.mkString`, and sub-millisecond `FiniteDuration` truncation |
| 36 | Low | Domain | `Domain.scala:45, 49-53, 59` | `Domain.Pair.apply` throws `ArithmeticException: / by zero` when first domain is empty; `OptionT`/`Disjunction` mishandle negative indices |
| 37 | Low | Lazy Evaluation | `Need.scala:5-9` | `Need.value` nulls `_thunk` before invoking `t()`, causing `NullPointerException` on retry if `t()` throws |
| 38 | Low | Registry / DSL | `GlobalRegistry.scala:54-61`, `StyleSheet.scala:149-155`, `Register.scala:54-60, 100-118` | Empty `GlobalRegistry.register()` appends empty `<style>` tag; `styleF("name")` lacks `apply(Domain[I])`; `registerFM`/`registerF2` ignore `doesntNeedClassName` |
| 39 | Low | Extensions (Scalatags) | `ScalatagsText.scala:11-13` | `ScalatagsText.styleaToTextTag` prepends `" "` before `s.htmlClass`, producing leading and double spaces in `class` attributes |
| 40 | Low | Build / Configuration | `ModeMacros.scala`, `project/Gen.scala:19, 29`, `project/Lib.scala:79`, `Cond.scala:13-18` | `ModeMacros.readConfig` ignores `getenv` when system property is empty; stale paths in `project/Gen.scala`; eager dead `Cond.sortKey` |

---

## Detailed Findings

### 1. Right-associative `:/:` operator swaps numerator and denominator of `Ratio`
- **File**: [Dsl.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Dsl.scala#L22-L24)
- **Code**:
  ```scala
  final class DslInt(private val self: Int) extends AnyVal {
    def :/:(y: Int) = Ratio(self, y)
  }
  ```
- **Defect**: In Scala, any symbolic operator ending with `:` is **right-associative** and binds to its right operand. Therefore, `4 :/: 3` desugars to `autoDslInt(3).:/:(4)`, where `self = 3` and `y = 4`. Because `:/:` constructs `Ratio(self, y)`, `4 :/: 3` produces `Ratio(3, 4)` (`"3/4"`) instead of `Ratio(4, 3)` (`"4/3"`).
- **Evidence**: In [InlineTest.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/test/scala/scalacss/full/InlineTest.scala#L33) and [StandaloneTest.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/test/scala/scalacss/full/StandaloneTest.scala#L22), `media.tv.minDeviceAspectRatio(4 :/: 3)` renders `@media tv and (min-device-aspect-ratio:3/4)`.
- **Fix**: Define `def :/:(x: Int) = Ratio(x, self)`.

---

### 2. `NaturalOrdering` equates distinct strings and breaks `Ordering` laws, corrupting `SortedMap[Cond, AVs]`
- **Files**: [NaturalOrdering.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/NaturalOrdering.scala#L91-L141), [Cond.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Cond.scala#L56-L57)
- **Code**:
  ```scala
  while (true) {
    // only count the number of zeroes leading the last number compared
    nza = 0
    nzb = 0

    ca = charAt(a, ia)
    cb = charAt(b, ib)

    // skip over leading spaces or zeros
    while (Character.isSpaceChar(ca) || ca == '0') {
      if (ca == '0')
        nza += 1
      else
        nza = 0
      ia += 1
      ca = charAt(a, ia)
    }
    ...
    if (Character.isDigit(ca) && Character.isDigit(cb)) {
      cr_ia = ia
      cr_ib = ib
      result = compareRight()
      if (result != 0)
        return result
    } else if (ca == 0 && cb == 0)
      return nza - nzb
  ```
- **Defects**:
  1. **`nza` and `nzb` are reset on every iteration**: When comparing `"01"` and `"1"`, iteration 1 skips `'0'` (`nza = 1, ca = '1'`) and `compareRight()` compares `'1'` vs `'1'`, returning `0`. Because `ca` and `cb` are `'1'` (not `0`), the `else if (ca == 0 && cb == 0)` branch is not taken. On iteration 2, `nza = 0` and `nzb = 0` wipe out the leading-zero counts before `ca == 0 && cb == 0` is reached, returning `0`! Thus `NaturalOrdering.compare("01", "1") == 0` and `NaturalOrdering.compare("01a", "1a") == 0`.
  2. **Non-leading `'0'` and space characters are skipped unconditionally**: The `while (Character.isSpaceChar(ca) || ca == '0')` loop skips `'0'` and spaces even when they are not followed by digits, and never falls back to `a.compareTo(b)`. Consequently:
     - `NaturalOrdering.compare("0a", "a") == 0` and `NaturalOrdering.compare("f0o", "fo") == 0`.
     - `NaturalOrdering.compare("a b", "ab") == 0` and `NaturalOrdering.compare("[a=\"x y\"]", "[a=\"xy\"]") == 0`.
     - Transitivity is violated: `"0z" > "a"` (because `'0'` is skipped so `'z' > 'a'`), while `"1z" < "a"` (`'1' < 'a'`).
  3. **Data loss in `StyleS.data`**: `StyleS.data` is a `SortedMap[Cond, AVs]` using `Cond.ordering = Ordering.by[Cond, String](_.toString)(NaturalOrdering)`. Any two distinct `Cond`s whose string representations return `0` under `NaturalOrdering` (e.g., `&.nthChild("01")` vs `&.nthChild("1")`, or `&.attr("a", "x y")` vs `&.attr("a", "xy")`) collide in the `SortedMap` and overwrite each other.
  4. **Redundant $O(k^2)$ digit scanning and dead code**: After `compareRight()` returns `0` on an equal multi-digit run, `ia` and `ib` are only incremented by `1` instead of advancing to `cr_ia`/`cr_ib`. Also, lines 73–74 (`else if (cr_ca == 0 && cr_cb == 0) return cr_bias`) are unreachable because `!Character.isDigit(0)` already matched on line 65.

---

### 3. `Attrs.content.escape` produces invalid and corrupted CSS string escapes
- **File**: [Attrs.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attrs.scala#L792-L816)
- **Code**:
  ```scala
  private def escapeChar(c: Char, sb: StringBuilder): Unit =
    c match {
      case '\\' | '\''            => sb.append('\\').append(c)
      case _ if c.toInt > 127     => sb.append("\\%04X".format(c.toInt))
      case _ if c.isControl       => sb.append("\\%04X".format(c.toInt))
      case _ if c.isWhitespace    => sb.append(c)
      case _ if c.isUnicodeIdentifierPart || c.isUnicodeIdentifierStart => sb.append(c)
      case _                      => sb.append("\\%04X".format(c.toInt))
    }
  ```
- **Defects**:
  1. **Hex escape swallows subsequent hex digits**: Per CSS Syntax Module Level 3 (§4.3.7), a backslash hex escape consumes **1 to 6 hex digits** (`\[0-9a-fA-F]{1,6}`) plus an optional trailing whitespace character. Because `escapeChar` emits only 4 hex digits (`\\%04X`) with no trailing delimiter—and escapes all ASCII punctuation (`:`, `!`, `#`, `/`, etc.) via the catch-all case—any escaped character followed by `[0-9a-fA-F]` merges with the following 1–2 characters! For example, `content.string(":cafe")` produces `'\003Acafe'`, which CSS parses as `U+3ACA` (`㫊`) followed by `"fe"` instead of `":cafe"`.
  2. **Hex escape swallows a following space**: If an escaped character is followed by a space (e.g., `content.string(": hello")` -> `'\003A hello'`), CSS consumes the space as the terminator of the hex escape, rendering `":hello"` (missing space).
  3. **UTF-16 surrogate pair corruption**: `escape` iterates over `Char` (`s.charAt(i)`), splitting supplementary Unicode code points (`U+10000`–`U+10FFFF`, such as emoji) into two surrogate code units (e.g., `\D83D\DE00`). Per CSS Syntax §4.3.7, surrogate code points in CSS escapes are invalid and replaced with `U+FFFD` (``).

---

### 4. `ScalatagsTextRenderer` HTML-escapes CSS inside `<style>` tags
- **File**: [ScalatagsText.scala](file:///Users/golly/projects/public/scalacss/ext-scalatags/shared/src/main/scala/scalacss/ScalatagsText.scala#L19-L22)
- **Code**:
  ```scala
  class ScalatagsTextRenderer(s: Renderer[String]) extends Renderer[TypedTag[String]] {
    override def apply(css: Css) =
      tags2.style(`type` := "text/css", s(css))
  }
  ```
- **Defect**: Passing `s(css)` as a `String` modifier instead of `raw(s(css))` causes Scalatags to HTML-escape the CSS body. Characters such as `>` (child combinator `.a > .b`), `"` (`font-family: "My Font"`, `[attr="val"]`), `'`, and `&` are turned into `&gt;`, `&quot;`, `&#39;`, and `&amp;`. Because `<style>` is a raw text element in HTML5, browsers do not decode HTML character references inside `<style>`, resulting in broken CSS rules.

---

### 5. `ScalatagsJsDom.styleaToJsDomTag` throws `DOMException` on space-containing or empty class names
- **File**: [ScalatagsJsDom.scala](file:///Users/golly/projects/public/scalacss/ext-scalatags/js/src/main/scala/scalacss/ScalatagsJsDom.scala#L11-L15)
- **Code**:
  ```scala
  implicit final def styleaToJsDomTag(s: StyleA): Modifier = new Modifier {
    def applyTo(t: dom.Element) =
      for (cn <- s.classNameIterator)
        t.classList.add(cn.value)
  }
  ```
- **Defect**: If a style uses `addClassName("btn btn-default")` (as in [ScalatagsTest.scala](file:///Users/golly/projects/public/scalacss/ext-scalatags/shared/src/test/scala/scalacss/ScalatagsTest.scala#L22)) or has an empty class name `""`, `t.classList.add(cn.value)` throws `DOMException: InvalidCharacterError` (for whitespace) or `DOMException: SyntaxError` (for `""`).

---

### 6. `AV.important` breaks vendor prefixing for `Transform.values` attributes
- **Files**: [AVs.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/AVs.scala#L9-L16), [Attr.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attr.scala#L137-L140)
- **Code**:
  ```scala
  // AVs.scala:9-16
  final case class AV(attr: Attr, value: Value) {
    def apply(env: Env): Vector[CssKV] = attr.gen(env)(value)
    def important: AV = copy(value = this.value + _important)
  }

  // Attr.scala:137-140
  def values(subject: Subject)(v1: Value, vn: Value*): Transform = {
    val whitelist = vn.toSet + v1
    values(subject, PrefixApply maybePrepend whitelist.contains)
  }
  ```
- **Defect**: `AV.important` appends `" !important"` directly to `AV.value` before `attr.gen(env)(value)` runs. When `Transform.values` checks `whitelist.contains(in)`, `in` is `"flex !important"` (or `"inline-flex !important"`, `"grid !important"`, `"zoom-in !important"`, `"max-content !important"`), which does not match `"flex"` in `whitelist`. Consequently, calling `.important` on any `Transform.values`-backed property (such as `display.flex.important`, `cursor.zoomIn.important`, `width.maxContent.important`) silently drops all vendor prefixes.

---

### 7. CSS Logical Properties share physical `RealAttr` sets, causing false conflicts and overwriting orthogonal declarations
- **Files**: [Attrs.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attrs.scala#L2654-L3236), [Attr.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attr.scala#L174-L179)
- **Code**:
  ```scala
  // Attrs.scala:2654-2656, 3039-3041
  object blockSize extends TypedAttr_Length {
    override protected def attr2 = Attr.alias("block-size", _)(_(height, width))
  }
  object inlineSize extends TypedAttr_Length {
    override protected def attr2 = Attr.alias("inline-size", _)(_(height, width))
  }

  // Attrs.scala:3071-3100
  object marginBlockEnd    extends TypedAttr_LenPctAuto { override val attr = Attr.alias("margin-block-end")(_(margin)) }
  object marginBlockStart  extends TypedAttr_LenPctAuto { override val attr = Attr.alias("margin-block-start")(_(margin)) }
  object marginInlineEnd   extends TypedAttr_LenPctAuto { override val attr = Attr.alias("margin-inline-end")(_(margin)) }
  object marginInlineStart extends TypedAttr_LenPctAuto { override val attr = Attr.alias("margin-inline-start")(_(margin)) }
  ```
  (Same pattern for `maxBlockSize`/`maxInlineSize`, `minBlockSize`/`minInlineSize`, `paddingBlock*`/`paddingInline*`, `offsetBlock*`/`offsetInline*`, `borderBlock*`/`borderInline*`.)
- **Defect**: `AttrCmp.byRealAttrs` determines whether two attributes conflict by checking `x.real exists y.real.contains`. Because `blockSize` and `inlineSize` both have `real = Set(height, width)`, `blockSize.cmp(inlineSize)` returns `AttrCmp.Overlap` (`conflict = true`). Setting both `blockSize(10.px)` and `inlineSize(20.px)` (or `marginBlockStart(5.px)` and `marginBlockEnd(10.px)`, or `paddingInlineStart(4.px)` and `paddingInlineEnd(8.px)`) in the same style triggers false conflict warnings and, under `Compose.Rules.replace`, deletes the earlier property!

---

### 8. `Compose.mergeAVs` drops exact matches before conflict checks, self-conflicts within the right-hand operand, and iterates over unordered `Map`s
- **Files**: [Compose.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Compose.scala#L21-L45), [AVs.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/AVs.scala#L43-L45)
- **Code**:
  ```scala
  def mergeAVs(c: Cond, newAVs: AVs, into: AVs): AVs = {
    // Remove exact matches
    var newData = newAVs.data
    for {(a, nvs) <- newData; ovs <- into.get(a)} {
      val o = ovs.toSet
      val newVals = nvs.whole.filterNot(o.contains)
      NonEmptyVector.maybe(newVals, newData -= a)(n => newData = newData.updated(a, n))
    }

    // Find conflicts
    newData.foldLeft(into) { case (results, (a, nvs)) =>
      ...
  ```
- **Defects**:
  1. **Exact matches are removed before checking conflicts**: If `into` has `margin(10.px)` followed by `marginLeft(20.px)` and `newAVs` has `margin(10.px)`, the "Remove exact matches" loop deletes `margin(10.px)` from `newData` because `into` already contains `margin -> 10px`. As a result, `margin(10.px)` never overrides or removes `marginLeft(20.px)`! This breaks associativity of composition (noted as `// TODO broke in 9259fd3` in [ComposeTest.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/test/scala/scalacss/internal/ComposeTest.scala#L39)).
  2. **`newData.foldLeft(into)` resolves `newAVs` entries against earlier entries from `newAVs` itself**: If `newAVs` (`b`) contains both `margin(10.px)` and `marginLeft(20.px)`, folding `newData` into `results` under `Rules.replace` causes the second attribute in `b` to conflict with and delete the first attribute from `b` itself, violating `(a ∘ b) ⊇ b` (noted at [ComposeTest.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/test/scala/scalacss/internal/ComposeTest.scala#L14)).
  3. **Unordered `Map` iteration**: `newData` (`newAVs.data`) and `r.data` (line 41) are `scala.collection.immutable.Map`s rather than ordered sequences (`newAVs.order` / `r.order`). When a style has 5 or more attributes (`HashMap`), `newData.foldLeft` iterates in hash order rather than declaration order, scrambling CSS property order and warning order.
  4. **`AVs.addTail` does not move an updated attribute to the end of `order`**: In `AVs.scala:44`, `addTail` uses `e => if (e.isEmpty) order :+ a else order`. When appending a new value to an existing attribute `a`, `a` remains at its old position in `order` rather than moving after attributes added in between.

---

### 9. Mixing an `addClassName`-only `StyleA` into another style silently drops its first class name
- **Files**: [Register.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/mutable/Register.scala#L62-L76), [Compose.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Compose.scala#L58)
- **Code**:
  ```scala
  // Register.scala:62-67
  val s =
    if (doesntNeedClassName(s0)) {
      val h = s0.addClassNames.head
      val t = s0.addClassNames.tail
      s0.copy(className = Some(h), addClassNames = t)
    } else s0
  ```
- **Defect**: When `val s1 = style(addClassName("btn"))` (or `addClassNames("btn", "btn-primary")`) is registered, `doesntNeedClassName(s0)` is `true`, so `registerS` removes `"btn"` from `s.addClassNames` and places it in `s.className`, storing the mutated `s` in `StyleA.style`. When `s1` is later mixed into another style `val s2 = style(s1, color.red)` (via `ToStyleStyleA(x: StyleA) = new ToStyle(x.style)`), `Compose.apply` sets `className = None` and only preserves `addClassNames`. As a result, `"btn"` is silently lost from `s2`!
- **Related defect**: Because `StyleA(ClassName("btn"), ...)` is added to `_styles`, defining a second style in the same stylesheet that also wraps `addClassName("btn")` triggers a false-positive warning: `Another style in the register has the same classname`.

---

### 10. `StyleLookup.boolean.get` wraps `null` in `Some(null)` for partial boolean domains
- **File**: [StyleLookup.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/StyleLookup.scala#L15-L29)
- **Code**:
  ```scala
  override def empty = new Array[StyleA](2)
  override def add   = (t, b, s) => { t(ind(b)) = s; t }

  def ind(b: Boolean) = if (b) 1 else 0

  override def get =
    array => {
      val t = Some(array(ind(true)))
      val f = Some(array(ind(false)))
      b => if (b) t else f
    }
  ```
- **Defect**: `empty` allocates `Array[StyleA](null, null)`. If a `StyleF[Boolean]` is defined with a subset domain such as `Domain.ofValues(true)` or `Domain.empty`, `array(0)` remains `null`. Because `get` wraps the array elements with `Some(...)` instead of `Option(...)`, `get(array)(false)` returns `Some(null)` instead of `None`. In `Register._registerF` (`i => f(i) getOrElse errHandler.badInput(s, i)`), `Some(null).getOrElse(...)` returns `null: StyleA` instead of calling `errHandler.badInput`.

---

### 11. `ColorLiteral` macro (`c"..."`) crashes with `StringIndexOutOfBoundsException` on empty/blank strings and `NumberFormatException` on large integers
- **Files**: [scala-2/scalacss/internal/Macros.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala-2/scalacss/internal/Macros.scala#L101-L165), [scala-3/scalacss/internal/Macros.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala-3/scalacss/internal/Macros.scala#L163-L239)
- **Code**:
  ```scala
  val text = ws.replaceAllIn(text0, "").toLowerCase
  ...
  def validateHex: Boolean =
    (text.charAt(0) == '#') && { ... }

  def validateInt(name: String, max: Int): V =
    s => {
      val i = s.toInt
      if (i < 0 || i > max)
        fail(s"Invalid $name value: $s")
    }
  ```
- **Defects**:
  1. **Empty/whitespace string crash**: When `text0` is `""` or `"   "`, `text` is `""` and `text.charAt(0)` throws an uncaught `StringIndexOutOfBoundsException` instead of calling `fail(...)` (also crashing `ColorLiteral.attempt("")` at runtime in Scala 3).
  2. **Integer overflow crash**: `int = "(-?\\d+)"` matches digit sequences of any length (e.g., `c"rgb(99999999999,0,0)"`), causing `s.toInt` to throw an uncaught `NumberFormatException`.
  3. **Indiscriminate whitespace stripping**: `ws.replaceAllIn(text0, "")` strips spaces inside tokens, accepting invalid CSS such as `c"# fff"`, `c"r g b(0,0,0)"`, and `c"rgb(100 %,0 %,0 %)"`.
  4. **Valid CSS colors rejected**: `validateInt("hue", 359)` rejects `360` (`hsl(360, 100%, 50%)`) and fractional degrees, and `rgba` only accepts integer RGB channels, rejecting `rgba(100%, 50%, 0%, 0.5)`.

---

### 12. Scala 3 `Macros.Dsl.nameQuoted` fails to detect `StyleSheet.Base` when superclass constructor arguments are passed or across compilation units, and strips `$`
- **File**: [scala-3/scalacss/internal/Macros.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala-3/scalacss/internal/Macros.scala#L33-L77)
- **Code**:
  ```scala
  val isStyleSheet: Symbol => Boolean = {
    val ss = TypeRepr.of[mutable.StyleSheet.Base]
    sym =>
      try {
        sym.tree match {
          case ClassDef(_, _, parents, _, _) =>
            parents.exists {
              case p: TypeTree => (p.tpe <:< ss)
              case _           => false
            }
          case _ => false
        }
      } catch {
        case _: Throwable => false
      }
  }
  ...
  def fixName(s: String): String =
    s.replace("$", "")
  ```
- **Defects**:
  1. In Scala 3 `quotes.reflect`, when a class/object passes explicit constructor arguments to its superclass (e.g., `class MyStyles(using r: Register) extends StyleSheet.Inline()(using r)` or extends a custom parameterized base stylesheet), the superclass entry in `ClassDef.parents` is a `Term` (`Apply`), **not** a `TypeTree`. In addition, if the enclosing stylesheet class was compiled in a separate compilation unit without `-Yretain-trees`, accessing `sym.tree` throws an exception caught by `case _: Throwable => false`. In both cases, `isStyleSheet` returns `false`, `classOwner` walks up to `<root>`, and nested object prefixes are lost. Checking `sym.typeRef <:< ss` avoids inspecting `sym.tree` altogether.
  2. `fixName` strips all `$` characters from already-decoded symbol names in Scala 3, turning a user-defined `val $foo = style(...)` into `"foo"` (whereas Scala 2 preserves `"$foo"`), and `import scala.reflect.NameTransformer` on line 5 is unused.

---

### 13. `Media.ScanValue.Interface` / `media.interface` is a typo for CSS `interlace`
- **File**: [Media.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Media.scala#L16-L18) (and line 185)
- **Code**:
  ```scala
  sealed abstract class ScanValue(final val value: String)
  case object Progressive extends ScanValue("progressive")
  case object Interface   extends ScanValue("interface")
  ```
- **Defect**: Per the W3C Media Queries specification, the valid values for the `scan` media feature are `progressive` and **`interlace`** (for interlaced CRT/TV displays). `"interface"` is a typo that produces invalid CSS `@media (scan:interface)`.

---

### 14. `Attrs.fontSize.sSmall` and `Literal.sSmall` emit invalid `"s-small"` instead of `"x-small"`
- **Files**: [Values.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Values.scala#L365), [Attrs.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attrs.scala#L1157)
- **Code**:
  ```scala
  // Values.scala:364-369
  def xxSmall            : Value = "xx-small"
  def sSmall             : Value = "s-small"
  def small              : Value = "small"
  def large              : Value = "large"
  def xLarge             : Value = "x-large"
  def xxLarge            : Value = "xx-large"
  ```
- **Defect**: In CSS Fonts (`<absolute-size>`), the keyword between `xx-small` and `small` is **`x-small`**, not `"s-small"`.

---

### 15. `Pseudo.AttrStartsWith` emits `"|="` instead of `"^="`
- **File**: [Pseudo.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Pseudo.scala#L220-L222)
- **Code**:
  ```scala
  /** Selects all elements with a name starting with value. */
  def AttrStartsWith(name: String, value: String) =
    AttrSelector(name, value, "|=")
  ```
- **Defect**: In CSS Selectors, `[attr^="val"]` selects elements whose attribute value starts with `"val"`. `[attr|="val"]` is the hyphen-separated subcode match (matching only `"val"` or `"val-..."`). `AttrStartsWith` mistakenly emits `"|="`, leaving ScalaCSS with no way to generate `[attr^="val"]` (or `[attr*="val"]`).

---

### 16. `backgroundClip.Accum` joins multiple values with spaces instead of commas
- **File**: [Attrs.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attrs.scala#L271-L278)
- **Code**:
  ```scala
  final class Accum private[backgroundClip](val v: Value) extends ToAV {
    override def av = AV(attr, v)
    private def add(v: Value) = new Accum(s"${this.v} $v")
  ```
- **Defect**: Per CSS Backgrounds and Borders Level 3 (`background-clip: <box>#`), multiple `background-clip` values must be comma-separated (e.g., `padding-box, content-box`), not space-separated (`padding-box content-box`).

---

### 17. `placeSelf` defines invalid CSS values `leftAuto` and `rightNormal`
- **File**: [Attrs.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attrs.scala#L3254-L3255)
- **Code**:
  ```scala
  def leftAuto    = av("left auto")
  def rightNormal = av("right normal")
  ```
- **Defect**: Per CSS Box Alignment Level 3 (§6.4), `place-self: <'align-self'> <'justify-self'>?`. The first value is `<'align-self'>`, which does not accept `left` or `right` (those are only valid for `<'justify-self'>`). `"left auto"` and `"right normal"` are invalid CSS. (Note also the typo in the scaladoc URL on line 3241: `palce-self`.)

---

### 18. `ValueT.Rules.ruleWidStyCol_P` erroneously allows `Percentage` in `border`, `outline`, and `columnRule`
- **File**: [ValueT.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/ValueT.scala#L144-L145)
- **Code**:
  ```scala
  // diverging implicit expansion requires these ↙ :(
  @inline implicit def ruleWidStyCol_L : WidStyCol  <=< Len = Rule.retype
  @inline implicit def ruleWidStyCol_P : WidStyCol  <=< Pct = Rule.retype
  ```
- **Defect**: `WidStyCol` represents `<br-width> || <br-style> || <color>`. While `<br-width>` accepts `<length>` (`BrWidth <=< Len`), CSS forbids `<percentage>` for border/outline/column-rule widths. `ruleWidStyCol_P` was copy-pasted from the `LenPct` rules below it, allowing invalid CSS such as `border(25.%%)` (`border: 25%`) to compile.

---

### 19. Wrong `CanIUse` subjects used in `overscrollBehavior`, `overscrollBehaviorX`, `overscrollBehaviorY`, and `rowGap`
- **File**: [Attrs.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attrs.scala#L1842-L1862) and [Attrs.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attrs.scala#L2073-L2075)
- **Code**:
  ```scala
  // Lines 1842-1862:
  object overscrollBehavior extends TypedAttrT2[OverflowBehaviour] with OverflowBehaviourOps {
    override val attr = Attr.real("overscroll-behavior", Transform keys CanIUse.overflowAnchor)
  }
  object overscrollBehaviorX extends TypedAttr_OverscrollBehavior {
    override val attr = Attr.real("overscroll-behavior-x", Transform keys CanIUse.overflowAnchor)
  }
  object overscrollBehaviorY extends TypedAttr_OverscrollBehavior {
    override val attr = Attr.real("overscroll-behavior-y", Transform keys CanIUse.overflowAnchor)
  }

  // Lines 2073-2075:
  object rowGap extends TypedAttrT1[LenPct] with ZeroLit {
    override val attr = Attr.real("row-gap", Transform keys CanIUse.multicolumn)
  ```
- **Defects**:
  1. `overscrollBehavior`, `overscrollBehaviorX`, and `overscrollBehaviorY` copy-pasted `CanIUse.overflowAnchor` from `overflowAnchor` right above them instead of using `CanIUse.overscrollBehavior` (which is defined in [CanIUse.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/CanIUse.scala#L1451) and remains completely unused).
  2. `rowGap` copy-pasted `Transform keys CanIUse.multicolumn` from `columnGap`, generating non-existent `-webkit-row-gap`, `-moz-row-gap`, and `-o-row-gap` properties.

---

### 20. Bogus vendor value transforms on `textAlign`, `resize`, `float`, `clear`, `captionSide`, and untransformed `width.available`
- **Files**: [Attrs.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attrs.scala#L636) (and lines 662, 1086, 2048–2050, 2170–2171), [ValueT.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/ValueT.scala#L281), [CanIUse2.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/CanIUse2.scala#L13-L15)
- **Defects**:
  1. `textAlign` (`Attrs.scala:2171`) applies `Transform.values(CanIUse.textJustify)(L.justify)`, which prefixes `text-align: justify` into `text-align: -ms-justify`! `CanIUse.textJustify` is for the `text-justify` property, not `text-align: justify` (standard unprefixed CSS1).
  2. `resize` (`Attrs.scala:2048-2050`) combines `Transform.keys(CanIUse.resize) * Transform.values(CanIUse.logicalProps)(L.block, L.inline)`, producing cross-vendor combinations like `-moz-resize: -webkit-block`. Similarly, `captionSide`, `clear`, and `float` prefix `inline-start`/`inline-end` using `CanIUse.logicalProps` (`-webkit-inline-start`), which browsers never supported as values on those properties.
  3. `TypedAttr_Length` (`ValueT.scala:281`, used by `width` and `height`) defines `final def available = av(L.available)` (`"available"`), whereas `CanIUse2.intrinsicWidthTransforms` (`CanIUse2.scala:15`) only transforms `L.fillAvailable` (`"fill-available"`, used by `TypedAttr_MinLength` and `TypedAttr_MaxLength`). Thus `width.available` and `height.available` are never vendor-prefixed.

---

### 21. `CanIUse2` prefixing bugs: `np` always `true`, invalid `-o-` prefixes, substring false positives, and multi-line regex failure
- **File**: [CanIUse2.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/CanIUse2.scala#L23-L78)
- **Code**:
  ```scala
  val needsPrefix: Support => Boolean = {
    case Unsupported | Full | Partial => false
    case FullX | PartialX             => true
    case Unknown                      => false
  }
  ...
  def agentPrefixes: Agent => SortedSet[Prefix] =
    a => emptyPrefixSet + a.prefix ++ a.prefixExceptions.values
  ...
  val prefixPlan: Subject => PrefixPlan =
    memo { s =>
      val ps = subjectPrefixes(s).toVector.map(Some.apply)
      val np = s.exists(_._2.exists(d => !needsPrefix(d)))
      if (np) ps :+ None else ps
    }

  val prefixed: String => Boolean = {
    val p = Prefix.values.iterator.map(_.prefix).mkString(".*(?:", "|", ").*").r.pattern
    in => p.matcher(in).matches
  }
  ```
- **Defects**:
  1. **`np` is always `true`**: Because `needsPrefix(Unsupported)` and `needsPrefix(Unknown)` return `false`, `!needsPrefix(d)` is `true` whenever *any* browser has `Unsupported` or `Unknown` support. Thus `np` is always `true`, emitting unprefixed declarations even for features that only exist with vendor prefixes (such as `-webkit-box-reflect`).
  2. **`agentPrefixes` always includes `prefixExceptions`**: `Agent.Opera` has `prefix = webkit` and `prefixExceptions = Map(... -> o)` for legacy Presto Opera 9–12. Because `agentPrefixes` unconditionally adds `a.prefixExceptions.values`, any feature supported with `-webkit-` in Chromium-based Opera 15+ also emits non-existent `-o-` prefixes (e.g., `-o-flex`, `-o-user-select`, `-o-appearance`).
  3. **`prefixed` matches substrings without word boundaries**: `.*(?:-moz-|-ms-|-o-|-webkit-).*` matches any value containing `-o-` or `-ms-` inside a URL, identifier, or string (e.g., `backgroundImage := "url(logo-o-2.png), linear-gradient(red, blue)"`), causing `runPlan` to skip vendor prefixing completely.
  4. **Multi-line values fail to match**: Neither `prefixed` (line 50) nor `PrefixApply.keywords` (line 75) enables `Pattern.DOTALL` (`(?s)`), so `.` fails to match across `\n` in multi-line CSS values.

---

### 22. `CanIUse2.agentsForPlatform` fails to match mobile browser names from `PlatformJs`, `PlatformJs.value` fails under CommonJS/AMD, and `Platform.env` ignores `PlatformJsEnv`
- **Files**: [CanIUse2.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/CanIUse2.scala#L106-L125), [PlatformJs.scala](file:///Users/golly/projects/public/scalacss/core/js/src/main/scala/scalacss/js/PlatformJs.scala#L19-L24), [Platform.scala](file:///Users/golly/projects/public/scalacss/core/js/src/main/scala/scalacss/internal/Platform.scala#L11)
- **Defects**:
  1. `PlatformJs` reports mobile browser names as `"Chrome Mobile"`, `"Firefox Mobile"`, `"IE Mobile"`, `"Opera Mini"`, and `"Opera Mobile"` (see [PlatformJs.scala](file:///Users/golly/projects/public/scalacss/core/js/src/main/scala/scalacss/js/PlatformJs.scala#L15)), whereas `CanIUse2.agentsForPlatform` only matches exact `"Chrome"`, `"Firefox"`, `"IE"`, and `"Opera"`. On Android Firefox (`p.name = Some("Firefox Mobile")`), `agentsForPlatform` falls into the Android wildcard `case _ => NonEmptyVector(AndroidBrowser, AndroidUC)`, emitting `-webkit-` prefixes and dropping `-moz-`!
  2. `PlatformJs.value` runs `js.eval(sourceCode)` and reads `js.Dynamic.global.platform`. However, in CommonJS/Node.js/Webpack environments (where `exports` and `module` exist) or AMD environments (`define.amd`), the bundled `platform.js` IIFE exports to `exports` or `define` instead of assigning `global.platform`, so `PlatformJs.value` evaluates to `undefined`.
  3. `core/js/src/main/scala/scalacss/internal/Platform.scala` hardcodes `implicit def env: Env = Env.empty` instead of using `PlatformJsEnv.value`.

---

### 23. `fontLanguageOverride` is missing from `generated.scala` (`Dsl`) due to a regex bug in `bin/generate-aliases`
- **Files**: [Attrs.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attrs.scala#L1143), [bin/generate-aliases](file:///Users/golly/projects/public/scalacss/bin/generate-aliases#L8), [generated.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/generated.scala#L730-L736)
- **Code**:
  ```scala
  // Attrs.scala:1143
  final def fontLanguageOverride = Attr.real("font-language-override")

  // bin/generate-aliases:8
  grep -vP '^ *(?:(?:\*|[/*]+).*|override|private| val +values|final class|object|def apply|$)'
  ```
- **Defect**: `bin/generate-aliases` filters out lines matching `override` without anchoring it as a keyword before `def`/`val`. Because `"font-language-override"` contains the substring `override`, `fontLanguageOverride` was stripped by the script and is missing from `AttrAliasesAndValueTRules` in `generated.scala`.

---

### 24. 9 attributes in `Attrs` are missing from `Attrs.valuesForAllAttr`, `Attrs.values`, and `Attrs.all`
- **File**: [Attrs.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attrs.scala#L9-L70)
- **Defect**: The following 9 attributes defined in `Attrs` are omitted from `valuesForAllAttr`:
  - `justifyItems` ([line 1511](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attrs.scala#L1511))
  - `overflowAnchor` ([line 1831](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attrs.scala#L1831))
  - `svgFloodOpacity` ([line 3326](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attrs.scala#L3326))
  - `svgLightingColor` ([line 3335](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attrs.scala#L3335))
  - `svgStopColor` ([line 3344](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attrs.scala#L3344))
  - `svgStopOpacity` ([line 3353](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attrs.scala#L3353))
  - `svgFillOpacity` ([line 3398](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attrs.scala#L3398))
  - `svgStroke` ([line 3442](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attrs.scala#L3442))
  - `svgStrokeOpacity` ([line 3486](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attrs.scala#L3486))
  Because `Attrs.all` uses `valuesForAllAttr`, `all` fails to detect conflicts with any of these 9 attributes, and `Attrs.values` is incomplete.

---

### 25. `AttrCmp.conflicts` fails to find transitive/chained conflicts
- **File**: [Attr.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attr.scala#L187-L198)
- **Code**:
  ```scala
  def conflicts(avs: Vector[AV]): Set[AV] = {
    @tailrec def go(untested: Vector[AV], conflicts: Set[AV]): Set[AV] =
      if (untested.length <= 1)
        conflicts
      else {
        val h = untested.head
        val (ko, ok) = untested.tail.partition(i => h.attr.cmp(i.attr).conflict)
        val c2 = if (ko.isEmpty) conflicts else conflicts + h ++ ko
        go(ok, c2)
      }
    go(avs, Set.empty)
  }
  ```
- **Defect**: `Attr.cmp(...).conflict` is not transitive (`marginLeft` conflicts with `margin`, and `margin` conflicts with `marginRight`, but `marginLeft` does not conflict with `marginRight`). When `avs = Vector(marginLeft(1.px), margin(2.px), marginRight(3.px))`, iteration 1 partitions `untested.tail` against `h = marginLeft`: `margin` goes into `ko` and `marginRight` goes into `ok`. Iteration 2 calls `go(Vector(marginRight), Set(marginLeft, margin))`, which immediately terminates because `untested.length <= 1`, failing to report `marginRight(3.px)` as conflicting with `margin(2.px)`.

---

### 26. `Pseudo.Composite` has broken `equals`/`hashCode` and makes `Cond.ordering` inconsistent with `Cond.equals`
- **Files**: [Pseudo.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Pseudo.scala#L37-L47) (and lines 72–78), [Cond.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Cond.scala#L56-L57)
- **Code**:
  ```scala
  case (a: Single      , b: Single      ) => Composite(a, SortedSet.empty[Single] + b)
  case (p: Single      , Composite(h, t)) => Composite(p, t + h)
  case (Composite(h, t), p: Single      ) => Composite(p, t + h)
  case (Composite(a, b), Composite(c, d)) => Composite(a, b ++ d + c)
  ```
- **Defects**:
  1. **Non-commutative `equals`/`hashCode`**: `(Hover & Focus)` is `Composite(Hover, SortedSet(Focus))` whereas `(Focus & Hover)` is `Composite(Focus, SortedSet(Hover))`. Both have `cssValue == ":focus:hover"`, yet `(Hover & Focus) != (Focus & Hover)`.
  2. **Non-associative `equals`/`hashCode`**: `((Hover & Focus) & Active)` is `Composite(Active, SortedSet(Focus, Hover))`, whereas `(Hover & (Focus & Active))` is `Composite(Hover, SortedSet(Active, Focus))`, so `((Hover & Focus) & Active) != (Hover & (Focus & Active))`.
  3. **Non-idempotent `Composite & Composite`**: `(Hover & Focus) & (Hover & Focus)` produces `Composite(Hover, SortedSet(Focus, Hover))` where `Hover` is duplicated in both `h` and `t`, so `(x & x) != x`.
  4. **Inconsistent with `Cond.ordering`**: Because `Cond.ordering` compares `_.toString` (which uses `Pseudo.cssValue`), `Cond.ordering.compare(&.hover.focus, &.focus.hover) == 0` while `(&.hover.focus == &.focus.hover)` is `false`, violating the `SortedMap` contract in `StyleS.data`.

---

### 27. `AVs` does not override `equals` or `hashCode`, breaking `StyleS` and `StyleA` value equality
- **Files**: [AVs.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/AVs.scala#L25), [Style.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Style.scala#L54)
- **Code**:
  ```scala
  final class AVs private[AVs](val order: NonEmptyVector[Attr], val data: Map[Attr, NonEmptyVector[Value]])
  ```
- **Defect**: `AVs` is a regular class that inherits `Object.equals` and `Object.hashCode` (reference equality), while `StyleS` and `StyleA` are `case class`es containing `AVs` (via `data: SortedMap[Cond, AVs]`). Two identically constructed `StyleS` or `StyleA` instances compare as unequal (`s1 != s2`) and have different hash codes.

---

### 28. `Cond.applyToStyle` does not update `StyleS.warnings` conditions or use `Compose`, and `unsafeRoot` drops outer pseudo-conditions
- **Files**: [Cond.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Cond.scala#L34-L49), [Css.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Css.scala#L66-L69), [Dsl.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Dsl.scala#L224)
- **Code**:
  ```scala
  // Cond.scala:34-49
  def applyToStyle(s: StyleS): StyleS = {
    val d = s.data.foldLeft(SortedMap.empty[Cond, AVs]){ case (q, (oldCond, av)) =>
      val newCond = this & oldCond
      val newValue = q.get(newCond).fold(av)(_ ++ av)
      q.updated(newCond, newValue)
    }
    val u = s.unsafeExts.map(e => e.copy(cond = this & e.cond))
    s.copy(data = d, unsafeExts = u)
  }
  ```
- **Defects**:
  1. `applyToStyle` does not update `s.warnings` (`w.copy(cond = this & w.cond)`), so warnings generated inside a conditional block (e.g., `&.hover(...)`) report `Cond.empty` instead of `&.hover`.
  2. When two entries in `s.data` map to the same `newCond` (`this & oldCond`), `applyToStyle` merges their `AVs` using `_ ++ av` instead of `Compose`, bypassing conflict resolution and warnings.
  3. In `Css.unsafeExt` (`val sel = u.sel(root + u.cond)`), `u.cond` is passed into `u.sel`. However, `Dsl.unsafeRoot` (`Dsl.scala:224`) defines `u.sel` as `_ => sel`, which ignores its argument and silently drops any pseudo-selector in `u.cond` (e.g., `&.hover(unsafeRoot(".foo")(...))`). Furthermore, `root + u.cond` uses `Cond.toString`, which embeds `@media ...` into the selector string if `u.cond` has media queries.

---

### 29. Composition warnings in `keyframe`, `unsafeChild`, `unsafeRoot`, `unsafeExt`, and `StyleSheet.Standalone` are silently discarded
- **Files**: [Register.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/mutable/Register.scala#L73) (and line 130), [StyleSheet.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/mutable/StyleSheet.scala#L100) (and lines 163–166), [Dsl.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Dsl.scala#L202-L203)
- **Defects**:
  1. `ToStyleUnsafeExts` (`Dsl.scala:203`) wraps `UnsafeExts` into `StyleS.empty.copy(unsafeExts = x)` with `warnings = Vector.empty`, and `Register.registerS` (`Register.scala:73`) only passes `s.warnings` to `emitRegistrationWarnings` without inspecting `s.unsafeExts`. Thus warnings inside `unsafeChild`, `unsafeRoot`, and `unsafeExt` are never emitted.
  2. `StyleSheet.Standalone` (`StyleSheet.scala:100`) converts all `&= / &-` rules into `UnsafeExt`s and never invokes `ErrorHandler.warn`, so conflict warnings in `StyleSheet.Standalone` are completely ignored.
  3. `Register.registerKeyframes` (`Register.scala:130`) calls `emitRegistrationWarnings(cn, Vector.empty)` instead of collecting warnings from `keyframes.frames`, discarding all warnings inside `keyframe(...)`.

---

### 30. `StringRenderer` and `Css.keyframes` defects: missing `;` in `formatTiny` keyframes, missing `fmt.done()` in `Raw`, inconsistent `font-family` quoting, and overwritten duplicate keyframe selectors
- **Files**: [Renderer.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Renderer.scala#L38-L46) (and lines 82–90, 115, 124–130), [Css.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Css.scala#L39-L40)
- **Defects**:
  1. **Missing `;` in `formatTiny` keyframes (`Renderer.scala:82-90, 146-147`)**: In `FormatSB.apply(CssEntry.Keyframes)`, `for (s <- styles) printCssKV(selO, s.mq, s.content)` calls `kv1` on the first property of *each* `s` in `styles`. Because `formatTiny`'s `kv1` does not emit a leading `;`, a keyframe containing multiple `CssEntry.Style` elements renders without a semicolon between them (e.g., `0%{color:redwidth:10px}`). Also, if a keyframe style has a media query `s.mq`, `@media` is never emitted around it, yet `formatPretty` still indents it via `mqIndent(mq)`.
  2. **`StringRenderer.Raw` omits `fmt.done()` (`Renderer.scala:38-46`)**: Unlike `StringRenderer.Default` (line 29), `StringRenderer.Raw` never calls `fmt.done()`.
  3. **Inconsistent and double-quoted `font-family` (`Renderer.scala:115, 124-130`, `Attrs.scala:1115`)**: `@font-face` quotes `fontFamily` containing spaces via `quoteIfNeeded` (`font-family: "My Font"`), whereas `Attrs.fontFamily(myFontFace)` emits `font-family: My Font` unquoted. Conversely, if `fontFamily` is already quoted (`"\"My Font\""`), `quoteIfNeeded` wraps it in quotes again (`font-family: ""My Font""`).
  4. **Duplicate keyframe selectors overwritten (`Css.scala:39-40`)**: `ListMap(kfs.frames.map(s => (s._1, styleA(s._2))):_*)` silently overwrites earlier frames that share the same percentage selector instead of merging them.

---

### 31. Thread-safety bugs in `Register.registerFontFace`, `internal.memo`, and `NameGen.nextShortPrefix`
- **Files**: [Register.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/mutable/Register.scala#L135-L141) (and lines 250–256), [package.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/package.scala#L64-L71)
- **Defects**:
  1. `Register` is documented as `Thread-safe` and synchronizes `registerS`, `registerKeyframes`, `styles`, `keyframes`, and `fontFaces` with `mutex { ... }`. However, `registerFontFace` (`Register.scala:135-141`) is missing `mutex { ... }`, mutating `_fontFaces :+= ff` unsafely, and also fails to call `emitRegistrationWarnings` when `_rendered` is true.
  2. `internal.memo` (`package.scala:64-71`) uses an unsynchronized `scala.collection.mutable.HashMap` and backs the global singleton `CanIUse2.prefixPlan` (`CanIUse2.scala:43`), risking race conditions and map corruption under concurrent JVM rendering.
  3. `NameGen.nextShortPrefix()` (`Register.scala:250-256`) reads and increments a mutable `var _nextShortPrefix` on the global `NameGen` object without synchronization.

---

### 32. Typo in attribute name: `fontVariantionSettings` instead of `fontVariationSettings`
- **Files**: [Attrs.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Attrs.scala#L1271) (and line 50), [generated.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/generated.scala#L808)
- **Code**:
  ```scala
  final def fontVariantionSettings = Attr.real("font-variation-settings", Transform keys CanIUse.fontVariationSettings)
  ```
- **Defect**: Misspelled with an extra `n` (`fontVariantionSettings` instead of `fontVariationSettings`).

---

### 33. `Literal.Typed.normal` is missing from `TypedLiteralAliases`
- **File**: [Values.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Values.scala#L11-L51) (and line 127)
- **Code**:
  ```scala
  // Values.scala:126-127
  sealed trait GapStyle
  object normal extends Literal("normal") with GapStyle
  ```
- **Defect**: Every typed literal in `Literal.Typed` is exported in `TypedLiteralAliases` except `Typed.normal`. Because `TypedLiteralAliases` is mixed into `DslBase` and `Literal` (`^`), `normal` is unavailable in the DSL for `gap(10.px, normal)` despite `GapStyle` and `ruleGapStyle_l` (`ValueT.scala:139`) being defined for it.

---

### 34. `Media.Query.+` returns `Vector[Query]` which cannot be used as a `Cond`, while `&` renders `, ` (logical OR)
- **File**: [Media.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Media.scala#L95-L102) (and line 253)
- **Code**:
  ```scala
  def +(q2: Query): Vector[Query] =
    Vector1(this) +: q2

  def &(q2: Query): Cond =
    Cond(None, this + q2)
  ```
- **Defect**: `Query.+` returns `Vector[Query]`, which has no conversion to `Cond` in `DslBase`, so `(media.screen + media.handheld)(...)` does not compile. Meanwhile, `q1 & q2` (and nested media query conditions `q1(q2(...))`) combines queries into `Vector[Query]`, which `Media.cssQueries` renders separated by `", "` (CSS logical **OR**) rather than `" and "` (logical **AND**).

---

### 35. Data and `ValueT` edge cases: `Length(0.0)` JVM/JS divergence, negative `Resolution`, `ValueT.mkString` escaping, and sub-millisecond `FiniteDuration` truncation
- **Files**: [Data.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Data.scala#L126-L178), [ValueT.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/ValueT.scala#L21-L26) (and lines 115–121)
- **Defects**:
  1. **`Length.value` zero check (`Data.scala:126-131`)**: `if (s == "0") "0" else s + u.value` checks `n.toString == "0"`. For `Double` `0.0`, `0.0.toString` is `"0.0"` on the JVM and `"0"` on Scala.js, so `0.0.px.value` renders `"0.0px"` on the JVM and `"0"` on Scala.js (and `(-0.0).px.value` renders `"-0.0px"`).
  2. **`Resolution.unary_-` and missing `UnivEq` (`Data.scala:167-178`)**: CSS `<resolution>` values cannot be negative, so `Resolution.unary_-` produces invalid CSS; `Resolution` is also missing a companion `UnivEq` instance.
  3. **`ValueT.mkString` does not escape quotes, backslashes, or newlines (`ValueT.scala:21-26`)**: Used by `quotes(...)`, `mkString` fails when a string contains both `'` and `"`, ends with `\`, or contains a newline.
  4. **`ruleTime_FD` truncates sub-millisecond durations (`ValueT.scala:115-121`)**: `d.toMillis` truncates `NANOSECONDS` and `MICROSECONDS` via integer division (e.g., `500.microseconds` becomes `"0ms"` instead of `"0.5ms"`).

---

### 36. `Domain.Pair.apply` throws `ArithmeticException: / by zero` when first domain is empty, and `OptionT`/`Disjunction` mishandle negative indices
- **File**: [Domain.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Domain.scala#L45-L60)
- **Code**:
  ```scala
  final class OptionT[A](of: Domain[A]) extends Domain[Option[A]] {
    override def size = of.size + 1
    override def apply(i: Int) = if (i == 0) None else Some(of(i - 1))
  }

  final class Pair[A, B](a: Domain[A], b: Domain[B]) extends Domain[(A, B)] {
    val as = a.size
    override val size = as * b.size
    override def apply(i: Int) = (a(i % as), b(i / as))
  }

  final class Disjunction[A, B](a: Domain[A], b: Domain[B]) extends Domain[Either[A, B]] {
    val as = a.size
    override val size = as + b.size
    override def apply(i: Int) = if (i < as) Left(a(i)) else Right(b(i - as))
  }
  ```
- **Defects**:
  1. In `Pair`, if `a` is empty (`as == 0`), calling `apply` evaluates `i % 0`, throwing `ArithmeticException: / by zero` instead of `IndexOutOfBoundsException`.
  2. In `Disjunction`, if `as == 0` and `i = -1`, `i < as` (`-1 < 0`) is `true` and delegates `-1` to `a(-1)`; in `OptionT`, if `i < 0` and `of` is a `Disjunction` whose left domain has size 1 (so `of` is `Disjunction(Empty, OptionT)`), out-of-bounds negative indices are not rejected upfront.

---

### 37. `Need.value` throws `NullPointerException` on retry if thunk throws an exception
- **File**: [Need.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Need.scala#L5-L9)
- **Code**:
  ```scala
  lazy val value: A = {
    val t = _thunk
    _thunk = null
    t()
  }
  ```
- **Defect**: `_thunk` is nulled out before `t()` is called. If `t()` throws an exception, Scala's `lazy val` remains uninitialized and retries on the next access, where `t` is now `null` and throws `NullPointerException`.

---

### 38. `GlobalRegistry.register()`, `styleF("name")`, and `registerFM`/`registerF2` inconsistencies
- **Files**: [GlobalRegistry.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/mutable/GlobalRegistry.scala#L54-L61), [StyleSheet.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/mutable/StyleSheet.scala#L149-L155), [Register.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/mutable/Register.scala#L100-L118)
- **Defects**:
  1. `StyleSheetRegistry.register(ss: ISS*)` does not guard `if (v.nonEmpty)` before calling `f(v)`. Calling `GlobalRegistry.register()` with no arguments triggers `_onRegistration(Vector.empty)`, which in `PlatformExports.addToDocumentOnRegistration` appends an empty `<style type="text/css"></style>` element to `document.head`.
  2. `DslMacros.MStyleF2` (`Dsl.scala:260-264`, returned by `styleF("manualName")`) only defines `.bool` and `.int`, omitting `def apply[I: StyleLookup](d: Domain[I])` (which is only on `MStyleF`). Thus `styleF("manualName")` cannot be used with custom domains.
  3. Unlike `Register.applyMacroName` (line 55), `Register.registerFM` and `Register.registerF2` (lines 100–118) assign synthetic class names even when `style.data.isEmpty && style.unsafeExts.isEmpty`.

---

### 39. `ScalatagsText.styleaToTextTag` prepends a redundant space to `class` attributes
- **File**: [ScalatagsText.scala](file:///Users/golly/projects/public/scalacss/ext-scalatags/shared/src/main/scala/scalacss/ScalatagsText.scala#L11-L13)
- **Code**:
  ```scala
  implicit final def styleaToTextTag(s: StyleA): Modifier = new Modifier {
    def applyTo(t: text.Builder) = t.appendAttr("class", Builder.GenericAttrValueSource(" " + s.htmlClass))
  }
  ```
- **Defect**: `scalatags.text.Builder.appendAttr("class", ...)` already joins multiple `class` attribute values with `" "`. Prepending `" " + s.htmlClass` produces a leading space (`class=" foo"`) and double spaces when multiple styles are applied (`class=" foo  bar"`, as seen in [ScalatagsTest.scala](file:///Users/golly/projects/public/scalacss/ext-scalatags/shared/src/test/scala/scalacss/ScalatagsTest.scala#L47)).

---

### 40. Build, configuration, and dead-code issues in `ModeMacros`, `project/Gen.scala`, `project/Lib.scala`, and `Cond.sortKey`
- **Files**: [core/jvm/src/main/scala-2/scalacss/internal/ModeMacros.scala](file:///Users/golly/projects/public/scalacss/core/jvm/src/main/scala-2/scalacss/internal/ModeMacros.scala#L32-L35) (and JS / Scala 3 counterparts), [Gen.scala](file:///Users/golly/projects/public/scalacss/project/Gen.scala#L19), [Lib.scala](file:///Users/golly/projects/public/scalacss/project/Lib.scala#L79), [Cond.scala](file:///Users/golly/projects/public/scalacss/core/shared/src/main/scala/scalacss/internal/Cond.scala#L13-L18)
- **Defects**:
  1. `ModeMacros.readConfig` evaluates `(Option(System.getProperty(key, null)) orElse Option(System.getenv(key))).map(_.trim.toLowerCase).filter(_.nonEmpty)`. If the JVM system property `scalacss.mode` is set to `""`, `Option(System.getProperty(...))` returns `Some("")`, preventing fallback to `System.getenv(key)` before `.filter(_.nonEmpty)` runs. (Also, the error message has a typo: `"Unrecognise option"`.)
  2. `project/Gen.scala` references the obsolete path `scala/japgolly/scalacss/Attrs.scala` and package `scalacss`.
  3. `project/Lib.scala:79` uses `Test / test := { if (scalaVersion.value.startsWith("2")) (Test / test).value }` instead of `Def.taskDyn`, which in sbt evaluates `(Test / test).value` unconditionally before the `if` condition.
  4. `Cond.sortKey` (`Cond.scala:13-18`) is a `private[Cond] val` that eagerly allocates and concatenates strings on every `Cond` instance, but is never referenced anywhere.
