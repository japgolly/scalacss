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
    final val MTest         = "0.6.7"
    final val Nyaya         = "0.8.1"
    final val ReactJs       = "15.5.4"
    final val Scala211      = "2.11.11"
    final val Scala212      = "2.12.8"
    final val ScalaJsDom    = "0.9.7"
    final val ScalaJsReact  = "1.4.1"
    final val Scalatags     = "0.6.8"
    final val Scalaz        = "7.2.18"
    final val UnivEq        = "1.0.6"
  }

  def scalacFlags = Def.setting(
    Seq(
      "-deprecation",
      "-unchecked",
      // "-Ywarn-dead-code",
      // "-Ywarn-unused",
      // "-Ywarn-value-discard",
      "-feature",
      "-language:postfixOps",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials")
    ++ (scalaVersion.value match {
      case x if x startsWith "2.11." => "-target:jvm-1.6" :: Nil
      case x if x startsWith "2.12." => "-target:jvm-1.8" :: "-opt:l:method" :: Nil
    }))

  val commonSettings = ConfigureBoth(
    _.settings(
      organization                  := "com.github.japgolly.scalacss",
      homepage                      := Some(url("https://github.com/japgolly/scalacss")),
      licenses                      += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0")),
      scalaVersion                  := Ver.Scala212,
      crossScalaVersions            := Seq(Ver.Scala211, Ver.Scala212),
      scalacOptions                ++= scalacFlags.value,
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
        "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided")))

  def utestSettings = ConfigureBoth(
    _.settings(
      libraryDependencies += "com.lihaoyi" %%% "utest" % Ver.MTest % "test",
      testFrameworks      += new TestFramework("utest.runner.Framework")))
    .jsConfigure(
      _.settings(jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv))

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
          commonJSName "ReactDOMServer"))
}
