package scalacss.internal

import java.util.regex.Pattern
import scala.reflect.macros.blackbox.Context
import scala.reflect.NameTransformer

object Macros {

  object Dsl {

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
      c.Expr(Apply(Ident(TermName(method)), Literal(Constant(name(c))) :: Nil))
    }

    import DslMacros._

    def implStyle    (c: Context): c.Expr[MStyle    ] = impl(c, "__macroStyle")
    def implStyleF   (c: Context): c.Expr[MStyleF   ] = impl(c, "__macroStyleF")
    def implKeyframes(c: Context): c.Expr[MKeyframes] = impl(c, "__macroKeyframes")

    trait Mixin {
      protected def __macroStyle    (name: String): MStyle
      protected def __macroStyleF   (name: String): MStyleF
      protected def __macroKeyframes(name: String): MKeyframes
      protected def __macroKeyframe               : MStyle
      protected def __macroFontFace               : MFontFace

      final protected def style    : MStyle     = macro implStyle
      final protected def styleF   : MStyleF    = macro implStyleF
      final protected def keyframes: MKeyframes = macro implKeyframes
      final protected def keyframe : MStyle     = __macroKeyframe
      final protected def fontFace : MFontFace  = __macroFontFace
    }
  }

  // ===================================================================================================================

  type Color = ValueT[ValueT.Color]

  object ColorLiteral {

    private def cssFnRegex(f: String, as: String*) = {
      val args = as mkString ","
      Pattern compile s"^$f\\($args\\)$$"
    }

    private def int = "(-?\\d+)"
    private def dbl = """(-?\d+(?:\.\d+)?|\.\d+)"""
    private def pct = s"$dbl%"

    private val ws   = "\\s+".r
    private val hex  = Pattern compile """^#(?:[0-9a-fA-F]{3}){1,2}$"""
    private val rgbI = cssFnRegex("rgb",  int, int, int)
    private val rgbP = cssFnRegex("rgb",  pct, pct, pct)
    private val rgba = cssFnRegex("rgba", int, int, int, dbl)
    private val hsl  = cssFnRegex("hsl",  int, pct, pct)
    private val hsla = cssFnRegex("hsla", int, pct, pct, dbl)

    def impl(c: Context)(args: c.Expr[Any]*): c.Expr[Color] = {
      import c.universe._

      c.prefix.tree match {
        case Apply(_, List(Apply(_, List(l @Literal(Constant(text0: String)))))) =>

          def fail(reason: String = null): Nothing = {
            var err = s"""Invalid colour literal: "$text0"."""
            if (reason ne null)
              err = s"$err $reason"
            c.abort(c.enclosingPosition, err)
          }

          val text = ws.replaceAllIn(text0, "").toLowerCase

          def pass = true
          def undecided = false

          def validateHex: Boolean =
            (text.charAt(0) == '#') && {
              if (hex.matcher(text).matches)
                pass
              else
                fail("Hex notation must be either #RRGGBB and #RGB.")
            }

          type V = String => Unit

          def validateInt(name: String, max: Int): V =
            s => {
              val i = s.toInt
              if (i < 0 || i > max)
                fail(s"Invalid $name value: $s")
            }

          def validateDbl(name: String, max: Double): V =
            s => {
              val d = s.toDouble
              if (d < 0 || d > max)
                fail(s"Invalid $name value: $s")
            }

          def validatePct(name: String): V =
            validateDbl(name, 100)

          def validateFn(p: Pattern, a: V, b: V, c: V, d: V = null): Boolean = {
            val m = p.matcher(text)
            if (m.matches) {
              a(m group 1)
              b(m group 2)
              c(m group 3)
              if (d ne null) d(m group 4)
              pass
            } else
              undecided
          }

          def ri = validateInt("red",   255)
          def gi = validateInt("green", 255)
          def bi = validateInt("blue",  255)
          def rp = validatePct("red")
          def gp = validatePct("green")
          def bp = validatePct("blue")
          def h  = validateInt("hue", 359)
          def s  = validatePct("saturation")
          def l  = validatePct("lightness")
          def a  = validateDbl("alpha", 1)

          def validateRgbI = validateFn(rgbI, ri, gi, bi)
          def validateRgbP = validateFn(rgbP, rp, gp, bp)
          def validateRgba = validateFn(rgba, ri, gi, bi, a)
          def validateHsl  = validateFn(hsl,  h, s, l)
          def validateHsla = validateFn(hsla, h, s, l, a)

          val validated =
            validateHex || validateRgbI || validateRgbP || validateRgba || validateHsl || validateHsla

          if (validated) {
            //println(showRaw(q"""_root_.scalacss.internal.Color($text)"""))
            c.Expr(Apply(Select(Select(Select(Ident(termNames.ROOTPKG), TermName("scalacss")), TermName("internal")), TermName("Color")),
              Literal(Constant(text)) :: Nil))
          } else
            fail()

        case t =>
          c.abort(c.enclosingPosition, s"Expected string literal. Got:\n$t($args)")
      }
    }
  }

  class ColourLiteral(private val sc: StringContext) extends AnyVal {
    /** c"#fc6" provides a validates Color */
    def c(args: Any*): Color = macro ColorLiteral.impl
  }
}
