package scalacss

package object mutable {

  /**
   * StyleSheets specify a string to identify themselves in CSS.
   * [[Register.NameGen]]s can choose to include it in the output class names, or ignore it.
   */
  case class ClassNameHint(value: String)
}
