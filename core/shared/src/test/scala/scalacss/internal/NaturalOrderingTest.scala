package scalacss.internal

import japgolly.microlibs.testutil.TestUtil._
import utest._

object NaturalOrderingTest extends TestSuite {

  override def tests = Tests {
    val O = NaturalOrdering

    "identical" - {
      assertEq(O.compare("", ""), 0)
      assertEq(O.compare("a", "a"), 0)
      assertEq(O.compare("abc", "abc"), 0)
      assertEq(O.compare("123", "123"), 0)
      assertEq(O.compare("file10.txt", "file10.txt"), 0)
    }

    "lexicographical" - {
      assert(O.compare("a", "b") < 0)
      assert(O.compare("b", "a") > 0)
      assert(O.compare("abc", "abd") < 0)
      assert(O.compare("abd", "abc") > 0)
      assert(O.compare("car", "cars") < 0)
      assert(O.compare("cars", "car") > 0)
      assert(O.compare("", "a") < 0)
      assert(O.compare("a", "") > 0)
    }

    "naturalNumberSorting" - {
      assert(O.compare("2", "10") < 0)
      assert(O.compare("10", "2") > 0)
      assert(O.compare("file2.txt", "file10.txt") < 0)
      assert(O.compare("file10.txt", "file2.txt") > 0)
      assert(O.compare("rfc1.txt", "rfc822.txt") < 0)
      assert(O.compare("rfc822.txt", "rfc2086.txt") < 0)
      assert(O.compare("a10b", "a2b") > 0)
      assert(O.compare("a2b", "a10b") < 0)
      assert(O.compare("v1.2.3", "v1.2.10") < 0)
      assert(O.compare("v1.2.10", "v1.2.3") > 0)
    }

    "mediaQueries" - {
      val mq = Seq(
        "@media (min-width: 576px)",
        "@media (min-width: 768px)",
        "@media (min-width: 992px)",
        "@media (min-width: 1200px)"
      )
      val sorted = mq.sorted(NaturalOrdering)
      assertEq(sorted, mq)
    }

    "leadingZeros" - {
      assert(O.compare("1", "01") < 0)
      assert(O.compare("01", "1") > 0)
      assert(O.compare("01", "001") < 0)
      assert(O.compare("001", "01") > 0)
      assert(O.compare("1a", "01a") < 0)
      assert(O.compare("01a", "1a") > 0)
      assert(O.compare("10", "010") < 0)
      assert(O.compare("010", "10") > 0)
    }

    "zerosBeforeLetters" - {
      assert(O.compare("0a", "a") < 0)
      assert(O.compare("a", "0a") > 0)
      assert(O.compare("f0o", "fo") < 0)
      assert(O.compare("fo", "f0o") > 0)
    }

    "spaces" - {
      assert(O.compare("a b", "ab") < 0)
      assert(O.compare("ab", "a b") > 0)
      assert(O.compare(" a", "a") < 0)
      assert(O.compare("a", " a") > 0)
      assert(O.compare(".a .b", ".a.b") < 0)
      assert(O.compare(".a.b", ".a .b") > 0)
    }

    "condOrdering" - {
      "attributesWithSpaces" - {
        val c1 = Cond(Some(Pseudo.Attr("custom-attr", "a b")), ArraySeq.empty)
        val c2 = Cond(Some(Pseudo.Attr("custom-attr", "ab")), ArraySeq.empty)
        assert(Cond.ordering.compare(c1, c2) < 0)
        assert(Cond.ordering.compare(c2, c1) > 0)

        val map = collection.immutable.SortedMap(c1 -> 1, c2 -> 2)
        assertEq(map.size, 2)
      }

      "nthChildLeadingZeros" - {
        val c1 = Cond(Some(Pseudo.NthChild("1")), ArraySeq.empty)
        val c2 = Cond(Some(Pseudo.NthChild("01")), ArraySeq.empty)
        assert(Cond.ordering.compare(c1, c2) < 0)
        assert(Cond.ordering.compare(c2, c1) > 0)

        val map = collection.immutable.SortedMap(c1 -> 1, c2 -> 2)
        assertEq(map.size, 2)
      }
    }

    "orderingLaws" - {
      "reflexivity" - {
        val samples = Seq("", "a", "1", "01", "file10.txt", "a b", ".a .b", ":hover")
        for (s <- samples)
          assertEq(O.compare(s, s), 0)
      }

      "antisymmetry" - {
        val pairs = Seq(
          ("a", "b"),
          ("2", "10"),
          ("file2", "file10"),
          ("1", "01"),
          ("0a", "a"),
          ("a b", "ab")
        )
        for ((a, b) <- pairs) {
          val ab = O.compare(a, b)
          val ba = O.compare(b, a)
          assert((ab < 0 && ba > 0) || (ab > 0 && ba < 0))
        }
      }

      "transitivity" - {
        val triples = Seq(
          ("1", "2", "10"),
          ("file1", "file2", "file10"),
          ("0", "0z", "a"),
          ("0", "1", "2")
        )
        for ((a, b, c) <- triples) {
          assert(O.compare(a, b) < 0)
          assert(O.compare(b, c) < 0)
          assert(O.compare(a, c) < 0)
        }
      }
    }
  }
}
