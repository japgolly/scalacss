package japgolly.scalacss.mutable

import scala.annotation.tailrec
import shapeless._
import shapeless.ops.hlist.Mapper
import japgolly.scalacss._
import StyleC.{Named, MkUsage}
import Register.{ErrorHandler, NameGen}

/**
 * TODO Doc & test MutableRegister and friends inc. Style[FC]
 *
 * Performs magic using mutability and side-effects so that ........
 *
 * Thread-safe.
 */
final class Register(initNameGen: NameGen, errHandler: ErrorHandler)(implicit mutex: Mutex) {

  var _nameGen = initNameGen
  var _styles = Vector.empty[StyleA]

  private def nextName(): ClassName =
    mutex {
      val (cn, ng) = _nameGen.next
      _nameGen = ng
      cn
    }

  // Convenience methods
  @inline def register(s: StyleS): StyleA = registerS(s)
  @inline def register[I](s: StyleF[I])(implicit l: StyleLookup[I]): I => StyleA = registerF(s)
  @inline def register[M <: HList](s: StyleC)(implicit m: Mapper.Aux[_registerC.type, s.S, M], u: MkUsage[M]): u.Out = registerC(s)(m, u)

  def registerS(s: StyleS): StyleA = {
    val cn = s.className getOrElse nextName()

    // Optional side-effects for warnings
    // TODO Warn about reregistration
    for {
      wf      <- errHandler.warn
      warning <- s.warnings
    } wf(cn, warning)

    // Register
    val a = StyleA(cn, s.addClassNames, s)
    mutex { _styles :+= a }
    a
  }

  def registerF[I](s: StyleF[I])(implicit l: StyleLookup[I]): I => StyleA = {
    val add = l.add
    val lookup = mutex(
      s.domain.toStream.foldLeft(l.empty)((q, i) =>
        add(q, i, registerS(s f i))))
    val f = l.get(lookup)
    i => f(i) getOrElse errHandler.badInput(s, i)
  }

  def registerC[M <: HList](s: StyleC)(implicit m: Mapper.Aux[_registerC.type, s.S, M], u: MkUsage[M]): u.Out =
    u apply m(s.styles)

  object _registerC extends Poly1 {
    implicit def cs[W]                : Case.Aux[Named[W, StyleS],    Named[W, StyleA     ]] = at(_ map registerS)
    implicit def cf[W, I: StyleLookup]: Case.Aux[Named[W, StyleF[I]], Named[W, I => StyleA]] = at(_ map registerF[I])
  }

  def styles: Vector[StyleA] =
    mutex(_styles)

  def render[Out](implicit r: Renderer[Out], env: Env): Out =
    r(Css(styles))
}


object Register { // ===================================================================================================

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

    val alphaNumericChars: Array[Char] =
      (('0' to '9') ++ ('a' to 'z') ++ ('A' to 'Z')).toArray

    def short(prefix: String = ""): NameGen =
      new NameGen.Alphabet(alphaNumericChars, prefix + _)

    def numbered(prefix: String = "scalacss-"): NameGen =
      new NameGen.IncFmt(prefix + "%04d")
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
      import Attrs._, Dsl._
      val s = style(backgroundColor("#ffbaba"), color("#d8000c"))(Compose.safe)
      StyleA(ClassName("_SCSS_ERROR_"), Vector.empty, s)
    }
  }
}