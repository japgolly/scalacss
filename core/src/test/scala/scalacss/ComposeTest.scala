package scalacss

import japgolly.nyaya._
import japgolly.nyaya.test.PropTest._
import utest._
import scalacss.TestUtil._

object ComposeTest extends TestSuite {

  type TT = (StyleS, StyleS)

//  val associativity =
//    Prop.equal[TTT]("Associativity")(
//      { case (a,b,c) => x((a compose b) compose c) },
//      { case (a,b,c) => x(a compose (b compose c)) })

  def flat(s: StyleS) =
    Css.flatten4(s.copy(className = None).inspectCss)

  val appendTest = {
    implicit val impc = new Compose(Compose.Rules.silent(Compose.Rules.append))
    Prop.equal[TT]("sort(a) + sort(b) = sort(a + b)")(
      {case (a,b) => (flat(a) ++ flat(b)).sorted },
      {case (a,b) => flat(a compose b).sorted })
  }

  val replaceTest = {
    implicit val impc = new Compose(Compose.Rules.silent(Compose.Rules.replace))
    Prop.allPresent[(TT)]("a∘b ⊇ b")(
      {case (_, b) => flat(b).toSet },
      {case (a, b) => flat(a compose b) })
  }

  object Issue25 {
    import DevDefaults._
    object SampleStyles extends StyleSheet.Inline {
      override implicit val classNameHint = mutable.ClassNameHint("TEST")
      import dsl._
      val other = style("other")(borderCollapse.collapse, &.hover(fontWeight._200), fontWeight._100)
      val outer = style("outer")(fontWeight.bold)
      val inner = style(color.red, outer)
    }
    def test(): Unit = {
      val css = SampleStyles.renderA[String].trim
      assertEq(SampleStyles.outer.htmlClass, "outer")
      assertEq(SampleStyles.inner.htmlClass, "TEST-0001")
      assertEq(css,
        """
          |.other {
          |  border-collapse: collapse;
          |  font-weight: 100;
          |}
          |
          |.other:hover {
          |  font-weight: 200;
          |}
          |
          |.outer {
          |  font-weight: bold;
          |}
          |
          |.TEST-0001 {
          |  color: red;
          |  font-weight: bold;
          |}
        """.stripMargin.trim)
    }
  }


  override val tests = TestSuite {
    'append  - appendTest .mustBeSatisfiedBy(RandomData.styleS.pair) //(defaultPropSettings.setSampleSize(2000))
    'replace - replaceTest.mustBeSatisfiedBy(RandomData.styleS.pair) //(defaultPropSettings.setSampleSize(2000))
    'issue25 - Issue25.test()
  }
}
