package scalacss.js

import org.scalajs.dom
import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scalacss.internal.Env
import scalacss.internal.Env._

/** Detects the browser/OS from `navigator.userAgent` to derive an [[Env]] instance for [[scalacss.internal.CanIUse2]]. */
object PlatformJsEnv {

  /** Returns the detected [[Env]] for the current JS runtime, or `None` when unavailable (e.g. Node.js tests). */
  def value: Option[Env] = {
    val win: UndefOr[dom.Window] =
      try dom.window
      catch { case _: Throwable => js.undefined }
    valueFromWindow(win)
  }

  private[js] def valueFromWindow(window: UndefOr[dom.Window]): Option[Env] =
    readUserAgent(window).flatMap(fromUserAgent)

  private implicit def undefToOption[A](a: UndefOr[A]): Option[A] =
    a.toOption.flatMap(Option(_))

  private[js] def readUserAgent(window: UndefOr[dom.Window]): Option[String] =
    for {
      w   <- undefToOption(window)
      nav <- undefToOption[dom.Navigator](w.navigator)
      ua  <- Option(nav.userAgent) if ua.nonEmpty
    } yield ua

  /** Parses a `navigator.userAgent` string into an [[Env]]. */
  def fromUserAgent(ua: String): Option[Env] =
    parseUserAgent(ua).map(Env(_))

  /** Parses `navigator.userAgent` for the fields used by [[scalacss.internal.CanIUse2.agentsForPlatform]]. */
  def parseUserAgent(ua: String): Option[Platform[Option]] =
    if (ua.isEmpty)
      None
    else {
      def has(re: String): Boolean =
        re.r.findFirstIn(ua).isDefined

      val osFamily: Option[String] =
        if (has("""\bWindows Phone\b"""))                     Some("Windows Phone")
        else if (has("""\bAndroid\b"""))                      Some("Android")
        else if (has("""\b(?:iPhone|iPad|iPod)\b"""))         Some("iOS")
        else if (has("""\b(?:BB10|PlayBook|BlackBerry)\b""")) Some("BlackBerry")
        else                                                  None

      val isAndroid = osFamily.contains("Android")
      val isIOS     = osFamily.contains("iOS")

      val browserName: Option[String] =
        if (has("""\b(?:Opera Mini|OPiOS)/"""))                                  Some("Opera Mini")
        else if (has("""\bIEMobile/"""))                                         Some("IE Mobile")
        else if (has("""\bOPT/""") || has("""\bOpera Mobi\b"""))                 Some("Opera Mobile")
        else if (has("""\bOPR/""")) {
          if (isAndroid || has("""\bMobile\b"""))                                Some("Opera Mobile")
          else                                                                   Some("Opera")
        } else if (has("""\bOpera/"""))                                          Some("Opera")
        else if (has("""\b(?:Edg|EdgA|EdgiOS|Edge)/"""))                         Some("Microsoft Edge")
        else if (has("""\bSamsungBrowser/"""))                                   Some("Samsung Internet")
        else if (has("""\bUCBrowser/"""))                                        Some("UC Browser")
        else if (has("""\b(?:CriOS|CrMo)/"""))                                   Some("Chrome Mobile")
        else if (has("""\b(?:Chrome|HeadlessChrome|Chromium)/""")) {
          if (isAndroid || isIOS || has("""\bMobile\b"""))                       Some("Chrome Mobile")
          else                                                                   Some("Chrome")
        } else if (has("""\bFxiOS/"""))                                          Some("Firefox Mobile")
        else if (has("""\b(?:Firefox|Fennec)/""")) {
          if (isAndroid || has("""\b(?:Mobile|Tablet)\b"""))                     Some("Firefox Mobile")
          else Some("Firefox")
        } else if (has("""\b(?:MSIE |Trident/)""")) Some("IE")
        else if (isAndroid && has("""\bAppleWebKit/""")) Some("Android Browser")
        else if (osFamily.contains("BlackBerry") && has("""\bAppleWebKit/"""))
          Some("BlackBerry")
        else if (has("""\bSafari/""") || (isIOS && has("""\bAppleWebKit/""")))   Some("Safari")
        else                                                                     None

      browserName.map { name =>
        val os = osFamily.map(f => OS[Option](None, Some(f), None))
        Platform[Option](Some(name), None, None, None, None, None, None, os)
      }
    }

  @deprecated("PlatformJs is deprecated; use parseUserAgent / fromUserAgent instead.", "1.1.1")
  def platform(p: PlatformJs): Platform[Option] = {
    if ((p.asInstanceOf[AnyRef] eq null) || js.isUndefined(p))
      Platform.empty[Option](None)
    else {
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
}
