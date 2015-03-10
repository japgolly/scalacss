package japgolly.scalacss

import scalaz.Equal
import japgolly.nyaya.Prop

/**
 * A style attribute.
 *
 * Often this will represent a single CSS attribute, eg. "margin-left".
 * This can also represent a high-level attribute and generate multiple CSS keys.
 *
 * @param id A name by which this attribute can be identified. It is used for attribute equality and describing itself
 *           in messages to the developer. It doesn't not appear in output CSS.
 */
final class Attr(val id: String, val gen: Attr.Gen, private[Attr] val cmp0: AttrCmp.Fn) {
  override def hashCode = id.##
  override def equals(t: Any) = t match {
    case b: Attr => b.id == id
    case _       => false
  }
  override def toString = id

  // TODO test transitivity
  def cmp(that: Attr): AttrCmp =
    AttrCmp.byEquality(this, that) >> cmp0(this, that) >> that.cmp0(that, this)
}

object Attr {
  type Gen = Env => Value => List[CssKV]

  implicit val equality: Equal[Attr] = Equal.equalA

  def apply(id: String, gen: Attr.Gen, cmp: (Attr, Attr) => AttrCmp): Attr =
    new Attr(id, gen, cmp)

  def simple(css: String): Attr =
    simpleG(css, AttrCmp.nop)

  def simpleO(css: String)(as: Attr*): Attr =
    simpleG(css, AttrCmp.overlap(as: _*))

  def simpleG(css: String, cmp: AttrCmp.Fn): Attr =
    Attr(css, _ => CssKV(css, _) :: Nil, cmp)

  def laws1: Prop[Attr] = (
    Prop.equal[Attr, AttrCmp]("cmp is reflexive: a.cmp(a) = Same", a => a cmp a, _ => AttrCmp.Same)
    & Prop.test("id is populated", _.id.nonEmpty)
  )

  def laws2: Prop[(Attr, Attr)] =
    Prop.equal[(Attr, Attr), AttrCmp]("cmp is commutative: a.cmp(b) = b.cmp(a)", t => t._1 cmp t._2, t => t._2 cmp t._1)

  def laws3: Prop[(Attr, Attr, Attr)] =
    Prop.atom("Overlap is transitive: a @ b ∧ b @ c ⇒ a @ c", { case (a, b, c) =>
      import AttrCmp.Overlap
      if (a.cmp(b) == Overlap && b.cmp(c) == Overlap && a.cmp(c) != Overlap)
        Some(s"$a overlaps $b which overlaps $c, but [$a cmp $c] = ${a cmp c}")
      else None
    })
}

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

  val nop: Fn =
    (_, _) => Unrelated

  val byEquality: Fn =
    (a, b) => if (Equal[Attr].equal(a, b)) Same else Unrelated

  def overlap(as: Attr*): Fn =
    set(as: _*)(Overlap, Unrelated)

  def set(as: Attr*)(t: AttrCmp, f: AttrCmp): Fn = {
    val set = Set(as: _*)
    cond(set.contains, t, f)
  }

  def cond(c: Attr => Boolean, t: AttrCmp, f: AttrCmp): Fn =
    (_, b) => if (c(b)) t else f
}