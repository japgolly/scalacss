import sbt._
import Keys._
import com.typesafe.sbt.pgp.PgpKeys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import ScalaJSPlugin._
import ScalaJSPlugin.autoImport._
import Dialect._
import Typical.{settings => _, _}

object ScalaCSS extends Build {

  val Scala211 = "2.11.6"

  val commonSettings: CDS =
    CDS.all(
      _.settings(
        organization       := "com.github.japgolly.scalacss",
        version            := "0.1.0-SNAPSHOT",
        homepage           := Some(url("https://github.com/japgolly/scalacss")),
        licenses           += ("LGPL v2.1+" -> url("http://www.gnu.org/licenses/lgpl-2.1.txt")),
        scalaVersion       := Scala211,
        // Needs Shapeless for Scala 2.10
        // crossScalaVersions := Seq("2.10.5", Scala211),
        scalacOptions     ++= Seq("-deprecation", "-unchecked", "-feature",
                                "-language:postfixOps", "-language:implicitConversions",
                                "-language:higherKinds", "-language:existentials"),
        updateOptions      := updateOptions.value.withCachedResolution(true))
      .configure(addCommandAliases(
        "/"   -> "project core",
        "cj"  -> "project core-jvm",
        "cjs" -> "project core-js",
        "qc"  -> "~ ;clear ;core-jvm/compile",
        "qtc" -> "~ ;clear ;core-jvm/test:compile",
        "qt"  -> "~ ;clear ;core-jvm/test"
      ))
    ) :+ Typical.settings("scalacss")

  object scalaz {
    private def m(n: String) = Library("org.scalaz", "scalaz-"+n, "7.1.1").myJsFork("scalaz").jsVersion(_+"-2")
    val core       = m("core")
    val effect     = m("effect") > core
    val concurrent = m("concurrent") > effect
  }
  object nyaya {
    private def m(n: String) = Library("com.github.japgolly.nyaya", "nyaya-"+n, "0.5.8")
    val core = m("core")
    val test = m("test")
  }
  val shapeless = Library("com.chuusai", "shapeless", "2.1.0").myJsFork("shapeless").jsVersion(_+"-2")

  // ==============================================================================================
  override def rootProject = Some(core)

  core.settings(libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3" % Test)

  lazy val (core, coreJvm, coreJs) =
    crossDialectProject("core", commonSettings
      .configure(utestSettings())
      .addLibs(scalaz.core, shapeless, nyaya.test % Test)
      .jj(_ => initialCommands := "import shapeless._, ops.hlist._, syntax.singleton._, japgolly.scalacss._")
    )
}
