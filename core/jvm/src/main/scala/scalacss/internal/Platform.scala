package scalacss.internal

// =================
// ====         ====
// ====   JVM   ====
// ====         ====
// =================

object Platform {

  implicit def env: Env =
    Env.empty
}
