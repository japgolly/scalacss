package scalacss.internal

import japgolly.univeq._
import scala.collection.immutable.SortedSet

/** http://www.w3.org/TR/selectors/#selector-syntax */
sealed abstract class PseudoType(val priority: Short)

object PseudoType {
  case object Attr    extends PseudoType(0)
  case object Class   extends PseudoType(1)
  case object Element extends PseudoType(2)

  implicit def univEq: UnivEq[PseudoType] = UnivEq.derive

  implicit val ordering: Ordering[PseudoType] =
    new Ordering[PseudoType] {
      def compare(x: PseudoType, y: PseudoType): Int =
        x.priority - y.priority
    }
}

/**
 * A pseudo-class is used to define a special state of an element.
 */
sealed abstract class Pseudo extends Pseudo.ChainOps[Pseudo]  {
  import Pseudo._

  def cssValue: String

  // apply() is used by Dsl
  final def modSelector(sel: CssSelector): CssSelector =
    sel + cssValue

  protected final def addPseudo(that: Pseudo): Pseudo =
    (this, that) match {
      case (a: Single, b: Single) =>
        if (a ==* b) a
        else {
          val s = SortedSet.empty[Single] + a + b
          Composite(s.head, s.tail)
        }

      case (a: Single, b: Composite) =>
        if (b contains a) b
        else {
          val s = b.singles + a
          Composite(s.head, s.tail)
        }

      case (a: Composite, b: Single) =>
        if (a contains b) a
        else {
          val s = a.singles + b
          Composite(s.head, s.tail)
        }

      case (a: Composite, b: Composite) =>
        val s = a.singles ++ b.singles
        if (s.size ==* a.singles.size) a
        else if (s.size ==* b.singles.size) b
        else Composite(s.head, s.tail)
    }

  @inline final def &(p: Pseudo): Pseudo =
    addPseudo(p)

  def contains(p: Single): Boolean
}

object Pseudo {

  type PseudoF = ChainOps[Pseudo] => Pseudo

  /**
   * A single [[Pseudo]] instance. Not a composite.
   *
   * ''(Note: [[Not]] may have a nested value but is not a composite.''
   */
  sealed abstract class Single(override val cssValue: String, val pseudoType: PseudoType) extends Pseudo {
    override def contains(p: Single) =
      this ==* p
  }

  final case class Custom(override val cssValue: String, override val pseudoType: PseudoType)
    extends Single(cssValue, pseudoType)

  final case class Composite private[Pseudo](h: Single, t: SortedSet[Single]) extends Pseudo {
    val singles: SortedSet[Single] =
      t + h

    override lazy val cssValue =
      singles.foldLeft("")(_ + _.cssValue)

    override def contains(p: Single) =
      singles contains p

    override def equals(o: Any): Boolean =
      o match {
        case c: Composite => (this eq c) || (singles == c.singles)
        case _            => false
      }

    override def hashCode(): Int =
      singles.hashCode()
  }

  /** Selects the active link. */
  case object Active extends Single(":active", PseudoType.Class)

  /** Selects every checked &lt;input&gt; element. */
  case object Checked extends Single(":checked", PseudoType.Class)

  /** Selects every disabled &lt;input&gt; element. */
  case object Disabled extends Single(":disabled", PseudoType.Class)

  /** Selects every &lt;p&gt; element that has no children. */
  case object Empty extends Single(":empty", PseudoType.Class)

  /** Selects every enabled &lt;input&gt; element. */
  case object Enabled extends Single(":enabled", PseudoType.Class)

  /** Selects every &lt;p&gt; elements that is the first child of its parent. */
  case object FirstChild extends Single(":first-child", PseudoType.Class)

  /** Selects every &lt;p&gt; element that is the first &lt;p&gt; element of its parent. */
  case object FirstOfType extends Single(":first-of-type", PseudoType.Class)

  /** Selects the &lt;input&gt; element that has focus. */
  case object Focus extends Single(":focus", PseudoType.Class)

  /** Selects an element that matches `:focus` and whose focus should be visibly indicated. */
  case object FocusVisible extends Single(":focus-visible", PseudoType.Class)

  /** Selects an element if it or any of its descendants has focus. */
  case object FocusWithin extends Single(":focus-within", PseudoType.Class)

  /** Selects links on mouse over. */
  case object Hover extends Single(":hover", PseudoType.Class)

  /** Selects &lt;input&gt; elements with a value within a specified range. */
  case object InRange extends Single(":in-range", PseudoType.Class)

  /** Selects all &lt;input&gt; elements with an invalid value. */
  case object Invalid extends Single(":invalid", PseudoType.Class)

  private def joinPseudos(s: Pseudo, sn: Seq[Pseudo]): String =
    if (sn.isEmpty) s.cssValue else (s +: sn).iterator.map(_.cssValue).mkString(", ")

  /** Matches any element that matches any of the selectors in the list. */
  final case class Is(selector: String) extends Single(s":is($selector)", PseudoType.Class)
  object Is {
    def apply(s: Pseudo, sn: Pseudo*): Is = Is(joinPseudos(s, sn))
    def apply(f: PseudoF): Is             = Is(f(ChainOps))
  }

  /** Selects every &lt;p&gt; element with a lang attribute value starting with "it". */
  final case class Lang(language: String) extends Single(s":lang($language)", PseudoType.Class)

  /** Selects every &lt;p&gt; elements that is the last child of its parent. */
  case object LastChild extends Single(":last-child", PseudoType.Class)

  /** Selects every &lt;p&gt; element that is the last &lt;p&gt; element of its parent. */
  case object LastOfType extends Single(":last-of-type", PseudoType.Class)

  /** Selects all unvisited link. */
  case object Link extends Single(":link", PseudoType.Class)

  /** Selects every element that is not a &lt;p&gt; element. */
  final case class Not(selector: String) extends Single(s":not($selector)", PseudoType.Class)
  object Not {
    def apply(selector: Pseudo): Not = Not(selector.cssValue)
    def apply(f: PseudoF)      : Not = Not(f(ChainOps))
  }

  type NthQuery = String

  object NthChildBase {
    private val queryPattern = """(?i)^(?:[+-]?\d+|odd|even)$""".r.pattern
    private val fQueryPattern = """(?i)^[+-]?\d*n(?:\s*[+-]\s*\d+)?$""".r.pattern
    private val ofSelectorPattern = """(?i)^(.+?)\s+of\s+(\S.*)$""".r.pattern

    private def validate(cls: String, query: NthQuery): String = {
      val trimmed = query.trim
      val m = ofSelectorPattern.matcher(trimmed)
      val anPlusB =
        if ((cls == "nth-child" || cls == "nth-last-child") && m.matches())
          m.group(1)
        else
          trimmed
      require(
        queryPattern.matcher(anPlusB).matches() || fQueryPattern.matcher(anPlusB).matches(),
        s"Invalid NthQuery: '$query'")
      trimmed
    }
  }

  sealed abstract class NthChildBase(cls: String, query: NthQuery)
    extends Single(s":$cls(${NthChildBase.validate(cls, query)})", PseudoType.Class)

  /** Selects every &lt;p&gt; element that is the second child of its parent. */
  final case class NthChild(query: NthQuery) extends NthChildBase("nth-child", query)

  /** Selects every &lt;p&gt; element that is the second child of its parent, counting from the last child. */
  final case class NthLastChild(query: NthQuery) extends NthChildBase("nth-last-child", query)

  /** Selects every &lt;p&gt; element that is the second &lt;p&gt; element of its parent, counting from the last child. */
  final case class NthLastOfType(query: NthQuery) extends NthChildBase("nth-last-of-type", query)

  /** Selects every &lt;p&gt; element that is the second &lt;p&gt; element of its parent. */
  final case class NthOfType(query: NthQuery) extends NthChildBase("nth-of-type", query)

  /** Selects every &lt;p&gt; element that is the only &lt;p&gt; element of its parent. */
  case object OnlyOfType extends Single(":only-of-type", PseudoType.Class)

  /** Selects every &lt;p&gt; element that is the only child of its parent. */
  case object OnlyChild extends Single(":only-child", PseudoType.Class)

  /** Selects &lt;input&gt; elements with no "required" attribute. */
  case object Optional extends Single(":optional", PseudoType.Class)

  /** Selects &lt;input&gt; elements with a value outside a specified range. */
  case object OutOfRange extends Single(":out-of-range", PseudoType.Class)

  /** Selects &lt;input&gt; elements with a "readonly" attribute specified. */
  case object ReadOnly extends Single(":read-only", PseudoType.Class)

  /** Selects &lt;input&gt; elements with no "readonly" attribute. */
  case object ReadWrite extends Single(":read-write", PseudoType.Class)

  /** Selects &lt;input&gt; elements with a "required" attribute specified. */
  case object Required extends Single(":required", PseudoType.Class)

  /** Selects the document's root element. */
  case object Root extends Single(":root", PseudoType.Class)

  /** Selects the current active #news element (clicked on a URL containing that anchor name). */
  case object Target extends Single(":target", PseudoType.Class)

  /** Selects all &lt;input&gt; elements with a valid value. */
  case object Valid extends Single(":valid", PseudoType.Class)

  /** Selects all visited link. */
  case object Visited extends Single(":visited", PseudoType.Class)

  /** Matches any element that matches any of the selectors in the list with 0 specificity. */
  final case class Where(selector: String) extends Single(s":where($selector)", PseudoType.Class)
  object Where {
    def apply(s: Pseudo, sn: Pseudo*): Where = Where(joinPseudos(s, sn))
    def apply(f: PseudoF): Where             = Where(f(ChainOps))
  }

  /** Insert content after every &lt;p&gt; element. */
  case object After extends Single("::after", PseudoType.Element)

  /** Insert content before every &lt;p&gt; element. */
  case object Before extends Single("::before", PseudoType.Element)

  /** Selects the first letter of every &lt;p&gt; element. */
  case object FirstLetter extends Single("::first-letter", PseudoType.Element)

  /** Selects the first line of every &lt;p&gt; element. */
  case object FirstLine extends Single("::first-line", PseudoType.Element)

  /** Selects placeholder text inside an input or textarea element. */
  case object Placeholder extends Single("::placeholder", PseudoType.Element)

  /** Selects the portion of an element that is selected by a user  . */
  case object Selection extends Single("::selection", PseudoType.Element)

  /** Selects all elements with a name attribute. */
  final case class AttrExists(name: String)
    extends Single(s"[$name]", PseudoType.Attr)

  final case class AttrSelector(name: String, value: String, op: String)
    extends Single(s"[$name$op${Util.quote(value, '"')}]", PseudoType.Attr)

  /** Selects all elements with a name="value". */
  def Attr(name: String, value: String) =
    AttrSelector(name, value, "=")

  /** Selects all elements with a name containing the word value. */
  def AttrContains(name: String, value: String) =
    AttrSelector(name, value, "~=")

  /** Selects all elements with a name starting with value. */
  def AttrStartsWith(name: String, value: String) =
    AttrSelector(name, value, "^=")

  /** Selects all elements with a name ends with value. */
  def AttrEndsWith(name: String, value: String) =
    AttrSelector(name, value, "$=")

  /** Selects all elements with an attribute equal to `value` or starting with `value-`. */
  def AttrDashMatch(name: String, value: String) =
    AttrSelector(name, value, "|=")

  /** Selects all elements with an attribute containing the substring `value`. */
  def AttrSubstring(name: String, value: String) =
    AttrSelector(name, value, "*=")

  implicit def univEqSingle   : UnivEq[Single   ] = UnivEq.derive
  implicit def univEqComposite: UnivEq[Composite] = UnivEq.force
  implicit def univEq         : UnivEq[Pseudo   ] = UnivEq.derive

  implicit val order: Ordering[Single] =
    new Ordering[Single] {
      def compare(x: Single, y: Single): Int = {
        val n = PseudoType.ordering.compare(x.pseudoType, y.pseudoType)
        if (n ==* 0)
          x.cssValue compareTo y.cssValue
        else
          n
      }
    }

  // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

  object ChainOps extends ChainOps[Pseudo] {
    protected def addPseudo(p: Pseudo) = p
  }

  /**
    * Trait providing a nice chaining DSL.
    */
  trait ChainOps[Out] {
    protected def addPseudo(p: Pseudo): Out
    final def active                                     : Out = addPseudo(Active)
    final def after                                      : Out = addPseudo(After)
    final def attr          (name: String, value: String): Out = addPseudo(Attr(name, value))
    final def attrContains  (name: String, value: String): Out = addPseudo(AttrContains(name, value))
    final def attrDashMatch (name: String, value: String): Out = addPseudo(AttrDashMatch(name, value))
    final def attrEndsWith  (name: String, value: String): Out = addPseudo(AttrEndsWith(name, value))
    final def attrExists    (name: String)               : Out = addPseudo(AttrExists(name))
    final def attrStartsWith(name: String, value: String): Out = addPseudo(AttrStartsWith(name, value))
    final def attrSubstring (name: String, value: String): Out = addPseudo(AttrSubstring(name, value))
    final def before                                     : Out = addPseudo(Before)
    final def checked                                    : Out = addPseudo(Checked)
    final def disabled                                   : Out = addPseudo(Disabled)
    final def empty                                      : Out = addPseudo(Empty)
    final def enabled                                    : Out = addPseudo(Enabled)
    final def firstChild                                 : Out = addPseudo(FirstChild)
    final def firstLetter                                : Out = addPseudo(FirstLetter)
    final def firstLine                                  : Out = addPseudo(FirstLine)
    final def firstOfType                                : Out = addPseudo(FirstOfType)
    final def focus                                      : Out = addPseudo(Focus)
    final def focusVisible                               : Out = addPseudo(FocusVisible)
    final def focusWithin                                : Out = addPseudo(FocusWithin)
    final def hover                                      : Out = addPseudo(Hover)
    final def inRange                                    : Out = addPseudo(InRange)
    final def invalid                                    : Out = addPseudo(Invalid)
    final def is            (f: PseudoF)                 : Out = addPseudo(Is(f))
    final def is            (s: Pseudo, sn: Pseudo*)     : Out = addPseudo(Is(s, sn: _*))
    final def is            (selector: String)           : Out = addPseudo(Is(selector))
    final def lang          (language: String)           : Out = addPseudo(Lang(language))
    final def lastChild                                  : Out = addPseudo(LastChild)
    final def lastOfType                                 : Out = addPseudo(LastOfType)
    final def link                                       : Out = addPseudo(Link)
    final def not           (f: PseudoF)                 : Out = addPseudo(Not(f))
    final def not           (selector: Pseudo)           : Out = addPseudo(Not(selector))
    final def not           (selector: String)           : Out = addPseudo(Not(selector))
    final def nthChild      (n: Int)                     : Out = addPseudo(NthChild(n.toString))
    final def nthChild      (q: NthQuery)                : Out = addPseudo(NthChild(q))
    final def nthLastChild  (n: Int)                     : Out = addPseudo(NthLastChild(n.toString))
    final def nthLastChild  (q: NthQuery)                : Out = addPseudo(NthLastChild(q))
    final def nthLastOfType (n: Int)                     : Out = addPseudo(NthLastOfType(n.toString))
    final def nthLastOfType (q: NthQuery)                : Out = addPseudo(NthLastOfType(q))
    final def nthOfType     (n: Int)                     : Out = addPseudo(NthOfType(n.toString))
    final def nthOfType     (q: NthQuery)                : Out = addPseudo(NthOfType(q))
    final def onlyChild                                  : Out = addPseudo(OnlyChild)
    final def onlyOfType                                 : Out = addPseudo(OnlyOfType)
    final def optional                                   : Out = addPseudo(Optional)
    final def outOfRange                                 : Out = addPseudo(OutOfRange)
    final def placeholder                                : Out = addPseudo(Placeholder)
    final def readOnly                                   : Out = addPseudo(ReadOnly)
    final def readWrite                                  : Out = addPseudo(ReadWrite)
    final def required                                   : Out = addPseudo(Required)
    final def root                                       : Out = addPseudo(Root)
    final def selection                                  : Out = addPseudo(Selection)
    final def target                                     : Out = addPseudo(Target)
    final def valid                                      : Out = addPseudo(Valid)
    final def visited                                    : Out = addPseudo(Visited)
    final def where         (f: PseudoF)                 : Out = addPseudo(Where(f))
    final def where         (s: Pseudo, sn: Pseudo*)     : Out = addPseudo(Where(s, sn: _*))
    final def where         (selector: String)           : Out = addPseudo(Where(selector))
  }
}
