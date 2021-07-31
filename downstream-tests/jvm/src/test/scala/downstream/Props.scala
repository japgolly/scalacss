package downstream

import java.io.File
import scala.Console._
import scala.io.Source

object Props {

  private object Prop {
    def get(property: String): Option[String] = {
      val o = Option(System.getProperty(property))
      println(s"$CYAN$property$RESET = $YELLOW${o.getOrElse("")}$RESET")
      o
    }

    def get(property: String, default: String): String =
      get(property).getOrElse(default)

    def need(property: String): String =
      get(property).getOrElse(throw new RuntimeException("Property not defined: " + property))
  }

  val jsFilename = Prop.need("js_file")

  val fastOptJS = jsFilename.contains("fast")

  val content: String = {
    val s = Source.fromFile(new File(jsFilename))
    try s.mkString finally s.close()
  }

  val mode       = Prop.get("scalacss.mode")
  val expectProd = Prop.get("downstream_tests.expect.prod").contains("1")
}
