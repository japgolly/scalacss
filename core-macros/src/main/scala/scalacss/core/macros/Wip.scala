package scalacss.core.macros

import japgolly.scalagraal._
import japgolly.scalagraal.GraalJs._

object Wip {

  private val ctx = ContextSync.fixedContext()

  private def evalOrThrow[A](expr: Expr[A]): A =
    ctx.eval(expr) match {
      case Right(a) => a
      case Left(e)  => throw e
    }

  private def initExpr =
    Expr.requireFileOnClasspath("scalacss.js")

  evalOrThrow(initExpr)

  private val mainExpr =
    Expr("main()").asString

  def main(): String =
    evalOrThrow(mainExpr)
}
