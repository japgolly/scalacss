# ScalaCSS Bug & Defect Report

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
