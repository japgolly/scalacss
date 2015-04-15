package scalacss

import scala.annotation.tailrec
import scalaz.{Equal, Need, Order}
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

final class AliasAttr(id: String, gen: Attr.Gen, val targets: Need[NonEmptyVector[Attr]]) extends Attr(id, gen) {
  override lazy val real = {
    @tailrec def go(seen: Set[Attr], found: Set[RealAttr], queue: Vector[Attr]): Set[RealAttr] =
      if (queue.isEmpty)
        found
      else {
        val t = queue.tail
        queue.head match {
          case a if seen contains a => go(seen, found, t)
          case (a: RealAttr)        => go(seen + a, found + a, t)
          case (a: AliasAttr)       => go(seen + a, found, a.targets.value.whole ++ t)
          // ↗ Using a.targets instead of a.realAttrs to avoid potential deadlock
        }
      }
    go(Set.empty, Set.empty, targets.value.whole)
  }
}

object Attr {
  type Gen = Env => Value => Vector[CssKV]

  implicit val order: Order[Attr] = Order.orderBy(_.id)

  def genSimple(css: String): Gen =
    _ => v => Vector1(CssKV(css, v))

  def real(css: String): Attr =
    new RealAttr(css, genSimple(css))

  def real(css: String, t: Transform): Attr =
    new RealAttr(css, t attrGen css)

  def alias(css: String) =
    _alias(css, genSimple(css))

  def alias(css: String, t: Transform) =
    _alias(css, t attrGen css)

  private def _alias(id: String, g: Gen): (AliasB.type => NonEmptyVector[Attr]) => Attr =
    f => new AliasAttr(id, g, Need(f(AliasB)))

  /**
   * Helper for creating a lazy NonEmptyVector[Attr] so that initialisation order doesn't matter,
   * and attributes can have recursive definitions.
   */
  object AliasB {
    @inline def apply(h: Attr, t: Attr*) = NonEmptyVector(h, t: _*)
  }
}

// =====================================================================================================================

/**
 * Transforms key-values into different and/or more key-values.
 *
 * Generally used to apply browser prefixes.
 * For example, to turn `border-radius: 1em/5em;` into
 * {{{
 *   -moz-border-radius: 1em/5em;
 *   -webkit-border-radius: 1em/5em;
 *   border-radius: 1em/5em;
 * }}}
 */
class Transform(val run: Env => CssKV => Vector[CssKV]) {

  /**
   * Applicative product of two transforms.
   */
  def *(next: Transform): Transform =
    new Transform(e => {
      val a = run(e)
      val b = next.run(e)
      a(_) flatMap b
    })

  def attrGen(key: String): Attr.Gen =
    e => {
      val x = run(e)
      v => x(CssKV(key, v))
    }
}

object Transform {
  import CanIUse.Subject
  import CanIUse2.PrefixApply

  def apply(run: Env => CssKV => Vector[CssKV]): Transform =
    new Transform(run)

  private def prefix[R](subject: Subject, pa: PrefixApply, l: CssKV.Lens): Transform = {
    import CanIUse2._
    val pp0 = prefixPlan(subject)
    Transform(e => {
      val pp = filteredPrefixPlan(e.prefixWhitelist, pp0)
      kv => runPlan(pp, pa, l, kv)
    })
  }

  def keys(subject: Subject): Transform =
    prefix(subject, PrefixApply.prepend, CssKV.key)

  def values(subject: Subject, pa: PrefixApply): Transform =
    prefix(subject, pa, CssKV.value)

  def values(subject: Subject)(v1: Value, vn: Value*): Transform = {
    val whitelist = vn.toSet + v1
    values(subject, PrefixApply maybePrepend whitelist.contains)
  }

  /** @see [[CanIUse2.PrefixApply.keywords]] */
  def valueKeywords(subject: Subject)(w1: String, wn: String*): Transform =
    values(subject, PrefixApply.keywords(w1, wn: _*))
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