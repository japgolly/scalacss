package japgolly.scalacss

import scalaz.{Memo, Semigroup}
import scalaz.std.map._
import scalaz.syntax.semigroup._
import CanIUse._
import Support._

/**
 * Derivations of the raw data in [[CanIUse]].
 */
object CanIUse2 {

  implicit val verStrSemigroup: Semigroup[VerStr] =
    new Semigroup[VerStr] {
      override def append(a: VerStr, b: => VerStr): VerStr = a + "," + b
    }

  val transforms = transforms2d |+| transforms3d

  /*
  sealed trait Browser
  case object Chrome  extends Browser
  case object Firefox extends Browser
  case object IE      extends Browser
  case object Safari  extends Browser
  case object Opera   extends Browser
  case object Other   extends Browser

//  val browserToAgent: Browser => List[Agent] = {
//    case Chrome => Agent.AndroidChrome :: Agent.Chrome :: Nil
//  }
//  val agentToBrowser: Agent => Browser = {
//    case Agent.AndroidBrowser => Other
//    case Agent.AndroidChrome  => Chrome
//    case Agent.AndroidFirefox => Firefox
//  }

  sealed trait Platform
  case object Desktop extends Platform
  case object Mobile  extends Platform
  */

  val needsPrefix: Support => Boolean = {
    case Unsupported | Full | Partial => false
    case FullX | PartialX             => true
    case Unknown                      => false
  }

  def agentPrefixes: Agent => Set[Prefix] =
    a => Set(a.prefix) ++ a.prefixExceptions.values

  def subjectPrefixes(s: Subject): Set[Prefix] =
    s.toStream
      .filter{ case (a, d) => d.keys exists needsPrefix }
      .map(ad => agentPrefixes(ad._1))
      .foldLeft(Set.empty[Prefix])(_ ++ _)

  type PrefixPlan = Vector[Option[Prefix]]
  val prefixPlan: Subject => PrefixPlan =
    Memo.mutableHashMapMemo(
      subjectPrefixes(_).toVector.map(Some.apply) :+ None)

  @inline def applyPrefix(op: Option[Prefix], key: String, value: String): CssKV =
    CssKV(op.fold(key)(p => s"-${p.value}-$key"), value)

  def applyPrefixes(ps: PrefixPlan, key: String, value: String): Vector[CssKV] =
    ps.foldLeft(Vector.empty[CssKV])(
      _ :+ applyPrefix(_, key, value))

}
