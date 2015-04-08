package scalacss

import scalaz.{==>>, Order}

trait StyleLookup[I] {
  type T
  def empty: T
  def add: (T, I, StyleA) => T
  def get: T => I => Option[StyleA]
}

trait StyleLookupLowPri {

  implicit def order[I: Order]: StyleLookup[I] =
    new StyleLookup[I] {
      override type T    = I ==>> StyleA
      override def empty = ==>>.empty
      override def add   = _.insert(_, _)
      override def get   = _.lookup
    }
}

object StyleLookup extends StyleLookupLowPri {

  implicit val boolean: StyleLookup[Boolean] =
    new StyleLookup[Boolean] {
      override type T    = Array[StyleA]
      override def empty = new Array[StyleA](2)
      override def add   = (t, b, s) => { t(ind(b)) = s; t }

      @inline def ind(b: Boolean) = if (b) 1 else 0

      override def get =
        array => {
          val t = Some(array(ind(true)))
          val f = Some(array(ind(false)))
          b => if (b) t else f
        }
    }

  implicit def scalaMap[I]: StyleLookup[I] =
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
