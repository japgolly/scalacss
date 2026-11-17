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
  val ios     = Some("iOS")

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
        "samsung" - {
          assertEq(agentsForPlatform(p("Samsung Internet", android)), NonEmptyVector(Agent.Samsung))
          assertEq(agentsForPlatform(p("Samsung", android)),          NonEmptyVector(Agent.Samsung))
          assertEq(agentsForPlatform(p("Samsung Browser", android)),  NonEmptyVector(Agent.Samsung))
        }
        "operaMini" - {
          assertEq(agentsForPlatform(p("Opera Mini", android)), NonEmptyVector(Agent.OperaMini))
        }
        "operaMobile" - {
          assertEq(agentsForPlatform(p("Opera Mobile", android)), NonEmptyVector(Agent.OperaMobile))
        }
        "edge" - {
          assertEq(agentsForPlatform(p("Microsoft Edge", android)), NonEmptyVector(Agent.Edge))
        }
        "ucBrowser" - {
          assertEq(agentsForPlatform(p("UC Browser", android)), NonEmptyVector(Agent.AndroidUC))
        }
        "androidBrowser" - {
          assertEq(agentsForPlatform(p("Android Browser", android)), NonEmptyVector(Agent.AndroidBrowser))
        }
        "other" - {
          assertEq(agentsForPlatform(p("Unknown", android)), NonEmptyVector(Agent.AndroidBrowser, Agent.AndroidUC))
        }
      }

      "ios" - {
        "safari" - {
          assertEq(agentsForPlatform(p("Safari", ios)), NonEmptyVector(Agent.IOSSafari))
          assertEq(prefixesForPlatform(p("Safari", ios)), SortedSet[Prefix](Prefix.webkit))
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
        "edge"          - {
          assertEq(agentsForPlatform(p("Edge")), NonEmptyVector(Agent.Edge))
          assertEq(agentsForPlatform(p("Microsoft Edge")), NonEmptyVector(Agent.Edge))
        }
        "safari"        - {
          assertEq(agentsForPlatform(p("Safari")),              NonEmptyVector(Agent.Safari))
          assertEq(agentsForPlatform(p("Safari", Some("OS X"))), NonEmptyVector(Agent.Safari))
          assertEq(agentsForPlatform(p("Mobile Safari")),       NonEmptyVector(Agent.IOSSafari))
          assertEq(agentsForPlatform(p("Safari Mobile")),       NonEmptyVector(Agent.IOSSafari))
          assertEq(agentsForPlatform(p("iOS Safari")),          NonEmptyVector(Agent.IOSSafari))
        }
        "samsung"       - {
          assertEq(agentsForPlatform(p("Samsung Internet")), NonEmptyVector(Agent.Samsung))
          assertEq(agentsForPlatform(p("Samsung")),          NonEmptyVector(Agent.Samsung))
          assertEq(agentsForPlatform(p("Samsung Browser")),  NonEmptyVector(Agent.Samsung))
        }
        "blackberry"    - {
          assertEq(agentsForPlatform(p("BlackBerry")),         NonEmptyVector(Agent.BlackberryBrowser))
          assertEq(agentsForPlatform(p("Blackberry")),         NonEmptyVector(Agent.BlackberryBrowser))
          assertEq(agentsForPlatform(p("BlackBerry Browser")), NonEmptyVector(Agent.BlackberryBrowser))
          assertEq(agentsForPlatform(p("BlackberryBrowser")),  NonEmptyVector(Agent.BlackberryBrowser))
        }
        "androidInUserAgentOrDesc" - {
          val spoof = Env.Platform[Option](Some("IE Mobile"), None, None, Some("Android"), None, None, Some("like Android 4.0"), None)
          assertEq(agentsForPlatform(spoof), NonEmptyVector(Agent.IEMobile))
        }
      }
    }

    "prefixPlanForAgents" - {
      def expectedPlan(s: CanIUse.Subject, agents: Set[Agent]): CanIUse2.PrefixPlan = {
        val sub = s.filter(kv => agents.contains(kv._1))
        val prefixes = CanIUse2.subjectPrefixes(sub).toVector.map(Some.apply)
        val noPrefix = sub.exists(_._2 exists CanIUse2.supportsWithoutPrefix)
        // prefixes can be empty if we have only Supported as the sole Support type.
        // Rather than completely omit CSS the user has declared, we emit the CSS once without any prefixes.
        if (noPrefix || prefixes.isEmpty) prefixes :+ None else prefixes
      }

      val subjects = Vector(
        CanIUse.borderRadius,
        CanIUse.reflections,
        CanIUse.flexbox,
        CanIUse.grid,
        CanIUse.gradients,
        CanIUse.transforms,
        CanIUse.userSelectNone,
      )

      "agents" - {
        val agentSets = Vector(
          Env.empty.agents,
          Set(Agent.Chrome),
          Set(Agent.Firefox),
          Set(Agent.IE),
          Set(Agent.OperaMobile),
          Set(Agent.AndroidUC),
          Set(Agent.Chrome, Agent.Firefox),
        )

        for {
          s <- subjects
          a <- agentSets
        } assertEq(CanIUse2.prefixPlanForAgents(s, a), expectedPlan(s, a))
      }

      "emptyEnv" - {
        assertEq(Env.empty.agents, CanIUse.Agent.values.whole.toSet)
        for (s <- subjects)
          assertEq(CanIUse2.prefixPlan(s), expectedPlan(s, Env.empty.agents))
      }
    }
  }
}
