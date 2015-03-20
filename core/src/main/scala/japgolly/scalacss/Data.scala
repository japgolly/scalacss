package japgolly.scalacss

sealed abstract class LengthUnit(val value: String)
object LengthUnit {
  case object cm   extends LengthUnit("cm")
  case object ch   extends LengthUnit("ch")
  case object em   extends LengthUnit("em")
  case object ex   extends LengthUnit("ex")
  case object in   extends LengthUnit("in")
  case object mm   extends LengthUnit("mm")
  case object pc   extends LengthUnit("pc")
  case object pt   extends LengthUnit("pt")
  case object px   extends LengthUnit("px")
  case object rem  extends LengthUnit("rem")
  case object vh   extends LengthUnit("vh")
  case object vmin extends LengthUnit("vmin")
  case object vmax extends LengthUnit("vmax")
  case object vw   extends LengthUnit("vw")
}

final case class Length[@specialized(scala.Int, scala.Double) N](n: N, u: LengthUnit) {
  def value = {
    val s = n.toString
    if (s == "0") "0" else s + u.value
  }

  @inline def *(m: N)(implicit N: Numeric[N]): Length[N] =
    copy(n = N.times(this.n, m))
}

final case class Percentage[@specialized(scala.Int, scala.Double) N](n: N) {
  def value: Value = n.toString + "%"
}

sealed abstract class ResolutionUnit(val value: String)
object ResolutionUnit {
  /** Dots per inch */
  case object dpi  extends LengthUnit("dpi")
  /** Dots per centimeter */
  case object dpcm extends LengthUnit("dpcm")
}

final case class Resolution[@specialized(scala.Int, scala.Double) N](n: N, u: ResolutionUnit) {
  def value = n.toString + u.value

  @inline def *(m: N)(implicit N: Numeric[N]): Resolution[N] =
    copy(n = N.times(this.n, m))
}

final case class Ratio(x: Int, y: Int) {
  def value = s"$x/$y"
}

object Ratio {

  /** Traditional TV format in the 20th century. */
  def tvTraditional = Ratio(4, 3)

  /** Modern, 'widescreen', TV format. 16:9 */
  def tvWidescreen = Ratio(16, 9)

  /** The most common movie format since the 1960s. 1.85:1 */
  def movieTraditional = Ratio(91, 50)

  /** The 'widescreen', anamorphic, movie format. 2.39:1 */
  def movieWidescreen = Ratio(239, 100)
}