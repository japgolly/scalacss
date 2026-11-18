package scalacss.internal

import scala.collection.immutable.SortedSet
import scalacss.internal.CanIUse.Support._
import scalacss.internal.CanIUse._
import scalacss.internal.{Literal => L}

/**
 * Derivations of the raw data in [[CanIUse]].
 */
object CanIUse2 {

  lazy val intrinsicWidthTransforms: Transform =
    Transform.values(intrinsicWidth)(
      L.fillAvailable, L.available, L.maxContent, L.minContent, L.fitContent, L.containFloats)

  lazy val backgroundImageTransforms: Transform = {
    val a = Transform.valueKeywords(gradients)("linear-gradient", "radial-gradient")
    val b = Transform.valueKeywords(repeatingGradients)("repeating-linear-gradient", "repeating-radial-gradient")
    val c = Transform.valueKeywords(imageSet)("image-set")
    a * b * c
  }

  val needsPrefix: Support => Boolean = {
    case Unsupported | Full | Partial => false
    case FullX | PartialX             => true
    case Unknown                      => false
  }

  val supportsWithoutPrefix: Support => Boolean = {
    case Full | Partial => true
    case _              => false
  }

  private[this] val emptyPrefixSet: SortedSet[Prefix] =
    SortedSet.empty[Prefix]

  def agentPrefixes: Agent => SortedSet[Prefix] =
    a => emptyPrefixSet + a.prefix ++ a.prefixExceptions.values

  def subjectPrefixes(s: Subject): SortedSet[Prefix] =
    s.iterator
      .filter(_._2 exists needsPrefix)
      .map(ad => agentPrefixes(ad._1))
      .foldLeft(emptyPrefixSet)(_ ++ _)

  type PrefixPlan = Vector[Option[Prefix]]

  private[this] val someMoz    = Some(Prefix.moz)
  private[this] val someMs     = Some(Prefix.ms)
  private[this] val someO      = Some(Prefix.o)
  private[this] val someWebkit = Some(Prefix.webkit)

  private def prefixBit(p: Prefix): Int = p match {
    case Prefix.moz    => 1
    case Prefix.ms     => 2
    case Prefix.o      => 4
    case Prefix.webkit => 8
  }

  private def agentPrefixMask(a: Agent): Int = {
    var m = prefixBit(a.prefix)
    if (a.prefixExceptions.nonEmpty) {
      val it = a.prefixExceptions.valuesIterator
      while (it.hasNext)
        m |= prefixBit(it.next())
    }
    m
  }

  private[internal] def prefixPlanForAgents(s: Subject, agents: Set[Agent]): PrefixPlan = {
    val checkAgents = (agents ne null) && (agents ne Env.empty.agents)
    var mask = 0
    var noPrefix = false
    val it = s.iterator
    while (it.hasNext) {
      val (agent, supports) = it.next()
      if (!checkAgents || agents.contains(agent)) {
        var agentNeedPrefix = false
        val sit = supports.iterator
        while (sit.hasNext) {
          val sup = sit.next()
          if (!agentNeedPrefix && needsPrefix(sup))
            agentNeedPrefix = true
          if (!noPrefix && supportsWithoutPrefix(sup))
            noPrefix = true
        }
        if (agentNeedPrefix)
          mask |= agentPrefixMask(agent)
      }
    }

    val b = Vector.newBuilder[Option[Prefix]]
    if ((mask & 1) != 0) b += someMoz
    if ((mask & 2) != 0) b += someMs
    if ((mask & 4) != 0) b += someO
    if ((mask & 8) != 0) b += someWebkit
    // prefixes can be empty if we have only Supported as the sole Support type.
    // Rather than completely omit CSS the user has declared, we emit the CSS once without any prefixes.
    if (noPrefix || mask == 0) b += None
    b.result()
  }

  val prefixPlan: Subject => PrefixPlan =
    prefixPlanForAgents(_, null)

  val prefixed: String => Boolean = {
    val prefixes = Prefix.values.iterator.map(_.prefix).mkString("|")
    val p = s"""(?:^|[\\s\\S]*[^a-zA-Z0-9_-])(?:$prefixes)[a-zA-Z][\\s\\S]*""".r.pattern
    val strip = """url\([^)]*\)|'[^']*'|"[^"]*"""".r
    in => {
      val unquoted = strip.replaceAllIn(in, "")
      p.matcher(unquoted).matches
    }
  }

  type PrefixApply = String => Option[Prefix => String]
  object PrefixApply {
    val prepend: PrefixApply =
      in => Some(_.prefix + in)

    def maybePrepend(f: String => Boolean): PrefixApply =
      in => {
        val v = if (in.endsWith(_important)) in.stripSuffix(_important) else in
        if (f(v) || f(in)) prepend(in) else None
      }

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
      val r1 = ws.mkString("(?:^|[\\s\\S]*[^a-zA-Z0-9_-])(?:", "|", ")(?![a-zA-Z0-9_-])[\\s\\S]*").r.pattern
      val r2 = ws.mkString("(^|[^a-zA-Z0-9_-])(", "|", ")(?![a-zA-Z0-9_-])").r
      in =>
        if (r1.matcher(in).matches)
          Some(p => r2.replaceAllIn(in, m => m.group(1) + p.prefix + m.group(2)))
        else
          None
    }
  }

  private[internal] def runPlanLazy(pp: => PrefixPlan, pa: PrefixApply, l: CssKV.Lens, kv: CssKV): Vector[CssKV] = {
    val tgt = l.get(kv)
    def nop = Vector1(kv)
    if (prefixed(tgt))
      nop
    else
      pa(tgt).fold(nop) { apply =>
        pp.map(op =>
          op.fold(kv)(p =>
            l.set(kv, apply(p))))
      }
  }

  def prefixesForPlatform(p: Env.Platform[Option]): SortedSet[Prefix] =
    agentsForPlatform(p).reduceMapLeft1(agentPrefixes)(_ ++ _)

  def agentsForPlatform(p: Env.Platform[Option]): NonEmptyVector[Agent] = {
    import Agent._
    val isAndroid = p.os.exists(_.family.exists(_.toLowerCase.contains("android")))
    val isIOS     = p.os.exists(_.family.exists(_.equalsIgnoreCase("iOS")))
    p.name.fold(Agent.values)({
      case n if n startsWith "Chrome"                       => NonEmptyVector(if (isAndroid) AndroidChrome else Chrome)
      case n if n startsWith "Firefox"                      => NonEmptyVector(if (isAndroid) AndroidFirefox else Firefox)
      case "IE Mobile"                                      => NonEmptyVector(IEMobile)
      case n if n startsWith "IE"                           => NonEmptyVector(IE)
      case "Opera Mini"                                     => NonEmptyVector(OperaMini)
      case "Opera Mobile"                                   => NonEmptyVector(OperaMobile)
      case n if n startsWith "Opera"                        => NonEmptyVector(Opera)
      case n if n endsWith "Edge"                           => NonEmptyVector(Edge)
      case n if n startsWith "Samsung"                      => NonEmptyVector(Samsung)
      case n if n.toLowerCase startsWith "blackberry"       => NonEmptyVector(BlackberryBrowser)
      case "Mobile Safari" | "Safari Mobile" | "iOS Safari" => NonEmptyVector(IOSSafari)
      case "Safari"                                         => NonEmptyVector(if (isIOS) IOSSafari else Safari)
      case "UC Browser"                                     => NonEmptyVector(AndroidUC)
      case "Android Browser"                                => NonEmptyVector(AndroidBrowser)
      case _ if isAndroid                                   => NonEmptyVector(AndroidBrowser, AndroidUC)
      case _                                                => Agent.values
    })
  }

  def filteredPrefixPlan(whitelist: Set[Prefix], pp: PrefixPlan): PrefixPlan =
    pp.filter(_ forall whitelist.contains)
}
