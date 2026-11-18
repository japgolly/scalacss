package scalacss.internal

import java.util.regex.Pattern
import scala.language.`3.0`
import scala.reflect.NameTransformer
import scala.quoted._

object Macros {

  object Dsl {

    trait Base {
      extension (inline sc: StringContext) {
        /** c"#fc6" provides a validates Color */
        inline def c(inline args: Any*): Color =
          ${ ColorLiteral.impl('sc) }
      }
    }

    inline def name(): String =
      ${ nameQuoted }

    private def nameQuoted(using Quotes): Expr[String] = {
      import quotes.reflect._

      val owner = {
        var o = Symbol.spliceOwner
        while (o.flags.is(Flags.Synthetic))
          o = o.owner
        o
      }

      val isStyleSheet: Symbol => Boolean = {
        val ss = TypeRepr.of[mutable.StyleSheet.Base]
        sym =>
          try {
            sym.tree match {
              case ClassDef(_, _, parents, _, _) =>
                parents.exists {
                  case p: TypeTree => (p.tpe <:< ss)
                  case _           => false
                }
              case _ => false
            }
          } catch {
            case _: Throwable => false
          }
      }

      val classOwner = {
        var o = owner.owner
        while (!isStyleSheet(o) && !o.maybeOwner.isNoSymbol)
          o = o.owner
        o
      }

      def fixName(s: String): String =
        s.replace("$", "")

      val localName =
        fixName(owner.name)

      // `style()` instead of `val x = style()` results in "<local OuterClass>"
      val finalName =
        if (localName startsWith "<")
          ""
        else {
          // Try to extract a name, relative to the stylesheet class
          val className = fixName(classOwner.fullName)
          val fullName  = fixName(owner.fullName)
          if ((fullName.length > className.length + 1) && fullName.startsWith(className + ".")) {
            val relName = fullName.substring(className.length + 1).replace('.', '-')
            relName
          } else
            // Default to local name
            localName
        }

      Inlined(None, Nil, Literal(StringConstant(finalName))).asExprOf[String]
    }

    import DslMacros._

    trait Mixin {
      protected def __macroStyle    (name: String): MStyle
      protected def __macroStyleF   (name: String): MStyleF
      protected def __macroKeyframes(name: String): MKeyframes
      protected def __macroKeyframe               : MStyle
      protected def __macroFontFace               : MFontFace

      final protected inline def style    : MStyle     = __macroStyle(name())
      final protected inline def styleF   : MStyleF    = __macroStyleF(name())
      final protected inline def keyframes: MKeyframes = __macroKeyframes(name())
      final protected inline def keyframe : MStyle     = __macroKeyframe
      final protected inline def fontFace : MFontFace  = __macroFontFace
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
    private def dblRaw = """(?:-?\d+(?:\.\d+)?|\.\d+)"""
    private def dbl = s"($dblRaw)"
    private def pct = s"$dbl%"

    // NPN = number, percentage, or none
    private def modernNPN = s"($dblRaw%?|none)"
    private def modernA = s"\\s*/\\s*($dblRaw%?)"
    private def modernH = s"($dblRaw(?:deg|grad|rad|turn)?|none)"
    private def modernSL = s"($dblRaw%?)"

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

    def impl(sc: Expr[StringContext])(using Quotes): Expr[Color] = {
      import quotes.reflect._

      import report.{errorAndAbort => fail}

      val arg: Expr[String] =
        sc match {
          case '{ StringContext(${Varargs(parts)}*) } =>
            parts.toList match {
              case a :: Nil => a
              case x        => fail(s"Expected exactly 1 StringContext part, got: $x")
            }
          case _ =>
            fail(s"Failed to extract StringContext parts")
        }

      val value: String =
        arg.asTerm match {
          case Literal(StringConstant(v))                => v
          case Inlined(_, _, Literal(StringConstant(v))) => v
          case x                                         => fail("Don't know how to handle: " + x)
        }

      attempt(value) match {
        case Right(c) =>
          val l = Inlined(None, Nil, Literal(StringConstant(c))).asExprOf[String]
          Inlined(None, Nil, '{ Color($l) }.asTerm).asExprOf[Color]
        case Left(e) =>
          report.errorAndAbort(e)
      }
    }

    def runtime(text: String): Color =
      attempt(text) match {
        case Right(c) => Color(c)
        case Left(e) => throw new IllegalArgumentException(e)
      }

    def attempt(text: String): Either[String, String] = {

      var result: Either[String, String] =
        null

      def pass = true
      def undecided = false
      def fail(err: String) = {
        if (result eq null)
          result = Left(s"Invalid colour literal: \"$text\". $err")
        false
      }

      def validateHex: Boolean =
        (text.nonEmpty && text.charAt(0) == '#') && {
          val v = text.drop(1)
          v.length match {
            case (3 | 6 | 4 | 8) if v.forall(isHex) => result = Right(text.toLowerCase); pass
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

      def validateDbl(name: String, max: Double, suffix: String = ""): V =
        s => {
          val d = s.toDouble
          if (d < 0 || d > max)
            fail(s"Invalid $name value: $s$suffix")
        }

      def validatePct(name: String): V =
        validateDbl(name, 100, "%")

      def normNone(s: String): String =
        if (s.equalsIgnoreCase("none")) "none" else s

      def normHue(s: String): String =
        if (s.equalsIgnoreCase("none")) "none"
        else {
          val l = s.toLowerCase
          if (l.endsWith("deg")) l.substring(0, l.length - 3) else l
        }

      def validateModernNPN(name: String, maxNum: Int): V =
        s =>
          if (s.equalsIgnoreCase("none"))
            ()
          else if (s.endsWith("%"))
            validatePct(name)(s.substring(0, s.length - 1))
          else
            validateDbl(name, maxNum)(s)

      def validateModernH(name: String): V =
        s => {
          val l = s.toLowerCase
          if (l == "none")             ()
          else if (l.endsWith("deg"))  validateDbl(name, 360)(l.substring(0, l.length - 3))
          else if (l.endsWith("grad")) validateDbl(name, 400, "grad")(l.substring(0, l.length - 4))
          else if (l.endsWith("turn")) validateDbl(name, 1, "turn")(l.substring(0, l.length - 4))
          else if (l.endsWith("rad"))  validateDbl(name, 2 * Math.PI, "rad")(l.substring(0, l.length - 3))
          else                         validateDbl(name, 360)(s)
        }

      def validateModernA(name: String): V =
        s =>
          if (s.endsWith("%"))
            validatePct(name)(s.substring(0, s.length - 1))
          else
            validateDbl(name, 1)(s)

      def validateModernSL(name: String): V =
        s =>
          if (s.endsWith("%"))
            validatePct(name)(s.substring(0, s.length - 1))
          else
            validateDbl(name, 100)(s)

      def validateFn3(p: Pattern, a: V, b: V, c: V, f: (String, String, String) => String): Boolean = {
        val m = p.matcher(text)
        if (m.matches) {
          a(m group 1)
          if (result eq null) b(m group 2)
          if (result eq null) c(m group 3)
          if (result eq null)
            result = Right(f(m group 1, m group 2, m group 3))
          pass
        } else
          undecided
      }

      def validateFn4(p: Pattern, a: V, b: V, c: V, d: V, f: (String, String, String, String) => String): Boolean = {
        val m = p.matcher(text)
        if (m.matches) {
          a(m group 1)
          if (result eq null) b(m group 2)
          if (result eq null) c(m group 3)
          if (result eq null) d(m group 4)
          if (result eq null)
            result = Right(f(m group 1, m group 2, m group 3, m group 4))
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
      def validateRgbM  = validateFn3(rgbM, rm, gm, bm,      (r, g, b)    => s"rgb(${normNone(r)} ${normNone(g)} ${normNone(b)})")
      def validateRgbaM = validateFn4(rgbaM, rm, gm, bm, am, (r, g, b, a) => s"rgb(${normNone(r)} ${normNone(g)} ${normNone(b)}/$a)")
      def validateHslM  = validateFn3(hslM, hm, sm, lm,      (h, s, l)    => s"hsl(${normHue(h)} $s $l)")
      def validateHslaM = validateFn4(hslaM, hm, sm, lm, am, (h, s, l, a) => s"hsl(${normHue(h)} $s $l/$a)")

      validateHex ||
        validateRgbI || validateRgbP || validateRgbaI || validateRgbaP || validateHsl || validateHsla ||
        validateRgbM || validateRgbaM || validateHslM || validateHslaM

      Option(result).getOrElse(Left("Invalid colour literal: \"" + text + "\"."))
    }
  }
}
