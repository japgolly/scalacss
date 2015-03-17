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

  lazy val intrinsicWidthTransforms: Transform =
    Transform.values(intrinsicWidth)(
      L.fill_available, L.max_content, L.min_content, L.fit_content, L.contain_floats)

  lazy val backgroundImageTransforms: Transform = {
    val a = Transform.valueKeywords(gradients)("linear-gradient", "radial-gradient")
    val b = Transform.valueKeywords(repeatingGradients)("repeating-linear-gradient", "repeating-radial-gradient")
    a * b
  }

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
    val p = Prefix.values.list.map(_.prefix).mkString(".*(?:", "|", ").*").r.pattern
    in => p.matcher(in).matches
  }

  type PrefixApply = String => Option[Prefix => String]
  object PrefixApply {
    val prepend: PrefixApply =
      in => Some(_.prefix + in)

    def maybePrepend(f: String => Boolean): PrefixApply =
      in => if (f(in)) prepend(in) else None //_ => in

    /**
     * Transforms only certain keywords.
     *
     * This makes sure that they aren't part of another keyword.
     * Eg. `"gradient"` only matches `"gradient"` and not `"linear-gradient"`.
     *
     * Input is expected to be regex-quoted already.
     * Input shouldn't end in "(". A different method is required for functions.
     */
    def keywords(w1: String, wn: String*): PrefixApply = {
      // JS (and thus Scala.JS) doesn't support negative look-behind :(
      // JS (and thus Scala.JS) doesn't support Pattern.quote
      val ws = w1 +: wn
      val r1 = ws.mkString("(?:^|.*[^a-zA-Z0-9_-])(?:", "|", ")(?![a-zA-Z0-9_-]).*").r.pattern
      val r2 = ws.mkString("(^|[^a-zA-Z0-9_-])(", "|", ")(?![a-zA-Z0-9_-])").r
      in =>
        if (r1.matcher(in).matches)
          Some(p => r2.replaceAllIn(in, m => m.group(1) + p.prefix + m.group(2)))
        else
          None
    }
  }

  def runPlan(pp: PrefixPlan, pa: PrefixApply, l: CssKV.Lens, kv: CssKV): Vector[CssKV] = {
    val tgt = l.get(kv)
    @inline def nop = Vector1(kv)
    if (prefixed(tgt))
      nop
    else
      pa(tgt).fold(nop)(apply =>
        pp.map(op =>
          op.fold(kv)(p =>
            l.set(kv)(apply(p)))))
  }

  @inline def prefixKeys(pp: PrefixPlan, pa: PrefixApply, kv: CssKV): Vector[CssKV] =
    runPlan(pp, pa, CssKV.key, kv)

  @inline def prefixValues(pp: PrefixPlan, pa: PrefixApply, kv: CssKV): Vector[CssKV] =
    runPlan(pp, pa, CssKV.value, kv)
}
