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

  def generateAttrAliases: CDS = {
    val r1 = """.*(?: val .*Attr[ \.]| extends ).*""".r.pattern
    val r2 = """^\s*(?://|\*).*""".r.pattern
    val r3 = """.*(?:override|private| val +values).*""".r.pattern
    val r4 = """^.* (?:object|val) +| *=.+| +extends.*""".r
    val r5 = """^[a-z].*""".r.pattern
    CDS.jj(_ =>
      sourceGenerators in Compile <+= (sourceDirectory in Compile, sourceManaged in Compile) map { (src, tgt) =>
        val attrs =
          IO.readLines(src / "scala/japgolly/scalacss/Attrs.scala").toStream
            .filter   (r1.matcher(_).matches)
            .filterNot(r2.matcher(_).matches)
            .filterNot(r3.matcher(_).matches)
            .map      (r4.replaceAllIn(_, ""))
            .filter   (r5.matcher(_).matches)
            .sorted
            .map(a => s"  @inline final def $a = Attrs.$a")
        val attrAliases =
        s"""
          |package japgolly.scalacss
          |
          |abstract class AttrAliasesAndValueTRules extends ValueT.Rules {
          |${attrs mkString "\n"}
          |}
        """.stripMargin

        val genFile = tgt / "Generated.scala"
        IO.write(genFile, attrAliases)

        genFile :: Nil
      }
    )
  }

  // ==============================================================================================
  override def rootProject = Some(core)

  core.settings(libraryDependencies += "org.apache.derby" % "derby" % "10.4.1.3" % Test)

  lazy val (core, coreJvm, coreJs) =
    crossDialectProject("core", commonSettings
      .configure(utestSettings(), generateAttrAliases)
      .addLibs(scalaz.core, shapeless, nyaya.test % Test)
      .jj(_ => initialCommands := "import shapeless._, ops.hlist._, syntax.singleton._, japgolly.scalacss._")
    )
}
