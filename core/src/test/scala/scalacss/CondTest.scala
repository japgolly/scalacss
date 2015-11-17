package scalacss

import utest._
import TestUtil._

object CondTest extends TestSuite {

  override val tests = TestSuite {
    'pseudo {
      import Pseudo._
      'not {
        assertEq(Not("div").cssValue, ":not(div)")
        assertEq(Not(Link).cssValue, ":not(:link)")
      }

      'attrSelectors {
        assertEq(AttrExists("custom-attr").cssValue, "[custom-attr]")
        assertEq(Attr("custom-attr", "bla").cssValue, "[custom-attr=\"bla\"]")
        assertEq(AttrContains("custom-attr", "bla").cssValue, "[custom-attr~=\"bla\"]")
        assertEq(AttrStartsWith("custom-attr", "bla").cssValue, "[custom-attr|=\"bla\"]")
        assertEq(AttrEndsWith("custom-attr", "bla").cssValue, "[custom-attr$=\"bla\"]")
      }
     
      'elementClassOrder {
        assertEq(Before.&(Hover).attrExists("custom-attr").cssValue, ":hover::before[custom-attr]")
        assertEq(Hover.attr("custom-attr", "bla").&(Before).cssValue, ":hover::before[custom-attr=\"bla\"]")
        assertEq(AttrEndsWith("custom-attr", "bla").&(Hover).&(Before).cssValue, ":hover::before[custom-attr$=\"bla\"]")
      }
    }
  }
}
