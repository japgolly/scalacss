package scalacss.defaults

object Exports extends Exports
trait Exports extends scalacss.defaults.PlatformExports {

  type PseudoType = scalacss.internal.PseudoType
  def  PseudoType = scalacss.internal.PseudoType

  type Pseudo     = scalacss.internal.Pseudo
  def  Pseudo     = scalacss.internal.Pseudo

  type Domain[A]  = scalacss.internal.Domain[A]
  def  Domain     = scalacss.internal.Domain

  type StyleA     = scalacss.internal.StyleA
  def  StyleA     = scalacss.internal.StyleA

  // Expose these because they are Setting constituents
  type CssRenderer[+O] = scalacss.internal.Renderer[O]
  type CssComposer     = scalacss.internal.Compose
  def  CssComposer     = scalacss.internal.Compose
  type CssEnv          = scalacss.internal.Env
  def  CssEnv          = scalacss.internal.Env

  object StyleSheet {
    type Inline     = scalacss.internal.mutable.StyleSheet.Inline
    type Standalone = scalacss.internal.mutable.StyleSheet.Standalone
    type Register   = scalacss.internal.mutable.Register
    type Settings   = scalacss.internal.mutable.Settings
  }
}
