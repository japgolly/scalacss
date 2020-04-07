package scalacss.core.macros

import japgolly.scalagraal.GraalJs._
import japgolly.scalagraal._
import java.util.function.Consumer
import org.graalvm.polyglot.Value
import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}
import scala.util.Try

object Wip {

//  private val _ctx = Context.newBuilder("js")
//    .allowAllAccess(true)
//    .allowExperimentalOptions(true)
//    .option("js.commonjs-require", "true")
//    .option("js.commonjs-require-cwd", "/home/golly/projects/public/scalacss/js-lib/node_modules")
//    .option("js.commonjs-core-modules-replacements", "./rules/at-rule-no-unknown:stylelint/lib/rules/at-rule-no-unknown")
//    .build()

  private val ctx = ContextSync.fixedContext()

  private def evalOrThrow[A](expr: Expr[A]): A =
    ctx.eval(expr) match {
      case Right(a) => a
      case Left(e)  => throw e
    }

  private def initExpr =
    Expr.requireFileOnClasspath("scalacss.js")

  evalOrThrow(initExpr)


  implicit class ValueExt(private val self: Value) extends AnyVal {
    def arrayElements(): Iterator[Value] =
      (0L until self.getArraySize).iterator.map(self.getArrayElement(_))
  }


  implicit def exprParamValueFn: ExprParam.ValueFn[Value] = ExprParam.RawValueFn // TODO wtf! this should be in SG
  implicit def exprParamJ[A]: ExprParam.ValueFn[Consumer[A]] = ExprParam.RawValueFn // TODO wtf! this should be in SG

  final class AsyncFunctionFailed(val failure: Value) extends RuntimeException(s"AsyncFunctionFailed: $failure")

  def exprAsync(expr: Expr[Value]): Expr[Future[Value]] =
    exprAsync(expr, identity)

  def exprAsync[A](expr: Expr[Value], f: Value => A): Expr[Future[A]] =
    for {
      v <- expr
      p = Promise[A]()
      onThen = (v => p.complete(Try(f(Value.asValue(v))))) : Consumer[AnyRef]
      onFail = (v => p.failure(new AsyncFunctionFailed(Value.asValue(v)))) : Consumer[AnyRef]
      _ <- Expr.apply3((a, b, c) => s"$a.then($b).catch($c)", v, onThen, onFail)
    } yield p.future

  private val timeout = 3.seconds

  private val mainExpr = Expr.compileFnCall1[String]("main")(e => exprAsync(e))

  // TODO don't forget thread-safety

  def main(cssIn: String): String = {
    val future = evalOrThrow(mainExpr(cssIn))
    val result = Await.result(future, timeout)
    val cssOut = result.getMember("css").asString()
    val warnings = result.getMember("warnings").arrayElements().map(_.asString()).toVector

    for (w <- warnings)
      println(s"${Console.YELLOW}WARNING: $w${Console.RESET}")

    cssOut
  }

  object Implicits {

    implicit class Blah(val sc: StringContext) extends AnyVal {
      def css(args: Any*) = {
        val str = sc.s(args: _*).stripMargin('|').trim
        main(str)
      }
    }

  }
}
