package japgolly.scalacss.mutable

import shapeless.HList
import shapeless.ops.hlist.Mapper
import japgolly.scalacss._
import DslBase.{DslCond, ToStyle}
import StyleC.MkUsage

/**
 * Mutable StyleSheets provide a context in which many styles can be created using a DSL.
 *
 * They are mutable because they maintain a list of registered styles, meaning you can declare each style one at a time
 * instead of having to create a list of styles in a single expression.
 *
 * Each style itself is immutable.
 */
object StyleSheet {

  abstract class Base {
    protected def register: Register

    protected object dsl extends DslBase {
      override def styleS(t: ToStyle*)(implicit c: Compose) =
        Dsl.style(t: _*)
    }

    @inline final protected def ^ = Literal

    /**
     * Render registered styles into some format, usually a String of plain CSS.
     *
     * @param env The target environment in which the styles are to be used.
     *            Allows customisation of required CSS.
     */
    final def render[Out](implicit r: Renderer[Out], env: Env): Out =
      register.render

    /**
     * Render registered styles into some format, usually a String of plain CSS.
     *
     * The `A` suffix stands for ''absolute'', in that it doesn't perform any environment customisation, and as such
     * an [[Env]] isn't required.
     */
    final def renderA[Out](implicit r: Renderer[Out]): Out =
      render(r, Env.empty)
  }


  /**
   * A standalone stylesheet has the following properties:
   *
   *   - Intent is to generate static CSS for external consumption.
   *   - It is comparable to SCSS/LESS.
   *   - Styles go into a pool of registered styles when they are declared and return `Unit`.
   *   - Style class names / CSS selectors must be provided.
   *   - Only static styles ([[StyleS]]) are usable.
   */
  abstract class Standalone(protected implicit val register: Register) extends Base {
    import dsl._

    @inline protected final implicit class RootStringOps(val sel: CssSelector) extends Pseudo.ChainOps[RootStringOps] {
      override protected def addPseudo(p: Pseudo): RootStringOps =
        new RootStringOps(p modSelector sel)

      /**
       * Create a root style.
       *
       * {{{
       *   "div.stuff" - (
       *     ...
       *   )
       * }}}
       */
      def -(t: ToStyle*)(implicit c: Compose): Unit =
        register registerS styleS(unsafeRoot(sel)(t: _*))
    }

    @inline protected final implicit class NestedCondOps(val cond: Cond) {
      /** Create a child style. */
      def - = new DslCond(cond)
    }

    protected final class NestedStringOps(val sel: CssSelector) extends Pseudo.ChainOps[NestedStringOps] {
      override protected def addPseudo(p: Pseudo): NestedStringOps =
        new NestedStringOps(p modSelector sel)

      /** Create a nested style. */
      def -(t: ToStyle*)(implicit c: Compose): StyleS =
        styleS(unsafeChild(sel)(t: _*))
    }

    /** Created a nested conditional style. */
    @inline final protected def & : Cond = Cond.empty

    /** Created a nested conditional style. */
    @inline final protected def &(q: Media.Query): Cond = Cond.empty & q

    /** Created a nested conditional style. */
    @inline final protected def &(c: Cond): Cond = c

    /** Created a child style. */
    @inline final protected def &(sel: CssSelector): NestedStringOps = new NestedStringOps(sel)
  }


  /**
   * An inline stylesheet has the following properties:
   *
   *   - Intent is to create styles that can be applied directly to HTML in Scala/Scala.JS.
   *   - Each style is stored in a `val` of type `StyleA`.
   *   - Styles are applied to HTML by setting the `class` attribute of the HTML to the class(es) in a `StyleA`.
   *   - Style class names / CSS selectors are automatically generated.
   *   - All style types ([[StyleS]], [[StyleF]], [[StyleC]]) are usable.
   */
  abstract class Inline(protected implicit val register: Register) extends Base {
    import dsl._

            final protected type Domain[A] = japgolly.scalacss.Domain[A]
    @inline final protected def  Domain    = japgolly.scalacss.Domain

    protected def style(t: ToStyle*)(implicit c: Compose): StyleA =
      register registerS Dsl.style(t: _*)

    protected def style(className: String = null)(t: ToStyle*)(implicit c: Compose): StyleA =
      register registerS Dsl.style(className)(t: _*)

    protected def boolStyle(f: Boolean => StyleS): Boolean => StyleA =
      styleF(Domain.boolean)(f)

    protected def intStyle(r: Range)(f: Int => StyleS): Int => StyleA =
      styleF(Domain ofRange r)(f)

    protected def styleF[I: StyleLookup](d: Domain[I])(f: I => StyleS): I => StyleA =
      register registerF StyleF(f)(d)

    protected def styleC[M <: HList](s: StyleC)(implicit m: Mapper.Aux[register._registerC.type, s.S, M], u: MkUsage[M]): u.Out =
      register.registerC(s)(m, u)

    /** Created a nested conditional style. */
    @inline final protected def & : Cond = Cond.empty

    /** Created a nested conditional style. */
    @inline final protected def &(q: Media.Query): Cond = Cond.empty & q

    /** Created a nested conditional style. */
    @inline final protected def &(c: Cond): Cond = c
  }
}
