package scalacss.internal

object Css {

  def apply(ss: TraversableOnce[StyleA], kfs: TraversableOnce[Keyframes], ff: TraversableOnce[FontFace[String]])(implicit env: Env): Css =
    styles(ss) append keyframes(kfs) append fontFaces(ff)

  def styles(ss: TraversableOnce[StyleA])(implicit env: Env): StyleStream =
    ss.toStream flatMap styleA

  def keyframes(kfs: TraversableOnce[Keyframes])(implicit env: Env): KeyframeStream =
    kfs.toStream map keyframes

  def fontFaces(ff: TraversableOnce[FontFace[String]]): FontFaceStream =
    ff.toStream map fontFaces

  def className(cn: ClassName): CssSelector =
    "." + cn.value

  def selector(cn: ClassName, c: Cond): CssSelector =
    selector(className(cn), c)

  def selector(cs: CssSelector, c: Cond): CssSelector =
    c.pseudo.fold(cs)(_ modSelector cs)

  def mediaQuery(c: Cond): CssMediaQueryO =
    NonEmptyVector.option(c.mediaQueries) map Media.css

  def keyframes(kfs: Keyframes)(implicit env: Env): CssEntry.Keyframes =
    CssEntry.Keyframes(kfs.name, kfs.frames.iterator.map(s => (s._1, styleA(s._2))).toMap)

  def fontFaces(ff: FontFace[String]): CssEntry.FontFace =
    CssEntry.FontFace(ff.fontFamily, ff.src, ff.fontStretchValue, ff.fontStyleValue, ff.fontWeightValue, ff.unicodeRangeValue)

  def styleA(s: StyleA)(implicit env: Env): StyleStream =
    style(className(s.className), s.style)

  def style(sel: CssSelector, s: StyleS)(implicit env: Env): StyleStream = {
    def main: StyleStream =
      s.data.toStream.flatMap {
        case (cond, avs) =>
          val kvs = avs.avStream.map(_(env)).foldLeft(Vector.empty[CssKV])(_ ++ _)
          NonEmptyVector.maybe(kvs, Stream.empty[CssEntry.Style]) {c =>
            val mq = mediaQuery(cond)
            val s  = selector(sel, cond)
            Stream(CssEntry.Style(mq, s, c))
          }
        }

    def exts: StyleStream =
      s.unsafeExts.toStream.flatMap(unsafeExt(sel, _))

    main append exts
  }

  def unsafeExt(root: CssSelector, u: Style.UnsafeExt)(implicit env: Env): StyleStream = {
    val sel = u.sel(root + u.cond)
    style(sel, u.style)
  }

  type ValuesByMediaQuery = NonEmptyVector[(CssSelector, NonEmptyVector[CssKV])]
  type ByMediaQuery       = Map[CssMediaQueryO, ValuesByMediaQuery]

  def separateStylesAndKeyframes(c: Css): (StyleStream, KeyframeStream, FontFaceStream) = {
    val styles = Stream.newBuilder[CssEntry.Style]
    val animations = Stream.newBuilder[CssEntry.Keyframes]
    val fontFaces = Stream.newBuilder[CssEntry.FontFace]
    c.foreach {
      case e: CssEntry.Style => styles += e
      case e: CssEntry.Keyframes => animations += e
      case e: CssEntry.FontFace => fontFaces += e
    }
    (styles.result(), animations.result(), fontFaces.result())
  }

  def mapByMediaQuery(c: StyleStream): ByMediaQuery = {
    val z: ByMediaQuery = Map.empty
    c.foldLeft(z){(q, e) =>
      val add = (e.sel, e.content)
      val k = e.mq
      q.updated(k, NonEmptyVector.endO(q get k, add))
    }
  }

  def flatten(css: StyleStream): Stream[(CssMediaQueryO, CssSelector, CssKV)] =
    css.flatMap { case CssEntry.Style(mq, sel, kvs) =>
      kvs.toStream.map(kv => (mq, sel, kv))
    }

  type Flat4 = (CssMediaQueryO, CssSelector, String, String)
  def flatten4(css: StyleStream): Stream[Flat4] =
    css.flatMap { case CssEntry.Style(mq, sel, kvs) =>
      kvs.toStream.map(kv => (mq, sel, kv.key, kv.value))
    }
}