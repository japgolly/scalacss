package scalacss.full

import scalacss.DevDefaults._

//object CopyDefaultsForStandalone extends Defaults
//import CopyDefaultsForStandalone._

object MyStandalone extends StyleSheet.Standalone {
  import dsl._

  "div.std" - (
    margin(12 px, auto),
    textAlign.left,
    cursor.pointer,

    &.hover -
      cursor.zoomIn,

    &("span") -
      color.red,

    (media.tv.minDeviceAspectRatio(4 :/: 3) & media.all.resolution(300 dppx)) -
      width(600 px),

    media.not.handheld.landscape.color -
      width(500 px)
  )

  "h1".firstChild -
    fontWeight.bold

  for (i <- 0 to 3)
    s".indent-$i" -
      paddingLeft(i * 2.ex)
}

object StandaloneTest extends utest.TestSuite {
  import japgolly.microlibs.testutil.TestUtil._
  import utest._

  override def tests = Tests {
    def norm(css: String) = css.trim
    assertMultiline(norm(MyStandalone.render), norm(
      """
        |div.std {
        |  margin: 12px auto;
        |  text-align: left;
        |  cursor: pointer;
        |}
        |
        |div.std:hover {
        |  cursor: -moz-zoom-in;
        |  cursor: -o-zoom-in;
        |  cursor: -webkit-zoom-in;
        |  cursor: zoom-in;
        |}
        |
        |div.std span {
        |  color: red;
        |}
        |
        |h1:first-child {
        |  font-weight: bold;
        |}
        |
        |.indent-0 {
        |  padding-left: 0;
        |}
        |
        |.indent-1 {
        |  padding-left: 2ex;
        |}
        |
        |.indent-2 {
        |  padding-left: 4ex;
        |}
        |
        |.indent-3 {
        |  padding-left: 6ex;
        |}
        |
        |@media not handheld and (orientation:landscape) and (color) {
        |  div.std {
        |    width: 500px;
        |  }
        |}
        |
        |@media tv and (min-device-aspect-ratio:3/4), all and (resolution:300dppx) {
        |  div.std {
        |    width: 600px;
        |  }
        |}
      """.stripMargin))
  }
}
