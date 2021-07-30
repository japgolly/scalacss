package scalacss.internal

import japgolly.univeq.UnivEq
import scala.annotation.nowarn

trait StyleLookup[I] {
  type T
  def empty: T
  def add: (T, I, StyleA) => T
  def get: T => I => Option[StyleA]
}

object StyleLookup {

  implicit val boolean: StyleLookup[Boolean] =
    new StyleLookup[Boolean] {
      override type T    = Array[StyleA]
      override def empty = new Array[StyleA](2)
      override def add   = (t, b, s) => { t(ind(b)) = s; t }

      def ind(b: Boolean) = if (b) 1 else 0

      override def get =
        array => {
          val t = Some(array(ind(true)))
          val f = Some(array(ind(false)))
          b => if (b) t else f
        }
    }

  @nowarn("cat=unused")
  implicit def scalaMap[I: UnivEq]: StyleLookup[I] =
    new StyleLookup[I] {
      override type T    = Map[I, StyleA]
      override def empty = Map.empty
      override def add   = _.updated(_, _)
      override def get   = _.get
    }

  // Convenience for common types.

  implicit val int   : StyleLookup[Int]    = scalaMap
  implicit val long  : StyleLookup[Long]   = scalaMap
  implicit val string: StyleLookup[String] = scalaMap
}
