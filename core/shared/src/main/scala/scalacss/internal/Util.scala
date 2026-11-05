package scalacss.internal

object Util {

  // https://developer.mozilla.org/en-US/docs/Web/CSS/Reference/Values/string
  // https://www.w3.org/TR/CSS2/syndata.html#escaped-characters
  def quote(input: String): String = {
    // fast in JS, speed doesn't matter in JVM for this case
    var newStr = "'"
    var i = 0
    val len = input.length
    while (i < len) {
      val cp = input.codePointAt(i)

      @inline def escape =
        cp match {
          case '\\' | '\'' => true
          case _           => cp < 32 || cp >= 127
        }

      val add =
        if (escape)
          "\\%06X".format(cp)
        else
          cp.toChar.toString

      newStr += add
      i += Character.charCount(cp)
    }

    newStr + "'"
  }
}
