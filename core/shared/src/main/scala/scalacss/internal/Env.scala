package scalacss.internal

import Media.{Orientation, Scan}

object Env {

  /**
   * @param monochrome The number of bits per pixel on a monochrome (greyscale) device.
   *                   If the device isn't monochrome, the device's value is 0.
   * @param color The number of bits per color component of the output device.
   *              If the device is not a color device, this value is zero.
   * @param colorIndex the number of entries in the color look-up table for the output device.
   * @param grid Whether or not the output device is a grid device or a bitmap device.
   *             If the device is grid-based (such as a TTY terminal or a phone display with only one font),
   *             the value is true, otherwise it is false.
   */
  final case class Media[F[_]](width            : F[Length[Int]],
                               height           : F[Length[Int]],
                               aspectRatio      : F[Double],
                               deviceWidth      : F[Length[Int]],
                               deviceHeight     : F[Length[Int]],
                               deviceAspectRatio: F[Double],
                               monochrome       : F[Int],
                               color            : F[Int],
                               colorIndex       : F[Int],
                               orientation      : F[Orientation],
                               resolution       : F[Resolution[Double]],
                               scan             : F[Scan],
                               grid             : F[Boolean])
  object Media {
    def empty[F[+_]](e: F[Nothing]): Media[F] =
      Media[F](e, e, e, e, e, e, e, e, e, e, e, e, e)
  }

  /**
   * @param name The name of the browser/environment.
   * @param layout The name of the browser's layout engine.
   * @param product The name of the product hosting the browser.
   */
  final case class Platform[F[_]](name        : F[String],
                                  layout      : F[String],
                                  version     : F[String],
                                  desc        : F[String],
                                  manufacturer: F[String],
                                  product     : F[String],
                                  userAgent   : F[String],
                                  os          : F[OS[F]])
  object Platform {
    def empty[F[+_]](e: F[Nothing]): Platform[F] =
      Platform[F](e, e, e, e, e, e, e, e)
  }

  sealed trait Architecture
  case object Bits32 extends Architecture
  case object Bits64 extends Architecture

  final case class OS[F[_]](arch   : F[Architecture],
                            family : F[String],
                            version: F[String])
  object OS {
    def empty[F[+_]](e: F[Nothing]): OS[F] =
      OS[F](e, e, e)

    def archFromInt(i: Int): Option[Architecture] =
      i match {
        case 64 => Some(Bits64)
        case 32 => Some(Bits32)
        case _  => None
      }

  }

  final val empty: Env =
    Env(Platform empty None, Media empty None)
}

import Env._

final case class Env(platform: Platform[Option],
                     media   : Media[Option]) {

  val prefixWhitelist: Set[CanIUse.Prefix] =
    CanIUse2.prefixesForPlatform(platform)
}