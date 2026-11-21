package scalacss.internal

import japgolly.univeq._

/**
 * Applicable style.
 *
 * A style that needs no more processing and can be applied to some target.
 *
 * @param addClassNames Additional class names that the style has requested be appended.
 *                      Allows ScalaCSS styles to use classname-based CSS libraries like Bootstrap.
 */
final case class StyleA(className: ClassName, addClassNames: Vector[ClassName], style: StyleS) {

  def selector: String =
    "." + className.value

  def classNameIterator: Iterator[ClassName] =
    Iterator.single(className) ++ addClassNames

  @deprecated("Use htmlClasses instead", "1.1.1")
  def htmlClass: String =
    htmlClasses

  /** HTML classes separated by spaces. */
  val htmlClasses: String =
    if (addClassNames.isEmpty)
      className.value
    else
      classNameIterator.map(_.value).mkString(" ")

  /**
   * Combine two already-registered styles by merging their class names (deduplicated, preserving order) and composing
   * their underlying [[StyleS]] definitions.
   *
   * Note: because the returned [[StyleA]] is not registered, any [[Compose]] warnings produced during composition are
   * not emitted, and CSS cascade precedence in the browser follows stylesheet declaration order rather than `a + b`
   * order. To compose styles with full warning reporting and predictable precedence, compose them inside a stylesheet
   * via `style(a, b)` instead.
   */
  def +(s: StyleA)(implicit c: Compose): StyleA = {
    val cns =
      if (addClassNames.isEmpty && s.addClassNames.isEmpty)
        if (className ==* s.className)
          Vector.empty
        else
          Vector1(s.className)
      else {
        val b    = Vector.newBuilder[ClassName]
        var seen = Set(className)
        def add(cn: ClassName): Unit =
          if (!seen.contains(cn)) {
            seen += cn
            b += cn
          }
        addClassNames.foreach(add)
        add(s.className)
        s.addClassNames.foreach(add)
        b.result()
      }
    StyleA(className, cns, style.compose(s.style))
  }
}
