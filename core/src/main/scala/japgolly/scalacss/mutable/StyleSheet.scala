package japgolly.scalacss.mutable

import japgolly.scalacss._
import DslBase.ToStyle

/**
 * TODO Doc
 */
abstract class StyleSheet(protected val reg: Register) extends DslBase {

  override protected def styleS(t: ToStyle*)(implicit c: Compose) =
    Dsl.style(t: _*)

  protected def style(className: String = null)(t: ToStyle*)(implicit c: Compose): StyleA =
    reg registerS Dsl.style(className)(t: _*)

  protected def style(t: ToStyle*)(implicit c: Compose): StyleA =
    reg registerS Dsl.style(t: _*)

  protected def boolStyle(f: Boolean => StyleS): Boolean => StyleA =
    reg registerF StyleF.bool(f)

  protected def styleF[I: StyleLookup](d: Domain[I])(f: I => StyleS): I => StyleA =
    reg registerF StyleF(f)(d)

  def render[Out](implicit render: Renderer[Out], env: Env): Out =
    reg.render
}
