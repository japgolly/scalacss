package scalacss.internal

import japgolly.microlibs.testutil.TestUtil._
import utest._

object ClassNameTest extends TestSuite {
  import java.lang.AssertionError

  override def tests = Tests {

    "construction" - {
      "valid" - {
        assertEq(ClassName("btn").value, "btn")
        assertEq(ClassName("btn-default").value, "btn-default")
        assertEq(ClassName("a_1").value, "a_1")
      }

      "empty" - {
        intercept[AssertionError] {
          ClassName("")
        }
      }

      "whitespace" - {
        "space" - intercept[AssertionError](ClassName("btn btn-default"))
        "leadingSpace" - intercept[AssertionError](ClassName(" btn"))
        "trailingSpace" - intercept[AssertionError](ClassName("btn "))
        "tab" - intercept[AssertionError](ClassName("btn\tdefault"))
        "newline" - intercept[AssertionError](ClassName("btn\ndefault"))
      }
    }

    "parse" - {
      "single" - {
        val result = ClassName.parse("btn").to(ArraySeq)
        assertEq(result, ArraySeq(ClassName("btn")))
      }

      "multiple" - {
        val result = ClassName.parse("btn btn-default").to(ArraySeq)
        assertEq(result, ArraySeq(ClassName("btn"), ClassName("btn-default")))
      }

      "extraWhitespace" - {
        val result = ClassName.parse("  btn \t btn-default \n btn-sm  ").to(ArraySeq)
        assertEq(result, ArraySeq(ClassName("btn"), ClassName("btn-default"), ClassName("btn-sm")))
      }

      "empty" - {
        assertEq(ClassName.parse("").to(ArraySeq), ArraySeq.empty)
      }

      "whitespaceOnly" - {
        assertEq(ClassName.parse("   \t  \n  ").to(ArraySeq), ArraySeq.empty)
      }
    }

    "dsl" - {
      import Dsl._

      "addClassName" - {
        "withSpaces" - {
          val s = addClassName("btn btn-default")
          assertEq(s.addClassNames, ArraySeq(ClassName("btn"), ClassName("btn-default")))
        }

        "empty" - {
          val s = addClassName("")
          assertEq(s.addClassNames, ArraySeq.empty)
        }

        "whitespaceOnly" - {
          val s = addClassName("   ")
          assertEq(s.addClassNames, ArraySeq.empty)
        }
      }

      "addClassNames" - {
        "multipleArgs" - {
          val s = addClassNames("btn btn-primary", " active ", "", "   ", "extra")
          assertEq(s.addClassNames, ArraySeq(ClassName("btn"), ClassName("btn-primary"), ClassName("active"), ClassName("extra")))
        }
      }
    }
  }
}
