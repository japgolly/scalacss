package japgolly.scalacss

import scala.annotation.elidable
import mutable._
import Register.{ErrorHandler, NameGen}

/**
 * Default config.
 *
 * Use the scalac `-Xelide-below` flag to switch from development- to production-mode.
 */
trait Defaults {

  @elidable(elidable.ASSERTION)
  private def _devMode = true

  protected def devMode = _devMode

  val registerNameGen: NameGen =
    if (devMode)
      NameGen.numbered()
    else
      NameGen.short()

  val registerErrorHandler: ErrorHandler =
    if (devMode)
      ErrorHandler.noisy
    else
      ErrorHandler.silent

  implicit def register: Register =
    new Register(registerNameGen, registerErrorHandler)

  implicit def stringRenderer: Renderer[String] =
    if (devMode)
      StringRenderer.defaultPretty
    else
      StringRenderer.formatTiny

  implicit def composition: Compose =
    if (devMode)
      Compose.safe
    else
      Compose.trust


  // ↓ These aren't defaults... hmmm...

  object StyleSheet {
    type Inline     = mutable.StyleSheet.Inline
    type Standalone = mutable.StyleSheet.Standalone
  }

  final type StyleA = japgolly.scalacss.StyleA
}

object Defaults extends Defaults