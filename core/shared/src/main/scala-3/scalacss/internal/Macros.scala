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

    private def cssFnRegex(f: String, as: String*) = {
      val args = as mkString "\\s*,\\s*"
      Pattern compile s"^(?i)\\s*$f\\s*\\(\\s*$args\\s*\\)\\s*$$"
    }

    private def int = "(-?\\d+)"
    private def dbl = """(-?\d+(?:\.\d+)?|\.\d+)"""
    private def pct = s"$dbl%"

    private val rgbI  = cssFnRegex("rgb",  int, int, int)
    private val rgbP  = cssFnRegex("rgb",  pct, pct, pct)
    private val rgbaI = cssFnRegex("rgba", int, int, int, dbl)
    private val rgbaP = cssFnRegex("rgba", pct, pct, pct, dbl)
    private val hsl   = cssFnRegex("hsl",  int, pct, pct)
    private val hsla  = cssFnRegex("hsla", int, pct, pct, dbl)

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

      def validateDbl(name: String, max: Double): V =
        s => {
          val d = s.toDouble
          if (d < 0 || d > max)
            fail(s"Invalid $name value: $s")
        }

      def validatePct(name: String): V =
        validateDbl(name, 100)

      def validateFn3(p: Pattern, a: V, b: V, c: V, f: (String, String, String) => String): Boolean = {
        val m = p.matcher(text)
        if (m.matches) {
          a(m group 1)
          b(m group 2)
          c(m group 3)
          if (result == null)
            result = Right(f(m group 1, m group 2, m group 3))
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
          if (result == null)
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
      def h  = validateInt("hue", 360)
      def s  = validatePct("saturation")
      def l  = validatePct("lightness")
      def a  = validateDbl("alpha", 1)

      def validateRgbI  = validateFn3(rgbI, ri, gi, bi,     (r, g, b)    => s"rgb($r,$g,$b)")
      def validateRgbP  = validateFn3(rgbP, rp, gp, bp,     (r, g, b)    => s"rgb($r%,$g%,$b%)")
      def validateRgbaI = validateFn4(rgbaI, ri, gi, bi, a, (r, g, b, a) => s"rgba($r,$g,$b,$a)")
      def validateRgbaP = validateFn4(rgbaP, rp, gp, bp, a, (r, g, b, a) => s"rgba($r%,$g%,$b%,$a)")
      def validateHsl   = validateFn3(hsl, h, s, l,         (h, s, l)    => s"hsl($h,$s%,$l%)")
      def validateHsla  = validateFn4(hsla, h, s, l, a,     (h, s, l, a) => s"hsla($h,$s%,$l%,$a)")

      validateHex || validateRgbI || validateRgbP || validateRgbaI || validateRgbaP || validateHsl || validateHsla

      Option(result).getOrElse(Left("Invalid colour literal: \"" + text + "\"."))
    }
  }
}
