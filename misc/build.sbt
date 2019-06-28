scalaVersion := "2.12.4"

libraryDependencies += "io.argonaut" %% "argonaut-scalaz" % "6.2"
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.28"

scalacOptions ++= Seq(
  "-unchecked", "-deprecation",
  "-feature", "-language:postfixOps", "-language:implicitConversions", "-language:higherKinds", "-language:existentials")

initialCommands := "import argonaut._, Argonaut._, scalaz._, Scalaz._"

