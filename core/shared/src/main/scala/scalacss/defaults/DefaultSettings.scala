package scalacss.defaults

import scalacss.internal._
import scalacss.internal.mutable._
import Register.{ErrorHandler, NameGen, MacroName}

object DefaultSettings {

  /**
   * Development-mode defaults. Pretty names, pretty CSS, warnings.
   */
  object Dev extends Dev
  trait Dev extends Settings {
    override          val cssRegisterNameGen     : NameGen          = NameGen.numbered()
    override          def cssRegisterMacroName   : MacroName        = MacroName.Use
    override          val cssRegisterErrorHandler: ErrorHandler     = ErrorHandler.noisy
    override implicit def cssStringRenderer      : Renderer[String] = StringRenderer.defaultPretty
    override implicit def cssComposition         : Compose          = Compose.safe
  }

  /**
   * Production-mode defaults. Minimised CSS, no warnings.
   */
  object Prod extends Prod
  trait Prod extends Settings {
    override          def cssRegisterNameGen     : NameGen          = NameGen.short()
    override          def cssRegisterMacroName   : MacroName        = MacroName.Ignore
    override          val cssRegisterErrorHandler: ErrorHandler     = ErrorHandler.silent
    override implicit def cssStringRenderer      : Renderer[String] = StringRenderer.formatTiny
    override implicit def cssComposition         : Compose          = Compose.trust
  }

  def devOrProd: Settings =
    if (Platform.DevMode)
      Dev
    else
      Prod
}
