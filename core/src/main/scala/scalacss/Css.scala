package scalacss

object Css {

  def prepareStyles(styles: TraversableOnce[StyleA])(implicit env: Env): StylesStream =
    styles.toStream flatMap styleA

  def prepareKeyframes(styles: TraversableOnce[Keyframes])(implicit env: Env): KeyframesStream =
    styles.toStream map keyframes

  def className(cn: ClassName): CssSelector =
    "." + cn.value

  def selector(cn: ClassName, c: Cond): CssSelector =
    selector(className(cn), c)

  def selector(cs: CssSelector, c: Cond): CssSelector =
    c.pseudo.fold(cs)(_ modSelector cs)

  def mediaQuery(c: Cond): CssMediaQueryO =
    NonEmptyVector.option(c.mediaQueries) map Media.css

  def keyframes(frames: Keyframes)(implicit env: Env): CssKeyframesAnimation = {
    CssKeyframesAnimation(frames.name, frames.frames.map(s => (s._1, styleA(s._2))).toMap)
  }

  def styleA(s: StyleA)(implicit env: Env): StylesStream =
    style(className(s.className), s.style)

  def style(sel: CssSelector, s: StyleS)(implicit env: Env): StylesStream = {
    def main: StylesStream =
      s.data.toStream.flatMap {
        case (cond, avs) =>
          val kvs = avs.avStream.map(_(env)).foldLeft(Vector.empty[CssKV])(_ ++ _)
          NonEmptyVector.maybe(kvs, Stream.empty[CssStyleEntry]) {c =>
            val mq = mediaQuery(cond)
            val s  = selector(sel, cond)
            Stream(CssStyleEntry(mq, s, c))
          }
        }

    def exts: StylesStream =
      s.unsafeExts.toStream.flatMap(unsafeExt(sel, _))

    main append exts
  }

  def unsafeExt(root: CssSelector, u: Style.UnsafeExt)(implicit env: Env): StylesStream = {
    val sel = u.sel(root)
    style(sel, u.style)
  }

  type ValuesByMediaQuery = NonEmptyVector[(CssSelector, NonEmptyVector[CssKV])]
  type ByMediaQuery       = Map[CssMediaQueryO, ValuesByMediaQuery]

  def findStylesAndAnimations(c: Css): (StylesStream, KeyframesStream) = {
    val styles = Stream.newBuilder[CssStyleEntry]
    val animations = Stream.newBuilder[CssKeyframesAnimation]
    c.foreach {
      case e: CssStyleEntry => styles += e
      case e: CssKeyframesAnimation => animations += e
    }
    (styles.result(), animations.result())
  }

  def mapByMediaQuery(c: StylesStream): ByMediaQuery = {
    val z: ByMediaQuery = Map.empty
    c.foldLeft(z){(q, e) =>
      val add = (e.sel, e.content)
      val k = e.mq
      q.updated(k, NonEmptyVector.endO(q get k, add))
    }
  }

  def flatten(css: StylesStream): Stream[(CssMediaQueryO, CssSelector, CssKV)] =
    css.flatMap { case CssStyleEntry(mq, sel, kvs) =>
      kvs.toStream.map(kv => (mq, sel, kv))
    }

  type Flat4 = (CssMediaQueryO, CssSelector, String, String)
  def flatten4(css: StylesStream): Stream[Flat4] =
    css.flatMap { case CssStyleEntry(mq, sel, kvs) =>
      kvs.toStream.map(kv => (mq, sel, kv.key, kv.value))
    }
}