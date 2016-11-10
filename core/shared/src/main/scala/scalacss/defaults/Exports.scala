package scalacss.defaults

object Exports extends Exports
trait Exports extends scalacss.defaults.PlatformExports {

  type PseudoType = scalacss.internal.PseudoType
  val  PseudoType = scalacss.internal.PseudoType

  type Pseudo     = scalacss.internal.Pseudo
  val  Pseudo     = scalacss.internal.Pseudo

  type Domain[A]  = scalacss.internal.Domain[A]
  val  Domain     = scalacss.internal.Domain

  type StyleA     = scalacss.internal.StyleA
  val  StyleA     = scalacss.internal.StyleA

  // Expose these because they are Setting constituents
  type CssRenderer[+O] = scalacss.internal.Renderer[O]
  type CssComposer     = scalacss.internal.Compose
  val  CssComposer     = scalacss.internal.Compose
  type CssEnv          = scalacss.internal.Env
  val  CssEnv          = scalacss.internal.Env

  object StyleSheet {
    type Inline     = scalacss.internal.mutable.StyleSheet.Inline
    type Standalone = scalacss.internal.mutable.StyleSheet.Standalone
    type Register   = scalacss.internal.mutable.Register
    type Settings   = scalacss.internal.mutable.Settings
  }
}
