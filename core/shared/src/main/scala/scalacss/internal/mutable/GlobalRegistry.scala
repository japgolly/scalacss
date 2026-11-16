package scalacss.internal.mutable

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

  private final class Listener(val fn: ISSs => Unit)

  private[this] val callbackMutex = new Mutex

  private[this] var _register: ISSs =
    Vector.empty[ISS]

  private[this] var _listeners: Vector[Listener] =
    Vector.empty[Listener]

  /** @return a function that, when called, will unregister the listener. */
  def registerListener(f: ISS => Unit): () => Unit =
    registerListenerN(_ foreach f)

  /** @return a function that, when called, will unregister the listener. */
  def registerListenerN(f: ISSs => Unit): () => Unit = {
    val l = new Listener(f)
    callbackMutex {
      val existing = mutex {
        _listeners :+= l
        _register
      }
      if (existing.nonEmpty)
        f(existing)
    }
    () => mutex {
      _listeners = _listeners.filterNot(_ eq l)
    }
  }

  def onRegistration(f: ISS => Unit): Unit = {
    registerListener(f)
    ()
  }

  def onRegistrationN(f: ISSs => Unit): Unit = {
    registerListenerN(f)
    ()
  }

  def register(ss: ISS*): Unit =
    callbackMutex {
      val (added, ls) = mutex {
        val fresh = ss.iterator.foldLeft(Vector.empty[ISS]) { (acc, s) =>
          if (_register.exists(_ eq s) || acc.exists(_ eq s))
            acc
          else
            acc :+ s
        }
        if (fresh.nonEmpty)
          _register ++= fresh
        (fresh, _listeners)
      }
      if (added.nonEmpty)
        ls.foreach(_.fn(added))
    }

  def unregister(ss: ISS*): Unit =
    if (ss.nonEmpty)
      mutex {
        _register = _register.filterNot(s => ss.exists(_ eq s))
      }

  def clear(): Unit =
    mutex {
      _register = Vector.empty[ISS]
    }

  def clearListeners(): Unit =
    mutex {
      _listeners = Vector.empty[Listener]
    }

  def apply[S <: ISS](implicit t: ClassTag[S]): Option[S] =
    mutex(_register.collectFirst({ case t(s) => s }))
}
