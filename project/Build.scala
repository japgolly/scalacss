import sbt._
import sbt.Keys._
import com.jsuereth.sbtpgp.PgpKeys
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import org.scalajs.jsdependencies.sbtplugin.JSDependenciesPlugin
import org.scalajs.jsdependencies.sbtplugin.JSDependenciesPlugin.autoImport._
import org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, _}
import sbtrelease.ReleasePlugin.autoImport._
import scalajscrossproject.ScalaJSCrossPlugin.autoImport._
import Dependencies._
import Lib._

object ScalaCssBuild {

  private val ghProject = "scalacss"

  private val publicationSettings =
    Lib.publicationSettings(ghProject)

  def scalacCommonFlags = Seq(
    "-deprecation",
    "-unchecked",
    "-feature",
    "-language:postfixOps",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:existentials",
  )

  def scalac2Flags = Seq(
    "-opt:l:inline",
    "-opt-inline-from:japgolly.univeq.**",
    "-opt-inline-from:scalacss.**",
    "-Ywarn-dead-code",
    "-Ywarn-unused",
    // "-Ywarn-value-discard",
  )

  def scalac3Flags = Seq(
    "-source", "3.0-migration",
  )

  val commonSettings = ConfigureBoth(
    _.settings(
      organization                  := "com.github.japgolly.scalacss",
      homepage                      := Some(url("https://github.com/japgolly/scalacss")),
      licenses                      += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0")),
      scalaVersion                  := Ver.scala2,
      crossScalaVersions            := Seq(Ver.scala2, Ver.scala3),
      scalacOptions                ++= scalacCommonFlags,
      scalacOptions                ++= scalac2Flags.filter(_ => scalaVersion.value.startsWith("2")),
      scalacOptions                ++= scalac3Flags.filter(_ => scalaVersion.value.startsWith("3")),
   // incOptions                    := incOptions.value.withNameHashing(true).withLogRecompileOnMacro(false),
      updateOptions                 := updateOptions.value.withCachedResolution(true),
      releasePublishArtifactsAction := PgpKeys.publishSigned.value,
      releaseTagComment             := s"v${(ThisBuild / version).value}",
      releaseVcsSign                := true))

  def definesMacros = ConfigureBoth(
    _.settings(
      scalacOptions ++= {
        if (scalaVersion.value.startsWith("2"))
          "-language:experimental.macros" :: Nil
        else
          Nil
      },
      libraryDependencies ++=  {
        if (scalaVersion.value.startsWith("2"))
          List(
            Dep.scalaReflect.value,
            Dep.scalaCompiler.value % Provided,
          )
        else
          Nil
      },
    )
  )

  def utestSettings = ConfigureBoth(
    _.settings(
      libraryDependencies ++= Seq(
        Dep.utest.value % Test,
        Dep.microlibsTestUtil.value % Test,
      ),
      testFrameworks := Seq(new TestFramework("utest.runner.Framework")),
    )
  ).jsConfigure(
    _.settings(jsEnv := new JSDOMNodeJSEnv)
  )

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
        Dep.univEq.value,
        Dep.nyayaGen.value % Test,
        Dep.nyayaProp.value % Test,
        Dep.nyayaTest.value % Test,
        Dep.cats.value % Test,
      ),
    )
    .jsSettings(
      libraryDependencies += Dep.scalaJsDom.value,
    )
    .jvmSettings(
      initialCommands := "import scalacss._",
    )

  lazy val elisionTestJVM = elisionTest.jvm
  lazy val elisionTestJS  = elisionTest.js
  lazy val elisionTest = crossProject(JSPlatform, JVMPlatform)
    .in(file("elision-test"))
    .configureCross(commonSettings, utestSettings)
    .configure(preventPublication, onlyScala2)
    .dependsOn(core)
    .settings(
      scalacOptions ++= Seq("-Xelide-below", "OFF"),
    )

  lazy val extScalatagsJVM = extScalatags.jvm
  lazy val extScalatagsJS  = extScalatags.js
  lazy val extScalatags = crossProject(JSPlatform, JVMPlatform)
    .in(file("ext-scalatags"))
    .dependsOn(core)
    .configureCross(commonSettings, publicationSettings, utestSettings)
    .configure(onlyScala2)
    .settings(
      moduleName := "ext-scalatags",
      libraryDependencies ++= Seq(
        Dep.scalatags.value,
        Dep.cats.value % Test,
      ),
    )

  lazy val extReact = project
    .in(file("ext-react"))
    .enablePlugins(ScalaJSPlugin)
    .configure(commonSettings.js, publicationSettings.js, utestSettings.js)
    .configure(addReactJsDependencies(Test))
    .dependsOn(coreJS)
    .settings(
      moduleName := "ext-react",
      libraryDependencies ++= Seq(
        Dep.scalaJsReactCoreGen.value % Provided,
        Dep.scalaJsReactDummy.value % Provided,
        Dep.scalaJsReactTest.value % Test,
        Dep.cats.value % Test,
      ),
    )
}
