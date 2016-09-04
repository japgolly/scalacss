package scalacss.internal.mutable

import scalacss.internal._
import Register.{ErrorHandler, NameGen, MacroName}

trait Settings {
           def cssRegisterNameGen     : NameGen
           def cssRegisterMacroName   : MacroName
           def cssRegisterErrorHandler: ErrorHandler
  implicit def cssStringRenderer      : Renderer[String]
  implicit def cssComposition         : Compose

  implicit def cssEnv: Env =
    Platform.env

  implicit def cssRegister: Register =
    new Register(cssRegisterNameGen, cssRegisterMacroName, cssRegisterErrorHandler)
}

object Settings {
  trait Delegate extends Settings {
    override          def cssRegisterNameGen      = cssSettings.cssRegisterNameGen
    override          def cssRegisterMacroName    = cssSettings.cssRegisterMacroName
    override          def cssRegisterErrorHandler = cssSettings.cssRegisterErrorHandler
    override implicit def cssStringRenderer       = cssSettings.cssStringRenderer
    override implicit def cssComposition          = cssSettings.cssComposition
    override implicit def cssEnv                  = cssSettings.cssEnv
    override implicit def cssRegister             = cssSettings.cssRegister

    protected def cssSettings: Settings
  }

//  class DelegateTo(override protected val cssSettings: Settings) extends Delegate
//
//  def DelegateTo(cssSettings: Settings) =
//    new DelegateTo(cssSettings)
}