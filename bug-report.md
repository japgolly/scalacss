# ScalaCSS Bug & Defect Report

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
