package scalacss.internal.mutable

import scalacss.internal._
import DslBase.{DslCond, ToStyle}
import scala.annotation.nowarn

/**
 * Mutable StyleSheets provide a context in which many styles can be created using a DSL.
 *
 * They are mutable because they maintain a list of registered styles, meaning you can declare each style one at a time
 * instead of having to create a list of styles in a single expression.
 *
 * Each style itself is immutable.
 */
object StyleSheet {

  /**
   * Classes defined in the REPL appear like this:
   *   $line8.$read.$iw.$iw.$iw.$iw.$iw.$iw.$iw.$iw.$iw.$iw.$iw.$iw.MyStyles
   */
  private def fixRepl(s: String): String =
    s.replaceFirst("""^\$line.+[.$]\$iw[.$]""", "")

  abstract class Base {
    protected def cssRegister: Register

    protected implicit val classNameHint: ClassNameHint =
      ClassNameHint(
        fixRepl(getClass.getName)
          .replaceFirst("\\$+$", "")
          .replaceFirst("""^(?:.+\.)(.+?)$""", "$1")
          .replaceAll("\\$+", "_"))

    protected object dsl extends DslBase {
      override def styleS(t: ToStyle*)(implicit c: Compose) = Dsl.style(t: _*)

      @inline final def ^ = Literal

      @inline final def Color(literal: String) = scalacss.internal.Color(literal)

      @inline implicit final def toCondOps[C](x: C)(implicit f: C => Cond) = new CondOps(x)
      final class CondOps(val cond: Cond) {
        @inline def - = new DslCond(cond, dsl)
      }
    }

    final def css(implicit env: Env): Css =
      cssRegister.css

    final def styles: Vector[StyleA] =
      cssRegister.styles

    /**
     * Render registered styles into some format, usually a String of plain CSS.
     *
     * @param env The target environment in which the styles are to be used.
     *            Allows customisation of required CSS.
     */
    final def render[Out](implicit r: Renderer[Out], env: Env): Out =
      cssRegister.render

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
  abstract class Standalone(protected implicit val cssRegister: Register) extends Base {
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
      cssRegister registerS styleS(unsafeRoot(sel)(t: _*))
    }

    protected final class NestedStringOps(val sel: CssSelector) extends Pseudo.ChainOps[NestedStringOps] {
      override protected def addPseudo(p: Pseudo): NestedStringOps =
        new NestedStringOps(p modSelector sel)

      /** Create a nested style. */
      def -(t: ToStyle*)(implicit c: Compose): StyleS =
        styleS(unsafeChild(sel)(t: _*))
    }

    @inline final protected def & : Cond =
      Cond.empty

    /** Create a child style. */
    @inline final protected def &(sel: CssSelector): NestedStringOps =
      new NestedStringOps(sel)
  }


  /**
   * An inline stylesheet has the following properties:
   *
   *   - Intent is to create styles that can be applied directly to HTML in Scala/Scala.JS.
   *   - Each style is stored in a `val` of type `StyleA`.
   *   - Styles are applied to HTML by setting the `class` attribute of the HTML to the class(es) in a `StyleA`.
   *   - Style class names / CSS selectors are automatically generated.
   *   - All style types ([[StyleS]], [[StyleF]]) are usable.
   */
  abstract class Inline(protected implicit val cssRegister: Register) extends Base with Macros.Dsl.Mixin {

    override protected def __macroStyle    (name: String) = new MStyle (name)
    override protected def __macroStyleF   (name: String) = new MStyleF(name)
    override protected def __macroKeyframes(name: String) = new MKeyframes(name)
    override protected def __macroKeyframe                = new MKStyle
    override protected def __macroFontFace                = new MFontFace

    protected class MStyle(name: String) extends DslMacros.MStyle {
      override def apply(t: ToStyle*)(implicit c: Compose): StyleA = {
        val s1 = Dsl.style(t: _*)
        val s2 = cssRegister.applyMacroName(name, s1)
        cssRegister registerS s2
      }

      override def apply(className: String)(t: ToStyle*)(implicit c: Compose): StyleA =
        cssRegister registerS Dsl.style(className)(t: _*)
    }

    protected class MStyleF(name: String) extends DslMacros.MStyleF {
      override protected def create[I: StyleLookup](manualName: Option[String], d: Domain[I], f: I => StyleS, classNameSuffix: (I, Int) => String) =
        manualName match {
          case None    => cssRegister.registerFM(StyleF(f)(d), name)(classNameSuffix)
          case Some(n) => cssRegister.registerF2(StyleF(f)(d), n)(classNameSuffix)
        }
    }

    protected class MKeyframes(name: String) extends DslMacros.MKeyframes {
      override def apply(frames: (KeyframeSelector, StyleA)*): Keyframes =
        cssRegister.registerKeyframes(Keyframes(ClassName(name), frames))
    }

    protected class MKStyle extends DslMacros.MStyle {
      override def apply(t: ToStyle*)(implicit c: Compose): StyleA = {
        val s = Dsl.style(t: _*)
        StyleA(ClassName("keyframe"), s.addClassNames, s)
      }

      override def apply(className: String)(t: ToStyle*)(implicit c: Compose): StyleA =
        apply(t:_*)
    }

    protected class MFontFace extends DslMacros.MFontFace {
      override def apply(config: FontFace.FontSrcSelector => FontFace[Option[String]]): FontFace[String] = {
        cssRegister.registerFontFace(config(new FontFace.FontSrcSelector(None)))
      }

      override def apply(fontFamily: String)(config: FontFace.FontSrcSelector => FontFace[Option[String]]): FontFace[String] = {
        cssRegister.registerFontFace(config(new FontFace.FontSrcSelector(Some(fontFamily))))
      }
    }

    @inline final protected def & : Cond =
      Cond.empty

    /**
     * Objects in Scala are lazy. If you put styles in inner objects you need to make sure they're initialised before
     * your styles are rendered.
     * To do so, call this at the end of your stylesheet with one style from each inner object.
     */
    @nowarn("cat=unused")
    protected def initInnerObjects(a: StyleA*) = ()
  }
}
