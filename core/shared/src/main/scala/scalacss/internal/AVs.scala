package scalacss.internal

import japgolly.univeq._
import scala.collection.GenTraversable

/**
 * An Attribute-and-Value pair.
 */
final case class AV(attr: Attr, value: Value) {
  def important: AV =
    if (value endsWith _important)
      this
    else
      copy(value = this.value + _important)

  def apply(env: Env) =
    attr.gen(env)(value)
}
object AV {
  implicit def univEq: UnivEq[AV] = UnivEq.derive
}

/**
 * One or more ordered attributes, each with one or more ordered values.
 */
final class AVs private[AVs](val order: NonEmptyVector[Attr], val data: Map[Attr, NonEmptyVector[Value]]) {
  override def toString = toStream.mkString("AVs(", ", ", ")")

  def +(av: AV): AVs =
    add(av.attr, av.value)

  def ++(avs: GenTraversable[AV]): AVs =
    avs.foldLeft(this)(_ + _)

  def ++(avs: AVs): AVs =
    avs.toStream.foldLeft(this)((q, t) => q.addAll(t._1, t._2))

  def add(a: Attr, v: Value): AVs =
    addTail(a, NonEmptyVector.end(_, v))

  def addAll(a: Attr, vs: NonEmptyVector[Value]): AVs =
    addTail(a, _ ++: vs)

  private def addTail(a: Attr, f: Vector[Value] => NonEmptyVector[Value]): AVs =
    addf(a, f, e => if (e.isEmpty) order :+ a else order)

  private def addf(a: Attr, f: Vector[Value] => NonEmptyVector[Value], g: Vector[Value] => NonEmptyVector[Attr]): AVs = {
    val existing = getv(a)
    val newOrder = g(existing)
    val newData  = data.updated(a, f(existing))
    new AVs(newOrder, newData)
  }

  private def addHead(a: Attr, f: Vector[Value] => NonEmptyVector[Value]): AVs =
    addf(a, f,
      e => if (e.isEmpty)
        a +: order
      else
        NonEmptyVector(a, order.whole.filterNot(a ==* _)))

  def +:(av: AV): AVs =
    addHead(av.attr, NonEmptyVector(av.value, _))

  def get(a: Attr): Option[NonEmptyVector[Value]] =
    data.get(a)

  def getv(a: Attr): Vector[Value] =
    data.get(a).fold(Vector.empty[Value])(_.whole)

  def filterKeys(f: Attr => Boolean): Option[AVs] =
    NonEmptyVector.option(order.whole.filter(f))
      .map(o => new AVs(o, data filterKeys f))

  def modify(a: Attr, f: Vector[Value] => NonEmptyVector[Value]): AVs =
    addTail(a, f)

  def toStream: Stream[(Attr, NonEmptyVector[Value])] =
    order.toStream.map(a => (a, data(a)))

  def avStream: Stream[AV] =
    toStream.flatMap(t => t._2.toStream.map(AV(t._1, _)))
}

object AVs {
  def apply(av1: AV, avn: AV*): AVs =
    apply(av1.attr, av1.value) ++ avn

  def apply(a: Attr, v: Value): AVs =
    apply(a, NonEmptyVector one v)

  def apply(a: Attr, vs: NonEmptyVector[Value]): AVs =
    new AVs(NonEmptyVector one a, Map.empty.updated(a, vs))
}
