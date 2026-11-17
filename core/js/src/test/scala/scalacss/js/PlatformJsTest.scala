package scalacss.js

import japgolly.microlibs.testutil.TestUtil._
import org.scalajs.dom
import scala.annotation.nowarn
import scala.scalajs.js
import scalacss.internal.CanIUse.Agent
import scalacss.internal.CanIUse2.agentsForPlatform
import scalacss.internal.NonEmptyVector
import sourcecode.Line
import utest._

object PlatformJsTest extends TestSuite {

  private def check(ua: String,
                    expectedName: String,
                    expectedOs: Option[String],
                    expectedAgents: NonEmptyVector[Agent])
                   (implicit q: Line): Unit = {
    val p = PlatformJsEnv.parseUserAgent(ua)
    assertEq(p.flatMap(_.name), Some(expectedName))
    assertEq(p.flatMap(_.os).flatMap(_.family), expectedOs)
    assertEq(p.map(agentsForPlatform), Some(expectedAgents))
    assertEq(PlatformJsEnv.fromUserAgent(ua).map(_.agents), Some(expectedAgents.toSet))
  }

  private val chromeWinUA =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

  override def tests = Tests {
    "desktop" - {
      "chromeWin" - check(
        chromeWinUA,
        "Chrome", None, NonEmptyVector(Agent.Chrome))

      "chromeMac" - check(
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
        "Chrome", None, NonEmptyVector(Agent.Chrome))

      "chromeLinux" - check(
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
        "Chrome", None, NonEmptyVector(Agent.Chrome))

      "chromeOS" - check(
        "Mozilla/5.0 (X11; CrOS x86_64 14541.0.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
        "Chrome", None, NonEmptyVector(Agent.Chrome))

      "headlessChrome" - check(
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/120.0.6099.109 Safari/537.36",
        "Chrome", None, NonEmptyVector(Agent.Chrome))

      "firefoxLinux" - check(
        "Mozilla/5.0 (X11; Linux x86_64; rv:121.0) Gecko/20100101 Firefox/121.0",
        "Firefox", None, NonEmptyVector(Agent.Firefox))

      "firefoxWin" - check(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:121.0) Gecko/20100101 Firefox/121.0",
        "Firefox", None, NonEmptyVector(Agent.Firefox))

      "safariMac" - check(
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Safari/605.1.15",
        "Safari", None, NonEmptyVector(Agent.IOSSafari, Agent.Safari))

      "edgeChromium" - check(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.2210.91",
        "Microsoft Edge", None, NonEmptyVector(Agent.Edge))

      "edgeHTML" - check(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Edge/18.19045",
        "Microsoft Edge", None, NonEmptyVector(Agent.Edge))

      "opera" - check(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 OPR/106.0.0.0",
        "Opera", None, NonEmptyVector(Agent.Opera))

      "operaPresto" - check(
        "Opera/9.80 (Windows NT 6.1; WOW64) Presto/2.12.388 Version/12.18",
        "Opera", None, NonEmptyVector(Agent.Opera))

      "ie11" - check(
        "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko",
        "IE", None, NonEmptyVector(Agent.IE))

      "ie10" - check(
        "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)",
        "IE", None, NonEmptyVector(Agent.IE))
    }

    "mobile" - {
      "chromeAndroidPhone" - check(
        "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36",
        "Chrome Mobile", Some("Android"), NonEmptyVector(Agent.AndroidChrome))

      "chromeAndroidTablet" - check(
        "Mozilla/5.0 (Linux; Android 13; SM-X700) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
        "Chrome Mobile", Some("Android"), NonEmptyVector(Agent.AndroidChrome))

      "chromeIOS" - check(
        "Mozilla/5.0 (iPhone; CPU iPhone OS 17_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/120.0.6099.119 Mobile/15E148 Safari/604.1",
        "Chrome Mobile", Some("iOS"), NonEmptyVector(Agent.Chrome))

      "firefoxAndroidPhone" - check(
        "Mozilla/5.0 (Android 14; Mobile; rv:121.0) Gecko/121.0 Firefox/121.0",
        "Firefox Mobile", Some("Android"), NonEmptyVector(Agent.AndroidFirefox))

      "firefoxAndroidTablet" - check(
        "Mozilla/5.0 (Android 13; Tablet; rv:121.0) Gecko/121.0 Firefox/121.0",
        "Firefox Mobile", Some("Android"), NonEmptyVector(Agent.AndroidFirefox))

      "firefoxIOS" - check(
        "Mozilla/5.0 (iPhone; CPU iPhone OS 17_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) FxiOS/121.0 Mobile/15E148 Safari/605.1.15",
        "Firefox Mobile", Some("iOS"), NonEmptyVector(Agent.Firefox))

      "safariIPhone" - check(
        "Mozilla/5.0 (iPhone; CPU iPhone OS 17_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Mobile/15E148 Safari/604.1",
        "Safari", Some("iOS"), NonEmptyVector(Agent.IOSSafari, Agent.Safari))

      "safariIPad" - check(
        "Mozilla/5.0 (iPad; CPU OS 17_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Mobile/15E148 Safari/604.1",
        "Safari", Some("iOS"), NonEmptyVector(Agent.IOSSafari, Agent.Safari))

      "iosWebView" - check(
        "Mozilla/5.0 (iPhone; CPU iPhone OS 17_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148",
        "Safari", Some("iOS"), NonEmptyVector(Agent.IOSSafari, Agent.Safari))

      "edgeAndroid" - check(
        "Mozilla/5.0 (Linux; Android 10; HD1913) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.6099.144 Mobile Safari/537.36 EdgA/120.0.2210.111",
        "Microsoft Edge", Some("Android"), NonEmptyVector(Agent.AndroidBrowser, Agent.AndroidUC))

      "edgeIOS" - check(
        "Mozilla/5.0 (iPhone; CPU iPhone OS 17_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 EdgiOS/120.2210.111 Mobile/15E148 Safari/605.1.15",
        "Microsoft Edge", Some("iOS"), NonEmptyVector(Agent.Edge))

      "samsungInternet" - check(
        "Mozilla/5.0 (Linux; Android 13; SM-S918B) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/23.0 Chrome/115.0.0.0 Mobile Safari/537.36",
        "Samsung Internet", Some("Android"), NonEmptyVector(Agent.AndroidBrowser, Agent.AndroidUC))

      "ucBrowser" - check(
        "Mozilla/5.0 (Linux; U; Android 11; en-US) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.108 UCBrowser/13.4.0.1306 Mobile Safari/537.36",
        "UC Browser", Some("Android"), NonEmptyVector(Agent.AndroidBrowser, Agent.AndroidUC))

      "androidAospBrowser" - check(
        "Mozilla/5.0 (Linux; U; Android 4.4.2; en-us; Nexus 5 Build/KOT49H) AppleWebKit/537.16 (KHTML, like Gecko) Version/4.0 Mobile Safari/537.16",
        "Android Browser", Some("Android"), NonEmptyVector(Agent.AndroidBrowser, Agent.AndroidUC))

      "operaMobileAndroid" - check(
        "Mozilla/5.0 (Linux; Android 10; VOG-L29) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36 OPR/79.1.4195.77401",
        "Opera Mobile", Some("Android"), NonEmptyVector(Agent.AndroidBrowser, Agent.AndroidUC))

      "operaTouchAndroid" - check(
        "Mozilla/5.0 (Linux; Android 10; VOG-L29) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36 OPT/2.9.2",
        "Opera Mobile", Some("Android"), NonEmptyVector(Agent.AndroidBrowser, Agent.AndroidUC))

      "operaMiniPresto" - check(
        "Opera/9.80 (Android; Opera Mini/36.2.2254/119.132; U; en) Presto/2.12.423 Version/12.16",
        "Opera Mini", Some("Android"), NonEmptyVector(Agent.AndroidBrowser, Agent.AndroidUC))

      "operaMiniIOS" - check(
        "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) OPiOS/16.0.10.123456 Mobile/18A373 Safari/9537.53",
        "Opera Mini", Some("iOS"), NonEmptyVector(Agent.OperaMini))

      "ieMobileSpoofUA" - check(
        "Mozilla/5.0 (Mobile; Windows Phone 8.1; Android 4.0; ARM; Trident/7.0; Touch; rv:11.0; IEMobile/11.0; NOKIA; Lumia 630) like iPhone OS 7_0_3 Mac OS X AppleWebKit/537 (KHTML, like Gecko) Mobile Safari/537",
        "IE Mobile", Some("Windows Phone"), NonEmptyVector(Agent.IEMobile))

      "blackBerry10" - {
        val ua = "Mozilla/5.0 (BB10; Touch) AppleWebKit/537.10+ (KHTML, like Gecko) Version/10.0.9.2372 Mobile Safari/537.10+"
        val p  = PlatformJsEnv.parseUserAgent(ua)
        assertEq(p.flatMap(_.name), Some("BlackBerry"))
        assertEq(p.flatMap(_.os).flatMap(_.family), Some("BlackBerry"))
      }
    }

    "unrecognised" - {
      "empty"  - assertEq(PlatformJsEnv.parseUserAgent("").isDefined, false)
      "nodejs" - assertEq(PlatformJsEnv.parseUserAgent("Node.js/22.0.0").isDefined, false)
      "curl"   - assertEq(PlatformJsEnv.parseUserAgent("curl/8.4.0").isDefined, false)
      "jsdom"  - assertEq(PlatformJsEnv.parseUserAgent("Mozilla/5.0 (linux) AppleWebKit/537.36 (KHTML, like Gecko) jsdom/20.0.0").isDefined, false)
    }

    "windowNavigatorGuards" - {
      def mockWin(o: js.Any): dom.Window = o.asInstanceOf[dom.Window]
      "undefinedWindow"     - assertEq(PlatformJsEnv.valueFromWindow(js.undefined).isDefined, false)
      "nullWindow"          - assertEq(PlatformJsEnv.valueFromWindow(mockWin(null)).isDefined, false)
      "missingNavigator"    - assertEq(PlatformJsEnv.valueFromWindow(mockWin(js.Dynamic.literal())).isDefined, false)
      "nullNavigator"       - assertEq(PlatformJsEnv.valueFromWindow(mockWin(js.Dynamic.literal(navigator = null))).isDefined, false)
      "undefinedNavigator"  - assertEq(PlatformJsEnv.valueFromWindow(mockWin(js.Dynamic.literal(navigator = js.undefined))).isDefined, false)
      "nullUserAgent"       - assertEq(PlatformJsEnv.valueFromWindow(mockWin(js.Dynamic.literal(navigator = js.Dynamic.literal(userAgent = null)))).isDefined, false)
      "emptyUserAgent"      - assertEq(PlatformJsEnv.valueFromWindow(mockWin(js.Dynamic.literal(navigator = js.Dynamic.literal(userAgent = "")))).isDefined, false)
      "validUserAgent"      - assertEq(PlatformJsEnv.valueFromWindow(mockWin(js.Dynamic.literal(navigator = js.Dynamic.literal(userAgent = chromeWinUA)))).map(_.agents), Some(Set(Agent.Chrome)))
      "runtimeNodeValue"    - assertEq(PlatformJsEnv.value.isDefined, false)
    }

    "deprecatedPlatformJsNullGuard" - {
      @nowarn("cat=deprecation")
      def checkNulls(): Unit = {
        assertEq(PlatformJsEnv.platform(null.asInstanceOf[PlatformJs]).name, None)
        assertEq(PlatformJsEnv.platform(js.undefined.asInstanceOf[PlatformJs]).name, None)
      }
      checkNulls()
    }
  }
}
