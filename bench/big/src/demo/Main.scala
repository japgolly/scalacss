package demo

import demo.CssSettings._

object Main {

  def main(args: Array[String]): Unit = {
    Styles.addToDocument()
    println(Styles.blahtable.colp.className)
  }
}
