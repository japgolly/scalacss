package japgolly.scalacss

import scalaz.OneAnd

object Css {

  def apply(styles: TraversableOnce[StyleA])(implicit env: Env): Css =
    styles.toStream flatMap styleA

  def className(cn: ClassName): CssSelector =
    "." + cn.value

  def selector(cn: ClassName, c: Cond): CssSelector =
    selector(className(cn), c)

  def selector(cs: CssSelector, c: Cond): CssSelector =
    c.pseudo.fold(cs)(_ modSelector cs)

  def mediaQuery(c: Cond): CssMediaQueryO =
    NonEmptyVector.option(c.mediaQueries) map Media.css

  def styleA(s: StyleA)(implicit env: Env): Css =
    style(className(s.className), s.style)

  def style(sel: CssSelector, s: StyleS)(implicit env: Env): Css = {
    def main: Css =
      s.data.toStream.flatMap {
        case (cond, avs1) =>
          val r: Vector[CssKV] = avs1.reduceMapLeft1(_(env))(_ ++ _)
          if (r.isEmpty)
            Stream.empty
          else {
            val mq = mediaQuery(cond)
            val s  = selector(sel, cond)
            val c  = OneAnd(r.head, r.tail)
            Stream(CssEntry(mq, s, c))
          }
        }

    def exts: Css =
      s.unsafeExts.toStream.flatMap(unsafeExt(sel, _))

    main append exts
  }

  def unsafeExt(root: CssSelector, u: Style.UnsafeExt)(implicit env: Env): Css = {
    val sel = u.sel(root)
    style(sel, u.style)
  }

  type ValuesByMediaQuery = Vector[(CssSelector, NonEmptyVector[CssKV])]
  type ByMediaQuery       = Map[CssMediaQueryO, ValuesByMediaQuery]

  def mapByMediaQuery(c: Css): ByMediaQuery = {
    val z: ByMediaQuery = Map.empty
    c.foldLeft(z){(q, e) =>
      val add = (e.sel, e.content)
      val k = e.mq
      q.updated(k, q.getOrElse(k, Vector.empty) :+ add)
    }
  }

  def flatten(css: Css): Stream[(CssMediaQueryO, CssSelector, CssKV)] =
    css.flatMap { case CssEntry(mq, sel, kvs) =>
      kvs.vector.toStream.map(kv => (mq, sel, kv))
    }

  type Flat4 = (CssMediaQueryO, CssSelector, String, String)
  def flatten4(css: Css): Stream[Flat4] =
    css.flatMap { case CssEntry(mq, sel, kvs) =>
      kvs.vector.toStream.map(kv => (mq, sel, kv.key, kv.value))
    }
}