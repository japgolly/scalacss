package scalacss

// ================
// ====        ====
// ====   JS   ====
// ====        ====
// ================


trait PlatformDefaults {

  implicit def env: Env =
    js.JsEnv.value
}
