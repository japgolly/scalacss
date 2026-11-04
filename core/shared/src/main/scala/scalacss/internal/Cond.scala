package scalacss.internal

import japgolly.univeq._

/**
 * Condition under which CSS is applicable.
 */
final case class Cond(pseudo: Option[Pseudo], mediaQueries: Vector[Media.Query]) extends Pseudo.ChainOps[Cond] {
  override def toString =
    NonEmptyVector.option(mediaQueries).map(Media.css).fold("")(_ + " ") +
    Css.selector("", this)

  private[Cond] val sortKey: String = {
    var s = "" // remember, this is JS
    for (p <- pseudo) s += p.cssValue
    for (q <- mediaQueries) s += q.cssSuffix
    s
  }

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

  def applyToStyle(s: StyleS)(implicit c: Compose = Compose.trust): StyleS = {
    val init = StyleS.empty.copy(
      warnings = s.warnings.map(w => w.copy(cond = this & w.cond))
    )

    val composed = s.data.foldLeft(init) { case (q, (oldCond, av)) =>
      val newCond = this & oldCond
      val item = StyleS.data1(newCond, av)
      c(q, item)
    }

    val u = s.unsafeExts.map(e =>
      e.copy(
        style = this.copy(pseudo = None).applyToStyle(e.style)(c),
        cond  = this.copy(mediaQueries = Vector.empty) & e.cond
      )
    )

    composed.copy(
      unsafeExts    = u,
      className     = s.className,
      addClassNames = s.addClassNames
    )
  }
}

object Cond {
  val empty: Cond =
    Cond(None, Vector.empty)

  implicit def univEq: UnivEq[Cond] =
    UnivEq.derive

  implicit val ordering: Ordering[Cond] =
    Ordering.by[Cond, String](_.toString)(NaturalOrdering)
}
