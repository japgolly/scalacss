package scalacss.internal.mutable

import japgolly.microlibs.testutil.TestUtil._
import scalacss.internal.ClassNameHint
import scalacss.internal.mutable.Register.NameGen
import utest._

object NameGenTest extends TestSuite {

  override def tests = Tests {
    "nameGenUniqueAndAscii" - {
      val classesAmount = 1000
      val classNames = (1 to classesAmount).map(_ => NameGen.short().next(ClassNameHint("placeholder"))._1.value)
      val uniqueClassNames = classNames.toSet

      assertEq(uniqueClassNames.size, classesAmount)
      assertEq(uniqueClassNames.count(_.matches("^[a-z0-9_]+$")), classesAmount)

      NameGen.resetNextShortPrefix()
    }

    "shortValidation" - {
      "rejectsEmptyPrefix" - {
        intercept[IllegalArgumentException](NameGen.short(prefix = "", prefix2 = ""))
      }

      "rejectsInvalidStartChar" - {
        val e1 = intercept[IllegalArgumentException](NameGen.short(prefix = "1abc", prefix2 = ""))
        assertContains(e1.getMessage, "1abc")
        val e2 = intercept[IllegalArgumentException](NameGen.short(prefix = "", prefix2 = "1abc"))
        assertContains(e2.getMessage, "1abc")
        val e3 = intercept[IllegalArgumentException](NameGen.short(prefix = "-a", prefix2 = ""))
        assertContains(e3.getMessage, "-a")
      }

      "rejectsWhitespaceAndIllegalChars" - {
        val e1 = intercept[IllegalArgumentException](NameGen.short(prefix = "a b", prefix2 = ""))
        assertContains(e1.getMessage, "a b")
        val e2 = intercept[IllegalArgumentException](NameGen.short(prefix = "a", prefix2 = "b c"))
        assertContains(e2.getMessage, "ab c")
        val e3 = intercept[IllegalArgumentException](NameGen.short(prefix = "a?b", prefix2 = ""))
        assertContains(e3.getMessage, "a?b")
      }

      "acceptsValidPrefixes" - {
        assertEq(NameGen.short(prefix = "a_", prefix2 = "1-b").next(ClassNameHint("x"))._1.value, "a_1-b0")
        assertEq(NameGen.short8.next(ClassNameHint("x"))._1.value.nonEmpty, true)
        NameGen.resetNextShortPrefix()
      }
    }

    "alphabetValidation" - {
      "emptyAlphabet" - {
        intercept[IllegalArgumentException](new NameGen.Alphabet(Array.empty, (_, c) => s"_$c"))
      }

      "negativeN" - {
        intercept[IllegalArgumentException](new NameGen.Alphabet(NameGen.nmchar7, (_, c) => s"_$c", n = -1))
      }

      "negativeCodeIndex" - {
        val alpha = new NameGen.Alphabet(NameGen.nmchar7, (_, c) => s"_$c")
        intercept[IllegalArgumentException](alpha.code(Nil, -1))
      }
    }

    "toAlphabet" - {
      assertEq(NameGen.toAlphabet(0), "a")
      assertEq(NameGen.toAlphabet(1), "b")
      assertEq(NameGen.toAlphabet(25), "z")
      assertEq(NameGen.toAlphabet(26), "aa")
      assertEq(NameGen.toAlphabet(27), "ab")
      assertEq(NameGen.toAlphabet(701), "zz")
      assertEq(NameGen.toAlphabet(702), "aaa")
      intercept[IllegalArgumentException](NameGen.toAlphabet(-1))
    }

    "numberedCssIdentStart" - {
      assertEq(NameGen.numbered().next(ClassNameHint(""))._1.value, "_-0001")
      assertEq(NameGen.numbered().next(ClassNameHint("123"))._1.value, "_123-0001")
      assertEq(NameGen.numbered("1-%s-%d").next(ClassNameHint("a"))._1.value, "_1-a-1")
      assertEq(NameGen.numbered("-%s-%d").next(ClassNameHint("a"))._1.value, "-a-1")
      assertEq(NameGen.numbered("%s").next(ClassNameHint("-"))._1.value, "_-")
      assertEq(NameGen.numbered().next(ClassNameHint("SS"))._1.value, "SS-0001")
    }
  }
}
