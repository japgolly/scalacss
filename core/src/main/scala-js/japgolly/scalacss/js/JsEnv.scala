package japgolly.scalacss.js

import scalajs.js.UndefOr
import japgolly.scalacss.Env
import Env._

/**
 * Uses [[PlatformJs]] to derive an [[Env]] instance.
 *
 * == Platform Values ==
 *
 * Platform(Some(PhantomJS),Some(WebKit),Some(2.0.0),Some(PhantomJS 2.0.0 (like Safari 8.x) on Linux 64-bit),None,None,Some(Mozilla/5.0 (Unknown; Linux x86_64) AppleWebKit/538.1 (KHTML, like Gecko) PhantomJS/2.0.0 Safari/538.1),Some(OS(Some(Bits64),Some(Linux),None)))
 * Platform(Some(Chrome),Some(Blink),Some(40.0.2214.115),Some(Chrome 40.0.2214.115 on Linux 64-bit),None,None,Some(Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36),Some(OS(Some(Bits64),Some(Linux),None)))
 * Platform(Some(Firefox),Some(Gecko),Some(36.0),Some(Firefox 36.0 on Linux 64-bit),None,None,Some(Mozilla/5.0 (X11; Linux x86_64; rv:36.0) Gecko/20100101 Firefox/36.0),Some(OS(Some(Bits64),Some(Linux),None)))
 */
object JsEnv {

  def value: Env =
    PlatformJs.value.fold(Env.empty)(p =>
      Env(platform(p), Media.empty(None)))

  @inline private implicit def undefToOption[A](a: UndefOr[A]): Option[A] =
    a.toOption.flatMap(Option(_))

  def platform(p: PlatformJs): Platform[Option] = {
    val envOs =
      p.os.map(i => OS[Option](
        arch    = i.architecture flatMap OS.archFromInt,
        family  = i.family,
        version = i.version))

    Platform[Option](
      name         = p.name,
      layout       = p.layout,
      version      = p.version,
      desc         = p.description,
      manufacturer = p.manufacturer,
      product      = p.product,
      userAgent    = p.ua,
      os           = envOs)
  }
}
