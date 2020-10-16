package scalacss.internal

import scala.collection.immutable.SortedMap

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

  def applyToStyle(s: StyleS): StyleS = {
    val d = s.data.foldLeft(SortedMap.empty[Cond, AVs]){ case (q, (oldCond, av)) =>
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

  implicit val ordering: Ordering[Cond] = {
    import Media._

    implicit val orderingValueExpr:Ordering[ValueExpr[Length[Any]]] = 
    Ordering[(Int, (String, Double, String))].on[ValueExpr[Length[Any]]]{ value => 
      def lengthAux(length: Length[Any]) = length match {
        case Length(n: Double, u: LengthUnit) => (u.value, n, n.toString)
        case Length(n: Int, u: LengthUnit) => (u.value, n.toDouble, n.toString)
        case Length(n, u: LengthUnit) => (u.value, Double.NegativeInfinity, n.toString)
      }
      value match {
        case Eql(l) => (1, lengthAux(l))
        case Min(l) => (2, lengthAux(l))
        case Max(l) => (3, lengthAux(l))
      }
    }

    implicit val orderingQuery:Ordering[Query] = new Ordering[Query] {
      override def compare(a:Query, b:Query): Int = (a, b) match {
        case (Query(Right(aHeadRight), aTail: Vector[Feature]), Query(Right(bHeadRight), bTail: Vector[Feature])) =>
          val compareHeads = (aHeadRight, bHeadRight) match {
            case (aFeature: Width[Any @unchecked], bFeature: Width[Any @unchecked]) =>
              orderingValueExpr.compare(aFeature.length, bFeature.length)
            case (aa, bb) =>
              aa.toString compare bb.toString
          }
          if (compareHeads != 0) compareHeads else { aTail.toString compare aTail.toString }
        case (aa, bb) => aa.toString compare bb.toString
      }
    }

    implicit val orderingMediaVec:Ordering[Vector[Media.Query]] = new Ordering[Vector[Query]] {
      override def compare(a:Vector[Query], b:Vector[Query]): Int = {
        a.sorted.zip(b.sorted)
        .find(e => orderingQuery.compare(e._1,e._2) != 0)
        .map(e => orderingQuery.compare(e._1,e._2))
        .getOrElse(a.size.compare(b.size))
      }
    }

    Ordering[(Vector[Media.Query], String)].on(e => (e.mediaQueries, e.pseudo.toString))
  }
}
