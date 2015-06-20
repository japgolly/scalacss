package scalacss.mutable

import scala.reflect.ClassTag

/**
 * An optional convenience for working with inline stylesheets.
 *
 * Ideally inline stylesheet modules should either be merged into a single app-wide object,
 * or passed around like normal dependencies.
 *
 * If that is unpalatable to your circumstances or preferences,
 * you can instead register style modules with this global registry,
 * and have client-code retrieve modules from this global registry by type.
 *
 * {{{
 *   class BoxStyles extends StyleSheet.Inline {
 *     ...
 *   }
 *
 *   // Register
 *   GlobalRegistry.register(new BoxStyles)
 *
 *   // Retrieve
 *   val boxStyles = GlobalRegistry[BoxStyles].get
 * }}}
 */
object GlobalRegistry extends StyleSheetRegistry

class StyleSheetRegistry(implicit mutex: Mutex) {

  import StyleSheet.{Inline => ISS}
  final type ISSs = Vector[ISS]

  private[this] var _register: ISSs =
    Vector.empty[ISS]

  private[this] var _onRegistration: ISSs => Unit =
    (_: ISSs) => ()

  def onRegistration(f: ISS => Unit): Unit =
    onRegistrationN(_ foreach f)

  def onRegistrationN(f: ISSs => Unit): Unit =
    mutex {
      // Apply to already registered
      if (_register.nonEmpty)
        f(_register)

      // Apply to future registrations
      val g = _onRegistration
      _onRegistration = s => { g(s); f(s) }
    }

  def register(ss: ISS*): Unit = {
    val v = ss.toVector
    val f = mutex {
      _register ++= v
      _onRegistration
    }
    f(v)
  }

  def apply[S <: ISS](implicit t: ClassTag[S]): Option[S] =
    mutex(_register.collectFirst({ case t(s) => s }))
}
