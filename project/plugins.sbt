val scalaJSVersion = Option(System.getenv("SCALAJS_VERSION")).getOrElse("0.6.32")

libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.0.0"

addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % scalaJSVersion)
addSbtPlugin("com.jsuereth"       % "sbt-pgp"                  % "1.1.2-1")
addSbtPlugin("com.github.gseitz"  % "sbt-release"              % "1.0.13")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.0.0")
addSbtPlugin("org.scala-js"       % "sbt-jsdependencies"       % "1.0.0")