package japgolly.scalacss

import scala.annotation.tailrec
import scalaz.{Equal, Need, NonEmptyList, Order}
import scalaz.std.string.stringInstance

/**
 * A style attribute.
 *
 * Often this will represent a single CSS attribute, eg. "margin-left".
 * This can also represent a high-level attribute and generate multiple CSS keys.
 *
 * @param id A name by which this attribute can be identified. It is used for attribute equality and describing itself
 *           in messages to the developer. It doesn't not appear in output CSS.
 */
sealed abstract class Attr(val id: String, val gen: Attr.Gen) {
  override final def hashCode = id.##
  override final def equals(t: Any) = t match {
    case b: Attr => b.id == id
    case _       => false
  }
  override final def toString = id

  final def cmp(that: Attr): AttrCmp =
    AttrCmp.default(this, that)

  def real: Set[RealAttr]
}

final class RealAttr(id: String, gen: Attr.Gen) extends Attr(id, gen) {
  override val real = Set[RealAttr](this)
}

final class AliasAttr(id: String, gen: Attr.Gen, val targets: Need[NonEmptyList[Attr]]) extends Attr(id, gen) {
  override lazy val real = {
    @tailrec def go(seen: Set[Attr], found: Set[RealAttr], queue: List[Attr]): Set[RealAttr] =
      queue match {
        case Nil                       => found
        case a :: t if seen contains a => go(seen, found, t)
        case (a: RealAttr)  :: t       => go(seen + a, found + a, t)
        case (a: AliasAttr) :: t       => go(seen + a, found, a.targets.value.list ::: t)
        // ↗ Using a.targets instead of a.realAttrs to avoid potential deadlock
      }
    go(Set.empty, Set.empty, targets.value.list)
  }
}

object Attr {
  type Gen = Env => Value => Vector[CssKV]

  implicit val order: Order[Attr] = Order.orderBy(_.id)

  def simpleGen(css: String): Gen =
    _ => v => Vector1(CssKV(css, v))

  def real(css: String): Attr =
    new RealAttr(css, simpleGen(css))

  def alias(css: String)(f: AliasB.type => NonEmptyList[Attr]): Attr =
    new AliasAttr(css, simpleGen(css), Need(f(AliasB)))

  /**
   * Helper for creating a lazy NonEmptyList[Attr] so that initialisation order doesn't matter,
   * and attributes can have recursive definitions.
   */
  object AliasB {
    @inline def apply(h: Attr, t: Attr*) = NonEmptyList.nel(h, t.toList)
  }
}

// =====================================================================================================================
sealed abstract class AttrCmp {
  def >>(next: => AttrCmp): AttrCmp
  def conflict: Boolean
}

object AttrCmp {
  case object Unrelated extends AttrCmp {
    override def >>(next: => AttrCmp): AttrCmp = next
    override def conflict = false
  }

  sealed abstract class Conflict extends AttrCmp  {
    override def >>(next: => AttrCmp): AttrCmp = this
    override def conflict = true
  }

  case object Same    extends Conflict
  case object Overlap extends Conflict

  implicit val equality: Equal[AttrCmp] = Equal.equalA

  type Fn = (Attr, Attr) => AttrCmp

  val byEquality: Fn =
    (a, b) => if (Equal[Attr].equal(a, b)) Same else Unrelated

  val byRealAttrs: Fn =
    (x, y) => {
      val a = x.real
      val b = y.real
      if (a exists b.contains) Overlap else Unrelated
    }

  def compose(fst: Fn, snd: Fn): Fn =
    (a, b) => fst(a,b) >> snd(a,b)

  val default: Fn =
    compose(byEquality, byRealAttrs)

  def conflicts(avs: Vector[AV]): Set[AV] = {
    @tailrec def go(untested: Vector[AV], conflicts: Set[AV]): Set[AV] =
      if (untested.length <= 1)
        conflicts
      else {
        val h = untested.head
        val (ko, ok) = untested.tail.partition(i => h.attr.cmp(i.attr).conflict)
        val c2 = if (ko.isEmpty) conflicts else conflicts + h ++ ko
        go(ok, c2)
      }
    go(avs, Set.empty)
  }
}