import shapeless._
import shapeless.syntax.singleton._

/**
 * This addresses:
 *   FR-01: Dev shall be able to define a style that requires a specific configuration of children such that the
 *   compiler will enforce that the children are styled.
 *
 * The most sensible way of doing this is to have a composite style produce a ClassName for each component but then
 * the composition order matters which is error-prone and fragile.
 * Eg. in (Style,Style) which one is for the label and which is for the checkbox?
 *
 * This PoC below forces the client-site to acknowledge each style by name.
 */
object NamedChildrenPoC {

  trait Style

  class Step1 {
    val w = Witness('header)
    private val s: Style = ???
    def apply[X](a: w.T, f: Style => Step2 => X): X = f(s)(new Step2)
  }
  class Step2 {
    val w = Witness('body)
    private val s: Style = ???
    def apply[X](a: w.T, f: Style => X): X = f(s)
  }

  val myCompStyle = new Step1


  // Example usage:

  myCompStyle('header, h =>
    _('body, b =>
      s"Header = $h, body = $b"))


  // Doesn't compile:
  // myCompStyle('header, h =>
  //   _('sidebar, b =>
  //     s"Header = $h, sidebar = $b"))
}
