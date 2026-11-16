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
