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

  object Ver {
    val Microlibs       = "2.0"
    val MTest           = "0.7.2"
    val Scala212        = "2.12.11"
    val Scala213        = "2.13.1"
    val ScalaCollCompat = "2.1.4"
    val ScalaGraal      = "0.4.0"
    val ScalaJsDom      = "0.9.8"
    val UnivEq          = "1.2.0"
  }

  def scalacFlags =
    Seq(
      "-deprecation",
      "-unchecked",
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-opt-inline-from:japgolly.univeq.**",
      "-opt-inline-from:scalacss.**",
      "-opt:l:inline",
      "-Xlint:infer-any",
      // "-Yno-adapted-args",
      "-Yno-generic-signatures",
      "-Ypatmat-exhaust-depth", "off",
      "-Ywarn-dead-code",
      // "-Ywarn-inaccessible",
      // "-Ywarn-infer-any",
      "-Ywarn-unused",
      "-Ywarn-value-discard")

  val commonSettings = ConfigureBoth(
    _.settings(
      organization                  := "com.github.japgolly.scalacss",
      homepage                      := Some(url("https://github.com/japgolly/scalacss")),
      licenses                      += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0")),
      scalaVersion                  := Ver.Scala213,
      crossScalaVersions            := Seq(Ver.Scala213, Ver.Scala212),
      scalacOptions                ++= scalacFlags,
      scalacOptions in Test        --= Seq("-Ywarn-unused", "-Ywarn-value-discard"),
      shellPrompt in ThisBuild      := ((s: State) => Project.extract(s).currentRef.project + "> "),
   // incOptions                    := incOptions.value.withNameHashing(true).withLogRecompileOnMacro(false),
      updateOptions                 := updateOptions.value.withCachedResolution(true),
      releasePublishArtifactsAction := PgpKeys.publishSigned.value,
      releaseTagComment             := s"v${(version in ThisBuild).value}",
      releaseVcsSign                := true))

  private val ghProject = "scalacss"

  private val publicationSettings =
    Lib.publicationSettings(ghProject)

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
      .aggregate(coreMacros, coreJVM, coreJS)

  // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

  lazy val coreMacros = project
    .in(file("core-macros"))
    .configure(
      commonSettings.jvm,
      publicationSettings.jvm,
      definesMacros.jvm,
      utestSettings.jvm)
    .settings(
      libraryDependencies ++= Seq(
        "com.github.japgolly.scala-graal" %% "core"   % Ver.ScalaGraal,
        "com.github.japgolly.univeq"      %% "univeq" % Ver.UnivEq))

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
        "org.scala-lang.modules"     %%% "scala-collection-compat" % Ver.ScalaCollCompat))
    .jsSettings(
      libraryDependencies += "org.scala-js" %%% "scalajs-dom" % Ver.ScalaJsDom)
}
