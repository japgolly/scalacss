package japgolly.scalacss

import japgolly.nyaya._
import japgolly.nyaya.test.PropTest._
import utest._
import japgolly.scalacss.TestUtil._

object ComposeTest extends TestSuite {

  type TT = (StyleS, StyleS)

//  val associativity =
//    Prop.equal[TTT]("Associativity")(
//      { case (a,b,c) => x((a compose b) compose c) },
//      { case (a,b,c) => x(a compose (b compose c)) })

  def flat(s: StyleS) =
    Css.flatten3(s.copy(className = None).inspectCss)

  val appendTest = {
    implicit val impc = new Compose(Compose.Rules.silent(Compose.Rules.append))
    Prop.equal[TT]("sort(a) + sort(b) = sort(a + b)")(
      {case (a,b) => (flat(a) ++ flat(b)).sorted },
      {case (a,b) => flat(a compose b).sorted })
  }

  // replace

  override val tests = TestSuite {
    'append - appendTest.mustBeSatisfiedBy(RandomData.styleS.pair) //(defaultPropSettings.setSampleSize(5000))
  }
}
