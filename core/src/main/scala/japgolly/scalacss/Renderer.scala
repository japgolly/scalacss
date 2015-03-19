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
    for ((sel, kvs) <- css) {
      fmt selStart sel
      fmt kv1 kvs.head
      kvs.tail foreach fmt.kvn
      fmt selEnd sel
    }
    fmt.done()
    sb.toString()
  }
}

object StringRenderer {
  type Format = StringBuilder => FormatSB

  implicit def autoFormatToRenderer(f: Format): StringRenderer =
    new StringRenderer(f)

  case class FormatSB(selStart: CssSelector => Unit,
                      kv1     : CssKV => Unit,
                      kvn     : CssKV => Unit,
                      selEnd  : CssSelector => Unit,
                      done    : () => Unit)

  /**
   * Generates tiny CSS intended for browsers. No unnecessary whitespace or colons.
   */
  val formatTiny: Format = sb => {
    val kv: CssKV => Unit = c => {
      sb append c.key
      sb append ':'
      sb append c.value
    }
    FormatSB(
      sel => {
        sb append sel
        sb append '{'
      },
      kv,
      c => {
        sb append ';'
        kv(c)
      },
      _ => sb append '}',
      () => ())
  }

  /**
   * Generates CSS intended for humans.
   */
  def formatPretty(indent: String = "  ", postColon: String = " "): Format = sb => {
    val kv: CssKV => Unit = c => {
      sb append indent
      sb append c.key
      sb append ':'
      sb append postColon
      sb append c.value
      sb append ";\n"
    }
    FormatSB(
      sel => {
        sb append sel
        sb append " {\n"
      },
      kv, kv,
      _ => sb append "}\n\n",
      () => ())
  }

  val defaultPretty =
    new StringRenderer(formatPretty())
}
