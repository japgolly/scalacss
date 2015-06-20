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
     
      'elementClassOrder {
        assertEq(Before.&(Hover).cssValue, ":hover::before")
        assertEq(Hover.&(Before).cssValue, ":hover::before")
      }
    }
  }
}
