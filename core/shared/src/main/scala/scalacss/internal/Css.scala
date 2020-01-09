package scalacss.internal

import scala.collection.compat._
import scala.collection.immutable.ListMap

object Css {

  def apply(ss: IterableOnce[StyleA],
            kfs: IterableOnce[Keyframes],
            ff: IterableOnce[FontFace[String]])
           (implicit env: Env): Css = {
    val b = Vector.newBuilder[CssEntry]
    b ++= styles(ss)
    b ++= keyframes(kfs)
    b ++= fontFaces(ff)
    b.result()
  }

  def styles(ss: IterableOnce[StyleA])(implicit env: Env): StyleStream =
    ss.iterator.flatMap(styleA).toVector

  def keyframes(kfs: IterableOnce[Keyframes])(implicit env: Env): KeyframeStream =
    kfs.iterator.map(keyframes).toList

  def fontFaces(ff: IterableOnce[FontFace[String]]): FontFaceStream =
    ff.iterator.map(fontFaces).toList

  def className(cn: ClassName): CssSelector =
    "." + cn.value

  def selector(cn: ClassName, c: Cond): CssSelector =
    selector(className(cn), c)

  def selector(cs: CssSelector, c: Cond): CssSelector =
    c.pseudo.fold(cs)(_ modSelector cs)

  def mediaQuery(c: Cond): CssMediaQueryO =
    NonEmptyVector.option(c.mediaQueries) map Media.css

  def keyframes(kfs: Keyframes)(implicit env: Env): CssEntry.Keyframes =
    CssEntry.Keyframes(kfs.name, ListMap(kfs.frames.map(s => (s._1, styleA(s._2))):_*))

  def fontFaces(ff: FontFace[String]): CssEntry.FontFace =
    CssEntry.FontFace(ff.fontFamily, ff.src, ff.fontStretchValue, ff.fontStyleValue, ff.fontWeightValue, ff.unicodeRangeValue)

  def styleA(s: StyleA)(implicit env: Env): StyleStream =
    style(className(s.className), s.style)

  def style(sel: CssSelector, s: StyleS)(implicit env: Env): StyleStream = {
    def main: Iterator[CssEntry.Style] =
      s.data.iterator.flatMap {
        case (cond, avs) =>
          val kvs = avs.avIterator.map(_(env)).foldLeft(Vector.empty[CssKV])(_ ++ _)
          NonEmptyVector.maybe(kvs, List.empty[CssEntry.Style]) { c =>
            val mq = mediaQuery(cond)
            val s = selector(sel, cond)
            CssEntry.Style(mq, s, c) :: Nil
          }
        }

    def exts: Iterator[CssEntry.Style] =
      s.unsafeExts.iterator.flatMap(unsafeExt(sel, _))

    (main ++ exts).toVector
  }

  def unsafeExt(root: CssSelector, u: Style.UnsafeExt)(implicit env: Env): StyleStream = {
    val sel = u.sel(root + u.cond)
    style(sel, u.style)
  }

  type ValuesByMediaQuery = NonEmptyVector[(CssSelector, NonEmptyVector[CssKV])]
  type ByMediaQuery       = ListMap[CssMediaQueryO, ValuesByMediaQuery]

  def separateStylesAndKeyframes(c: Css): (StyleStream, KeyframeStream, FontFaceStream) = {
    val styles = Vector.newBuilder[CssEntry.Style]
    val animations = List.newBuilder[CssEntry.Keyframes]
    val fontFaces = List.newBuilder[CssEntry.FontFace]
    c.foreach {
      case e: CssEntry.Style => styles += e
      case e: CssEntry.Keyframes => animations += e
      case e: CssEntry.FontFace => fontFaces += e
    }
    (styles.result(), animations.result(), fontFaces.result())
  }

  def mapByMediaQuery(c: StyleStream): ByMediaQuery = {
    val z: ByMediaQuery = ListMap.empty
    c.foldLeft(z){(q, e) =>
      val add = (e.sel, e.content)
      val k = e.mq
      q.updated(k, NonEmptyVector.endO(q get k, add))
    }
  }

  def flatten(css: StyleStream): Vector[(CssMediaQueryO, CssSelector, CssKV)] =
    css.flatMap { case CssEntry.Style(mq, sel, kvs) =>
      kvs.iterator.map(kv => (mq, sel, kv))
    }

  type Flat4 = (CssMediaQueryO, CssSelector, String, String)
  def flatten4(css: StyleStream): Vector[Flat4] =
    css.flatMap { case CssEntry.Style(mq, sel, kvs) =>
      kvs.iterator.map(kv => (mq, sel, kv.key, kv.value))
    }
}