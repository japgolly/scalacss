package object scalacss
    extends scalacss.defaults.Exports
       with internal.ModeMacros.DevOrProdDefaults {

  import defaults.{DefaultSettings, Exports}

  object DevDefaults  extends Exports with DefaultSettings.Dev
  object ProdDefaults extends Exports with DefaultSettings.Prod

  @deprecated("import scalacss.Defaults._ no longer works. Replace with one of the following:" +
    "\n    1. import scalacss.DevDefaults._" +
    "\n    2. import scalacss.ProdDefaults._" +
    "\n    3. val CssSettings = scalacss.devOrProdDefaults; import CssSettings._",
    "0.5.3")
  object Defaults extends Exports with DefaultSettings.Dev
}
