package japgolly.scalacss

import shapeless._
import shapeless.ops.hlist.Reverse
import japgolly.TODO.Domain

/**
 * A high-level style, that can describe a subject and its children in a variety of conditions.
 *
 * Flavours
 * ========
 * [[StyleS]]: (S = static)
 * [[StyleF]]: (F = function)
 * [[StyleC]]: (C = composite)
 */
sealed trait Style

/**
 * A single style that applied to a single subject.
 */
sealed abstract class Style1 extends Style {
  final def named(w: Witness): StyleC.Named[w.T, this.type] = StyleC.Named(this)
}

object Style {

  /**
   * Unsafe extensions to a style.
   *
   * Unsafe because by using this, you knowingly opt-out of type-safety.
   * There will be no relationship visible to the compiler between this additional CSS and its intended targets.
   *
   * An example of what this would be useful for, is styling the entire page, declaring the default font and size,
   * the properties of all `h1` elements, etc.
   */
  type UnsafeExts = List[UnsafeExt]

  /**
   * @param sel A CSS selector based on the intended parent's selector.
   *            Example: `_ + " h1"` will style all child `h1` elements.
   *            Example: `_ + ".debug"` will specify a style only active when a `"debug"` class is present.
   */
  final case class UnsafeExt(sel: CssSelector => CssSelector, style: StyleS)
}

/**
 * A static style.
 *
 * @param className Manually specifies this style's class name. By default it is automatically generated.
 */
final class StyleS(val data      : Map[Cond, AVsAndWarnings],
                   val unsafeExts: Style.UnsafeExts,
                   val className : Option[String]) extends Style1

/**
 * A function to a style. A style that depends on input provided when used.
 *
 * @tparam I Input required by the style.
 * @param domain The function domain. All possible (or legal) inputs to this function.
 */
final class StyleF[I](val f: I => StyleS, val domain: Domain[I]) extends Style1

object StyleF {
  def apply[I](f: I => StyleS): Domain[I] => StyleF[I] =
    new StyleF(f, _)
}

/**
 * A composite style. Two more or more styles that must be applied as a whole in order to be successful.
 *
 * This ''demands'' that anyone using this style:
 *
 *   1. Obtain an instance of all sub-styles.
 *   2. Acknowledge the purpose of each sub-style (and thus not accept them in the wrong order and apply them to the
 *   wrong targets).
 *
 * Before styles can be composed, they must be annotated with a name. Do so like `avatarStyle.named('avatar)`.
 * Named styles can then be composed using `:*:`.
 *
 * {{{
 *   val profileStyle: StyleC =
 *     titleStyle.named('title) :*: avatarStyle.named('avatar) :*: buttonStyle.named('btn)
 * }}}
 */
sealed trait StyleC extends Style {
  import StyleC._
  type S <: HList
  val styles: S

  def :*:[W, A](a: Named[W, A])(implicit u: UniqueCons[Named[W, A], S]): u.Out =
    u(a, styles)

  def :*:[H <: HList, R <: HList](h: Aux[H])(implicit r: Reverse.Aux[H, R], u: UniquePrepend[R, S]): u.Out =
    u(r(h.styles), styles)
}


object StyleC {
  type Aux[s <: HList] = StyleC { type S = s }

  /**
   * Annotates a value `A` with a type `W`.
   */
  final case class Named[W, A](value: A) {
    type This = Named[W, A]

    def map[B](f: A => B): Named[W, B] = Named(f(value))

    def :*:[HW, H](h: Named[HW, H])(implicit u: UniqueCons[Named[HW, H], This :: HNil]): u.Out =
      u(h, this :: HNil)

    def :*:[H <: HList, R <: HList](h: Aux[H])(implicit r: Reverse.Aux[H, R], u: UniquePrepend[R, This :: HNil]): u.Out =
      u(r(h.styles), this :: HNil)
  }

  /**
   * Proof that `A` can be added to a HList `L` without any duplicate names appearing in the result.
   */
  final class UniqueCons[A, L <: HList] {
    type Out = Aux[A :: L]
    def apply(a: A, l: L): Out =
      new StyleC {
        override type S     = A :: L
        override val styles = a :: l
      }
  }
  object UniqueCons {
    implicit def base[A, N <: HNil]: UniqueCons[A, N] =
      new UniqueCons[A, N]
    implicit def ind[AW, AA, HW, HA, T <: HList](implicit ev: AW =:!= HW, next: UniqueCons[Named[AW,AA], T]): UniqueCons[Named[AW,AA], Named[HW,HA] :: T] =
      new UniqueCons[Named[AW,AA], Named[HW,HA] :: T]
  }

  /**
   * Proof that `A` can be prepended (head-to-tail) to a HList `L` without any duplicate names appearing in the result.
   */
  sealed trait UniquePrepend[A <: HList, L <: HList] {
    type Out <: Aux[_]
    def apply(a: A, l: L): Out
  }
  object UniquePrepend {
    def apply[A <: HList, L <: HList, O <: Aux[_]](f: (A, L) => O): UniquePrepend[A, L] { type Out = O } =
      new UniquePrepend[A, L] {
        override type Out = O
        override def apply(a: A, l: L): O = f(a, l)
      }
    implicit def base[A, L <: HList](implicit w: UniqueCons[A, L]) =
      UniquePrepend[A :: HNil, L, w.Out]((a, l) => w(a.head, l))
    implicit def ind[H, T <: HList, L <: HList](implicit w: UniqueCons[H, L], next: UniquePrepend[T, H :: L]) =
      UniquePrepend[H :: T, L, next.Out]((ht, l) => next(ht.tail, w(ht.head, l).styles))
  }
}
