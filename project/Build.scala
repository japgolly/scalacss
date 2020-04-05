import sbt._
import sbt.Keys._
import com.typesafe.sbt.pgp.PgpKeys
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.{crossProject => _, CrossType => _, _}
import sbtcrossproject.CrossPlugin.autoImport._
import sbtrelease.ReleasePlugin.autoImport._
import scalajscrossproject.ScalaJSCrossPlugin.autoImport._
import Lib._

object ScalaCssBuild {

  private val ghProject = "scalacss"

  private val publicationSettings =
    Lib.publicationSettings(ghProject)

  object Ver {
    val Microlibs       = "2.0"
    val MTest           = "0.7.1"
    val Nyaya           = "0.9.0"
  //val ReactJs         = "16.7.0"
    val ReactJs         = "15.5.4"
    val Scala212        = "2.12.10"
    val Scala213        = "2.13.1"
    val ScalaCollCompat = "2.1.3"
    val ScalaJsDom      = "0.9.8"
    val ScalaJsReact    = "1.5.0"
    val Scalatags       = "0.8.6"
    val Scalaz          = "7.2.30"
    val UnivEq          = "1.1.0"
  }

  def scalacFlags =
    Seq(
      "-deprecation",
      "-unchecked",
      "-feature",
      "-language:postfixOps",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials",
      "-opt:l:inline",
      "-opt-inline-from:japgolly.univeq.**",
      "-opt-inline-from:scalacss.**")
      // "-Ywarn-dead-code",
      // "-Ywarn-unused",
      // "-Ywarn-value-discard",

  val commonSettings = ConfigureBoth(
    _.settings(
      organization                  := "com.github.japgolly.scalacss",
      homepage                      := Some(url("https://github.com/japgolly/scalacss")),
      licenses                      += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0")),
      scalaVersion                  := Ver.Scala213,
      crossScalaVersions            := Seq(Ver.Scala213, Ver.Scala212),
      scalacOptions                ++= scalacFlags,
      scalacOptions in Test        --= Seq("-Ywarn-unused"),
      shellPrompt in ThisBuild      := ((s: State) => Project.extract(s).currentRef.project + "> "),
   // incOptions                    := incOptions.value.withNameHashing(true).withLogRecompileOnMacro(false),
      updateOptions                 := updateOptions.value.withCachedResolution(true),
      releasePublishArtifactsAction := PgpKeys.publishSigned.value,
      releaseTagComment             := s"v${(version in ThisBuild).value}",
      releaseVcsSign                := true))

  def definesMacros = ConfigureBoth(
    _.settings(
      scalacOptions += "-language:experimental.macros",
      libraryDependencies ++= Seq(
        "org.scala-lang" % "scala-reflect" % scalaVersion.value,
        // "org.scala-lang" % "scala-library" % scalaVersion.value,
        "org.scala-lang" % "scala-compiler" % scalaVersion.value % Provided)))

  def utestSettings = ConfigureBoth(
    _.settings(
      libraryDependencies ++= Seq(
        "com.lihaoyi"                   %%% "utest"     % Ver.MTest     % Test,
        "com.github.japgolly.microlibs" %%% "test-util" % Ver.Microlibs % Test),
      testFrameworks := Seq(new TestFramework("utest.runner.Framework"))))
    .jsConfigure(
      _.settings(jsEnv := new JSDOMNodeJSEnv))

  // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

  lazy val root =
    Project("root", file("."))
      .configure(commonSettings.jvm, preventPublication)
      .aggregate(rootJVM, rootJS, BenchBuild.bench)

  lazy val rootJVM =
    Project("JVM", file(".rootJVM"))
      .configure(commonSettings.jvm, preventPublication)
      .aggregate(coreJVM, elisionTestJVM, extScalatagsJVM)

  lazy val rootJS =
    Project("JS", file(".rootJS"))
      .configure(commonSettings.jvm, preventPublication)
      .aggregate(coreJS, elisionTestJS, extScalatagsJS, extReact)

  // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

  lazy val coreJVM = core.jvm
  lazy val coreJS  = core.js
  lazy val core = crossProject(JSPlatform, JVMPlatform)
    .configureCross(
      commonSettings,
      publicationSettings,
      definesMacros,
      utestSettings)
    .settings(
      libraryDependencies ++= Seq(
        "com.github.japgolly.univeq" %%% "univeq"                  % Ver.UnivEq,
        "org.scala-lang.modules"     %%% "scala-collection-compat" % Ver.ScalaCollCompat,
        "com.github.japgolly.nyaya"  %%% "nyaya-gen"               % Ver.Nyaya  % Test,
        "com.github.japgolly.nyaya"  %%% "nyaya-prop"              % Ver.Nyaya  % Test,
        "com.github.japgolly.nyaya"  %%% "nyaya-test"              % Ver.Nyaya  % Test,
        "org.scalaz"                 %%% "scalaz-core"             % Ver.Scalaz % Test))
    .jsSettings(
      libraryDependencies += "org.scala-js" %%% "scalajs-dom" % Ver.ScalaJsDom)
    .jvmSettings(
      initialCommands := "import scalacss._")

  lazy val elisionTestJVM = elisionTest.jvm
  lazy val elisionTestJS  = elisionTest.js
  lazy val elisionTest = crossProject(JSPlatform, JVMPlatform)
    .in(file("elision-test"))
    .configureCross(commonSettings, utestSettings)
    .configure(preventPublication)
    .dependsOn(core)
    .settings(
      scalacOptions ++= Seq("-Xelide-below", "OFF")
    )

  lazy val extScalatagsJVM = extScalatags.jvm
  lazy val extScalatagsJS  = extScalatags.js
  lazy val extScalatags = crossProject(JSPlatform, JVMPlatform)
    .in(file("ext-scalatags"))
    .configureCross(commonSettings, publicationSettings)
    .dependsOn(core)
    .configureCross(utestSettings)
    .settings(
      moduleName := "ext-scalatags",
      libraryDependencies ++= Seq(
        "com.lihaoyi" %%% "scalatags"   % Ver.Scalatags,
        "org.scalaz"  %%% "scalaz-core" % Ver.Scalaz % Test))

  lazy val extReact = project
    .in(file("ext-react"))
    .enablePlugins(ScalaJSPlugin)
    .configure(commonSettings.js, publicationSettings.js, utestSettings.js)
    .dependsOn(coreJS)
    .settings(
      moduleName := "ext-react",
      libraryDependencies ++= Seq(
        "com.github.japgolly.scalajs-react" %%% "core"        % Ver.ScalaJsReact,
        "com.github.japgolly.scalajs-react" %%% "test"        % Ver.ScalaJsReact % Test,
        "org.scalaz"                        %%% "scalaz-core" % Ver.Scalaz       % Test),
      jsDependencies ++= Seq(
        "org.webjars.bower" % "react" % Ver.ReactJs % Test
          /        "react-with-addons.js"
          minified "react-with-addons.min.js"
          commonJSName "React",
        "org.webjars.bower" % "react" % Ver.ReactJs % Test
          /         "react-dom.js"
          minified  "react-dom.min.js"
          dependsOn "react-with-addons.js"
          commonJSName "ReactDOM",
        "org.webjars.bower" % "react" % Ver.ReactJs % Test
          /         "react-dom-server.js"
          minified  "react-dom-server.min.js"
          dependsOn "react-dom.js"
          commonJSName "ReactDOMServer"))
}
