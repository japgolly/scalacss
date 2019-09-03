package scalacss.internal

import utest._
import scalacss.test.TestUtil._
import scalacss.DevDefaults._

object StyleTest extends TestSuite {

  override def tests = Tests {

    "styleA" - {
      "addOperator" - {
        import Dsl._

        val s1 = StyleA(ClassName("s1"), Vector("c1", "c2", "c3").map(ClassName.apply), style(backgroundColor.blue))
        val s2 = StyleA(ClassName("s2"), Vector("c4", "c5").map(ClassName.apply), style(color.red))

        val r1 = s1 + s2
        assertEq(r1.className, s1.className)
        assertEq(r1.addClassNames.size, 6)
        assert(r1.addClassNames.contains(s2.className))
        for (cn <- s1.addClassNames ++ s2.addClassNames)
          assert(r1.addClassNames.contains(cn))
        val r1Data = r1.style.data(Cond.empty)
        assert(r1Data.get(backgroundColor).get.head ==* backgroundColor.blue.value)
        assert(r1Data.get(color).get.head ==* color.red.value)

        val r2 = s2 + s1
        assertEq(r2.className, s2.className)
        assertEq(r2.addClassNames.size, 6)
        assert(r2.addClassNames.contains(s1.className))
        for (cn <- s1.addClassNames ++ s2.addClassNames)
          assert(r2.addClassNames.contains(cn))
        val r2Data = r2.style.data(Cond.empty)
        assert(r2Data.get(backgroundColor).get.head ==* backgroundColor.blue.value)
        assert(r2Data.get(color).get.head ==* color.red.value)
      }
    }
  }
}
