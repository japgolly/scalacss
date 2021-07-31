package scalacss.internal

// ================
// ====        ====
// ====   JS   ====
// ====        ====
// ================

object Platform {

  implicit def env: Env =
    Env.empty
}
