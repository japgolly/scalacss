package scalacss

import scalaz.{Equal, ISet, Monoid, Order, Ordering, Semigroup}
import scalaz.std.string.stringInstance
import scalaz.std.tuple._
import scalaz.syntax.equal._

// TODO Rename PseudoXxxxx (selector probably)

/** http://www.w3.org/TR/selectors/#selector-syntax */
sealed trait PseudoType
case object PseudoElement extends PseudoType
case object PseudoClass   extends PseudoType

object PseudoType {
  // Class comes before Element: http://www.w3.org/TR/selectors/#selector-syntax
  implicit val psuedoTypeOrder: Order[PseudoType] = new Order[PseudoType] {
    def order(x: PseudoType, y: PseudoType): Ordering = {
      if (x == y) Ordering.EQ
      else if (x == PseudoClass && y == PseudoElement) Ordering.LT
      else Ordering.GT
    }
  }
}

/**
* A pseudo-class is used to define a special state of an element.
*/
sealed abstract class Pseudo extends Pseudo.ChainOps[Pseudo]  {
  import Pseudo.Composite

  val cssValue: String

  // apply() is used by Dsl
  final def modSelector(sel: CssSelector): CssSelector =
    sel + cssValue

  protected final def addPseudo(that: Pseudo): Pseudo =
    (this, that) match {
      // Avoid
      case (a, b: Pseudo1) if a contains b => a
      case (a: Pseudo1, b) if b contains a => b
      // Append
      case (a: Pseudo1     , b: Pseudo1     ) => Composite(a, ISet singleton b)
      case (p: Pseudo1     , Composite(h, t)) => Composite(p, t insert h)
      case (Composite(h, t), p: Pseudo1     ) => Composite(p, t insert h)
      case (Composite(a, b), Composite(c, d)) => Composite(a, b union d insert c)
    }

  @inline final def &(p: Pseudo): Pseudo =
    addPseudo(p)

  def contains(p: Pseudo1): Boolean
}

/**
* A single [[Pseudo]] instance. Not a composite.
*
* ''(Note: [[Not]] may have a nested value but is not a composite.''
*/
sealed abstract class Pseudo1(override val cssValue: String, val pseudoType: PseudoType) extends Pseudo {
  override def contains(p: Pseudo1) =
    this === p
}

object Pseudo {
  implicit val pseudoTC: Semigroup[Pseudo] with Equal[Pseudo] =
    new Semigroup[Pseudo] with Equal[Pseudo] {
      override def equalIsNatural                  = true
      override def equal (a: Pseudo, b: Pseudo)    = a == b
      override def append(a: Pseudo, b: => Pseudo) = a & b
    }

  implicit val optionPseudoTC: Monoid[Option[Pseudo]] =
    scalaz.std.option.optionMonoid
  
  implicit val psuedo1Order: Order[Pseudo1] =
    Order.orderBy[Pseudo1, (PseudoType, String)](p => (p.pseudoType, p.cssValue))

  final case class Composite private[scalacss](h: Pseudo1, t: ISet[Pseudo1]) extends Pseudo {
    override lazy val cssValue =
      (t insert h).foldLeft("")(_ + _.cssValue)

    override def contains(p: Pseudo1) =
      (h === p) || (t contains p)
  }

  final case class Custom(override val cssValue: String, override val pseudoType: PseudoType) extends Pseudo1(cssValue, pseudoType)

  type PseudoF = ChainOps[Pseudo] => Pseudo

  object ChainOps extends ChainOps[Pseudo] {
    protected def addPseudo(p: Pseudo) = p
  }

  /**
   * Trait providing a nice chaining DSL.
   */
  trait ChainOps[Out] {
    protected def addPseudo(p: Pseudo): Out
    final def active                         : Out = addPseudo(Active)
    final def checked                        : Out = addPseudo(Checked)
    final def disabled                       : Out = addPseudo(Disabled)
    final def empty                          : Out = addPseudo(Empty)
    final def enabled                        : Out = addPseudo(Enabled)
    final def firstChild                     : Out = addPseudo(FirstChild)
    final def firstOfType                    : Out = addPseudo(FirstOfType)
    final def focus                          : Out = addPseudo(Focus)
    final def hover                          : Out = addPseudo(Hover)
    final def inRange                        : Out = addPseudo(InRange)
    final def invalid                        : Out = addPseudo(Invalid)
    final def lang         (language: String): Out = addPseudo(Lang(language))
    final def lastChild                      : Out = addPseudo(LastChild)
    final def lastOfType                     : Out = addPseudo(LastOfType)
    final def link                           : Out = addPseudo(Link)
    final def not          (selector: String): Out = addPseudo(Not(selector))
    final def not          (selector: Pseudo): Out = addPseudo(Not(selector))
    final def not          (f: PseudoF)      : Out = addPseudo(Not(f))
    final def nthChild     (n: Int)          : Out = addPseudo(NthChild(n))
    final def nthLastChild (n: Int)          : Out = addPseudo(NthLastChild(n))
    final def nthLastOfType(n: Int)          : Out = addPseudo(NthLastOfType(n))
    final def nthOfType    (n: Int)          : Out = addPseudo(NthOfType(n))
    final def onlyOfType                     : Out = addPseudo(OnlyOfType)
    final def onlyChild                      : Out = addPseudo(OnlyChild)
    final def optional                       : Out = addPseudo(Optional)
    final def outOfRange                     : Out = addPseudo(OutOfRange)
    final def readOnly                       : Out = addPseudo(ReadOnly)
    final def readWrite                      : Out = addPseudo(ReadWrite)
    final def required                       : Out = addPseudo(Required)
    final def target                         : Out = addPseudo(Target)
    final def valid                          : Out = addPseudo(Valid)
    final def visited                        : Out = addPseudo(Visited)
    final def after                          : Out = addPseudo(After)
    final def before                         : Out = addPseudo(Before)
    final def firstLetter                    : Out = addPseudo(FirstLetter)
    final def firstLine                      : Out = addPseudo(FirstLine)
    final def selection                      : Out = addPseudo(Selection)

    final def attrExists(name: String)       : Out = addPseudo(AttrExists(name))
    final def attr(name: String, value: String) : Out = addPseudo(Attr(name, value))
    final def attrContains(name: String, value: String) : Out = addPseudo(AttrContains(name, value))
    final def attrStartsWith(name: String, value: String) : Out = addPseudo(AttrStartsWith(name, value))
    final def attrEndsWith(name: String, value: String) : Out = addPseudo(AttrEndsWith(name, value))
  }

  /** Selects the active link. */
  case object Active extends Pseudo1(":active", PseudoClass)

  /** Selects every checked &lt;input&gt; element. */
  case object Checked extends Pseudo1(":checked", PseudoClass)

  /** Selects every disabled &lt;input&gt; element. */
  case object Disabled extends Pseudo1(":disabled", PseudoClass)

  /** Selects every &lt;p&gt; element that has no children. */
  case object Empty extends Pseudo1(":empty", PseudoClass)

  /** Selects every enabled &lt;input&gt; element. */
  case object Enabled extends Pseudo1(":enabled", PseudoClass)

  /** Selects every &lt;p&gt; elements that is the first child of its parent. */
  case object FirstChild extends Pseudo1(":first-child", PseudoClass)

  /** Selects every &lt;p&gt; element that is the first &lt;p&gt; element of its parent. */
  case object FirstOfType extends Pseudo1(":first-of-type", PseudoClass)

  /** Selects the &lt;input&gt; element that has focus. */
  case object Focus extends Pseudo1(":focus", PseudoClass)

  /** Selects links on mouse over. */
  case object Hover extends Pseudo1(":hover", PseudoClass)

  /** Selects &lt;input&gt; elements with a value within a specified range. */
  case object InRange extends Pseudo1(":in-range", PseudoClass)

  /** Selects all &lt;input&gt; elements with an invalid value. */
  case object Invalid extends Pseudo1(":invalid", PseudoClass)

  /** Selects every &lt;p&gt; element with a lang attribute value starting with "it". */
  final case class Lang(language: String) extends Pseudo1(s":lang($language)", PseudoClass)

  /** Selects every &lt;p&gt; elements that is the last child of its parent. */
  case object LastChild extends Pseudo1(":last-child", PseudoClass)

  /** Selects every &lt;p&gt; element that is the last &lt;p&gt; element of its parent. */
  case object LastOfType extends Pseudo1(":last-of-type", PseudoClass)

  /** Selects all unvisited link. */
  case object Link extends Pseudo1(":link", PseudoClass)

  /** Selects every element that is not a &lt;p&gt; element. */
  final case class Not(selector: String) extends Pseudo1(s":not($selector)", PseudoClass)
  object Not {
    def apply(selector: Pseudo): Not = Not(selector.cssValue)
    def apply(f: PseudoF)      : Not = Not(f(ChainOps))
  }

  /** Selects every &lt;p&gt; element that is the second child of its parent. */
  final case class NthChild(n: Int) extends Pseudo1(s":nth-child($n)", PseudoClass)

  /** Selects every &lt;p&gt; element that is the second child of its parent, counting from the last child. */
  final case class NthLastChild(n: Int) extends Pseudo1(s":nth-last-child($n)", PseudoClass)

  /** Selects every &lt;p&gt; element that is the second &lt;p&gt; element of its parent, counting from the last child. */
  final case class NthLastOfType(n: Int) extends Pseudo1(s":nth-last-of-type($n)", PseudoClass)

  /** Selects every &lt;p&gt; element that is the second &lt;p&gt; element of its parent. */
  final case class NthOfType(n: Int) extends Pseudo1(s":nth-of-type($n)", PseudoClass)

  /** Selects every &lt;p&gt; element that is the only &lt;p&gt; element of its parent. */
  case object OnlyOfType extends Pseudo1(":only-of-type", PseudoClass)

  /** Selects every &lt;p&gt; element that is the only child of its parent. */
  case object OnlyChild extends Pseudo1(":only-child", PseudoClass)

  /** Selects &lt;input&gt; elements with no "required" attribute. */
  case object Optional extends Pseudo1(":optional", PseudoClass)

  /** Selects &lt;input&gt; elements with a value outside a specified range. */
  case object OutOfRange extends Pseudo1(":out-of-range", PseudoClass)

  /** Selects &lt;input&gt; elements with a "readonly" attribute specified. */
  case object ReadOnly extends Pseudo1(":read-only", PseudoClass)

  /** Selects &lt;input&gt; elements with no "readonly" attribute. */
  case object ReadWrite extends Pseudo1(":read-write", PseudoClass)

  /** Selects &lt;input&gt; elements with a "required" attribute specified. */
  case object Required extends Pseudo1(":required", PseudoClass)

  // /** Selects the document's root element. */
  // case object Root extends Pseudo1(":root")

  /** Selects the current active #news element (clicked on a URL containing that anchor name). */
  case object Target extends Pseudo1(":target", PseudoClass)

  /** Selects all &lt;input&gt; elements with a valid value. */
  case object Valid extends Pseudo1(":valid", PseudoClass)

  /** Selects all visited link. */
  case object Visited extends Pseudo1(":visited", PseudoClass)


  /** Insert content after every &lt;p&gt; element. */
  case object After extends Pseudo1("::after", PseudoElement)

  /** Insert content before every &lt;p&gt; element. */
  case object Before extends Pseudo1("::before", PseudoElement)

  /** Selects the first letter of every &lt;p&gt; element. */
  case object FirstLetter extends Pseudo1("::first-letter", PseudoElement)

  /** Selects the first line of every &lt;p&gt; element. */
  case object FirstLine extends Pseudo1("::first-line", PseudoElement)

  /** Selects the portion of an element that is selected by a user  . */
  case object Selection extends Pseudo1("::selection", PseudoElement)


  class AttrSelector(name: String, value: String, cmp: String) extends Pseudo1(s"[$name$cmp${"\"" + value + "\""}]", PseudoElement)

  /** Selects all elements with a name attribute. */
  case class AttrExists(name: String) extends Pseudo1(s"[$name]", PseudoElement)

  /** Selects all elements with a name="value". */
  case class Attr(name: String, value: String) extends AttrSelector(name, value, "=")

  /** Selects all elements with a name containing the word value. */
  case class AttrContains(name: String, value: String) extends AttrSelector(name, value, "~=")

  /** Selects all elements with a name starting with value. */
  case class AttrStartsWith(name: String, value: String) extends AttrSelector(name, value, "|=")

  /** Selects all elements with a name ends with value. */
  case class AttrEndsWith(name: String, value: String) extends AttrSelector(name, value, "$=")
}
