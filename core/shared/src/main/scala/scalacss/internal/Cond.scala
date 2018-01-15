package scalacss.internal

import scala.collection.immutable.ListMap

/**
 * Condition under which CSS is applicable.
 */
final case class Cond(pseudo: Option[Pseudo], mediaQueries: Vector[Media.Query]) extends Pseudo.ChainOps[Cond] {
  override def toString =
    NonEmptyVector.option(mediaQueries).map(Media.css).fold("")(_ + " ") +
    Css.selector("", this)

  protected def addPseudo(p: Pseudo): Cond =
    copy(pseudo = Some(this.pseudo.fold(p)(_ & p)))

  @inline def &(p: Pseudo): Cond =
    addPseudo(p)

  def &(q: Media.Query): Cond =
    copy(mediaQueries = this.mediaQueries +: q)

  def &(b: Cond): Cond =
    Cond(
      optionAppend(pseudo, b.pseudo)(_ & _),
      b.mediaQueries.foldLeft(mediaQueries)(_ +: _))

  def applyToStyle(s: StyleS): StyleS = {
    val d = s.data.foldLeft(ListMap.empty[Cond, AVs]){ case (q, (oldCond, av)) =>
      val newCond = this & oldCond
      val newValue = q.get(newCond).fold(av)(_ ++ av)
      q.updated(newCond, newValue)
    }

    val u = s.unsafeExts.map(e =>
      e.copy(
        style = this.copy(pseudo = None).applyToStyle(e.style),
        cond  = this.copy(mediaQueries = Vector.empty) & e.cond
      )
    )

    s.copy(data = d, unsafeExts = u)
  }
}

object Cond {
  val empty: Cond =
    Cond(None, Vector.empty)
}
