package scalacss.mutable

import scala.annotation.tailrec
import scalaz.syntax.equal._
import shapeless._
import shapeless.ops.hlist.Mapper
import scalacss._
import StyleC.{Named, MkUsage}
import Register.{MacroName, ErrorHandler, NameGen}

/**
 * TODO Doc & test MutableRegister and friends inc. Style[FC]
 *
 * Performs magic using mutability and side-effects so that ........
 *
 * Thread-safe.
 */
final class Register(initNameGen: NameGen, macroName: MacroName, errHandler: ErrorHandler)(implicit mutex: Mutex) {

  var _nameGen  = initNameGen
  var _styles   = Vector.empty[StyleA]
  var _rendered = false

  private def nextName(implicit cnh: ClassNameHint): ClassName =
    mutex {
      val (cn, ng) = _nameGen.next(cnh)
      _nameGen = ng
      cn
    }

  // Special case: addClassNames-only styles don't need a class-name generated
  private def doesntNeedClassName(s0: StyleS): Boolean =
    s0.addClassNames.nonEmpty && s0.data.isEmpty && s0.className.isEmpty && s0.unsafeExts.isEmpty

  private def isTaken(className: ClassName): Boolean =
    mutex(_styles.exists(_.className === className))

  private def ensureUnique(cn: ClassName): ClassName =
    mutex(
      if (isTaken(cn)) {
        @tailrec def go(suf: Int): ClassName = {
          val n = ClassName(s"$cn-$suf")
          if (isTaken(n))
            go(suf + 1)
          else
            n
        }
        go(2)
      } else
        cn
    )

  def applyMacroName(name: String, style: StyleS)(implicit cnh: ClassNameHint): StyleS =
    if (style.data.isEmpty && style.unsafeExts.isEmpty)
      style
    else {
      val cn = macroName(cnh, name) map ensureUnique
      style.copy(className = cn)
    }

  def registerS(s0: StyleS)(implicit cnh: ClassNameHint): StyleA = mutex {

    val s =
      if (doesntNeedClassName(s0)) {
        val h = s0.addClassNames.head
        val t = s0.addClassNames.tail
        s0.copy(className = Some(h), addClassNames = t)
      } else s0

    val cn = s.className getOrElse nextName(cnh)

    // Optional side-effects for warnings
    errHandler.warn.foreach { f =>

      @inline def warn(s: String): Unit =
        f(cn, Warning(Cond.empty, s))

      if (_rendered)
        warn("Style being registered after stylesheet has been rendered (via .render), or extracted (via .styles).")

      if (isTaken(cn))
        warn("Another style in the register has the same classname.")

      s.warnings foreach (f(cn, _))
    }

    // Register
    val a = StyleA(cn, s.addClassNames, s)
    _styles :+= a
    a
  }

  def registerF[I](s: StyleF[I])(implicit cnh: ClassNameHint, l: StyleLookup[I]): I => StyleA = {
    val add = l.add
    val lookup = mutex(
      s.domain.toStream.foldLeft(l.empty)((q, i) =>
        add(q, i, registerS(s f i))))
    val f = l.get(lookup)
    i => f(i) getOrElse errHandler.badInput(s, i)
  }

  def registerC[M <: HList](s: StyleC)(implicit cnh: ClassNameHint, m: Mapper.Aux[_registerC.type, s.S, M], u: MkUsage[M]): u.Out =
    u apply m(s.styles)

  object _registerC extends Poly1 {
    implicit def cs[W]                (implicit cnh: ClassNameHint): Case.Aux[Named[W, StyleS],    Named[W, StyleA     ]] = at(_ map registerS)
    implicit def cf[W, I: StyleLookup](implicit cnh: ClassNameHint): Case.Aux[Named[W, StyleF[I]], Named[W, I => StyleA]] = at(_ map registerF[I])
  }

  def styles: Vector[StyleA] = mutex {
    _rendered = true
    _styles
  }

  def css(implicit env: Env): Css =
    Css(styles)

  def render[Out](implicit r: Renderer[Out], env: Env): Out =
    r(css)
}


object Register { // ===================================================================================================

  trait MacroName {
    def apply(cnh: ClassNameHint, name: String): Option[ClassName]
  }
  object MacroName {
    object Use extends MacroName {
      override def apply(cnh: ClassNameHint, name: String) =
        if (name.isEmpty)
          None
        else
          // TODO ensure name valid
          Some(ClassName(s"${cnh.value}-$name"))
    }
    object Ignore extends MacroName {
      override def apply(cnh: ClassNameHint, name: String) =
        None
    }
  }

  trait NameGen {
    def next(cnh: ClassNameHint): (ClassName, NameGen)
  }

  object NameGen {
    class IncFmt(fmt: String, nextInt: Int = 1) extends NameGen {
      override def next(cnh: ClassNameHint) = {
        val cn = ClassName(String.format(fmt, cnh.value, nextInt: java.lang.Integer))
        val ng = new IncFmt(fmt, nextInt + 1)
        (cn, ng)
      }
    }

    def numbered(format: String = "%s-%04d"): NameGen =
      new NameGen.IncFmt(format)

    class Alphabet(alphabet: Array[Char], fmt: (ClassNameHint, String) => String, n: Int = 0) extends NameGen {
      @tailrec final def code(cs: List[Char], i: Int): List[Char] = {
        val m = i % alphabet.length
        val r = i / alphabet.length - 1
        val c = alphabet(m)
        val x = c :: cs
        if (r == -1) x else code(x, r)
      }

      override def next(cnh: ClassNameHint) = {
        val c = String valueOf code(Nil, n).toArray
        val cn = ClassName(fmt(cnh, c))
        val ng = new Alphabet(alphabet, fmt, n + 1)
        (cn, ng)
      }
    }

    // http://www.w3.org/TR/CSS21/grammar.html#scanner
    // nonascii   [\240-\377]
    // unicode    \\{h}{1,6}(\r\n|[ \t\r\n\f])?
    // escape     {unicode}|\\[^\r\n\f0-9a-f]
    // nmstart    [_a-z]|{nonascii}|{escape}
    // nmchar     [_a-z0-9-]|{nonascii}|{escape}

    lazy val nmchar7: Array[Char] = (('0' to '9') ++ ('a' to 'z') :+ '-' :+ '_').toArray
    lazy val nmchar8: Array[Char] = ((0xA0 to 0xFF).map(_.toChar) ++ nmchar7).toArray

    private[this] var _nextShortPrefix = 0

    def nextShortPrefix(): String = {
      val p = ('a' + _nextShortPrefix).toChar.toString
      _nextShortPrefix += 1
      p
    }

    def short(prefix  : String      = "_",
              prefix2 : String      = nextShortPrefix(),
              alphabet: Array[Char] = nmchar7): NameGen = {
      val fullPrefix = prefix + prefix2
      if (!fullPrefix.matches("^[_a-zA-Z\u00a0-\u00ff].*"))
        System.err.println(s"[NameGen.short] CSS class names must begin with a-z, an underscore, or A0-FF. Not: '$prefix'.")
      new NameGen.Alphabet(alphabet, (_, c) => fullPrefix + c)
    }

    def short8: NameGen =
      short("\u00a2", alphabet = nmchar8)
  }

  // ===================================================================================================================

  trait ErrorHandler {
    def warn: Option[(ClassName, Warning) => Unit]
    def badInput[I](s: StyleF[I], input: I): StyleA
  }

  object ErrorHandler {

    class Noisy(println: String => Unit) extends ErrorHandler {
      override def warn =
        Some((cn, w) => {
          this.println(s"[CSS WARNING] ${Css.selector(cn, w.cond)} -- ${w.msg}")
        })
      override def badInput[I](s: StyleF[I], i: I): Nothing = {
        val legal = s.domain.toStream.mkString(",")
        val err = s"[CSS ERROR] Invalid StyleF input: [$i]. Legal values are {$legal}."
        this.println(err)
        throw new java.lang.IllegalArgumentException(err)
      }
    }

    val noisy: ErrorHandler =
      new Noisy(Console.err.println)

    val silent: ErrorHandler =
      new ErrorHandler {
        override def warn                            = None
        override def badInput[I](s: StyleF[I], i: I) = fallbackStyle
      }

    val fallbackStyle: StyleA = {
      import Dsl._
      val s = style(backgroundColor("#ffbaba"), color("#d8000c"))(Compose.safe)
      StyleA(ClassName("_SCSS_ERROR_"), Vector.empty, s)
    }
  }
}