import sbt._
import Keys._
import Lib._

object Gen {

  def attrAliases: PE = {
    val r1 = """.*(?: val .*Attr[ \.]| extends ).*""".r.pattern
    val r2 = """^\s*(?://|\*).*""".r.pattern
    val r3 = """.*(?:override|private| val +values).*""".r.pattern
    val r4 = """^.* (?:object|val) +| *=.+| +extends.*""".r
    val r5 = """^[a-z].*""".r.pattern

    (_: Project).configure(_.settings(
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
          |package scalacss
          |
          |abstract class AttrAliasesAndValueTRules extends ValueT.Rules {
          |${attrs mkString "\n"}
          |}
        """.stripMargin

        val genFile = tgt / "Generated.scala"
        IO.write(genFile, attrAliases)

        genFile :: Nil
      }
    ))
  }

}
