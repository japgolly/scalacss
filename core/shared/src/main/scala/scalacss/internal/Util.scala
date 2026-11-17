package scalacss.internal

object Util {

  // https://developer.mozilla.org/en-US/docs/Web/CSS/Reference/Values/string
  // https://www.w3.org/TR/CSS2/syndata.html#escaped-characters
  def quote(input: String): String =
    quote(input, '\'')

  def quote(input: String, quoteChar: Char): String = {
    val len = input.length
    val sb = new StringBuilder(len + 2)
    sb.append(quoteChar)
    val quoteCp = quoteChar.toInt
    var i = 0
    while (i < len) {
      val cp = input.codePointAt(i)
      if (cp == '\\' || cp == quoteCp || cp < 32 || cp >= 127)
        sb.append("\\%06X".format(cp))
      else
        sb.append(cp.toChar)
      i += Character.charCount(cp)
    }
    sb.append(quoteChar).toString
  }
}
