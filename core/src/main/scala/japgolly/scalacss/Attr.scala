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
final case class Attr(id: String, gen: Attr.Gen, cmp: Attr => AttrComparison) {
  override def hashCode = id.##
  override def equals(t: Any) = t match {
    case b: Attr => b.id == id
    case _       => false
  }
  override def toString = id
}

object Attr {
  type Gen = Env => Value => List[CssKV]

  implicit val equality: Equal[Attr] = Equal.equalA
}

sealed trait AttrComparison
object AttrComparison {
  case object Same            extends AttrComparison
  case object FullOverride    extends AttrComparison // margin fully overrides margin-left
  case object PartialOverride extends AttrComparison // margin-left partially overrides margin
  case object Unrelated       extends AttrComparison
  // TODO laws
}