import sbt._
import Keys._
import com.jsuereth.sbtpgp.PgpKeys._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbtcrossproject.CrossPlugin.autoImport._
import sbtcrossproject.CrossProject
import scalajscrossproject.ScalaJSCrossPlugin.autoImport._
import xerial.sbt.Sonatype.autoImport._

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
        publishTo := sonatypePublishToBundle.value,
        pomExtra :=
          <scm>
            <connection>scm:git:github.com/japgolly/{ghProject}</connection>
            <developerConnection>scm:git:git@github.com:japgolly/{ghProject}.git</developerConnection>
            <url>github.com:japgolly/{ghProject}.git</url>
          </scm>
          <developers>
            <developer>
              <id>japgolly</id>
              <name>David Barri</name>
            </developer>
          </developers>))
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

  def onlyScala2(p: Project) = {
    def clearWhenDisabled[A](key: SettingKey[Seq[A]]) =
      Def.setting[Seq[A]] {
        val disabled = scalaVersion.value.startsWith("3")
        val as = key.value
        if (disabled) Nil else as
      }
    p.settings(
      libraryDependencies                  := clearWhenDisabled(libraryDependencies).value,
      Compile / unmanagedSourceDirectories := clearWhenDisabled(Compile / unmanagedSourceDirectories).value,
      Test / unmanagedSourceDirectories    := clearWhenDisabled(Test / unmanagedSourceDirectories).value,
      publish / skip                       :=  ((publish / skip).value || scalaVersion.value.startsWith("3")),
      Test / test                           := { if (scalaVersion.value.startsWith("2")) (Test / test).value },
    )
  }

}
