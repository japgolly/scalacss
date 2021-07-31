package scalacss.internal

import scala.reflect.macros.whitebox.Context
import scalacss.defaults._

// ==========================
// ====                  ====
// ====   Scala 2 / JS   ====
// ====                  ====
// ==========================

object ModeMacros {

  private def readConfig(key: String): Option[String] =
    (Option(System.getProperty(key, null)) orElse Option(System.getenv(key)))
      .map(_.trim.toLowerCase)
      .filter(_.nonEmpty)

  trait DevOrProdDefaults {
    def devOrProdDefaults: Exports with mutable.Settings =
      macro ModeMacros.devOrProdDefaults
  }

  def devOrProdDefaults(c: Context): c.Expr[Exports with mutable.Settings] = {
    import c.universe._

    def fail(msg: String): Nothing =
      c.abort(c.enclosingPosition, msg)

    c.Expr[Exports with mutable.Settings](
      readConfig("scalacss.mode") match {

        case None =>
          q"""
            if (_root_.scala.scalajs.LinkingInfo.developmentMode)
              _root_.scalacss.DevDefaults
            else
              _root_.scalacss.ProdDefaults
          """

        case Some("dev") =>
          q"_root_.scalacss.DevDefaults"

        case Some("prod") =>
          q"_root_.scalacss.ProdDefaults"

        case Some(x) =>
          fail(s"Unrecognise option for scalacss.mode: $x. Legal values are 'dev' and 'prod'.")
      }
    )
  }
}
