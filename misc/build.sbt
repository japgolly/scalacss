scalaVersion := "2.11.8"

libraryDependencies += "io.argonaut" %% "argonaut" % "6.1-M5" exclude("org.scala-lang", "scala-compiler")

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.1"

scalacOptions ++= Seq(
  "-unchecked", "-deprecation",
  "-feature", "-language:postfixOps", "-language:implicitConversions", "-language:higherKinds", "-language:existentials")

initialCommands := "import argonaut._, Argonaut._, scalaz._, Scalaz._"

