package japgolly.scalacss

import scalaz.NonEmptyList
import utest._

// TODO QuickDemo is not a test
object QuickDemo extends TestSuite {

  import Attrs._

  val style1 = new StyleS(Map(
    NoCond -> AVsAndWarnings(NonEmptyList(
      AV(margin, "12px"), AV(textAlign, "center"), AV(color, "red")), Nil)
    , Pseudo.Visited -> AVsAndWarnings(NonEmptyList(
      AV(color, "#d4d4d4"), AV(fontWeight, "bold")), Nil)
  ), Nil, None)

  val style2 = new StyleS(Map(
    NoCond -> AVsAndWarnings(NonEmptyList(
      AV(fontWeight, "bold")), Nil)
    , (Pseudo.Hover & Pseudo.Not(".debug")) -> AVsAndWarnings(NonEmptyList(
      AV(backgroundColor, "green"), AV(fontWeight, "normal")), Nil)
  ), Nil, None)

  import MutableRegister._

  //  val ng = new NameGen.IncFmt("class_%04d")
  val ng = new NameGen.Alphabet(NameGen.alphaNumeric, "_" + _)
  val reg = new MutableRegister(ng, ErrorHandler.noisy)
  reg register style1
  reg register style2

    val fmt = StringRenderer.formatPretty()
//  val fmt = StringRenderer.formatTiny
  val css = reg.render(fmt, Env.empty)
  println("\n" + css)

  override val tests = TestSuite {
  }
}
