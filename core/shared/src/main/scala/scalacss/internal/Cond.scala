package scalacss.internal

import scala.collection.immutable.SortedMap

/**
 * Condition under which CSS is applicable.
 */
final case class Cond(pseudo: Option[Pseudo], mediaQueries: Vector[Media.Query]) extends Pseudo.ChainOps[Cond] {
  override def toString =
    NonEmptyVector.option(mediaQueries).map(Media.css).fold("")(_ + " ") +
    Css.selector("", this)

  // def toStringForSorting = {
  //   import Media._
  //   def valueExpr2String(value: ValueExpr[Length[Any]]): String = {
  //     def length2String(l: Length[Any]): String = {
  //       l.u.value + {l.n match {
  //         case i:Int => "%06d".format(i)
  //         case d:Double => "%013.6f".format(d)
  //         case stg => stg
  //       }}
  //     }
  //     value match {
  //       case Eql(l) => length2String(l)
  //       case Min(l) => length2String(l)
  //       case Max(l) => length2String(l)
  //     }
  //   }
  //   val aux = this.mediaQueries.map{
  //     case Media.Query(Right(headRight), aTail: Vector[Media.Feature]) =>
  //       headRight match {
  //         // case Color(bits) => 
  //         // case ColorIndex(index) =>
  //         // case AspectRatio(ratio) =>
  //         case Media.Height(length) => "Height(" + valueExpr2String(length) + ")"
  //         case Media.Width(length) => "Width(" + valueExpr2String(length) + ")"
  //         // case DeviceAspectRatio(ratio) =>
  //         // case DeviceHeight(length) =>
  //         // case DeviceWidth(length) =>
  //         // case Monochrome(bitsPerPx) =>
  //         // case scalacss.internal.Media.Resolution(res) =>
  //         // case Orientation(value) =>
  //         // case Scan(value) =>
  //         // case Grid(value) =>
  //         case e => e.toString
  //       }
  //       s"Query(Right($headRight), $aTail)"
  //     case any => any.toString
  //   }

  //   aux + Css.selector("", this)
  // }

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

  implicit val ordering: Ordering[Cond] = 
   Ordering.by(_.toString)
}
