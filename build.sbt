name      := "ScalaCSS"
startYear := Some(2015)

ThisBuild / shellPrompt := ((s: State) => Project.extract(s).currentRef.project + "> ")

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
