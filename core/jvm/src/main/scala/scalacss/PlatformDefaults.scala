package scalacss

// =================
// ====         ====
// ====   JVM   ====
// ====         ====
// =================


trait PlatformDefaults {

  implicit def env: Env =
    Env.empty
}
