package scalacss.core.macros

import japgolly.scalagraal._
import japgolly.scalagraal.GraalJs._
import java.util.function.Consumer
import org.graalvm.polyglot.Value
import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.duration._
import scala.util.Try

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


  implicit def exprParamValueFn: ExprParam.ValueFn[Value] = ExprParam.RawValueFn // TODO wtf! this should be in SG
  implicit def exprParamJ[A]: ExprParam.ValueFn[Consumer[A]] = ExprParam.RawValueFn // TODO wtf! this should be in SG

  final class AsyncFunctionFailed(val failure: Value) extends RuntimeException(s"AsyncFunctionFailed: $failure")

  def exprAsync[A](expr: Expr[Value], f: Value => A): Expr[Future[A]] =
    for {
      v <- expr
      p = Promise[A]()
      onThen = (v => p.complete(Try(f(Value.asValue(v))))) : Consumer[AnyRef]
      onFail = (v => p.failure(new AsyncFunctionFailed(Value.asValue(v)))) : Consumer[AnyRef]
      _ <- Expr.apply3((a, b, c) => s"$a.then($b).catch($c)", v, onThen, onFail)
    } yield p.future

  private val timeout = 3.seconds

  private val mainExpr = Expr.compileFnCall1[String]("main")(e => exprAsync(e, _.asString))

  // TODO don't forget thread-safety

  def main(css: String): String = {
    val future = evalOrThrow(mainExpr(css))
    val result = Await.result(future, timeout)
    result
  }
}
