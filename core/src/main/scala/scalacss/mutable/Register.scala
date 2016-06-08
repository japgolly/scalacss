package scalacss.mutable

import scala.annotation.tailrec
import scalaz.syntax.equal._
import scalacss._
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
  var _keyframes = Vector.empty[Keyframes]
  var _fontFaces = Vector.empty[FontFace]
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
    mutex(
      _styles.exists(_.className === className)
        || _keyframes.exists(_.name === className)
        || _fontFaces.exists(_.fontFamily == className.value))

  private def ensureUnique(cn: ClassName): ClassName =
    mutex(
      if (isTaken(cn)) {
        @tailrec def go(suf: Int): ClassName = {
          val n = ClassName(s"${cn.value}-$suf")
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

    // Register
    emitRegistrationWarnings(cn, s.warnings)
    val a = StyleA(cn, s.addClassNames, s)
    _styles :+= a
    a
  }

  /** Optional side-effects for warnings */
  private def emitRegistrationWarnings(cn: ClassName, warnings: => Vector[Warning]): Unit =
    errHandler.warn.foreach { f =>

      def warn(s: String): Unit =
        f(cn, Warning(Cond.empty, s))

      if (_rendered)
        warn("Style being registered after stylesheet has been rendered (via .render), or extracted (via .styles).")

      if (isTaken(cn))
        warn("Another style in the register has the same classname.")

      warnings foreach (f(cn, _))
    }

  def registerF[I](s: StyleF[I])(implicit cnh: ClassNameHint, l: StyleLookup[I]): I => StyleA =
    _registerF(s, l)((add, domain) =>
      domain.toStream.foldLeft(l.empty)((q, i) =>
        add(q, i, s f i)))

  def registerF2[I](s: StyleF[I], manualName: String)(toCN: (I, Int) => String)(implicit cnh: ClassNameHint, l: StyleLookup[I]): I => StyleA =
    _registerF(s, l)((add, domain) =>
      domain.toStream.zipWithIndex.foldLeft(l.empty) { case (q, (i, index)) =>
        val cn = ClassName(manualName + "-" + toCN(i, index))
        val style = (s f i).copy(className = Some(cn))
        add(q, i, style)
      }
    )

  def registerFM[I](s: StyleF[I], nameFromMacro: String)(toCN: (I, Int) => String)(implicit cnh: ClassNameHint, l: StyleLookup[I]): I => StyleA =
    _registerF(s, l)((add, domain) =>
      domain.toStream.zipWithIndex.foldLeft(l.empty) { case (q, (i, index)) =>
        var style = s f i
        if (style.className.isEmpty && (nameFromMacro ne null))
          for (cn <- macroName(cnh, nameFromMacro, toCN(i, index)))
            style = style.copy(className = Some(ensureUnique(cn)))
        add(q, i, style)
      }
    )

  private def _registerF[I](s: StyleF[I], l: StyleLookup[I])(build: ((l.T, I, StyleS) => l.T, Domain[I]) => l.T)(implicit cnh: ClassNameHint): I => StyleA = {
    val add    = l.add
    val lookup = mutex(build((q, i, styleS) => add(q, i, registerS(styleS)), s.domain))
    val f      = l.get(lookup)
    i => f(i) getOrElse errHandler.badInput(s, i)
  }

  def registerKeyframes(keyframes: Keyframes)(implicit cnh: ClassNameHint): Keyframes = mutex {
    val cn = macroName(cnh, keyframes.name.value).fold(nextName(cnh))(ensureUnique)
    val kf = keyframes.copy(name = cn)
    emitRegistrationWarnings(cn, Vector.empty)
    _keyframes :+= kf
    kf
  }

  def registerFontFace(fontFace: FontFace)(implicit cnh: ClassNameHint): FontFace = {
    val cn = macroName(cnh, fontFace.fontFamily).fold(nextName(cnh))(ensureUnique)
    val ff = fontFace.copy(fontFamily = cn.value)
    emitRegistrationWarnings(cn, Vector.empty)
    _fontFaces :+= ff
    ff
  }

  def styles: Vector[StyleA] = mutex {
    _rendered = true
    _styles
  }

  def keyframes: Vector[Keyframes] = mutex {
    _rendered = true
    _keyframes
  }

  def fontFaces: Vector[FontFace] = mutex {
    _rendered = true
    _fontFaces
  }

  def css(implicit env: Env): Css =
    Css(styles, keyframes, fontFaces)

  def render[Out](implicit r: Renderer[Out], env: Env): Out =
    r(css)
}


object Register { // ===================================================================================================

  /**
   * Determines how to generate a class name for a style, using a name provided by a macro (i.e. the variable name).
   */
  trait MacroName {
    def apply(cnh: ClassNameHint, name: String): Option[ClassName]

    def apply(cnh: ClassNameHint, name: String, domain: => String): Option[ClassName] =
      apply(cnh, name).map(n => ClassName(n.value + "-" + domain))
  }

  object MacroName {

    /**
     * Makes a macro-provided name safe for CSS and appends it to the ClassNameHint.
     */
    object Use extends MacroName {
      val cssIllegal = "[^_a-zA-Z0-9\u00a0-\u00ff-]".r

      override def apply(cnh: ClassNameHint, name: String) =
        if (name.isEmpty)
          None
        else {
          val name2 = cssIllegal.replaceAllIn(name, "_")
          Some(ClassName(s"${cnh.value}-$name2"))
        }
    }

    /**
     * Ignore the macro-provided name. Styles will be named arbitrarily by [[NameGen]].
     */
    object Ignore extends MacroName {
      override def apply(cnh: ClassNameHint, name: String) =
        None
    }
  }

  /**
   * Generates an arbitrary name for a style.
   */
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
      val s = style(backgroundColor(Color("#ffbaba")), color(Color("#d8000c")))(Compose.safe)
      StyleA(ClassName("_SCALACSS_ERROR_"), Vector.empty, s)
    }
  }
}
