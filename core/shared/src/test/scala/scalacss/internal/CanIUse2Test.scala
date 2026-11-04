package scalacss.internal

import japgolly.microlibs.testutil.TestUtil._
import scala.collection.immutable.SortedSet
import scalacss.internal.CanIUse.{Agent, Prefix}
import scalacss.internal.CanIUse2.{agentsForPlatform, prefixesForPlatform}
import utest._

object CanIUse2Test extends TestSuite {

  def p(name: String, os: Option[String] = None): Env.Platform[Option] = {
    val o = os.map(o => Env.OS[Option](None, Some(o), None))
    Env.Platform[Option](Some(name), None, None, None, None, None, None, o)
  }

  val android = Some("Android")

  override def tests = Tests {
    "agentsForPlatform" - {
      "android" - {
        "firefoxMobile" - {
          assertEq(agentsForPlatform(p("Firefox Mobile", android)), NonEmptyVector(Agent.AndroidFirefox))
          assertEq(prefixesForPlatform(p("Firefox Mobile", android)), SortedSet[Prefix](Prefix.moz))
        }
        "chromeMobile" - {
          assertEq(agentsForPlatform(p("Chrome Mobile", android)), NonEmptyVector(Agent.AndroidChrome))
          assertEq(prefixesForPlatform(p("Chrome Mobile", android)), SortedSet[Prefix](Prefix.webkit))
        }
      }

      "nonAndroid" - {
        "chromeMobile"  - assertEq(agentsForPlatform(p("Chrome Mobile")),  NonEmptyVector(Agent.Chrome))
        "firefoxMobile" - assertEq(agentsForPlatform(p("Firefox Mobile")), NonEmptyVector(Agent.Firefox))
        "ieMobile"      - {
          assertEq(agentsForPlatform(p("IE Mobile")), NonEmptyVector(Agent.IEMobile))
          assertEq(prefixesForPlatform(p("IE Mobile")), SortedSet[Prefix](Prefix.ms))
        }
        "operaMini"     - {
          assertEq(agentsForPlatform(p("Opera Mini")), NonEmptyVector(Agent.OperaMini))
          assertEq(prefixesForPlatform(p("Opera Mini")), SortedSet[Prefix](Prefix.o))
        }
        "operaMobile"   - {
          assertEq(agentsForPlatform(p("Opera Mobile")), NonEmptyVector(Agent.OperaMobile))
          assertEq(prefixesForPlatform(p("Opera Mobile")), SortedSet[Prefix](Prefix.o, Prefix.webkit))
        }
        "opera"         - {
          assertEq(agentsForPlatform(p("Opera")), NonEmptyVector(Agent.Opera))
        }
      }
    }
  }
}
