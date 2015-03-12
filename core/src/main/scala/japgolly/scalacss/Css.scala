package japgolly.scalacss

import scalaz.NonEmptyList

object Css {

  def apply(styles: TraversableOnce[StyleA])(implicit env: Env): Css =
    styles.toStream flatMap style

  def selector(cn: ClassName, c: Cond): CssSelector =
    c match {
      case NoCond    => s".${cn.value}"
      case p: Pseudo => s".${cn.value}${p.value}"
    }

  def av(av: AV)(implicit env: Env): List[CssKV] =
    av.attr.gen(env)(av.value)

  def style(s: StyleA)(implicit env: Env): Css = {
    val cn = s.className
    s.style.data.toStream.flatMap {
      case (cond, AVsAndWarnings(avs, _)) =>
        avs.list.flatMap(av) match {
          case h :: t => Stream((selector(cn, cond), NonEmptyList.nel(h, t)))
          case Nil    => Stream.empty
        }
    }
  }
}