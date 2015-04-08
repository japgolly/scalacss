package scalacss

import scala.annotation.elidable
import mutable._
import Register.{ErrorHandler, NameGen}

trait DefaultImports {

  object StyleSheet {
    type Inline     = mutable.StyleSheet.Inline
    type Standalone = mutable.StyleSheet.Standalone
  }

  final type StyleA = scalacss.StyleA
}

trait IDefaults extends PlatformDefaults with DefaultImports {
           def registerNameGen     : NameGen
           val registerErrorHandler: ErrorHandler
  implicit def stringRenderer      : Renderer[String]
  implicit def composition         : Compose

  implicit def register: Register =
    new Register(registerNameGen, registerErrorHandler)
}

/**
 * Development-mode defaults. Pretty names, pretty CSS, warnings.
 */
object DevDefaults extends DevDefaults
trait DevDefaults extends IDefaults {
  override          val registerNameGen     : NameGen          = NameGen.numbered()
  override          val registerErrorHandler: ErrorHandler     = ErrorHandler.noisy
  override implicit def stringRenderer      : Renderer[String] = StringRenderer.defaultPretty
  override implicit def composition         : Compose          = Compose.safe
}

/**
 * Production-mode defaults. Minimised CSS, no warnings.
 */
object ProdDefaults extends ProdDefaults
trait ProdDefaults extends IDefaults {
  override          def registerNameGen     : NameGen          = NameGen.short()
  override          val registerErrorHandler: ErrorHandler     = ErrorHandler.silent
  override implicit def stringRenderer      : Renderer[String] = StringRenderer.formatTiny
  override implicit def composition         : Compose          = Compose.trust
}

/**
 * Default config.
 *
 * Use the scalac `-Xelide-below` flag to switch from development- to production-mode.
 */
object Defaults extends Defaults
trait Defaults extends IDefaults {

  @elidable(elidable.ASSERTION)
  private def _devMode = true

  protected def devMode = _devMode

  protected def defaults: IDefaults =
    if (devMode)
      DevDefaults
    else
      ProdDefaults

  override          def registerNameGen     : NameGen          = defaults.registerNameGen
  override          val registerErrorHandler: ErrorHandler     = defaults.registerErrorHandler
  override implicit def stringRenderer      : Renderer[String] = defaults.stringRenderer
  override implicit def composition         : Compose          = defaults.composition
}
