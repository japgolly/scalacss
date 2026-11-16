package scalacss.internal.mutable

import japgolly.univeq._
import scala.annotation.tailrec
import scalacss.internal._
import scalacss.internal.mutable.Register.{ErrorHandler, MacroName, NameGen}

/**
 * TODO Doc & test MutableRegister and friends inc. Style[FC]
 *
 * Performs magic using mutability and side-effects so that ........
 *
 * Thread-safe.
 */
final class Register(initNameGen: NameGen, macroName: MacroName, errHandler: ErrorHandler)(implicit mutex: Mutex) {

  var _nameGen   = initNameGen
  var _styles    = Vector.empty[StyleA]
  var _keyframes = Vector.empty[Keyframes]
  var _fontFaces = Vector.empty[FontFace[FontFamily]]
  var _rendered  = false

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
      _styles.exists(s => (s.style.data.nonEmpty || s.style.unsafeExts.nonEmpty) && s.className ==* className)
        || _keyframes.exists(_.name ==* className))

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
    if (style.className.nonEmpty || doesntNeedClassName(style))
      style
    else {
      val cn = macroName(cnh, name) map ensureUnique
      style.copy(className = cn)
    }

  def registerS(s0: StyleS)(implicit cnh: ClassNameHint): StyleA = mutex {
    val (cn, addClassNames) =
      if (doesntNeedClassName(s0))
        (s0.addClassNames.head, s0.addClassNames.tail)
      else
        (s0.className getOrElse nextName(cnh), s0.addClassNames)

    // Register
    emitRegistrationWarnings(Right(cn), s0.allWarnings, checkDuplicates = !doesntNeedClassName(s0))
    val a = StyleA(cn, addClassNames, s0)
    _styles :+= a
    a
  }

  /** Optional side-effects for warnings */
  private def emitRegistrationWarnings(src: Either[FontFamily, ClassName], warnings: => Vector[Warning], checkDuplicates: Boolean = true): Unit =
    errHandler.warn.foreach { f =>

      def warn(s: String): Unit =
        f(src, Warning(Cond.empty, s))

      if (_rendered)
        warn("Style being registered after stylesheet has been rendered (via .render), or extracted (via .styles).")

      if (checkDuplicates && src.exists(isTaken))
        warn("Another style in the register has the same classname.")

      warnings foreach (f(src, _))
    }

  def registerF[I](s: StyleF[I])(implicit cnh: ClassNameHint, l: StyleLookup[I]): I => StyleA =
    _registerF(s, l)((add, domain) =>
      domain.iterator.foldLeft(l.empty)((q, i) =>
        add(q, i, s f i)))

  def registerF2[I](s: StyleF[I], manualName: String)(toCN: (I, Int) => String)(implicit cnh: ClassNameHint, l: StyleLookup[I]): I => StyleA = {
    val mn = if (manualName eq null) "" else MacroName.Use.sanitise(manualName.trim)
    _registerF(s, l)((add, domain) =>
      domain.iterator.zipWithIndex.foldLeft(l.empty) { case (q, (i, index)) =>
        var style = s f i
        if (mn.nonEmpty) {
          val suffix = MacroName.Use.sanitise(Option(toCN(i, index)).getOrElse(""))
          val cn = ClassName(if (suffix.isEmpty) mn else s"$mn-$suffix")
          style = style.copy(className = Some(cn))
        }
        add(q, i, style)
      }
    )
  }

  def registerFM[I](s: StyleF[I], nameFromMacro: String)(toCN: (I, Int) => String)(implicit cnh: ClassNameHint, l: StyleLookup[I]): I => StyleA =
    _registerF(s, l)((add, domain) =>
      domain.iterator.zipWithIndex.foldLeft(l.empty) { case (q, (i, index)) =>
        var style = s f i
        if (style.className.isEmpty && !doesntNeedClassName(style) && (nameFromMacro ne null))
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

  def registerKeyframes(keyframes: Keyframes)(implicit cnh: ClassNameHint): Keyframes =
    registerKeyframes(keyframes.name.value, keyframes.frames)

  def registerKeyframes(name: String, frames: Seq[(KeyframeSelector, StyleA)])(implicit cnh: ClassNameHint): Keyframes =
    mutex {
      val cn = macroName(cnh, name).fold(nextName(cnh))(ensureUnique)
      val kf = Keyframes(cn, frames)
      emitRegistrationWarnings(Right(cn), frames.iterator.flatMap(_._2.style.allWarnings).toVector)
      _keyframes :+= kf
      kf
    }

  def registerFontFace(fontFace: FontFace[Option[FontFamily]])(implicit cnh: ClassNameHint): FontFace[FontFamily] = mutex {
    // This allows duplicate fontFamily names to be specified
    val name = FontFamily(fontFace.fontFamily.fold(nextName(cnh).value)(_.name))
    emitRegistrationWarnings(Left(name), Vector.empty, checkDuplicates = false)
    val ff = fontFace.copy(fontFamily = name)
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

  def fontFaces: Vector[FontFace[FontFamily]] = mutex {
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
      apply(cnh, name).map { n =>
        val raw = domain
        val d   = if (raw eq null) "" else MacroName.Use.sanitise(raw)
        if (d.isEmpty) n else ClassName(n.value + "-" + d)
      }
  }

  object MacroName {

    /**
     * Makes a macro-provided name safe for CSS and appends it to the ClassNameHint.
     */
    object Use extends MacroName {
      val cssIllegal = "[^_a-zA-Z0-9\u00a0-\u00ff-]".r

      private[mutable] def sanitise(s: String): String =
        cssIllegal.replaceAllIn(s, "_")

      override def apply(cnh: ClassNameHint, name: String) =
        if (name.isEmpty)
          None
        else {
          val name2 = sanitise(name)
          val cnh2  = sanitise(cnh.value)
          Some(ClassName(if (cnh2.isEmpty) name2 else s"$cnh2-$name2"))
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

    // This starts at 1/b instead of 0/a because of https://github.com/japgolly/scalacss/issues/358
    private[this] var _nextShortPrefix = 1

    def nextShortPrefix(): String = synchronized {
      val p = toAlphabet(_nextShortPrefix)
      _nextShortPrefix += 1
      p
    }

    private[this] def toAlphabet(i: Int): String = {
      val quot = i / 26
      val rem = i % 26
      val letter = ('a' + rem).toChar
      if (quot == 0) letter.toString
      else toAlphabet(quot - 1) + letter
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

    private[mutable] def resetNextShortPrefix() = synchronized {
      // This starts at 1/b instead of 0/a because of https://github.com/japgolly/scalacss/issues/358
      _nextShortPrefix = 1
    }
  }

  // ===================================================================================================================

  trait ErrorHandler {
    def warn: Option[(Either[FontFamily, ClassName], Warning) => Unit]
    def badInput[I](s: StyleF[I], input: I): StyleA
  }

  object ErrorHandler {

    class Noisy(println: String => Unit) extends ErrorHandler {
      override def warn =
        Some({
          case (Right(cn), w) => this.println(s"[CSS WARNING] ${Css.selector(cn, w.cond)} -- ${w.msg}")
          case (Left(ff), w)  => this.println(s"[CSS WARNING] font-family: ${ff.name} -- ${w.msg}")
        })
      override def badInput[I](s: StyleF[I], i: I): Nothing = {
        val legal = s.domain.iterator.mkString(",")
        val err = s"[CSS ERROR] Invalid StyleF input: [$i]. Legal values are {$legal}."
        this.println(err)
        throw new java.lang.IllegalArgumentException(err)
      }
    }

    def noisy: ErrorHandler =
      new Noisy(Console.err.println)

    def silent: ErrorHandler =
      new ErrorHandler {
        override def warn                            = None
        override def badInput[I](s: StyleF[I], i: I) = fallbackStyle
      }

    def fallbackStyle: StyleA = {
      import Dsl._
      val s = style(backgroundColor(Color("#ffbaba")), color(Color("#d8000c")))(Compose.safe)
      StyleA(ClassName("_SCALACSS_ERROR_"), Vector.empty, s)
    }
  }
}
