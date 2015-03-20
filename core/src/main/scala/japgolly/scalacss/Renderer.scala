package japgolly.scalacss

/**
 * Render [[Css]], an internal representation of CSS, into a different format; usually real CSS text.
 */
trait Renderer[Out] {
  def apply(css: Css): Out
}

final class StringRenderer(format: StringRenderer.Format) extends Renderer[String] {
  override def apply(css: Css): String = {
    val sb = new StringBuilder
    val fmt = format(sb)
    
    Css.mapByMediaQuery(css).fold(()) { (omq, data, _) =>
      omq foreach fmt.mqStart
      for ((sel, kvs) <- data) {
        fmt.selStart(omq, sel)
        fmt.kv1(omq, kvs.head)
        kvs.tail.foreach(fmt.kvn(omq, _))
        fmt.selEnd(omq, sel)
      }
      omq foreach fmt.mqEnd
    }
    fmt.done()

    sb.toString()
  }
}

object StringRenderer {
  type Format = StringBuilder => FormatSB

  implicit def autoFormatToRenderer(f: Format): StringRenderer =
    new StringRenderer(f)

  case class FormatSB(mqStart : CssMediaQuery                 => Unit,
                      selStart: (CssMediaQueryO, CssSelector) => Unit,
                      kv1     : (CssMediaQueryO, CssKV)       => Unit,
                      kvn     : (CssMediaQueryO, CssKV)       => Unit,
                      selEnd  : (CssMediaQueryO, CssSelector) => Unit,
                      mqEnd   : CssMediaQuery                 => Unit,
                      done    : ()                            => Unit)

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
        sb append "}\n\n"
      },
      _ => sb append "}\n\n",
      () => ())
  }

  val defaultPretty =
    new StringRenderer(formatPretty())
}
