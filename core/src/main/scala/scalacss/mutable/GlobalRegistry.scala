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

  private[this] var _register =
    Vector.empty[ISS]

  private[this] var _onRegistration: ISS => Unit =
    (_: ISS) => ()

  def onRegistration(f: ISS => Unit): Unit =
    mutex {
      // Apply to already registered
      _register foreach f

      // Apply to future registrations
      val g = _onRegistration
      _onRegistration = s => { g(s); f(s) }
    }

  def register(ss: ISS*): Unit = {
    val f = mutex {
      _register ++= ss
      _onRegistration
    }
    ss foreach f
  }

  def apply[S <: ISS](implicit t: ClassTag[S]): Option[S] =
    mutex(_register.collectFirst({ case t(s) => s }))
}
