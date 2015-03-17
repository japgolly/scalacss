package japgolly.scalacss

import scalaz.{Memo, Semigroup}
import scalaz.std.map._
import scalaz.syntax.semigroup._
import japgolly.scalacss.{Literal => L}
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

  lazy val intrinsicWidthTransforms =
    Transform.values(intrinsicWidth)(
      L.fill_available, L.max_content, L.min_content, L.fit_content, L.contain_floats)

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
    Memo.mutableHashMapMemo {s =>
      val ps = subjectPrefixes(s).toVector.map(Some.apply)
      val np = s.exists(_._2.exists(t => !needsPrefix(t._1)))
      if (np) ps :+ None else ps
    }

  val prefixed: String => Boolean = {
    val p = Prefix.values.list.map(_.value).mkString("^-(?:", "|", ")-.*").r.pattern
    in => p.matcher(in).matches
  }

  def prefixStr(p: Prefix, to: String): String =
    s"-${p.value}-$to"

  def runPlan(pp: PrefixPlan, l: CssKV.Lens, kv: CssKV): Vector[CssKV] = {
    val tgt = l.get(kv)
    if (prefixed(tgt))
      Vector1(kv)
    else
      pp.map(op =>
        op.fold(kv)(p =>
          l.set(kv)(prefixStr(p, tgt))))

  }

  @inline def prefixKeys(pp: PrefixPlan, kv: CssKV): Vector[CssKV] =
    runPlan(pp, CssKV.key, kv)

  @inline def prefixValues(pp: PrefixPlan, kv: CssKV): Vector[CssKV] =
    runPlan(pp, CssKV.value, kv)
}
