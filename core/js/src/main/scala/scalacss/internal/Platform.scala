package scalacss.internal

import scalacss.js.PlatformJsEnv

// ================
// ====        ====
// ====   JS   ====
// ====        ====
// ================

object Platform {

  implicit def env: Env =
    PlatformJsEnv.value getOrElse Env.empty
}
