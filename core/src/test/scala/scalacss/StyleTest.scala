package scalacss

import shapeless.test._
import utest._

object StyleTest extends TestSuite with TestUtil with Defaults {

  val s  = StyleS.data(Map.empty)
  val sf = StyleF[Int](_ => null)(null)

  val af = sf named 'a
  val a  = s named 'a
  val b  = s named 'b
  val c  = s named 'c
  val d  = s named 'd
  val e  = s named 'e
  val x  = s named 'x
  val y  = s named 'y
  val z  = s named 'z

  val ab   = a :*: b
  val abc  = a :*: b :*: c
  val abcd = a :*: b :*: c :*: d
  val bcd  = b :*: c :*: d
  val cd   = c :*: d
  val ad   = a :*: d
  val bc   = b :*: c
  val xyz  = x :*: y :*: z

  override val tests = TestSuite {
    'styleC {
      'uniqueNamesCons {
        illTyped("a :*: ab")
        illTyped("a :*: abc")
        illTyped("a :*: abcd")

        illTyped("b :*: ab")
        illTyped("b :*: abc")
        illTyped("b :*: abcd")

        c :*: ab
        illTyped("c :*: abc")
        illTyped("c :*: abcd")

        d :*: ab
        d :*: abc
        illTyped("d :*: abcd")

        e :*: ab
        e :*: abc
        e :*: abcd

        illTyped("af :*: ab")
        illTyped("af :*: abc")
        illTyped("af :*: abcd")
      }

      'uniqueNamesAppend {
        val ab_cd = ab :*: cd
        sameTyped(abcd)(ab_cd)
        illTyped("abc  :*: cd")
        illTyped("abcd :*: cd")
        illTyped("bcd  :*: cd")
        illTyped("cd   :*: cd")
        illTyped("ad   :*: cd")

        cd :*: ab
        illTyped("sameTyped(abcd)(cd :*: ab)")
        illTyped("cd :*: abc ")
        illTyped("cd :*: abcd")
        illTyped("cd :*: bcd ")
        illTyped("cd :*: ad  ")

        illTyped("ab   :*: ab")
        illTyped("abc  :*: ab")
        illTyped("abcd :*: ab")
        illTyped("bcd  :*: ab")
        illTyped("ad   :*: ab")

        illTyped("ab :*: abc ")
        illTyped("ab :*: abcd")
        illTyped("ab :*: bcd ")
        illTyped("ab :*: ab  ")
        illTyped("ab :*: ad  ")

        ad :*: bc
        bc :*: ad

        val xa_b = (xyz :*: a) :*: bcd
        val x_ab = xyz :*: (a :*: bcd)
        val xab  = xyz :*: abcd
        sameTyped(xab)(xa_b)
        sameTyped(xab)(x_ab)
      }
    }

    'styleA {
      'addOperator {
        import Dsl._

        val s1 = StyleA(ClassName("s1"), Vector("c1", "c2", "c3").map(ClassName), style(backgroundColor.blue))
        val s2 = StyleA(ClassName("s2"), Vector("c4", "c5").map(ClassName), style(color.red))

        val r1 = s1 + s2
        assertEq(r1.className, s1.className)
        assertEq(r1.addClassNames.size, 6)
        assert(r1.addClassNames.contains(s2.className))
        for (cn <- s1.addClassNames ++ s2.addClassNames)
          assert(r1.addClassNames.contains(cn))
        val r1Data = r1.style.data(Cond.empty)
        assert(r1Data.get(backgroundColor).get.head == backgroundColor.blue.value)
        assert(r1Data.get(color).get.head == color.red.value)

        val r2 = s2 + s1
        assertEq(r2.className, s2.className)
        assertEq(r2.addClassNames.size, 6)
        assert(r2.addClassNames.contains(s1.className))
        for (cn <- s1.addClassNames ++ s2.addClassNames)
          assert(r2.addClassNames.contains(cn))
        val r2Data = r2.style.data(Cond.empty)
        assert(r2Data.get(backgroundColor).get.head == backgroundColor.blue.value)
        assert(r2Data.get(color).get.head == color.red.value)
      }
    }
  }
}
