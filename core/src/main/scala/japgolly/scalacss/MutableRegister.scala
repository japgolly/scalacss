package japgolly.scalacss

import scala.annotation.tailrec
import scalaz.NonEmptyList
import MutableRegister.{ErrorHandler, NameGen}

/**
 * TODO Doc MutableRegister and friends
 *
 * This is the only portion of the library that is mutable and has side-effects.
 */
final class MutableRegister(initNameGen: NameGen, errHandler: ErrorHandler)(implicit mutex: Mutex) {

  var _nameGen = initNameGen
  var _styles = List.empty[StyleA]

  private def nextName(): ClassName =
    mutex {
      val (cn, ng) = _nameGen.next
      _nameGen = ng
      cn
    }

  // Convenience methods
  @inline def register(s: StyleS): StyleA = registerS(s)
  @inline def register[I](s: StyleF[I])(implicit m: StyleLookup[I]): I => StyleA = registerF(s)

  def registerS(s: StyleS): StyleA = {
    val cn = s.className.fold(nextName())(ClassName)

    // Optional side-effects for warnings
    // TODO Warn about reregistration
    for {
      wf                            <- errHandler.warn
      (cond, AVsAndWarnings(_, ws)) <- s.data if ws.nonEmpty
      warning                       <- ws
    } wf(cn, cond, warning)

    // Register
    val a = StyleA(cn, s)
    mutex { _styles ::= a }
    a
  }

  def registerF[I](s: StyleF[I])(implicit m: StyleLookup[I]): I => StyleA = {
    val add = m.add
    val lookup = mutex(
      s.domain.toStream.foldLeft(m.empty)((q, i) =>
        add(q, i, registerS(s f i))))
    val f = m.get(lookup)
    i => f(i) getOrElse errHandler.badInput(s, i)
  }

  def styles: List[StyleA] =
    mutex(_styles)

  def render[Out](implicit r: Renderer[Out], env: Env): Out =
    r(Css(styles))
}


object MutableRegister { // ============================================================================================

  trait NameGen {
    def next: (ClassName, NameGen)
  }

  object NameGen {
    class IncFmt(fmt: String, nextInt: Int = 1) extends NameGen {
      override def next = {
        val cn = ClassName(String.format(fmt, nextInt: java.lang.Integer))
        val ng = new IncFmt(fmt, nextInt + 1)
        (cn, ng)
      }
    }

    class Alphabet(alphabet: Array[Char], fmt: String => String, n: Int = 0) extends NameGen {
      @tailrec final def code(cs: List[Char], i: Int): List[Char] = {
        val m = i % alphabet.length
        val r = i / alphabet.length - 1
        val c = alphabet(m)
        val x = c :: cs
        if (r == -1) x else code(x, r)
      }

      override def next = {
        val c = String valueOf code(Nil, n).toArray
        val cn = ClassName(fmt(c))
        val ng = new Alphabet(alphabet, fmt, n + 1)
        (cn, ng)
      }
    }

    val alphaNumeric: Array[Char] =
      (('0' to '9') ++ ('a' to 'z') ++ ('A' to 'Z')).toArray
  }

  // ===================================================================================================================

  trait ErrorHandler {
    def warn: Option[(ClassName, Cond, Warning) => Unit]
    def badInput[I](s: StyleF[I], input: I): StyleA
  }

  object ErrorHandler {

    class Noisy(println: String => Unit) extends ErrorHandler {
      override def warn =
        Some((cn, cond, w) => {
          this.println(s"[CSS WARNING] ${Css.selector(cn, cond)} -- $w")
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
      import Attrs._
      val s = new StyleS(Map(NoCond -> AVsAndWarnings(NonEmptyList(
        AV(backgroundColor, "#ffbaba"),
        AV(color, "#d8000c"))
        , Nil)), Nil, None)
      StyleA(ClassName("_SCSS_ERROR_"), s)
    }
  }
}