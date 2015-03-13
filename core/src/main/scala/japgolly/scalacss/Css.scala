package japgolly.scalacss

import scalaz.OneAnd
import scalaz.syntax.foldable1._

object Css {

  def apply(styles: TraversableOnce[StyleA])(implicit env: Env): Css =
    styles.toStream flatMap style

  def className(cn: ClassName): String =
    "." + cn.value

  def selector(cn: ClassName, c: Cond): CssSelector = {
    var sel = className(cn)
    c.pseudo.foreach { p => sel += p.cssValue }
    sel
  }

  def av(av: AV)(implicit env: Env): Vector[CssKV] =
    av.attr.gen(env)(av.value)

  def style(s: StyleA)(implicit env: Env): Css = {
    val cn = s.className
    s.style.data.toStream.flatMap {
      case (cond, avs1) =>
        val r = avs1.foldMapLeft1(av)(_ ++ av(_))
        if (r.isEmpty)
          Stream.empty
        else
          Stream((selector(cn, cond), OneAnd(r.head, r.tail)))
        }
  }
}