# ScalaCSS Improvement, Fix & Optimisation Suggestions

## Performance & Memory Optimisations

## JS / DOM & Platform Detection

## CSS Spec Completeness, `CanIUse` Wiring & API Polish

29. **Clean up `ModeMacros` duplication and config bugs (`core/{js,jvm}/src/main/scala-{2,3}/scalacss/internal/ModeMacros.scala`)**
    3. Fix `"Unrecognise option..."` typo and use `_root_.scalacss.*` consistently in Scala 3 quotes (`scala-3/.../ModeMacros.scala:44,47` and `jvm/.../scala-3/...:36,39`).

---

## Build, Tooling & Tests

30. **Fix broken scripts in `bin/*` and `downstream-tests/run`, plus `misc/caniuse.scala`**
    3. `bin/ci:36` runs `free -h` under `set -euo pipefail`, which fails on macOS; `downstream-tests/run:18` uses Bash 4+ `declare -A`, which fails on macOS's default Bash 3.2.
    4. `bin/generate-aliases:24-27` splits blocks on `\n[ \t]*\n+`, silently dropping consecutive declarations without a blank line (currently omits `fontVariantionSettings` at `Attrs.scala:1247`).

31. **Improve build configuration and test hygiene (`project/Build.scala`, `build.sbt`, `scalafix.sbt`, tests)**
    1. `project/Build.scala:36` has `-Ywarn-value-discard` commented out (allowing bugs like #8 in `Macros.scala:237` to go unnoticed).
    2. `build.sbt:11` disables `parallelExecution` globally (`ThisBuild / parallelExecution := false`) due to global mutable state in `NameGen.short` (`_nextShortPrefix`); isolating `NameGen.short` state per test or `Register` would allow parallel test execution.
    3. `ErrorHandler.Noisy.badInput` (`Register.scala:299`) calls `s.domain.iterator.mkString(",")`, which can allocate huge strings / OOM on large ranges (truncating to e.g. `.take(20)` avoids this).
