package demo

import scala.scalajs.js.annotation.JSExport
import CssDefaults._

@JSExport("Main")
object Main {

  @JSExport("main")
  def main(): Unit = {
    Styles.addToDocument()
    println(Styles.blahtable.colp.className)
  }
}
