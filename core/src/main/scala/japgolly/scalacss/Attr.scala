package japgolly.scalacss

import japgolly.nyaya._
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
  type Gen = Env => Value => List[CssKV]

  implicit val order: Order[Attr] = Order.orderBy(_.id)

  def simpleGen(css: String): Gen =
    _ => CssKV(css, _) :: Nil

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

  def laws1: Prop[Attr] = (
    Prop.equal[Attr, AttrCmp]("cmp is reflexive: a.cmp(a) = Same", a => a cmp a, _ => AttrCmp.Same)
    & Prop.test("id is populated", _.id.nonEmpty)
  )

  def laws2: Prop[(Attr, Attr)] =
    Prop.equal("cmp is commutative: a.cmp(b) = b.cmp(a)", t => t._1 cmp t._2, t => t._2 cmp t._1)
}

// =====================================================================================================================
sealed abstract class AttrCmp {
  def >>(next: => AttrCmp): AttrCmp
}

object AttrCmp {
  case object Unrelated extends AttrCmp {
    override def >>(next: => AttrCmp): AttrCmp = next
  }

  sealed abstract class Conflict extends AttrCmp  {
    override def >>(next: => AttrCmp): AttrCmp = this
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
}