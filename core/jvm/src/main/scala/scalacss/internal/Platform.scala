package scalacss.internal

import scala.annotation.elidable

// =================
// ====         ====
// ====   JVM   ====
// ====         ====
// =================

object Platform {

  /**
   * Use the scalac `-Xelide-below` flag to switch from development- to production-mode.
   */
  @elidable(elidable.ASSERTION)
  @inline def DevMode: Boolean =
    true

  implicit def env: Env =
    Env.empty

}
