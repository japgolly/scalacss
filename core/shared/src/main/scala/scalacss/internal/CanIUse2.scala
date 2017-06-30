package scalacss.internal

import scalacss.internal.{Literal => L}
import CanIUse._
import Support._

/**
 * Derivations of the raw data in [[CanIUse]].
 */
object CanIUse2 {

  lazy val intrinsicWidthTransforms: Transform =
    Transform.values(intrinsicWidth)(
      L.fillAvailable, L.maxContent, L.minContent, L.fitContent, L.containFloats)

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
    s.iterator
      .filter(_._2 exists needsPrefix)
      .map(ad => agentPrefixes(ad._1))
      .foldLeft(Set.empty[Prefix])(_ ++ _)

  type PrefixPlan = Vector[Option[Prefix]]
  val prefixPlan: Subject => PrefixPlan =
    memo { s =>
      val ps = subjectPrefixes(s).toVector.map(Some.apply)
      val np = s.exists(_._2.exists(d => !needsPrefix(d)))
      if (np) ps :+ None else ps
    }

  val prefixed: String => Boolean = {
    val p = Prefix.values.iterator.map(_.prefix).mkString(".*(?:", "|", ").*").r.pattern
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
    def nop = Vector1(kv)
    if (prefixed(tgt))
      nop
    else
      pa(tgt).fold(nop)(apply =>
        pp.map(op =>
          op.fold(kv)(p =>
            l.set(kv, apply(p)))))
  }

  def prefixKeys(pp: PrefixPlan, pa: PrefixApply, kv: CssKV): Vector[CssKV] =
    runPlan(pp, pa, CssKV.key, kv)

  def prefixValues(pp: PrefixPlan, pa: PrefixApply, kv: CssKV): Vector[CssKV] =
    runPlan(pp, pa, CssKV.value, kv)

  def prefixesForPlatform(p: Env.Platform[Option]): Set[Prefix] =
    agentsForPlatform(p).reduceMapLeft1(agentPrefixes)(_ ++ _)

  def agentsForPlatform(p: Env.Platform[Option]): NonEmptyVector[Agent] = {
    import Agent._
    def dunno = Agent.values
    if (p.toString.toLowerCase contains "android")
      p.name.fold(dunno)({
        case "Chrome"  => NonEmptyVector(AndroidChrome)
        case "Firefox" => NonEmptyVector(AndroidFirefox)
        case _         => NonEmptyVector(AndroidBrowser, AndroidUC)
      })
    else
      p.name.fold(dunno)({
        case "Chrome"               => NonEmptyVector(Chrome)
        case "Firefox"              => NonEmptyVector(Firefox)
        case "IE"                   => NonEmptyVector(IE, IEMobile)
        case "Opera"                => NonEmptyVector(Opera, OperaMini, OperaMobile)
        case "Safari"               => NonEmptyVector(IOSSafari, Safari)
        case n if n endsWith "Edge" => NonEmptyVector(Edge)
        case _                      => dunno
      })
  }

  def filteredPrefixPlan(whitelist: Set[Prefix], pp: PrefixPlan): PrefixPlan =
    pp.filter(_ forall whitelist.contains)
}
