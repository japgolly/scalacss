import sbt._
import Keys._
import org.scalajs.sbtplugin.{ScalaJSPlugin, ScalaJSPluginInternal, Stage}
import ScalaJSPlugin.autoImport._
import ScalaJSPluginInternal.stageKeys
import Lib._
import ScalaCssBuild._

object BenchBuild {

  private def benchModule(name: String, dir: File => File): Project =
    Project(name, dir(file("bench")))
      .configure(preventPublication)
      .settings(scalaSource in Compile := baseDirectory.value / "src")

  private def benchModuleJS(name: String, dir: File => File): Project =
    benchModule("bench-" + name, dir)
      .configure(commonSettings.js)
      .enablePlugins(ScalaJSPlugin)

  lazy val bench =
    benchModule("bench", identity)
      .configure(commonSettings.jvm)
      .aggregate(benchReactWith, benchReactWithout, benchBig)
      .settings(
        jsSizes       := jsSizesTask.value,
        cmpJsSizeFast := cmpJsSize(fastOptJS).value,
        cmpJsSizeFull := cmpJsSize(fullOptJS).value)
      .configure(
        addCommandAliases(
          "cmpJsSize" -> ";cmpJsSizeFast ;cmpJsSizeFull",
          "B"         -> ";cmpJsSizeFull;jsSizes"))

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
      .settings(
        libraryDependencies ++= Seq(
          "com.github.japgolly.scalajs-react" %%% "extra"        % Ver.ScalaJsReact,
          "com.github.japgolly.scalajs-react" %%% "ext-scalaz72" % Ver.ScalaJsReact,
          "org.scalaz"                        %%% "scalaz-core"  % Ver.Scalaz))

  lazy val benchReactWith =
    benchModuleJS("react-with", _ / "react-with")
      .dependsOn(extReact)
      .settings(
        libraryDependencies ++= Seq(
          "com.github.japgolly.scalajs-react" %%% "extra"        % Ver.ScalaJsReact,
          "com.github.japgolly.scalajs-react" %%% "ext-scalaz72" % Ver.ScalaJsReact,
          "org.scalaz"                        %%% "scalaz-core"  % Ver.Scalaz))

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
      .dependsOn(coreJS)
}
