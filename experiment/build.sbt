scalaVersion := "2.11.6"

libraryDependencies += "org.scalaz" %% "scalaz-effect" % "7.1.1"

libraryDependencies += "com.chuusai" %% "shapeless" % "2.1.0"

scalacOptions ++= Seq(
  "-unchecked", "-deprecation",
  "-feature", "-language:postfixOps", "-language:implicitConversions", "-language:higherKinds", "-language:existentials")

initialCommands := "import scalaz._, shapeless._, shapeless.syntax.singleton._"

