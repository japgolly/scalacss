package scalacss.internal

import java.util.regex.Pattern
import scala.reflect.NameTransformer
import scala.reflect.macros.blackbox.Context

object Macros {

  object Dsl {

    trait Base {
      @inline final implicit def colourLiteralMacro(sc: StringContext): Macros.ColourLiteral =
        new Macros.ColourLiteral(sc)
    }

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

    private def cssFnRegexLegacy(f: String, as: String*) = {
      val args = as mkString "\\s*,\\s*"
      Pattern compile s"^(?i)\\s*$f\\s*\\(\\s*$args\\s*\\)\\s*$$"
    }

    private def cssFnRegexModern(f: String, as: String*)(suffix: String) = {
      val args = as mkString "\\s+"
      Pattern compile s"^(?i)\\s*$f\\s*\\(\\s*$args$suffix\\s*\\)\\s*$$"
    }

    private def int = "(-?\\d+)"
    private def dbl = """(-?\d+(?:\.\d+)?|\.\d+)"""
    private def pct = s"$dbl%"

    // NPN = number, percentage, or none
    private def modernNPN = """(-?\d+%?|none)"""
    private def modernA = s"\\s*/\\s*(-?\\d+%?|$dbl)"
    private def modernH = """(-?\d+(?:deg)?|none)"""
    private def modernSL = """(-?\d+%?)"""

    private val rgbI  = cssFnRegexLegacy("rgb",  int, int, int)
    private val rgbP  = cssFnRegexLegacy("rgb",  pct, pct, pct)
    private val rgbaI = cssFnRegexLegacy("rgba", int, int, int, dbl)
    private val rgbaP = cssFnRegexLegacy("rgba", pct, pct, pct, dbl)
    private val hsl   = cssFnRegexLegacy("hsl",  int, pct, pct)
    private val hsla  = cssFnRegexLegacy("hsla", int, pct, pct, dbl)
    private val rgbM  = cssFnRegexModern("rgb", modernNPN, modernNPN, modernNPN)("")
    private val rgbaM = cssFnRegexModern("rgb", modernNPN, modernNPN, modernNPN)(modernA)
    private val hslM  = cssFnRegexModern("hsl", modernH, modernSL, modernSL)("")
    private val hslaM = cssFnRegexModern("hsl", modernH, modernSL, modernSL)(modernA)

    private def isHex: Char => Boolean =
      c => (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')

    def impl(c: Context)(args: c.Expr[Any]*): c.Expr[Color] = {
      import c.universe._

      c.prefix.tree match {
        case Apply(_, List(Apply(_, List(Literal(Constant(text: String)))))) =>

          def fail(reason: String = null): Nothing = {
            var err = s"""Invalid colour literal: "$text"."""
            if (reason ne null)
              err = s"$err $reason"
            c.abort(c.enclosingPosition, err)
          }

          var result: String = null
          def pass = true
          def undecided = false

          def validateHex: Boolean =
            (text.nonEmpty && text.charAt(0) == '#') && {
              val v = text.drop(1)
              v.length match {
                case (3 | 6 | 4 | 8) if v.forall(isHex) => result = text.toLowerCase; pass
                case _                                  => fail("Hex notation must be #RGB, #RRGGBB, #RGBA, or #RRGGBBAA.")
              }
            }

          type V = String => Unit

          def validateInt(name: String, max: Int): V =
            s => {
              try {
                val i = s.toInt
                if (i < 0 || i > max)
                  fail(s"Invalid $name value: $s")
              } catch {
                case _: NumberFormatException =>
                  fail(s"Invalid $name value: $s")
              }
            }

          def validateDbl(name: String, max: Double): V =
            s => {
              val d = s.toDouble
              if (d < 0 || d > max)
                fail(s"Invalid $name value: $s")
            }

          def validatePct(name: String): V =
            validateDbl(name, 100)

          def validateModernNPN(name: String, maxInt: Int): V =
            s =>
              if (s == "none")
                ()
              else if (s.endsWith("%"))
                validatePct(name)(s.stripSuffix("%"))
              else
                validateInt(name, maxInt)(s)

          def validateModernH(name: String): V =
            s =>
              if (s == "none")
                ()
              else
                validateInt(name, 360)(s.stripSuffix("deg"))

          def validateModernA(name: String): V =
            s =>
              if (s.endsWith("%"))
                validatePct(name)(s.stripSuffix("%"))
              else
                validateDbl(name, 1)(s)

          def validateModernSL(name: String): V =
            s =>
              if (s.endsWith("%"))
                validatePct(name)(s.stripSuffix("%"))
              else
                validateInt(name, 100)(s)

          def validateFn3(p: Pattern, a: V, b: V, c: V, f: (String, String, String) => String): Boolean = {
            val m = p.matcher(text)
            if (m.matches) {
              a(m group 1)
              b(m group 2)
              c(m group 3)
              result = f(m group 1, m group 2, m group 3)
              pass
            } else
              undecided
          }

          def validateFn4(p: Pattern, a: V, b: V, c: V, d: V, f: (String, String, String, String) => String): Boolean = {
            val m = p.matcher(text)
            if (m.matches) {
              a(m group 1)
              b(m group 2)
              c(m group 3)
              d(m group 4)
              result = f(m group 1, m group 2, m group 3, m group 4)
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
          def hi = validateInt("hue", 360)
          def sp = validatePct("saturation")
          def lp = validatePct("lightness")
          def a  = validateDbl("alpha", 1)
          def rm = validateModernNPN("red", 255)
          def gm = validateModernNPN("green", 255)
          def bm = validateModernNPN("blue", 255)
          def hm = validateModernH("hue")
          def am = validateModernA("alpha")
          def sm = validateModernSL("saturation")
          def lm = validateModernSL("lightness")

          def validateRgbI  = validateFn3(rgbI, ri, gi, bi,      (r, g, b)    => s"rgb($r,$g,$b)")
          def validateRgbP  = validateFn3(rgbP, rp, gp, bp,      (r, g, b)    => s"rgb($r%,$g%,$b%)")
          def validateRgbaI = validateFn4(rgbaI, ri, gi, bi, a,  (r, g, b, a) => s"rgba($r,$g,$b,$a)")
          def validateRgbaP = validateFn4(rgbaP, rp, gp, bp, a,  (r, g, b, a) => s"rgba($r%,$g%,$b%,$a)")
          def validateHsl   = validateFn3(hsl, hi, sp, lp,       (h, s, l)    => s"hsl($h,$s%,$l%)")
          def validateHsla  = validateFn4(hsla, hi, sp, lp, a,   (h, s, l, a) => s"hsla($h,$s%,$l%,$a)")
          def validateRgbM  = validateFn3(rgbM, rm, gm, bm,      (r, g, b)    => s"rgb($r $g $b)")
          def validateRgbaM = validateFn4(rgbaM, rm, gm, bm, am, (r, g, b, a) => s"rgb($r $g $b/$a)")
          def validateHslM  = validateFn3(hslM, hm, sm, lm,      (h, s, l)    => s"hsl(${h.stripSuffix("deg")} $s $l)")
          def validateHslaM = validateFn4(hslaM, hm, sm, lm, am, (h, s, l, a) => s"hsl(${h.stripSuffix("deg")} $s $l/$a)")

          validateHex
            validateRgbI || validateRgbP || validateRgbaI || validateRgbaP || validateHsl || validateHsla ||
            validateRgbM || validateRgbaM || validateHslM || validateHslaM

          if (result ne null) {
            //println(showRaw(q"""_root_.scalacss.internal.Color($text)"""))
            c.Expr(Apply(Select(Select(Select(Ident(termNames.ROOTPKG), TermName("scalacss")), TermName("internal")), TermName("Color")),
              Literal(Constant(result)) :: Nil))
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
