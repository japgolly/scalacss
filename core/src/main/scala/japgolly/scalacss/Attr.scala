package japgolly.scalacss

import scalaz.Equal

/**
 * A style attribute.
 *
 * Often this will represent a single CSS attribute, eg. "margin-left".
 * This can also represent a high-level attribute and generate multiple CSS keys.
 *
 * @param id A name by which this attribute can be identified. It is used for attribute equality and describing itself
 *           in messages to the developer. It doesn't not appear in output CSS.
 */
final class Attr(val id: String, val gen: Attr.Gen, private[Attr] val cmp0: AttrComparison.Fn) {
  override def hashCode = id.##
  override def equals(t: Any) = t match {
    case b: Attr => b.id == id
    case _       => false
  }
  override def toString = id

  import AttrComparison._
  def cmp(that: Attr): AttrComparison =
    cmp0(this, that) match {
      case Unrelated =>
        that.cmp0(that, this) match {
          case Unrelated       => Unrelated
          case FullOverride    => PartialOverride
          case PartialOverride => FullOverride
          case Same            => Same
        }
      case r => r
    }
}

object Attr {
  type Gen = Env => Value => List[CssKV]

  implicit val equality: Equal[Attr] = Equal.equalA

  def apply(id: String, gen: Attr.Gen, cmp: (Attr, Attr) => AttrComparison): Attr =
    new Attr(id, gen, cmp)

  def simple(css: String): Attr =
    Attr(css, _ => CssKV(css, _) :: Nil, AttrComparison.byEquality)

  def simpleFO(css: String, overrides: Attr*): Attr =
    Attr(css, _ => CssKV(css, _) :: Nil, AttrComparison.full(overrides: _*))
}

sealed trait AttrComparison
object AttrComparison {
  case object Same            extends AttrComparison
  case object Unrelated       extends AttrComparison
  sealed trait Override       extends AttrComparison
  case object FullOverride    extends Override // margin fully overrides margin-left
  case object PartialOverride extends Override // margin-left partially overrides margin
  // TODO laws

  type Fn = (Attr, Attr) => AttrComparison

  val byEquality: Fn =
    (a, b) => if (Equal[Attr].equal(a, b)) Same else Unrelated

  def full(overrides: Attr*): Fn = {
    val set = Set(overrides: _*)
    (_, b) => if (set contains b) FullOverride else Unrelated
  }
}