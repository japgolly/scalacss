package scalacss

import scala.reflect.macros.blackbox.Context
import scala.reflect.NameTransformer

object Macros {

  private def name(c: Context): String = {
    val localName = c.internal.enclosingOwner.name.toString.trim

    // `style()` instead of `val x = style()` results in "<local OuterClass>"
    if (localName startsWith "<")
      ""
    else {

      // Try to extract a name, relative to the stylesheet class
      val className = c.prefix.actualType.typeSymbol.fullName
      val fullName  = c.internal.enclosingOwner.fullName
      if ((fullName.length > className.length + 1) && fullName.startsWith(className + ".")) {
        val relName = fullName.substring(className.length + 1).replace('.', '-')
        NameTransformer decode relName
      } else

        // Default to local name
        NameTransformer decode localName
    }
  }

  private def impl[A](c: Context, method: String): c.Expr[A] = {
    import c.universe._
    c.Expr(Apply(Ident(TermName(method)), List(Literal(Constant(name(c))))))
  }

  // ===================================================================================================================

  import DslMacros._

  def implStyle (c: Context): c.Expr[MStyle]  = impl(c, "__macroStyle")
  def implStyleF(c: Context): c.Expr[MStyleF] = impl(c, "__macroStyleF")

  trait DslMixin {
    protected def __macroStyle (name: String): MStyle
    protected def __macroStyleF(name: String): MStyleF
    final protected def style : MStyle  = macro implStyle
    final protected def styleF: MStyleF = macro implStyleF
  }
}
