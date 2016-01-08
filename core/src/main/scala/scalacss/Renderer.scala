package scalacss

/**
 * Render [[Css]], an internal representation of CSS, into a different format; usually real CSS text.
 */
trait Renderer[Out] {
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
      val (styles, keyframes) = Css.separateStylesAndKeyframes(css)
      val byMQ = Css.mapByMediaQuery(styles)

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

  case class FormatSB(kfStart : String                                     => Unit,
                      kfsStart: Value                                      => Unit,
                      mqStart : CssMediaQuery                              => Unit,
                      selStart: (CssMediaQueryO, CssSelector)              => Unit,
                      kv1     : (KeyframeSelectorO, CssMediaQueryO, CssKV) => Unit,
                      kvn     : (KeyframeSelectorO, CssMediaQueryO, CssKV) => Unit,
                      selEnd  : (CssMediaQueryO, CssSelector)              => Unit,
                      mqEnd   : CssMediaQuery                              => Unit,
                      kfsEnd  : KeyframeSelector                           => Unit,
                      kfEnd   : KeyframeAnimationName                      => Unit,
                      done    : ()                                         => Unit) {

    def apply(mq: CssMediaQueryO, data: Css.ValuesByMediaQuery): Unit = {
      mq foreach mqStart
      for ((sel, kvs) <- data.whole)
        apply(None, mq, sel, kvs)
      mq foreach mqEnd
    }

    def apply(cssEntry: CssEntry): Unit =
      cssEntry match {

        case e: CssStyleEntry =>
          e.mq foreach mqStart
          apply(None, e.mq, e.sel, e.content)
          e.mq foreach mqEnd

        case e: CssKeyframesEntry =>
          kfStart(e.name.value)
          for ((sel, styles) <- e.frames) {
            kfsStart(sel.value)
            val selO = Some(sel)
            for (s <- styles)
              printCssKV(selO, s.mq, s.content)
            kfsEnd(sel)
          }
          kfEnd(e.name)
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

  /**
   * Generates tiny CSS intended for browsers. No unnecessary whitespace or colons.
   */
  val formatTiny: Format = sb => {
    def kv(c: CssKV): Unit = {
      sb append c.key
      sb append ':'
      sb append c.value
    }
    FormatSB(
      kfStart  = n         => { sb append "@keyframes "; sb append n; sb append '{' },
      kfsStart = s         => { sb append s; sb append '{' },
      mqStart  = m         => { sb append m; sb append '{' },
      selStart = (_, s)    => { sb append s; sb append '{' },
      kv1      = (_, _, c) => kv(c),
      kvn      = (_, _, c) => { sb append ';'; kv (c) },
      selEnd   = (_, _)    => sb append '}',
      mqEnd    = _         => sb append '}',
      kfsEnd   = _         => sb append '}',
      kfEnd    = _         => sb append '}',
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
    val kv: (KeyframeSelectorO, CssMediaQueryO, CssKV) => Unit =
      (kf, mq, c) => {
        kfIndent(kf)
        mqIndent(mq)
        sb append indent
        sb append c.key
        sb append ':'
        sb append postColon
        sb append c.value
        sb append ";\n"
      }
    FormatSB(
      kfStart  = n => { sb append "@keyframes "; sb append n; sb append " {\n" },
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
      kv1      = kv,
      kvn      = kv,
      selEnd   = (mq, _) => {
                   mqIndent(mq)
                   sb append "}\n"
                   if (mq.isEmpty) sb append '\n'
                 },
      mqEnd    = _ => sb append "}\n\n",
      kfsEnd   = _ => { sb append indent; sb append "}\n\n" },
      kfEnd    = _ => sb append "}\n\n",
      done     = () => ())
  }

  val defaultPretty: Renderer[String] =
    formatPretty()
}
