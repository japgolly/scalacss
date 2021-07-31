package scalacss.internal.mutable

import scalacss.internal._
import scalacss.internal.mutable.Register.{ErrorHandler, MacroName, NameGen}

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
    override          def cssRegisterNameGen     : NameGen          = cssSettings.cssRegisterNameGen
    override          def cssRegisterMacroName   : MacroName        = cssSettings.cssRegisterMacroName
    override          def cssRegisterErrorHandler: ErrorHandler     = cssSettings.cssRegisterErrorHandler
    override implicit def cssStringRenderer      : Renderer[String] = cssSettings.cssStringRenderer
    override implicit def cssComposition         : Compose          = cssSettings.cssComposition
    override implicit def cssEnv                 : Env              = cssSettings.cssEnv
    override implicit def cssRegister            : Register         = cssSettings.cssRegister

    protected[scalacss] def cssSettings: Settings
  }

//  class DelegateTo(override protected val cssSettings: Settings) extends Delegate
//
//  def DelegateTo(cssSettings: Settings) =
//    new DelegateTo(cssSettings)
}