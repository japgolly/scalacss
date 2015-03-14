package japgolly.scalacss

import Style.{UnsafeExt, UnsafeExts}

object DSL {

  // -------------------------------------------------------------------------------------------------------------------
  // Implicits

  @inline implicit final class DslAttr(val self: Attr) extends AnyVal {
    @inline def ~(value: Value): AV = AV(self, value)
  }

  @inline implicit final class DslAV(val self: AV) extends AnyVal {
    @inline def &(b: AV) : AVs = NonEmptyVector(self, b)
    @inline def &(b: AVs): AVs = self +: b
  }

  @inline implicit final class DslAVs(val self: AVs) extends AnyVal {
    @inline def &(b: AV) : AVs = self :+ b
    @inline def &(b: AVs): AVs = self ++ b
  }

  final class DslCond(val c: Cond) extends AnyVal {
    @inline def apply()             : ToStyle = new ToStyle(StyleS.empty)
    @inline def apply(h: AV, t: AV*): ToStyle = apply(NonEmptyVector(h, t: _*))
    @inline def apply(avs: AVs)     : ToStyle = (c, avs)
  }
  @inline implicit def DslCond[C <% Cond](x: C): DslCond = new DslCond(x)

  @inline implicit def CondPseudo(x: Pseudo): Cond = Cond(Some(x))

  final class ToStyle(val s: StyleS) extends AnyVal
  @inline implicit def ToStyleAV             (x: AV)        : ToStyle = ToStyleAVs(NonEmptyVector(x))
          implicit def ToStyleAVs            (x: AVs)       : ToStyle = new ToStyle(StyleS.data1(Cond.empty, x))
          implicit def ToStyleCAV [C <% Cond](x: (C, AV))   : ToStyle = new ToStyle(StyleS.data1(x._1, NonEmptyVector(x._2)))
          implicit def ToStyleCAVs[C <% Cond](x: (C, AVs))  : ToStyle = new ToStyle(StyleS.data1(x._1, x._2))
  @inline implicit def ToStyleUnsafeExt      (x: UnsafeExt) : ToStyle = ToStyleUnsafeExts(Vector1(x))
          implicit def ToStyleUnsafeExts     (x: UnsafeExts): ToStyle = new ToStyle(StyleS.empty.copy(unsafeExts = x))
  @inline implicit def ToStyleStyleS         (x: StyleS)    : ToStyle = new ToStyle(x)

  // -------------------------------------------------------------------------------------------------------------------
  // Explicits

  def style(className: String = null)(t: ToStyle*)(implicit c: Compose): StyleS =
    style(t: _*).copy(className = Option(className))

  def style(t: ToStyle*)(implicit c: Compose): StyleS =
    if (t.isEmpty) StyleS.empty
    else t.map(_.s).reduce(_ compose _)

  def unsafeExt(f: String => String)(t: ToStyle*)(implicit c: Compose): UnsafeExt =
    UnsafeExt(f, style(t: _*))

  def unsafeChild(n: String)(t: ToStyle*)(implicit c: Compose): Style.UnsafeExt =
    unsafeExt(_ + " " + n)(t: _*)
}