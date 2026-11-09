package scalacss.internal

import japgolly.microlibs.nonempty.NonEmptyArraySeq
import japgolly.univeq._

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
final class AVs private[AVs](val order: NonEmptyArraySeq[Attr], val data: Map[Attr, NonEmptyArraySeq[Value]]) {
  override def toString = iterator.mkString("AVs(", ", ", ")")

  def +(av: AV): AVs =
    add(av.attr, av.value)

  def ++(avs: Iterable[AV]): AVs =
    avs.foldLeft(this)(_ + _)

  def ++(avs: AVs): AVs =
    avs.iterator.foldLeft(this)((q, t) => q.addAll(t._1, t._2))

  def add(a: Attr, v: Value): AVs =
    addTail(a, NonEmptyArraySeq.end(_, v))

  def addAll(a: Attr, vs: NonEmptyArraySeq[Value]): AVs =
    addTail(a, _ ++: vs)

  private def addTail(a: Attr, f: ArraySeq[Value] => NonEmptyArraySeq[Value]): AVs =
    addf(a, f, e => if (e.isEmpty) order :+ a else order)

  private def addf(a: Attr, f: ArraySeq[Value] => NonEmptyArraySeq[Value], g: ArraySeq[Value] => NonEmptyArraySeq[Attr]): AVs = {
    val existing = getv(a)
    val newOrder = g(existing)
    val newData  = data.updated(a, f(existing))
    new AVs(newOrder, newData)
  }

  private def addHead(a: Attr, f: ArraySeq[Value] => NonEmptyArraySeq[Value]): AVs =
    addf(a, f,
      e => if (e.isEmpty)
        a +: order
      else
        NonEmptyArraySeq(a, order.whole.filterNot(a ==* _): _*))

  def +:(av: AV): AVs =
    addHead(av.attr, tail => NonEmptyArraySeq(av.value, tail: _*))

  def get(a: Attr): Option[NonEmptyArraySeq[Value]] =
    data.get(a)

  def getv(a: Attr): ArraySeq[Value] =
    data.get(a).fold(ArraySeq.empty[Value])(_.whole)

  def filterKeys(f: Attr => Boolean): Option[AVs] =
    NonEmptyArraySeq.option(order.whole.filter(f))
      .map(o => new AVs(o, data.iterator.filter(kv => f(kv._1)).toMap))

  def modify(a: Attr, f: ArraySeq[Value] => NonEmptyArraySeq[Value]): AVs =
    addTail(a, f)

  def iterator: Iterator[(Attr, NonEmptyArraySeq[Value])] =
    order.iterator.map(a => (a, data(a)))

  def avIterator: Iterator[AV] =
    iterator.flatMap(t => t._2.iterator.map(AV(t._1, _)))
}

object AVs {
  def apply(av1: AV, avn: AV*): AVs =
    apply(av1.attr, av1.value) ++ avn

  def apply(a: Attr, v: Value): AVs =
    apply(a, NonEmptyArraySeq one v)

  def apply(a: Attr, vs: NonEmptyArraySeq[Value]): AVs =
    new AVs(NonEmptyArraySeq one a, Map.empty.updated(a, vs))
}
