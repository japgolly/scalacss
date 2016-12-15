package scalacss.internal

/**
 * Render [[Css]], an internal representation of CSS, into a different format; usually real CSS text.
 */
trait Renderer[+Out] {
  def apply(css: Css): Out
}

object StringRenderer {
  type Format = StringBuilder => FormatSB

  /**
   * Default CSS generator.
   *
   * Merges CSS with the same media query into a single media query clause.
   */
  final class Default(format: Format) extends Renderer[String] {
    override def apply(css: Css): String = {
      val sb = new StringBuilder
      val fmt = format(sb)
      val (styles, keyframes, fontFaces) = Css.separateStylesAndKeyframes(css)
      val byMQ = Css.mapByMediaQuery(styles)

      fontFaces.foreach(fmt(_))                              // Render font faces
      keyframes.foreach(fmt(_))                              // Render keyframes
      byMQ.foreach(t => if (t._1.isEmpty)   fmt(None, t._2)) // Render styles without MQs
      byMQ.foreach(t => if (t._1.isDefined) fmt(t._1, t._2)) // Render styles with MQs
      fmt.done()

      sb.toString()
    }
  }

  /**
   * Generates CSS however it's received in. No pre-processing.
   */
  final class Raw(format: Format) extends Renderer[String] {
    override def apply(css: Css): String = {
      val sb = new StringBuilder
      val fmt = format(sb)

      css foreach (fmt(_))

      sb.toString()
    }
  }

  implicit def autoFormatToRenderer(f: Format): Renderer[String] =
    new Default(f)

  type KeyframeSelectorO = Option[KeyframeSelector]

  case class FormatSB(kfStart : (String, String)                           => Unit,
                      kfsStart: Value                                      => Unit,
                      mqStart : CssMediaQuery                              => Unit,
                      selStart: (CssMediaQueryO, CssSelector)              => Unit,
                      kv1     : (KeyframeSelectorO, CssMediaQueryO, CssKV) => Unit,
                      kvn     : (KeyframeSelectorO, CssMediaQueryO, CssKV) => Unit,
                      selEnd  : (CssMediaQueryO, CssSelector)              => Unit,
                      mqEnd   : CssMediaQuery                              => Unit,
                      kfsEnd  : KeyframeSelector                           => Unit,
                      kfEnd   : KeyframeAnimationName                      => Unit,
                      ff      : CssEntry.FontFace                          => Unit,
                      done    : ()                                         => Unit) {

    def apply(mq: CssMediaQueryO, data: Css.ValuesByMediaQuery): Unit = {
      mq foreach mqStart
      for ((sel, kvs) <- data.whole)
        apply(None, mq, sel, kvs)
      mq foreach mqEnd
    }

    def apply(cssEntry: CssEntry): Unit =
      cssEntry match {

        case e: CssEntry.Style =>
          e.mq foreach mqStart
          apply(None, e.mq, e.sel, e.content)
          e.mq foreach mqEnd

        case e: CssEntry.Keyframes =>
          Seq("-webkit-", "-moz-", "-o-", "").foreach { p => 
            kfStart(p, e.name.value)
            for ((sel, styles) <- e.frames) {
              kfsStart(sel.value)
              val selO = Some(sel)
              for (s <- styles)
                printCssKV(selO, s.mq, s.content)
              kfsEnd(sel)
            }
            kfEnd(e.name)
          }

        case e: CssEntry.FontFace =>
          ff(e)
      }

    def apply(kf: KeyframeSelectorO, mq: CssMediaQueryO, sel: CssSelector, kvs: NonEmptyVector[CssKV]): Unit = {
      selStart(mq, sel)
      printCssKV(kf, mq, kvs)
      selEnd(mq, sel)
    }

    def printCssKV(kf: KeyframeSelectorO, mq: CssMediaQueryO, kvs: NonEmptyVector[CssKV]): Unit = {
      kv1(kf, mq, kvs.head)
      kvs.tail.foreach(kvn(kf, mq, _))
    }
  }

  def printFontFace(fontface: CssEntry.FontFace,
                    start   : ()                        => Unit,
                    kv      : (String, String, Boolean) => Unit, //Key, value, wrap
                    end     : ()                        => Unit) = {
    start()
    kv("font-family", fontface.fontFamily, true)
    kv("src", fontface.src.toStream.mkString(","), false)
    for (v <- fontface.fontStretch ) kv("font-stretch" , v         , false)
    for (v <- fontface.fontStyle   ) kv("font-style"   , v         , false)
    for (v <- fontface.fontWeight  ) kv("font-weight"  , v         , false)
    for (v <- fontface.unicodeRange) kv("unicode-range", v.toString, false)
    end()
  }

  private def quoteIfNeeded(sb: StringBuilder, s: String, quote: Boolean): Unit =
    if (quote && s.contains(" ")) {
      sb append '"'
      sb append s
      sb append '"'
    } else
      sb append s

  /**
   * Generates tiny CSS intended for browsers. No unnecessary whitespace or colons.
   */
  lazy val formatTiny: Format = sb => {
    def kv(c: CssKV, quote: Boolean = false): Unit = {
      sb append c.key
      sb append ':'
      quoteIfNeeded(sb, c.value, quote)
    }
    FormatSB(
      kfStart  = (p, n)    => { sb append s"@${p}keyframes "; sb append n; sb append '{' },
      kfsStart = s         => { sb append s; sb append '{' },
      mqStart  = m         => { sb append m; sb append '{' },
      selStart = (_, s)    => { sb append s; sb append '{' },
      kv1      = (_, _, c) => kv(c),
      kvn      = (_, _, c) => { sb append ';'; kv (c) },
      selEnd   = (_, _)    => sb append '}',
      mqEnd    = _         => sb append '}',
      kfsEnd   = _         => sb append '}',
      kfEnd    = _         => sb append '}',
      ff       = fontface  =>
        printFontFace(
          fontface,
          () => sb append "@font-face {",
          (key: String, value: String, quote: Boolean) => kv(CssKV(key, value), quote),
          () => sb append "}"
        ),
      done     = ()        => ())
  }

  /**
   * Generates CSS intended for humans.
   */
  def formatPretty(indent: String = "  ", postColon: String = " "): Format = sb => {
    def mqIndent(mq: CssMediaQueryO): Unit =
      if (mq.isDefined) sb append indent
    def kfIndent(kf: KeyframeSelectorO): Unit =
      if (kf.isDefined) sb append indent
    def kv(kf: KeyframeSelectorO, mq: CssMediaQueryO, c: CssKV, quote: Boolean = false) = {
      kfIndent(kf)
      mqIndent(mq)
      sb append indent
      sb append c.key
      sb append ':'
      sb append postColon
      quoteIfNeeded(sb, c.value, quote)
      sb append ";\n"
    }
    FormatSB(
      kfStart  = (p, n) => { sb append s"@${p}keyframes "; sb append n; sb append " {\n" },
      kfsStart = s => { sb append indent; sb append s; sb append " {\n" },
      mqStart  = mq => {
                   sb append mq
                   sb append " {\n"
                 },
      selStart = (mq, sel) => {
                   mqIndent(mq)
                   sb append sel
                   sb append " {\n"
                 },
      kv1      = (kf: KeyframeSelectorO, mq: CssMediaQueryO, c: CssKV) => kv(kf, mq, c),
      kvn      = (kf: KeyframeSelectorO, mq: CssMediaQueryO, c: CssKV) => kv(kf, mq, c),
      selEnd   = (mq, _) => {
                   mqIndent(mq)
                   sb append "}\n"
                   if (mq.isEmpty) sb append '\n'
                 },
      mqEnd    = _ => sb append "}\n\n",
      kfsEnd   = _ => { sb append indent; sb append "}\n\n" },
      kfEnd    = _ => sb append "}\n\n",
      ff       = fontface =>
        printFontFace(
          fontface,
          () => sb append "@font-face {\n",
          (key: String, value: String, quote: Boolean) => kv(None, None, CssKV(key, value), quote),
          () => sb append "}\n\n"
        ),
      done     = () => ())
  }

  lazy val defaultPretty: Renderer[String] =
    formatPretty()
}
