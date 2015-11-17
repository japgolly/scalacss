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
        assertEq(Before.&(Hover).nthChild("2n+1").cssValue, ":hover:nth-child(2n+1)::before")
        assertEq(Before.&(Hover).nthChild("n+4").cssValue, ":hover:nth-child(n+4)::before")
        assertEq(Before.&(Hover).nthChild("n").cssValue, ":hover:nth-child(n)::before")
        assertEq(Before.&(Hover).nthChild("4").cssValue, ":hover:nth-child(4)::before")
        assertEq(Before.&(Hover).nthChild("odd").cssValue, ":hover:nth-child(odd)::before")
        assertEq(Before.&(Hover).nthChild("even").cssValue, ":hover:nth-child(even)::before")
        assertEq(Hover.&(Before).cssValue, ":hover::before")
      }

      'brokenNthChildQueries {
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2n+k").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2x+1").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2n+-1").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("--2n+1").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2n+1+3").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2-n+1").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2*n+1").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("2n*1").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("n/3").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("1+4").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("nn").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("-").cssValue }
        intercept[IllegalArgumentException] { Before.&(Hover).nthChild("+").cssValue }
      }
    }
  }
}
