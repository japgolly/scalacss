package scalacss.internal

object NaturalOrdering extends Ordering[String] {

  override def compare(a: String, b: String): Int = {
    var ia: Int = 0
    var ib: Int = 0
    val aLen = a.length
    val bLen = b.length
    var firstZeroDiff: Int = 0

    while (ia < aLen && ib < bLen) {
      val ca = a.charAt(ia)
      val cb = b.charAt(ib)

      if (Character.isDigit(ca) && Character.isDigit(cb)) {
        // Both start a numeric run; count leading zeroes
        var nza = 0
        while (ia < aLen && a.charAt(ia) == '0') {
          nza += 1
          ia += 1
        }
        var nzb = 0
        while (ib < bLen && b.charAt(ib) == '0') {
          nzb += 1
          ib += 1
        }

        val aHasDigits = ia < aLen && Character.isDigit(a.charAt(ia))
        val bHasDigits = ib < bLen && Character.isDigit(b.charAt(ib))

        if (!aHasDigits && !bHasDigits) {
          // Both numbers evaluated to 0
          if (firstZeroDiff == 0 && nza != nzb)
            firstZeroDiff = nza - nzb
        } else if (!aHasDigits) {
          // a was 0, b is > 0
          return -1
        } else if (!bHasDigits) {
          // a is > 0, b was 0
          return 1
        } else {
          // Both have non-zero digits; scan digit run
          var bias = 0
          var cr_ia = ia
          var cr_ib = ib
          var done = false
          var numResult = 0

          while (!done) {
            val cr_aDigit = cr_ia < aLen && Character.isDigit(a.charAt(cr_ia))
            val cr_bDigit = cr_ib < bLen && Character.isDigit(b.charAt(cr_ib))

            if (!cr_aDigit && !cr_bDigit) {
              done = true
              numResult = bias
            } else if (!cr_aDigit) {
              done = true
              numResult = -1
            } else if (!cr_bDigit) {
              done = true
              numResult = 1
            } else {
              if (bias == 0) {
                val cr_ca = a.charAt(cr_ia)
                val cr_cb = b.charAt(cr_ib)
                if (cr_ca < cr_cb) bias = -1
                else if (cr_ca > cr_cb) bias = 1
              }
              cr_ia += 1
              cr_ib += 1
            }
          }

          ia = cr_ia
          ib = cr_ib

          if (numResult != 0)
            return numResult

          if (firstZeroDiff == 0 && nza != nzb)
            firstZeroDiff = nza - nzb
        }
      } else if (ca != cb) {
        return ca - cb
      } else {
        ia += 1
        ib += 1
      }
    }

    if (ia < aLen)
      1
    else if (ib < bLen)
      -1
    else
      firstZeroDiff
  }
}
