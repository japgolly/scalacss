package japgolly.scalacss

sealed trait Cond

/** No condition. */
case object NoCond extends Cond

// =====================================================================================================================

/**
 * A pseudo-class is used to define a special state of an element.
 *
 * @param value  The CSS representation.
 */
// Not final
abstract class Pseudo(val value: String) extends Cond {
  import Pseudo.Composite

  final def &(that: Pseudo): Composite =
    (this, that) match {
      case (Composite(a, b), Composite(c, d)) => Composite(a, b ::: c :: d)
      case (Composite(h, t), a              ) => Composite(a, h :: t)
      case (a              , Composite(h, t)) => Composite(a, h :: t)
      case (a              , b              ) => Composite(a, b :: Nil)
    }
}

object Pseudo {

  case class Composite private[scalacss] (h: Pseudo, t: List[Pseudo]) extends Pseudo((h :: t).foldLeft("")(_ + _.value))

  /** Selects the active link. */
  case object Active extends Pseudo(":active")

  /** Selects every checked &lt;input&gt; element. */
  case object Checked extends Pseudo(":checked")

  /** Selects every disabled &lt;input&gt; element. */
  case object Disabled extends Pseudo(":disabled")

  /** Selects every &lt;p&gt; element that has no children. */
  case object Empty extends Pseudo(":empty")

  /** Selects every enabled &lt;input&gt; element. */
  case object Enabled extends Pseudo(":enabled")

  /** Selects every &lt;p&gt; elements that is the first child of its parent. */
  case object FirstChild extends Pseudo(":first-child")

  /** Selects every &lt;p&gt; element that is the first &lt;p&gt; element of its parent. */
  case object FirstOfType extends Pseudo(":first-of-type")

  /** Selects the &lt;input&gt; element that has focus. */
  case object Focus extends Pseudo(":focus")

  /** Selects links on mouse over. */
  case object Hover extends Pseudo(":hover")

  /** Selects &lt;input&gt; elements with a value within a specified range. */
  case object InRange extends Pseudo(":in-range")

  /** Selects all &lt;input&gt; elements with an invalid value. */
  case object Invalid extends Pseudo(":invalid")

  /** Selects every &lt;p&gt; element with a lang attribute value starting with "it". */
  case class Lang(language: String) extends Pseudo(s":lang($language)")

  /** Selects every &lt;p&gt; elements that is the last child of its parent. */
  case object LastChild extends Pseudo(":last-child")

  /** Selects every &lt;p&gt; element that is the last &lt;p&gt; element of its parent. */
  case object LastOfType extends Pseudo(":last-of-type")

  /** Selects all unvisited link. */
  case object Link extends Pseudo(":link")

  /** Selects every element that is not a &lt;p&gt; element. */
  case class Not(selector: String) extends Pseudo(s":not($selector)")
  object Not {
    def apply(selector: Pseudo): Not = Not(selector.value)
  }

  /** Selects every &lt;p&gt; element that is the second child of its parent. */
  case class NthChild(n: Int) extends Pseudo(s":nth-child($n)")

  /** Selects every &lt;p&gt; element that is the second child of its parent, counting from the last child. */
  case class NthLastChild(n: Int) extends Pseudo(s":nth-last-child($n)")

  /** Selects every &lt;p&gt; element that is the second &lt;p&gt; element of its parent, counting from the last child. */
  case class NthLastOfType(n: Int) extends Pseudo(s":nth-last-of-type($n)")

  /** Selects every &lt;p&gt; element that is the second &lt;p&gt; element of its parent. */
  case class NthOfType(n: Int) extends Pseudo(s":nth-of-type($n)")

  /** Selects every &lt;p&gt; element that is the only &lt;p&gt; element of its parent. */
  case object OnlyOfType extends Pseudo(":only-of-type")

  /** Selects every &lt;p&gt; element that is the only child of its parent. */
  case object OnlyChild extends Pseudo(":only-child")

  /** Selects &lt;input&gt; elements with no "required" attribute. */
  case object Optional extends Pseudo(":optional")

  /** Selects &lt;input&gt; elements with a value outside a specified range. */
  case object OutOfRange extends Pseudo(":out-of-range")

  /** Selects &lt;input&gt; elements with a "readonly" attribute specified. */
  case object ReadOnly extends Pseudo(":read-only")

  /** Selects &lt;input&gt; elements with no "readonly" attribute. */
  case object ReadWrite extends Pseudo(":read-write")

  /** Selects &lt;input&gt; elements with a "required" attribute specified. */
  case object Required extends Pseudo(":required")

  // /** Selects the document's root element. */
  // case object Root extends Pseudo(":root")

  /** Selects the current active #news element (clicked on a URL containing that anchor name). */
  case object Target extends Pseudo(":target")

  /** Selects all &lt;input&gt; elements with a valid value. */
  case object Valid extends Pseudo(":valid")

  /** Selects all visited link. */
  case object Visited extends Pseudo(":visited")


  /** Insert content after every &lt;p&gt; element. */
  case object After extends Pseudo("::after")

  /** Insert content before every &lt;p&gt; element. */
  case object Before extends Pseudo("::before")

  /** Selects the first letter of every &lt;p&gt; element. */
  case object FirstLetter extends Pseudo("::first-letter")

  /** Selects the first line of every &lt;p&gt; element. */
  case object FirstLine extends Pseudo("::first-line")

  /** Selects the portion of an element that is selected by a user  . */
  case object Selection extends Pseudo("::selection")
}

// =====================================================================================================================

// TODO MediaQuery
// case class MediaQuery(q: String) extends Cond // like @media (max-width: 600px)
// Later, this can be turned into an AST and run to omit unnecessary CSS.
