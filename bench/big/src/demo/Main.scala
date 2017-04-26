package demo

import scala.scalajs.js.JSApp
import CssSettings._

object Main extends JSApp {

  override def main(): Unit = {
    Styles.addToDocument()
    println(Styles.blahtable.colp.className)
  }
}
