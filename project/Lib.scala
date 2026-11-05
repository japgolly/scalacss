import sbt._
import Keys._
import org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbtcrossproject.CrossPlugin.autoImport._
import sbtcrossproject.CrossProject
import scalajscrossproject.ScalaJSCrossPlugin.autoImport._
import Dependencies._

object Lib {
  type CPE = CrossProject => CrossProject
  type PE = Project => Project

  class ConfigureBoth(val jvm: PE, val js: PE) {
    def jvmConfigure(f: PE) = new ConfigureBoth(f compose jvm, js)
    def  jsConfigure(f: PE) = new ConfigureBoth(jvm, f compose js)
  }

  def ConfigureBoth(both: PE) = new ConfigureBoth(both, both)

  implicit def _configureBothToCPE(p: ConfigureBoth): CPE =
    _.jvmConfigure(p.jvm).jsConfigure(p.js)

  def addCommandAliases(m: (String, String)*): PE = {
    val s = m.map(p => addCommandAlias(p._1, p._2)).reduce(_ ++ _)
    _.settings(s: _*)
  }

  implicit class CrossProjectExt(val cp: CrossProject) extends AnyVal {
    def bothConfigure(fs: PE*): CrossProject =
      fs.foldLeft(cp)((q, f) =>
        q.jvmConfigure(f).jsConfigure(f))
  }
  implicit def CrossProjectExtB(b: CrossProject.Builder) =
    new CrossProjectExt(b)

  def publicationSettings(ghProject: String) =
    ConfigureBoth(
      _.settings(
        developers := List(
          Developer("japgolly", "David Barri", "japgolly@gmail.com", url("https://japgolly.github.io/japgolly/")),
        ),
      )
    )
    .jsConfigure(
      sourceMapsToGithub(ghProject))

  def sourceMapsToGithub(ghProject: String): Project => Project =
    p => p.settings(
      scalacOptions ++= {
        val isDotty = scalaVersion.value startsWith "3"
        val ver     = version.value
        if (isSnapshot.value)
          Nil
        else {
          val a = p.base.toURI.toString.replaceFirst("[^/]+/?$", "")
          val g = s"https://raw.githubusercontent.com/japgolly/$ghProject"
          val flag = if (isDotty) "-scalajs-mapSourceURI" else "-P:scalajs:mapSourceURI"
          s"$flag:$a->$g/v$ver/" :: Nil
        }
      }
    )

  def preventPublication: PE =
    _.settings(publish / skip := true)

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

}
