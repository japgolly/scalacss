# ScalaCSS Improvement, Fix & Optimisation Suggestions

## Performance & Memory Optimisations

## JS / DOM & Platform Detection

27. **Harden `PlatformExports.installStyle` and `addToDocumentOnRegistration` (`core/js/src/main/scala/scalacss/defaults/PlatformExports.scala`)**
    1. `installStyle` (line 23) assumes `document.head` is non-null (throws in SVG/XML docs or early head scripts; fallback to `Option(document.head).getOrElse(document.documentElement)`).
    2. `addToDocumentOnRegistration` (lines 37-45) creates empty `<style>` elements when `ss` renders to `""` (e.g., `addClassNames`-only sheets or empty `register()` calls), ignores custom `Renderer[HTMLStyleElement]` instances, and joins sheets with `"\n"` even under `formatTiny`.

---

## CSS Spec Completeness, `CanIUse` Wiring & API Polish

28. **Clean up and complete CSS properties (`Attrs.scala`, `Values.scala`, `ValueT.scala`, `Media.scala`, `Pseudo.scala`)**
    1. Fix public typo `RorderRadiusOps` (`Attrs.scala:2848, 2853, 2857`) -> `BorderRadiusOps` (with a deprecated type alias for binary/source compat).
    2. Add missing CSS features/overloads: `ValueT.TypedAttrBase.revert` (`ValueT.scala:151-170`), `animationFillMode.none` (`Attrs.scala:180`), multi-value/comma-separated support on `animation*`/`transitionTimingFunction` and fractional `animationIterationCount`, `overflow.clip` and 2-arg `overflow(x, y)`, `Typed.TimingFunction` rule in `ValueT.Rules`, `TypedLiteralAliases.count`, decimal/angle-unit support in `ColorLiteral` modern `rgb`/`hsl`, `:root` / `:focus-visible` / `:focus-within` / `:is` / `:where` / `::placeholder` / `|=` / `*=` in `Pseudo.scala`, and `prefers-color-scheme` / `prefers-reduced-motion` / `pointer` / `hover` media features in `Media.scala`.
    3. Resolve explicit code TODOs (`Pseudo.scala:6`, `Attrs.scala:788`, `Attrs.scala:1651`, `Data.scala:169`).

29. **Clean up `ModeMacros` duplication and config bugs (`core/{js,jvm}/src/main/scala-{2,3}/scalacss/internal/ModeMacros.scala`)**
    1. `readConfig` is duplicated across all 4 files and places `.filter(_.nonEmpty)` *after* `orElse`, so an empty `-Dscalacss.mode=` prevents falling back to env vars.
    2. `System.getenv("scalacss.mode")` uses a dot (not exportable in POSIX shells; consider also supporting `SCALACSS_MODE`).
    3. Fix `"Unrecognise option..."` typo and use `_root_.scalacss.*` consistently in Scala 3 quotes (`scala-3/.../ModeMacros.scala:44,47` and `jvm/.../scala-3/...:36,39`).

---

## Build, Tooling & Tests

30. **Fix broken scripts in `bin/*` and `downstream-tests/run`, plus `misc/caniuse.scala`**
    1. `bin/stats:11,16` and `bin/untyped:4` point to non-existent `core/src/main/scala/scalacss/Attrs.scala` (should be `core/shared/src/main/scala/scalacss/internal/Attrs.scala`) and use `grep -P` (fails on macOS BSD `grep`); `bin/stats:6-8` and `bin/todo-list:6` use `*/src` which misses `core/{shared,js,jvm}/src` and `ext-scalatags/{shared,js,jvm}/src`.
    2. `bin/generate-platformjs:15` points to non-existent `core/src/main/scala-js/scalacss/js/PlatformJs.scala` (should be `core/js/src/main/scala/scalacss/js/PlatformJs.scala`).
    3. `bin/ci:36` runs `free -h` under `set -euo pipefail`, which fails on macOS; `downstream-tests/run:18` uses Bash 4+ `declare -A`, which fails on macOS's default Bash 3.2.
    4. `bin/generate-aliases:24-27` splits blocks on `\n[ \t]*\n+`, silently dropping consecutive declarations without a blank line (currently omits `fontVariantionSettings` at `Attrs.scala:1247`).

31. **Improve build configuration and test hygiene (`project/Build.scala`, `build.sbt`, `scalafix.sbt`, tests)**
    1. `project/Build.scala:36` has `-Ywarn-value-discard` commented out (allowing bugs like #8 in `Macros.scala:237` to go unnoticed).
    2. `build.sbt:11` disables `parallelExecution` globally (`ThisBuild / parallelExecution := false`) due to global mutable state in `NameGen.short` (`_nextShortPrefix`); isolating `NameGen.short` state per test or `Register` would allow parallel test execution.
    3. `ErrorHandler.Noisy.badInput` (`Register.scala:299`) calls `s.domain.iterator.mkString(",")`, which can allocate huge strings / OOM on large ranges (truncating to e.g. `.take(20)` avoids this).
