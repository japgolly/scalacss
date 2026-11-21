package scalacss.internal

import japgolly.microlibs.testutil.TestUtil._
import scalacss.DevDefaults._
import utest._

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

        "deduplication" - {
          "self" - {
            val r = s1 + s1
            assertEq(r.className, s1.className)
            assertEq(r.addClassNames, s1.addClassNames)
            assertEq(r.htmlClasses, s1.htmlClasses)
          }

          "sharedAddClassNames" - {
            val a = StyleA(ClassName("a"), Vector("btn", "c1").map(ClassName.apply), style(backgroundColor.blue))
            val b = StyleA(ClassName("b"), Vector("btn", "c2").map(ClassName.apply), style(color.red))
            val r = a + b
            assertEq(r.className, ClassName("a"))
            assertEq(r.addClassNames.map(_.value), Vector("btn", "c1", "b", "c2"))
            assertEq(r.htmlClasses, "a btn c1 b c2")
          }

          "crossClassName" - {
            val a = StyleA(ClassName("a"), Vector("b", "c1").map(ClassName.apply), style(backgroundColor.blue))
            val b = StyleA(ClassName("b"), Vector("a", "c2").map(ClassName.apply), style(color.red))
            val r = a + b
            assertEq(r.className, ClassName("a"))
            assertEq(r.addClassNames.map(_.value), Vector("b", "c1", "c2"))
            assertEq(r.htmlClasses, "a b c1 c2")
          }

          "repeated" - {
            val s3 = StyleA(ClassName("s3"), Vector("c6").map(ClassName.apply), style(marginTop(1.px)))
            val r1 = (s1 + s2) + s2
            assertEq(r1.htmlClasses, (s1 + s2).htmlClasses)
            assertEq(((s1 + s2) + s3).htmlClasses, (s1 + (s2 + s3)).htmlClasses)
          }
        }
      }

      "compose" - {
        import Dsl._

        "multiClass" - {
          val reg = new mutable.Register(mutable.Register.NameGen.numbered(), mutable.Register.MacroName.Use, mutable.Register.ErrorHandler.silent)
          implicit val cnh: ClassNameHint = ClassNameHint("blah")
          val a = reg registerS addClassNames("btn", "btn-default")
          val composed = Dsl.style(a, color.red)
          assertEq(composed.addClassNames.map(_.value), Vector("btn", "btn-default"))
        }

        "singleClass" - {
          val reg = new mutable.Register(mutable.Register.NameGen.numbered(), mutable.Register.MacroName.Use, mutable.Register.ErrorHandler.silent)
          implicit val cnh: ClassNameHint = ClassNameHint("blah")
          val a = reg registerS addClassName("btn")
          val composed = Dsl.style(a, color.red)
          assertEq(composed.addClassNames.map(_.value), Vector("btn"))
        }
      }
    }

    "styleS" - {
      "dataSorted" - {
        import Dsl._
        val & = Cond.empty

        "empty" - {
          assertEq(StyleS.empty.dataSorted.map(_._1), Vector.empty)
        }

        "single" - {
          val single = style(color.red)
          assertEq(single.dataSorted.map(_._1), Vector(Cond.empty))
        }

        "multi" - {
          val multi = style(
            &.nthChild("10")(color.red),
            &.nthChild("2")(color.blue),
            &.hover(color.green),
            color.black
          )
          val sortedConds = multi.dataSorted.map(_._1)
          assertEq(sortedConds, multi.data.keys.toVector.sorted(Cond.ordering))
          assertEq(sortedConds, Vector(Cond.empty, Cond.empty.hover, Cond.empty.nthChild("2"), Cond.empty.nthChild("10")))
        }
      }
    }
  }
}
