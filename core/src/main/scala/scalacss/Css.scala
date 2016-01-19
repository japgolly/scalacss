package scalacss

object Css {

  def apply(ss: TraversableOnce[StyleA], kfs: TraversableOnce[Keyframes])(implicit env: Env): Css =
    styles(ss) append keyframes(kfs)

  def styles(ss: TraversableOnce[StyleA])(implicit env: Env): StyleStream =
    ss.toStream flatMap styleA

  def keyframes(kfs: TraversableOnce[Keyframes])(implicit env: Env): KeyframeStream =
    kfs.toStream map keyframes

  def className(cn: ClassName): CssSelector =
    "." + cn.value

  def selector(cn: ClassName, c: Cond): CssSelector =
    selector(className(cn), c)

  def selector(cs: CssSelector, c: Cond): CssSelector =
    c.pseudo.fold(cs)(_ modSelector cs)

  def mediaQuery(c: Cond): CssMediaQueryO =
    NonEmptyVector.option(c.mediaQueries) map Media.css

  def keyframes(kfs: Keyframes)(implicit env: Env): CssKeyframesEntry =
    CssKeyframesEntry(kfs.name, kfs.frames.iterator.map(s => (s._1, styleA(s._2))).toMap)

  def styleA(s: StyleA)(implicit env: Env): StyleStream =
    style(className(s.className), s.style)

  def style(sel: CssSelector, s: StyleS)(implicit env: Env): StyleStream = {
    def main: StyleStream =
      s.data.toStream.flatMap {
        case (cond, avs) =>
          val kvs = avs.avStream.map(_(env)).foldLeft(Vector.empty[CssKV])(_ ++ _)
          NonEmptyVector.maybe(kvs, Stream.empty[CssStyleEntry]) {c =>
            val mq = mediaQuery(cond)
            val s  = selector(sel, cond)
            Stream(CssStyleEntry(mq, s, c))
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

  def separateStylesAndKeyframes(c: Css): (StyleStream, KeyframeStream) = {
    val styles = Stream.newBuilder[CssStyleEntry]
    val animations = Stream.newBuilder[CssKeyframesEntry]
    c.foreach {
      case e: CssStyleEntry => styles += e
      case e: CssKeyframesEntry => animations += e
    }
    (styles.result(), animations.result())
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
    css.flatMap { case CssStyleEntry(mq, sel, kvs) =>
      kvs.toStream.map(kv => (mq, sel, kv))
    }

  type Flat4 = (CssMediaQueryO, CssSelector, String, String)
  def flatten4(css: StyleStream): Stream[Flat4] =
    css.flatMap { case CssStyleEntry(mq, sel, kvs) =>
      kvs.toStream.map(kv => (mq, sel, kv.key, kv.value))
    }
}