package japgolly.scalacss

object Env {

  sealed trait Orientation
  case object Landscape extends Orientation
  case object Portrait  extends Orientation

  sealed trait Scan
  case object Progressive extends Scan
  case object Interface   extends Scan

  sealed trait Resolution
  /** Dots per inch */
  case class DPI(value: Int) extends Orientation
  /** Dots per centimeter */
  case class DPCM(value: Int) extends Orientation

  sealed trait MediaType
  case object Aural      extends MediaType
  case object Braille    extends MediaType
  case object Embossed   extends MediaType
  case object Handheld   extends MediaType
  case object Print      extends MediaType
  case object Projection extends MediaType
  case object Screen     extends MediaType
  case object TTY        extends MediaType
  case object TV         extends MediaType

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
  final case class Media[F[_]](width            : F[Int],
                               height           : F[Int],
                               aspectRatio      : F[Double],
                               deviceWidth      : F[Int],
                               deviceHeight     : F[Int],
                               deviceAspectRatio: F[Double],
                               monochrome       : F[Int],
                               color            : F[Int],
                               colorIndex       : F[Int],
                               orientation      : F[Orientation],
                               resolution       : F[Resolution],
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
  }

  val empty: Env =
    EnvF.empty(None)
}

import Env._

final case class EnvF[F[_]](platform: Platform[F], media: Media[F])

object EnvF {
  def empty[F[+_]](e: F[Nothing]): EnvF[F] =
    EnvF[F](Platform empty e, Media empty e)
}