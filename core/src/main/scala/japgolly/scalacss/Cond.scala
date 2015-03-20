package japgolly.scalacss

import scalaz.{Equal, Monoid}
import scalaz.syntax.monoid._

/**
 * Condition under which CSS is applicable.
 */
final case class Cond(pseudo: Option[Pseudo], mediaQuery: Vector[Media.Query]) extends Pseudo.ChainOps[Cond] {

  protected def addPseudo(p: Pseudo): Cond =
    copy(pseudo = Some(this.pseudo.fold(p)(_ & p)))

  @inline def &(p: Pseudo): Cond =
    addPseudo(p)
}

object Cond {
  val empty: Cond =
    Cond(None, Vector.empty)

  implicit val condTypeclass: Monoid[Cond] with Equal[Cond] =
    new Monoid[Cond] with Equal[Cond] {
      override def equalIsNatural              = true
      override def equal(a: Cond, b: Cond)     = a == b
      override def zero                        = empty
      override def append(a: Cond, b: => Cond) = Cond(a.pseudo |+| b.pseudo, a.mediaQuery ++ b.mediaQuery)
    }
}
