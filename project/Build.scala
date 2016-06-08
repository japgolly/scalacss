import sbt._
import Keys._
import com.typesafe.sbt.pgp.PgpKeys._
import org.scalajs.sbtplugin.{ScalaJSPlugin, ScalaJSPluginInternal, Stage}
import ScalaJSPlugin._
import ScalaJSPlugin.autoImport.{crossProject => _, _}
import ScalaJSPluginInternal.stageKeys
import Dialect._
import Typical.{settings => _, _}

object ScalaCSS extends Build {

  val Scala211 = "2.11.8"

  val commonSettings: CDS =
    CDS.all(
      _.settings(
        organization       := "com.github.japgolly.scalacss",
        version            := "0.5.0-SNAPSHOT",
        homepage           := Some(url("https://github.com/japgolly/scalacss")),
        licenses           += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0")),
        scalaVersion       := Scala211,
        // crossScalaVersions := Seq("2.10.5", Scala211),
        scalacOptions     ++= Seq("-deprecation", "-unchecked", "-feature",
                                "-language:postfixOps", "-language:implicitConversions",
                                "-language:higherKinds", "-language:existentials"),
        updateOptions      := updateOptions.value.withCachedResolution(true))
      .configure(addCommandAliases(
        "cj"  -> "project core-jvm",
        "cjs" -> "project core-js",
        "qc"  -> "~core-jvm/compile",
        "qtc" -> "~core-jvm/test:compile",
        "qt"  -> "~core-jvm/test"
      ))
    ) :+ Typical.settings("scalacss")

  val univeq = Library("com.github.japgolly.univeq", "univeq", "1.0.1")
  object scalaz {
    private def m(n: String) = Library("org.scalaz", "scalaz-"+n, "7.2.1")
    val core       = m("core")
    val effect     = m("effect") > core
    val concurrent = m("concurrent") > effect
  }
  object nyaya {
    private def m(n: String) = Library("com.github.japgolly.nyaya", "nyaya-"+n, "0.7.0")
    val core = m("core")
    val test = m("test")
  }
  object react {
    private def m(n: String) = "com.github.japgolly.scalajs-react" %%%! n % "0.11.0"
    val core        = m("core")
    val test        = m("test")
    val extra       = m("extra")
    val extScalaz72 = m("ext-scalaz72")
  }


  // ==============================================================================================
  override def rootProject = Some(root)

  lazy val root =
    Project("root", file("."))
      .configure(commonSettings.rootS, preventPublication)
      .aggregate(core, extReact, extScalatags, bench)
      .settings(
        scalaJSUseRhino in Global := false)

  lazy val (core, coreJvm, coreJs) =
    crossDialectProject("core", commonSettings
      .configure(definesMacros, utestSettings()) //, Gen.attrAliases)
      .addLibs(univeq, scalaz.core % Test, nyaya.test % Test)
      .jj(_ => initialCommands := "scalacss._")
      .js(_.settings(
        libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.0"))
    )

  lazy val (extScalatags, extScalatagsJvm, extScalatagsJs) =
    crossDialectProject("ext-scalatags", commonSettings
      .configure(utestSettings())
      .dependsOn(coreJvm, coreJs)
      .addLibs(Library("com.lihaoyi", "scalatags", "0.5.0"), scalaz.core % Test)
    )

  lazy val extReact =
    Project("ext-react", file("ext-react"))
      .enablePlugins(ScalaJSPlugin)
      .configure(commonSettings.jsS, utestSettings().jsS)
      .dependsOn(coreJs)
      .settings(
        libraryDependencies ++= Seq(react.core, react.test % "test"),
        libraryDependencies ++= (scalaz.core % Test)(JS),
        jsDependencies ++= Seq(

          "org.webjars.bower" % "react" % "15.0.1" % "test"
            /        "react.js"
            minified "react.min.js"
            commonJSName "React",

          "org.webjars.bower" % "react" % "15.0.1" % "test"
            /         "react-dom.js"
            minified  "react-dom.min.js"
            dependsOn "react.js"
            commonJSName "ReactDOM",

          "org.webjars.bower" % "react" % "15.0.1" % "test"
            /         "react-dom-server.js"
            minified  "react-dom-server.min.js"
            dependsOn "react-dom.js"
            commonJSName "ReactDOMServer"))

  // ==============================================================================================
  private def benchModule(name: String, dir: File => File): Project =
    Project(name, dir(file("bench")))
      .configure(commonSettings.rootS, preventPublication)
      .settings(scalaSource in Compile := baseDirectory.value / "src")

  private def benchModuleJS(name: String, dir: File => File): Project =
    benchModule("bench-" + name, dir)
      .enablePlugins(ScalaJSPlugin)

  lazy val bench =
    benchModule("bench", identity)
      .aggregate(benchReactWith, benchReactWithout, benchBig)
      .settings(
        jsSizes       := jsSizesTask.value,
        cmpJsSizeFast := cmpJsSize(fastOptJS).value,
        cmpJsSizeFull := cmpJsSize(fullOptJS).value)
      .configure(addCommandAliases(
        "cmpJsSize" -> ";cmpJsSizeFast ;cmpJsSizeFull"
      ))

  // --------------------------------------------------------------------------------------

  val cmpJsSizeFast = TaskKey[Unit]("cmpJsSizeFast", "Compare JS sizes (using fastOptJS).")
  val cmpJsSizeFull = TaskKey[Unit]("cmpJsSizeFull", "Compare JS sizes (using fullOptJS).")
  def cmpJsSize(js: TaskKey[Attributed[sbt.File]]) = Def.task {
    val a = (js in Compile in benchReactWithout).value.data
    val b = (js in Compile in benchReactWith   ).value.data
    val x = a.length()
    val y = b.length()
    val d = y - x
    printf(
      """
        | Without: %,10d
        |    With: %,10d
        |Increase: %,10d (+%.0f%%)
        |
        |""".stripMargin,
      x, y, d, d.toDouble / x * 100)
  }

  lazy val benchReactWithout =
    benchModuleJS("react-without", _ / "react-without")
      .settings(libraryDependencies ++= Seq(react.extra, react.extScalaz72))

  lazy val benchReactWith =
    benchModuleJS("react-with", _ / "react-with")
      .dependsOn(extReact)
      .settings(libraryDependencies ++= Seq(react.extra, react.extScalaz72))

  // --------------------------------------------------------------------------------------

  val jsSizes = TaskKey[Unit]("jsSizes", "Print JS sizes.")

  def gzipLength(in: File): Long = {
    import java.io._
    import java.util.zip._
    val bos = new ByteArrayOutputStream()
    try {
      val gzip = new GZIPOutputStream(bos) { this.`def`.setLevel(Deflater.BEST_COMPRESSION) }
      try
        IO.transfer(in, gzip)
      finally
        gzip.close()
    } finally
      bos.close()
    bos.toByteArray.length
  }

  def jsSizesTask = Def.task[Unit] {
    def report(name: String, f: Attributed[File]): Unit =
      printf("%,11d → %,9d - %s\n", f.data.length, gzipLength(f.data), name)
    val header = "JS Sizes"
    println()
    println(header)
    println("=" * header.length)
    report("bench-big (fast)", (stageKeys(Stage.FastOpt) in Compile in benchBig).value)
    report("bench-big (full)", (stageKeys(Stage.FullOpt) in Compile in benchBig).value)
    println()
  }

  lazy val benchBig =
    benchModuleJS("big", _ / "big")
      .dependsOn(coreJs)
}
