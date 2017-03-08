name      := "ScalaCSS"
startYear := Some(2015)

version in ThisBuild := "0.5.3-RC1"

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
