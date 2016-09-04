import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import ScalaJSPlugin.autoImport._
import Lib._

object ScalaCssBuild {

  private val ghProject = "scalacss"

  private val publicationSettings =
    Lib.publicationSettings(ghProject)

  object Ver {
    final val MTest         = "0.4.3"
    final val Nyaya         = "0.7.2"
    final val ReactJs       = "15.3.1"
    final val Scala211      = "2.11.8"
    final val ScalaJsDom    = "0.9.1"
    final val ScalaJsReact  = "0.11.1"
    final val Scalatags     = "0.6.0"
    final val Scalaz        = "7.2.5"
    final val UnivEq        = "1.0.1"
  }

  val commonSettings = ConfigureBoth(
    _.settings(
      organization       := "com.github.japgolly.scalacss",
      version            := "0.5.0",
      homepage           := Some(url("https://github.com/japgolly/scalacss")),
      licenses           += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0")),
      scalaVersion       := Ver.Scala211,
      // crossScalaVersions := Seq("2.10.5", Scala211),
      scalacOptions     ++= Seq("-deprecation", "-unchecked", "-feature",
                              "-language:postfixOps", "-language:implicitConversions",
                              "-language:higherKinds", "-language:existentials"),
      shellPrompt in ThisBuild := ((s: State) => Project.extract(s).currentRef.project + "> "),
      triggeredMessage         := Watched.clearWhenTriggered,
      incOptions               := incOptions.value.withNameHashing(true),
      updateOptions            := updateOptions.value.withCachedResolution(true))
    .configure(
      addCommandAliases(
        "/"   -> "project root",
        "L"   -> "root/publishLocal",
        "C"   -> "root/clean",
        "T"   -> ";root/clean;root/test",
        "TL"  -> ";T;L",
        "c"   -> "compile",
        "tc"  -> "test:compile",
        "t"   -> "test",
        "to"  -> "test-only",
        "tq"  -> "test-quick",
        "cc"  -> ";clean;compile",
        "ctc" -> ";clean;test:compile",
        "ct"  -> ";clean;test")))
    .jsConfigure(
      _.settings(scalaJSUseRhino := false))

  def definesMacros = ConfigureBoth(
    _.settings(
      scalacOptions += "-language:experimental.macros",
      libraryDependencies ++= Seq(
        "org.scala-lang" % "scala-reflect" % Ver.Scala211,
        // "org.scala-lang" % "scala-library" % Ver.Scala211,
        "org.scala-lang" % "scala-compiler" % Ver.Scala211 % "provided")))

  def utestSettings = ConfigureBoth(
    _.settings(
      libraryDependencies += "com.lihaoyi" %%% "utest" % Ver.MTest % "test",
      testFrameworks      += new TestFramework("utest.runner.Framework")))
    .jsConfigure(
      // Not mandatory; just faster.
      _.settings(jsEnv in Test := new PhantomJS2Env(scalaJSPhantomJSClassLoader.value)))

  // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

  lazy val root =
    Project("root", file("."))
      .configure(commonSettings.jvm, preventPublication)
      .aggregate(rootJVM, rootJS, BenchBuild.bench)

  lazy val rootJVM =
    Project("JVM", file(".rootJVM"))
      .configure(commonSettings.jvm, preventPublication)
      .aggregate(coreJVM, extScalatagsJVM)

  lazy val rootJS =
    Project("JS", file(".rootJS"))
      .configure(commonSettings.jvm, preventPublication)
      .aggregate(coreJS, extScalatagsJS, extReact)

  // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

  lazy val coreJVM = core.jvm
  lazy val coreJS  = core.js
  lazy val core = crossProject
    .configure(
      commonSettings,
      publicationSettings,
      definesMacros,
      //, Gen.attrAliases
      utestSettings)
    .settings(
      libraryDependencies ++= Seq(
        "com.github.japgolly.univeq" %%% "univeq"      % Ver.UnivEq,
        "com.github.japgolly.nyaya"  %%% "nyaya-gen"   % Ver.Nyaya  % "test",
        "com.github.japgolly.nyaya"  %%% "nyaya-prop"  % Ver.Nyaya  % "test",
        "com.github.japgolly.nyaya"  %%% "nyaya-test"  % Ver.Nyaya  % "test",
        "org.scalaz"                 %%% "scalaz-core" % Ver.Scalaz % "test"))
    .jsSettings(
      libraryDependencies += "org.scala-js" %%% "scalajs-dom" % Ver.ScalaJsDom)
    .jvmSettings(
      initialCommands := "import scalacss._")

  lazy val extScalatagsJVM = extScalatags.jvm
  lazy val extScalatagsJS  = extScalatags.js
  lazy val extScalatags = crossProject
    .in(file("ext-scalatags"))
    .configure(commonSettings, publicationSettings)
    .dependsOn(core)
    .configure(utestSettings)
    .settings(
      moduleName := "ext-scalatags",
      libraryDependencies ++= Seq(
        "com.lihaoyi" %%% "scalatags"   % Ver.Scalatags,
        "org.scalaz"  %%% "scalaz-core" % Ver.Scalaz % "test"))

  lazy val extReact = project
    .in(file("ext-react"))
    .enablePlugins(ScalaJSPlugin)
    .configure(commonSettings.js, publicationSettings.js, utestSettings.js)
    .dependsOn(coreJS)
    .settings(
      moduleName := "ext-react",
      libraryDependencies ++= Seq(
        "com.github.japgolly.scalajs-react" %%% "core"        % Ver.ScalaJsReact,
        "com.github.japgolly.scalajs-react" %%% "test"        % Ver.ScalaJsReact % "test",
        "org.scalaz"                        %%% "scalaz-core" % Ver.Scalaz       % "test"),
      jsDependencies ++= Seq(
        "org.webjars.bower" % "react" % Ver.ReactJs % "test"
          /        "react-with-addons.js"
          minified "react-with-addons.min.js"
          commonJSName "React",
        "org.webjars.bower" % "react" % Ver.ReactJs % "test"
          /         "react-dom.js"
          minified  "react-dom.min.js"
          dependsOn "react-with-addons.js"
          commonJSName "ReactDOM",
        "org.webjars.bower" % "react" % Ver.ReactJs % "test"
          /         "react-dom-server.js"
          minified  "react-dom-server.min.js"
          dependsOn "react-dom.js"
          commonJSName "ReactDOMServer"),
      requiresDOM := true)
}
