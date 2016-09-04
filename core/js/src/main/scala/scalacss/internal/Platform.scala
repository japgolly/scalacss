package scalacss.internal

import scala.annotation.elidable

// ================
// ====        ====
// ====   JS   ====
// ====        ====
// ================

object Platform {

  /**
   * Use the scalac `-Xelide-below` flag to switch from development- to production-mode.
   */
  @elidable(elidable.ASSERTION)
  @inline def DevMode: Boolean =
    true
//  @inline def DevMode: Boolean =
//    scalajs.LinkingInfo.developmentMode

  implicit def env: Env =
    Env.empty
}
