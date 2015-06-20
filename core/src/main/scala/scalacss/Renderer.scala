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

      val m = Css.mapByMediaQuery(css)                    // Group by MQ
      m.foreach(t => if (t._1.isEmpty)   fmt(None, t._2)) // CSS without MQs first
      m.foreach(t => if (t._1.isDefined) fmt(t._1, t._2)) // CSS with MQs last
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

  case class FormatSB(mqStart : CssMediaQuery                 => Unit,
                      selStart: (CssMediaQueryO, CssSelector) => Unit,
                      kv1     : (CssMediaQueryO, CssKV)       => Unit,
                      kvn     : (CssMediaQueryO, CssKV)       => Unit,
                      selEnd  : (CssMediaQueryO, CssSelector) => Unit,
                      mqEnd   : CssMediaQuery                 => Unit,
                      done    : ()                            => Unit) {

    def apply(mq: CssMediaQueryO, data: Css.ValuesByMediaQuery): Unit = {
      mq foreach mqStart
      for ((sel, kvs) <- data.whole)
        apply(mq, sel, kvs)
      mq foreach mqEnd
    }

    def apply(e: CssEntry): Unit = {
      import e._
      mq foreach mqStart
      apply(mq, sel, content)
      mq foreach mqEnd
    }

    def apply(mq: CssMediaQueryO, sel: CssSelector, kvs: NonEmptyVector[CssKV]): Unit = {
      selStart(mq, sel)
      kv1(mq, kvs.head)
      kvs.tail.foreach(kvn(mq, _))
      selEnd(mq, sel)
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
      m      => { sb append m; sb append '{' },
      (_, s) => { sb append s; sb append '{' },
      (_, c) => kv(c),
      (_, c) => { sb append ';'; kv (c) },
      (_, _) => sb append '}',
      _      => sb append '}',
      ()     => ())
  }

  /**
   * Generates CSS intended for humans.
   */
  def formatPretty(indent: String = "  ", postColon: String = " "): Format = sb => {
    def mqIndent(mq: CssMediaQueryO): Unit =
      if (mq.isDefined) sb append indent
    val kv: (CssMediaQueryO, CssKV) => Unit =
      (mq, c) => {
        mqIndent(mq)
        sb append indent
        sb append c.key
        sb append ':'
        sb append postColon
        sb append c.value
        sb append ";\n"
      }
    FormatSB(
      mq => {
        sb append mq
        sb append " {\n"
      },
      (mq, sel) => {
        mqIndent(mq)
        sb append sel
        sb append " {\n"
      },
      kv, kv,
      (mq, _) => {
        mqIndent(mq)
        sb append "}\n"
        if (mq.isEmpty) sb append '\n'
      },
      _ => sb append "}\n\n",
      () => ())
  }

  val defaultPretty: Renderer[String] =
    formatPretty()
}
