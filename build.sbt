name      := "ScalaCSS"
startYear := Some(2015)

val root              = ScalaCssBuild.root
val rootJVM           = ScalaCssBuild.rootJVM
val rootJS            = ScalaCssBuild.rootJS

val core              = ScalaCssBuild.core
val coreJVM           = ScalaCssBuild.coreJVM
val coreJS            = ScalaCssBuild.coreJS

val extScalatags      = ScalaCssBuild.extScalatags
val extScalatagsJVM   = ScalaCssBuild.extScalatagsJVM
val extScalatagsJS    = ScalaCssBuild.extScalatagsJS
val extReact          = ScalaCssBuild.extReact

val bench             = BenchBuild.bench
val benchReactWithout = BenchBuild.benchReactWithout
val benchReactWith    = BenchBuild.benchReactWith
val benchBig          = BenchBuild.benchBig
