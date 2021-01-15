package scalacss.internal

/*
 NaturalOrdering.scala

  A direct port of NaturalOrderComparator.java by David Barri (@japgolly)
  with the following differences:

    - In compare()
      - inline compareRight() and extract its variables for reuse
      - Add `else` before `if (ca == 0 && cb == 0)`

    - In compareRight()
      - reuse ca/cb vars between loops
      - avoid multiple, identical calls to Character.isDigit

 -------------------------------------------------------------------------------

 NaturalOrderComparator.java -- Perform 'natural order' comparisons of strings in Java.
 Copyright (C) 2003 by Pierre-Luc Paour <natorder@paour.com>

 Based on the C version by Martin Pool, of which this is more or less a straight conversion.
 Copyright (C) 2000 by Martin Pool <mbp@humbug.org.au>

 This software is provided 'as-is', without any express or implied
 warranty.  In no event will the authors be held liable for any damages
 arising from the use of this software.

 Permission is granted to anyone to use this software for any purpose,
 including commercial applications, and to alter it and redistribute it
 freely, subject to the following restrictions:

 1. The origin of this software must not be misrepresented; you must not
 claim that you wrote the original software. If you use this software
 in a product, an acknowledgment in the product documentation would be
 appreciated but is not required.
 2. Altered source versions must be plainly marked as such, and must not be
 misrepresented as being the original software.
 3. This notice may not be removed or altered from any source distribution.
 */
object NaturalOrdering extends Ordering[String] {

  override def compare(a: String, b: String): Int = {

    // Variables for compareRight
    var cr_bias: Int    = 0
    var cr_ia  : Int    = 0
    var cr_ib  : Int    = 0
    var cr_ca  : Char   = 0
    var cr_cb  : Char   = 0
    var cr_a   : String = null
    var cr_b   : String = null

    @inline def compareRight(): Int = {
      cr_bias = 0
      cr_ia   = 0
      cr_ib   = 0

      // The longest run of digits wins. That aside, the greatest
      // value wins, but we can't know that it will until we've scanned
      // both numbers to know that they have the same magnitude, so we
      // remember it in BIAS.
      while (true) {
        cr_ca = charAt(cr_a, cr_ia)
        cr_cb = charAt(cr_b, cr_ib)
        if (!Character.isDigit(cr_ca)) {
          return if (Character.isDigit(cr_cb)) -1 else cr_bias
        } else if (!Character.isDigit(cr_cb))
          return 1
        else if (cr_ca < cr_cb) {
          if (cr_bias == 0) cr_bias = -1
        } else if (cr_ca > cr_cb) {
          if (cr_bias == 0) cr_bias = 1
        } else if (cr_ca == 0 && cr_cb == 0)
          return cr_bias

        cr_ia += 1
        cr_ib += 1
      }
      0
    }

    // Begin port of compare()
    var ia    : Int  = 0
    var ib    : Int  = 0
    var nza   : Int  = 0
    var nzb   : Int  = 0
    var ca    : Char = 0
    var cb    : Char = 0
    var result: Int  = 0

    while (true) {

      // only count the number of zeroes leading the last number compared
      nza = 0
      nzb = 0

      ca = charAt(a, ia)
      cb = charAt(b, ib)

      // skip over leading spaces or zeros
      while (Character.isSpaceChar(ca) || ca == '0') {
        if (ca == '0')
          nza += 1
        else
          // only count consecutive zeroes
          nza = 0

        ia += 1
        ca = charAt(a, ia)
      }

      while (Character.isSpaceChar(cb) || cb == '0') {
        if (cb == '0')
          nzb += 1
        else
          // only count consecutive zeroes
          nzb = 0
        ib += 1
        cb = charAt(b, ib)
      }

      // process run of digits
      if (Character.isDigit(ca) && Character.isDigit(cb)) {
        cr_a = a.substring(ia)
        cr_b = b.substring(ib)
        result = compareRight()
        if (result != 0)
          return result

      } else if (ca == 0 && cb == 0)
        // The strings compare the same. Perhaps the caller
        // will want to call strcmp to break the tie.
        return nza - nzb

      if (ca < cb)
        return -1

      if (ca > cb)
        return 1

      ia += 1
      ib += 1
    }

    0
  }

  private def charAt(s: String, i: Int): Char =
    if (i >= s.length)
      0
    else
      s.charAt(i)
}
