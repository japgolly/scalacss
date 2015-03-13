package japgolly.scalacss

import utest._

// TODO QuickDemo is not a test
object QuickDemo extends TestSuite {

  import Attrs._

  val style1 = new StyleS(Map(
    Cond.empty -> NonEmptyVector(
      AV(margin, "12px"), AV(textAlign, "center"), AV(color, "red"))
    , Cond.empty.visited -> NonEmptyVector(
      AV(color, "#d4d4d4"), AV(fontWeight, "bold"))
  ), Nil, None, Nil)

  val style2 = new StyleS(Map(
    Cond.empty -> NonEmptyVector(
      AV(fontWeight, "bold"))
    , Cond.empty.hover.not(".debug") -> NonEmptyVector(
      AV(backgroundColor, "green"), AV(fontWeight, "normal"))
  ), Nil, None, Nil)

  import MutableRegister._

  //  val ng = new NameGen.IncFmt("class_%04d")
  val ng = NameGen.short("_")
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
