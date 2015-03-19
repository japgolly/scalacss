scalaVersion := "2.11.6"

libraryDependencies += "io.argonaut" %% "argonaut" % "6.1-M5" exclude("org.scala-lang", "scala-compiler")

scalacOptions ++= Seq(
  "-unchecked", "-deprecation",
  "-feature", "-language:postfixOps", "-language:implicitConversions", "-language:higherKinds", "-language:existentials")

initialCommands := "import argonaut._, Argonaut._"

