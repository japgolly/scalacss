package japgolly.scalacss

import scalaz.OneAnd
import scalaz.syntax.foldable1._

object Css {

  def apply(styles: TraversableOnce[StyleA])(implicit env: Env): Css =
    styles.toStream flatMap styleA

  def className(cn: ClassName): CssSelector =
    "." + cn.value

  def cond(cs: CssSelector, c: Cond): CssSelector =
    c.pseudo.fold(cs)(_ modSelector cs)

  def selector(cn: ClassName, c: Cond): CssSelector =
    cond(className(cn), c)

  def styleA(s: StyleA)(implicit env: Env): Css =
    style(className(s.className), s.style)

  def style(sel: CssSelector, s: StyleS)(implicit env: Env): Css = {
    def main: Css =
      s.data.toStream.flatMap {
        case (cond, avs1) =>
          val r = avs1.foldMapLeft1(_(env))(_ ++ _(env))
          if (r.isEmpty)
            Stream.empty
          else
            Stream((Css.cond(sel, cond), OneAnd(r.head, r.tail)))
          }

    def exts: Css =
      s.unsafeExts.toStream.flatMap(unsafeExt(sel, _))

    main append exts
  }

  def unsafeExt(root: CssSelector, u: Style.UnsafeExt)(implicit env: Env): Css = {
    val sel = u.sel(root)
    style(sel, u.style)
  }

  def flatten(css: Css): Stream[(CssSelector, CssKV)] =
    css.flatMap { case (sel, kvs) =>
      kvs.vector.toStream.map(kv => (sel, kv))
    }

  type Flat3 = (CssSelector, String, String)
  def flatten3(css: Css): Stream[Flat3] =
    css.flatMap { case (sel, kvs) =>
      kvs.vector.toStream.map(kv => (sel, kv.key, kv.value))
    }
}