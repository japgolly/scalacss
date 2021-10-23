name := "ScalaCSS"

ThisBuild / homepage      := Some(url("https://github.com/japgolly/scalacss"))
ThisBuild / licenses      += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0"))
ThisBuild / organization  := "com.github.japgolly.scalacss"
ThisBuild / shellPrompt   := ((s: State) => Project.extract(s).currentRef.project + "> ")
ThisBuild / startYear     := Some(2015)
ThisBuild / versionScheme := Some("early-semver")

// Mutability & NameGen causes occasional test failures
ThisBuild / parallelExecution := false

val root              = ScalaCssBuild.root
val rootJVM           = ScalaCssBuild.rootJVM
val rootJS            = ScalaCssBuild.rootJS

val coreJVM           = ScalaCssBuild.coreJVM
val coreJS            = ScalaCssBuild.coreJS
val extScalatagsJVM   = ScalaCssBuild.extScalatagsJVM
val extScalatagsJS    = ScalaCssBuild.extScalatagsJS
val extReact          = ScalaCssBuild.extReact

val bench             = BenchBuild.bench
val benchReactWithout = BenchBuild.benchReactWithout
val benchReactWith    = BenchBuild.benchReactWith
val benchBig          = BenchBuild.benchBig
