package scalacss.internal

/**
 * Applicable style.
 *
 * A style that needs no more processing and can be applied to some target.
 *
 * @param addClassNames Additional class names that the style has requested be appended.
 *                      Allows ScalaCSS styles to use classname-based CSS libraries like Bootstrap.
 */
final case class StyleA(className: ClassName, addClassNames: Vector[ClassName], style: StyleS) {
  def classNameIterator: Iterator[ClassName] =
    Iterator.single(className) ++ addClassNames

  /** Value to be applied to a HTML element's `class` attribute. */
  val htmlClass: String =
    classNameIterator.map(_.value).mkString(" ")

  def +(s: StyleA)(implicit c: Compose): StyleA =
    StyleA(className, s.className +: (addClassNames ++ s.addClassNames), style.compose(s.style))
}
