package object scalacss extends scalacss.defaults.Exports  {

  object Defaults     extends defaults.Exports with defaults.DefaultSettings.DevOrProd
  object DevDefaults  extends defaults.Exports with defaults.DefaultSettings.Dev
  object ProdDefaults extends defaults.Exports with defaults.DefaultSettings.Prod
}
