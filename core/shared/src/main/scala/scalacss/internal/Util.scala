package scalacss.internal

object Util {

  // https://developer.mozilla.org/en-US/docs/Web/CSS/Reference/Values/string
  // https://www.w3.org/TR/CSS2/syndata.html#escaped-characters
  def quote(input: String): String =
    quote(input, '\'')

  def quote(input: String, quoteChar: Char): String = {
    // fast in JS, speed doesn't matter in JVM for this case
    var newStr = quoteChar.toString
    var i = 0
    val len = input.length
    val quoteCp = quoteChar.toInt
    while (i < len) {
      val cp = input.codePointAt(i)

      @inline def escape =
        cp == '\\' || cp == quoteCp || cp < 32 || cp >= 127

      val add =
        if (escape)
          "\\%06X".format(cp)
        else
          cp.toChar.toString

      newStr += add
      i += Character.charCount(cp)
    }

    newStr + quoteChar
  }
}
