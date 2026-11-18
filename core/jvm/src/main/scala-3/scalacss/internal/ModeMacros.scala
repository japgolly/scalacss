package scalacss.internal

import scalacss.defaults._
import scala.language.`3.0`
import scala.quoted._

// ===========================
// ====                   ====
// ====   Scala 3 / JVM   ====
// ====                   ====
// ===========================

object ModeMacros {

  private def readConfig(propKey: String, envKey: String): Option[String] =
    Option(System.getProperty(propKey, null)).map(_.trim.toLowerCase).filter(_.nonEmpty) orElse
    Option(System.getenv(envKey)).map(_.trim.toLowerCase).filter(_.nonEmpty)

  trait DevOrProdDefaults {
    transparent inline def devOrProdDefaults: Exports with mutable.Settings =
      ${ ModeMacros.devOrProdDefaults }
  }

  def devOrProdDefaults(using Quotes): Expr[Exports with mutable.Settings] = {
    import quotes.reflect._

    def fail(msg: String): Nothing =
      report.errorAndAbort(msg)

    type S = Exports with mutable.Settings
    val expr: Expr[S] =
      readConfig("scalacss.mode", "SCALACSS_MODE") match {

        case Some("dev") | None =>
          '{ scalacss.DevDefaults }

        case Some("prod") =>
          '{ scalacss.ProdDefaults }

        case Some(x) =>
          fail(s"Unrecognise option for scalacss.mode: $x. Legal values are 'dev' and 'prod'.")
      }

    Inlined(None, Nil, expr.asTerm).asExprOf[S]
  }
}
