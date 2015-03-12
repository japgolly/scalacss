package japgolly.scalacss

import scalaz.{==>>, Order}
import scalaz.std.anyVal.{intInstance, longInstance}
import scalaz.std.string.stringInstance

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

  // Convenience for common types. Users won't need to import scalaz.std instances.

  implicit val int   : StyleLookup[Int]    = order
  implicit val long  : StyleLookup[Long]   = order
  implicit val string: StyleLookup[String] = order
}
