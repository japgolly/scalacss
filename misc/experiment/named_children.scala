import shapeless._
import shapeless.syntax.singleton._

/**
 * This addresses:
 *   FR-01: Dev shall be able to define a style that requires a specific configuration of children such that the
 *   compiler will enforce that the children are styled.
 *
 * The most sensible way of doing this is to have a composite style produce a ClassName for each component but then
 * the composition order matters which is error-prone and fragile.
 * Eg. in (Style,Style) which one is for the label and which is for the checkbox?
 *
 * This PoC below forces the client-site to acknowledge each style by name.
 */
object NamedChildrenPoC {

  trait Style

  class Step1 {
    val w = Witness('header)
    private val s: Style = ???
    def apply[X](a: w.T, f: Style => Step2 => X): X = f(s)(new Step2)
  }
  class Step2 {
    val w = Witness('body)
    private val s: Style = ???
    def apply[X](a: w.T, f: Style => X): X = f(s)
  }

  val myCompStyle = new Step1


  // Example usage:

  myCompStyle('header, h =>
    _('body, b =>
      s"Header = $h, body = $b"))

  // Doesn't compile:
  // myCompStyle('header, h =>
  //   _('sidebar, b =>
  //     s"Header = $h, sidebar = $b"))

  // -----------------------------------------------------

  val x = (Witness('a), "Aye") :: (Witness('b), "BB") :: (Witness('c), "Sea") :: HNil

  val l = x.tail.tail

  final class MidStep[W, A, B](a: A, b: B) {
    def apply[C](n: W, f: A => B => C): C = f(a)(b)
  }
  final class LastStep[W, A](a: A) {
    def apply[B](n: W, f: A => B): B = f(a)
  }
  def MidStep[A,B](w: Witness, a: A, b: B) = new MidStep[w.T, A, B](a, b)
  def LastStep[A](w: Witness, a: A) = new LastStep[w.T, A](a)

  val s3 = LastStep(Witness('c), 3)
  val s2 = MidStep(Witness('b), 20, s3)
  val s1 = MidStep(Witness('a), 100, s2)

  s1('a, a =>
      _('b, b =>
        _('c, a + b + _)))

  // -----------------------------------------------------

  case class Named[W,A](a: A)

  trait MkStep[L <: HList] {
    type Out
    def apply(l: L): Out
  }
  def mkSteps[L <: HList](l: L)(implicit m: MkStep[L]): m.Out = m(l)

  type MkStepAux[L <: HList, O] = MkStep[L]{ type Out = O }

  trait LowPri {
    implicit def mkMidStep[W, A, T <: HList](implicit next: MkStep[T]): MkStepAux[Named[W,A] :: T, MidStep[W,A,next.Out]] = {
      type L = Named[W,A] :: T
      new MkStep[L] {
        override type Out = MidStep[W,A,next.Out]
        override def apply(l: L): Out = new MidStep[W,A,next.Out](l.head.a, next(l.tail))
      }
    }
  }

  object TopPri extends LowPri {
    implicit def mkTailStep[W,A]: MkStepAux[Named[W,A] :: HNil, LastStep[W,A]] = {
      type L = Named[W,A] :: HNil
      new MkStep[L] {
        override type Out = LastStep[W,A]
        override def apply(l: L): Out = new LastStep[W,A](l.head.a)
      }
    }
  }
  import TopPri._


  val wc = Witness('c)
  val hc = Named[wc.T, Int](123) :: HNil
  val sc = mkSteps(hc)
  sc('c, identity)

  val wb = Witness('b)
  val hb = Named[wb.T, Int](100) :: hc
  val sb = mkSteps(hb)
  sb('b, b => _('c, _ + b))

}

// =====================================================================================================================
// Again for real

object CompositeStyleStuff {

  case class Named[W,A](a: A) {
    def map[B](f: A => B): Named[W,B] = Named(f(a))
  }

  final class UsageH[W, A, B](a: A, b: B) {
    def apply[C](n: W, f: A => B => C): C = f(a)(b)
  }

  final class UsageT[W, A](a: A) {
    def apply[B](n: W, f: A => B): B = f(a)
  }

  sealed abstract class MkUsage[L <: HList] {
    type Out
    val apply: L => Out
  }

  object MkUsage extends MkUsageLowPri {
    type Aux[L <: HList, O] = MkUsage[L]{ type Out = O }

    def apply[L <: HList, O](f: L => O): Aux[L, O] =
      new MkUsage[L] {
        override type Out = O
        override val apply = f
      }

    implicit def mkUsageT[W,A]: Aux[Named[W,A] :: HNil, UsageT[W,A]] =
      MkUsage(l => new UsageT(l.head.a))
  }

  sealed trait MkUsageLowPri {
    implicit def mkUsageH[W, A, T <: HList](implicit t: MkUsage[T]): MkUsage.Aux[Named[W,A] :: T, UsageH[W,A,t.Out]] =
      MkUsage(l => new UsageH(l.head.a, t apply l.tail))
  }

  def usage[L <: HList](l: L)(implicit m: MkUsage[L]): m.Out = m apply l
}
