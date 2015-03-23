import sbt._
import Keys._
import com.typesafe.sbt.pgp.PgpKeys._
import org.scalajs.jsenv.JSEnv
import org.scalajs.sbtplugin.ScalaJSPlugin
import ScalaJSPlugin._
import ScalaJSPlugin.autoImport._


sealed abstract class Dialect(val name: String)
case object JVM extends Dialect("jvm")
case object JS  extends Dialect("js")

object Dialect {

  type PE = Project => Project

  case class Library(jvmG: String, jvmA: String, jvmV: String,
                     jsG: String, jsA: String, jsV: String,
                     scope: Option[String], tdeps: List[Library]) {
    def jsGroup(v: String) = copy(jsG = v)
    def jsArtifact(v: String) = copy(jsA = v)
    def jsVersion(v: String) = copy(jsV = v)
    def jsVersion(f: String => String) = copy(jsV = f(jsV))
    def %(s: String): Library = copy(scope = Some(s))
    def %(s: Configuration): Library = %(s.name)
    def myJsFork(n: String) = jsGroup("com.github.japgolly.fork."+n)

    def moduleId(d: Dialect): ModuleID = {
      val l = d match {
        case JVM => jvmG %% jvmA % jvmV
        case JS  => jsG %%%! jsA % jsV
      }
      scope.fold(l)(l % _)
    }
    def apply(d: Dialect): List[ModuleID] = {
      val t = scope.fold(tdeps)(s => tdeps.map(_ % s))
      (this :: t).map(_ moduleId d)
    }
    def >(l: Library) = copy(tdeps = l :: this.tdeps)
  }
  object Library {
    def apply(jvmG: String, jvmA: String, jvmV: String) =
      new Library(jvmG, jvmA, jvmV, jvmG, jvmA, jvmV, None, Nil)
  }

  /** CDS = Cross Dialect Settings */
  class CDS(val rootS: PE, val jvmS: PE, val jsS: PE) {
    def apply(d: Option[Dialect]): PE = d.fold(rootS)(apply(_))
    def apply(d: Dialect)        : PE = d match { case JVM => jvmS; case JS => jsS }

    def root(g: PE) = new CDS(g compose rootS, jvmS, jsS)
    def all (g: PE) = new CDS(g compose rootS, g compose jvmS, g compose jsS)
    def jvm (g: PE) = new CDS(rootS, g compose jvmS, jsS)
    def js  (g: PE) = new CDS(rootS, jvmS, g compose jsS)
    def jj  (g: Dialect => PE) = new CDS(rootS, g(JVM) compose jvmS, g(JS) compose jsS)

    def :+(s: CDS) = new CDS(rootS andThen s.rootS, jvmS andThen s.jvmS, jsS andThen s.jsS)
    def +:(s: CDS) = s :+ this

    // Like Project.configure
    def configure(ss: CDS*): CDS = ss.foldLeft(this)(_ :+ _)

    def addLibs(ls: Library*): CDS =
      jj(d => _.settings(libraryDependencies ++= ls.flatMap(_(d))))

    def dependsOn(jvm: => Project, js: => Project) =
      jj(d => _.dependsOn(d match {case JVM => jvm; case JS => js}))
  }

  val CDS = new CDS(identity, identity, identity)

  implicit def settingsToPE(s: Seq[Setting[_]]): PE = _.settings(s: _*)
  implicit def settingToPE(s: Setting[_]): PE = _.settings(s)

  /*
  def sharedCDS(s: PE) = new {
    def jvmCDS(jvm: PE) = new {
      def jsCDS(js: PE): Dialect => PE = {
        case JVM => s andThen jvm
        case JS  => s andThen js
      }
    }
  }

  implicit def liftNoRootCDS(f: Dialect => PE): Option[Dialect] => PE =
    _.fold(identity[PE])(f)

  def rootCDS(r: PE) = new {
    def sharedCDS(s: PE) = new {
      def jvmCDS(jvm: PE) = new {
        def jsCDS(js: PE): Option[Dialect] => PE = {
          case None      => s andThen r
          case Some(JVM) => s andThen jvm
          case Some(JS)  => s andThen js
        }
      }
    }
  }
  */

  // class CrossProject(_root: => Project, _jvm: => Project, _js: => Project) {
    // lazy val root = _root
    // lazy val jvm  = _jvm
    // lazy val js   = _js
  // }
  def crossDialectProject(shortName: String, settings: CDS) = {
    val pname, dir = s"$shortName"
    def mk(d: Dialect) = {
      val n = s"$pname-${d.name}"
      Project(n, file(dir))
        .configure(settings(Some(d)))
        .settings(
          name       := n,
          moduleName := pname,
          target     := baseDirectory.value / "target" / d.name)
    }
    lazy val jvm = mk(JVM)
    lazy val js  = mk(JS)
    lazy val agg = Project(pname, file("."))
                     .aggregate(jvm, js)
                     .configure(settings(None))
                     .settings(target := file(dir) / "target")
    (agg, jvm, js)
  }
}

object Typical {
  import Dialect._

  val clearScreenTask = TaskKey[Unit]("clear", "Clears the screen.")

  def addSourceDialect(name: String): PE =
    _.settings(
      unmanagedSourceDirectories in Compile += baseDirectory.value / s"src/main/scala-$name",
      unmanagedSourceDirectories in Test    += baseDirectory.value / s"src/test/scala-$name")

  def preventPublication: PE =
    _.settings(
      publish            := (),
      publishLocal       := (),
      publishSigned      := (),
      publishLocalSigned := (),
      publishArtifact    := false,
      publishTo          := Some(Resolver.file("Unused transient repository", target.value / "fakepublish")),
      packagedArtifacts  := Map.empty)
    // .disablePlugins(plugins.IvyPlugin)

  def sourceMapsToGithub(ghProject: String): PE =
    p => p.settings(
      scalacOptions ++= (if (isSnapshot.value) Seq.empty else Seq({
        val a = p.base.toURI.toString.replaceFirst("[^/]+/?$", "")
        val g = s"https://raw.githubusercontent.com/japgolly/$ghProject"
        s"-P:scalajs:mapSourceURI:$a->$g/v${version.value}/"
      }))
    )

  def addCommandAliases(m: (String, String)*): PE = {
    val s = m.map(p => addCommandAlias(p._1, p._2)).reduce(_ ++ _)
    _.settings(s: _*)
  }

  def settings(ghProject: String): CDS =
    CDS.root(
      preventPublication
    ).
    all(
      _.settings(
        clearScreenTask := { println("\033[2J\033[;H") })
      .configure(
        addCommandAliases(
        "t"  -> ";clear ; test:compile ; test",
        "tt" -> ";clear ;+test:compile ;+test",
        "T"  -> ";clear ; clean ;t",
        "TT" -> ";clear ;+clean ;tt"))
    ).
    jj(_ => publicationSettings(ghProject)).
    jvm(
      addSourceDialect(JVM.name)
    ).
    js(
      _.enablePlugins(ScalaJSPlugin).configure(
        addSourceDialect(JS.name),
        sourceMapsToGithub(ghProject))
    )

  def publicationSettings(ghProject: String): PE =
    _.settings(
      publishTo := {
        val nexus = "https://oss.sonatype.org/"
        if (isSnapshot.value)
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases"  at nexus + "service/local/staging/deploy/maven2")
      },
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
        </developers>)

  def utestSettings(scope: String = "test", phantom: Boolean = false): CDS =
    CDS.addLibs(Library("com.lihaoyi", "utest", "0.3.1") % scope)
      .jj(_ => testFrameworks += new TestFramework("utest.runner.Framework"))
      .js(_.settings(
        scalaJSStage in Test := FastOptStage,
        jsEnv in Test        := (if (phantom) PhantomJSEnv().value else NodeJSEnv().value)))
}

