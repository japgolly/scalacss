package japgolly.scalacss.mutable

import shapeless.HList
import shapeless.ops.hlist.Mapper
import japgolly.scalacss._
import DslBase.ToStyle
import StyleC.MkUsage

/**
 * TODO Doc
 */
abstract class StyleSheet(protected implicit val register: Register) extends DslBase {

  override protected def styleS(t: ToStyle*)(implicit c: Compose) =
    Dsl.style(t: _*)

  protected def style(className: String = null)(t: ToStyle*)(implicit c: Compose): StyleA =
    register registerS Dsl.style(className)(t: _*)

  protected def style(t: ToStyle*)(implicit c: Compose): StyleA =
    register registerS Dsl.style(t: _*)

  protected def boolStyle(f: Boolean => StyleS): Boolean => StyleA =
    register registerF StyleF.bool(f)

  protected def styleF[I: StyleLookup](d: Domain[I])(f: I => StyleS): I => StyleA =
    register registerF StyleF(f)(d)

  protected def styleC[M <: HList](s: StyleC)(implicit m: Mapper.Aux[register._registerC.type, s.S, M], u: MkUsage[M]): u.Out =
    register.registerC(s)(m, u)

  def render[Out](implicit render: Renderer[Out], env: Env): Out =
    register.render
}
