package japgolly.scalacss.js

import scalajs.js._

/**
 * https://github.com/bestiejs/platform.js/blob/master/doc/README.md#readme
 */
trait PlatformJs extends Object {
  val description : UndefOr[String]       = native
  val layout      : UndefOr[String]       = native
  val manufacturer: UndefOr[String]       = native
  val name        : UndefOr[String]       = native
  val prerelease  : UndefOr[String]       = native
  val product     : UndefOr[String]       = native
  val ua          : UndefOr[String]       = native
  val version     : UndefOr[String]       = native
  val os          : UndefOr[PlatformJsOS] = native
}

trait PlatformJsOS extends Object {
  val architecture: UndefOr[Int]    = native
  val family      : UndefOr[String] = native
  val version     : UndefOr[String] = native
}

object PlatformJs {

  /**
   * Returns `undefined` in NodeJS unit tests.
   */
  lazy val value: UndefOr[PlatformJs] = {
    eval(PlatformJsSrc.javascript)
    Dynamic.global.platform.asInstanceOf[UndefOr[PlatformJs]]
  }
}

