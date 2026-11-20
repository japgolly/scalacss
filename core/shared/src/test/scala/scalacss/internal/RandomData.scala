package scalacss.internal

import cats.data.NonEmptyList
import nyaya.gen._
import nyaya.util._
import scalacss.internal.Style.{UnsafeExt, UnsafeExts}

object RandomData {

  val str  = Gen.alphaNumeric.string(0 to 32)
  val str1 = Gen.alphaNumeric.string(1 to 32)

  def nev[A](g: Gen[A])(implicit ss: SizeSpec): Gen[NonEmptyVector[A]] =
    g.vector.flatMap(as => g.map(NonEmptyVector(_, as)))

  val attr: Gen[Attr] =
    Gen.choose(Attrs.values.head, Attrs.values.tail: _*)

  val value: Gen[Value] =
    str1

  val av: Gen[AV] =
    Gen.apply2(AV.apply)(attr, value)

  def avs(implicit ss: SizeSpec): Gen[AVs] =
    nev(av).map(n => AVs(n.head, n.tail: _*))

  val pseduo: Gen[Pseudo] = {
    import Pseudo._

    val objects = NonEmptyList.of[Pseudo](
      Active, AnyLink, Checked, Disabled, Empty, Enabled, FirstChild, FirstOfType, Focus, FocusVisible, FocusWithin,
      Hover, InRange, Invalid, LastChild, LastOfType, Link, OnlyOfType, OnlyChild, Optional, OutOfRange,
      PlaceholderShown, ReadOnly, ReadWrite, Required, Root, Target, Valid, Visited, After, Backdrop, Before,
      FirstLetter, FirstLine, Marker, Placeholder, Selection)

    val needNthQuery = NonEmptyList.of[NthQuery => Pseudo](
      NthChild.apply,
      NthLastChild.apply,
      NthLastOfType.apply,
      NthOfType.apply,
    )

    val needStr = NonEmptyList.of[String => Pseudo](
      Custom(_, PseudoType.Class), Custom(_, PseudoType.Element), new Has(_), new Is(_), Lang.apply, new Not(_),
      new Where(_), AttrExists.apply)

    val attrCase: Gen[AttrCase] =
      Gen.choose(AttrCase.Default, AttrCase.Insensitive, AttrCase.Sensitive)

    val needAttr = NonEmptyList.of[(String, String, AttrCase) => Pseudo](
      Pseudo.Attr, AttrContains, AttrStartsWith, AttrEndsWith, AttrDashMatch, AttrSubstring)

    lazy val self: Gen[Pseudo] =
      Gen.frequency[Pseudo](
        objects.size      -> Gen.chooseNE(objects),
        needNthQuery.size -> Gen.chooseNE(needNthQuery).flatMap(Gen.numeric.string(1 to 20).map),
        needStr.size      -> Gen.chooseNE(needStr).flatMap(Gen.alphaNumeric.string(1 to 20).map),
        needAttr.size     -> Gen.chooseNE(needAttr).flatMap(x => Gen.apply3(x)(Gen.alphaNumeric.string(1 to 20), Gen.alphaNumeric.string(1 to 20), attrCase)),
        2                 -> Gen.lazily(self.list(1 to 3).map(l => Not(l.head, l.tail: _*))),
        1                 -> Gen.lazily(self.list(1 to 3).map(l => Is(l.head, l.tail: _*))),
        1                 -> Gen.lazily(self.list(1 to 3).map(l => Where(l.head, l.tail: _*))),
        1                 -> Gen.lazily(self.list(1 to 3).map(l => Has(l.head, l.tail: _*))),
        1                 -> Gen.lazily(self.list(1 to 4).map(_.reduce(_ & _)))
      )
    self
  }

  val mediaQuery: Gen[Media.Query] = {
    import Media._

    val mediaType: Gen[TypeA] =
      Gen.choose(All, Aural, Braille, Embossed, Handheld, Print, Projection, Screen, TTY, TV)

    val typeExpr: Gen[TypeExpr] =
      Gen.frequency(
        4 -> mediaType.map(Just.apply),
        1 -> mediaType.map(Only.apply),
        1 -> mediaType.map(Not.apply)
      )

    val length: Gen[Length[Int]] =
      Gen.apply2(Length.apply[Int])(
        Gen.choose(100, 500, 800, 1024),
        Gen.choose(LengthUnit.px, LengthUnit.em, LengthUnit.rem, LengthUnit.vw)
      )

    val ratio: Gen[Ratio] =
      Gen.choose(Ratio.tvTraditional, Ratio.tvWidescreen, Ratio(1, 1))

    val resolution: Gen[scalacss.internal.Resolution[Int]] =
      Gen.apply2(scalacss.internal.Resolution.apply[Int])(
        Gen.choose(1, 2, 96, 300),
        Gen.choose(ResolutionUnit.dpi, ResolutionUnit.dpcm, ResolutionUnit.dppx)
      )

    val rangeOp: Gen[RangeOp] =
      Gen.choose(RangeOp.Lt, RangeOp.Lte, RangeOp.Gt, RangeOp.Gte, RangeOp.Eql)

    def valueExpr[T](g: Gen[T]): Gen[ValueExpr[T]] =
      Gen.frequency(
        2 -> g.map(Eql.apply),
        2 -> g.map(Min.apply),
        2 -> g.map(Max.apply),
        2 -> Gen.apply2(Range.apply[T])(rangeOp, g),
        1 -> Gen.apply2((a: T, b: T) => Between(a, RangeOp.Lte, RangeOp.Lte, b))(g, g)
      )

    val colorBits: Gen[Int] = Gen.choose(1, 4, 8, 16)

    val feature: Gen[Feature] =
      Gen.chooseGen(
        valueExpr(length).map(Width(_)),
        valueExpr(length).map(Height(_)),
        valueExpr(length).map(DeviceWidth(_)),
        valueExpr(length).map(DeviceHeight(_)),
        valueExpr(ratio).map(AspectRatio(_)),
        valueExpr(ratio).map(DeviceAspectRatio(_)),
        valueExpr(resolution).map(Media.Resolution(_)),
        valueExpr(colorBits).option.map(Media.Color(_)),
        valueExpr(colorBits).option.map(ColorIndex(_)),
        valueExpr(colorBits).option.map(Monochrome(_)),
        Gen.choose(Landscape, Portrait).map(Orientation(_)),
        Gen.choose(Progressive, Interlace).map(Scan(_)),
        Gen.choose(0, 1).option.map(Grid(_)),
        Gen.choose(ColorScheme.Light, ColorScheme.Dark).map(PrefersColorScheme(_)),
        Gen.choose(ReducedMotion.NoPreference, ReducedMotion.Reduce).option.map(PrefersReducedMotion(_)),
        Gen.choose(PointerValue.None, PointerValue.Coarse, PointerValue.Fine).option.map(Pointer(_)),
        Gen.choose(HoverValue.None, HoverValue.Hover).option.map(Hover(_)),
        Gen.choose(PointerValue.None, PointerValue.Coarse, PointerValue.Fine).option.map(AnyPointer(_)),
        Gen.choose(HoverValue.None, HoverValue.Hover).option.map(AnyHover(_)),
        Gen.choose(ContrastValue.NoPreference, ContrastValue.More, ContrastValue.Less, ContrastValue.Custom).option.map(PrefersContrast(_)),
        Gen.choose(ForcedColorsValue.None, ForcedColorsValue.Active).option.map(ForcedColors(_))
      )

    val head: Gen[Either[TypeExpr, Feature]] =
      Gen.frequency(
        1 -> typeExpr.map(Left(_)),
        2 -> feature.map(Right(_))
      )

    Gen.apply2(Query.apply)(head, feature.vector(0 to 2))
  }

  val cond: Gen[Cond] =
    Gen.apply2(Cond.apply)(
      pseduo.option,
      Gen.frequency(
        3 -> Gen.pure(Vector.empty[Media.Query]),
        2 -> mediaQuery.vector(1 to 2)
      )
    )

  val unsafeCssSelEndo: Gen[CssSelector => CssSelector] =
    Gen.alphaNumeric.string(1 to 8).pair.map {
      case (a, b) => a + _ + b
    }

  def unsafeExt(g: Gen[StyleS]): Gen[UnsafeExt] =
    Gen.apply3(UnsafeExt.apply)(unsafeCssSelEndo, cond, g)

  private def sized(from: Int, jvm: Int, js: Int): SizeSpec =
    from to (jvm `JVM|JS` js)

  def unsafeExts(o: Option[Gen[StyleS]]): Gen[UnsafeExts] =
    o.fold[Gen[UnsafeExts]](Gen pure Vector.empty)(g =>
      unsafeExt(g).vector(sized(1, 8, 3)))

  val warning: Gen[Warning] =
    Gen.apply2(Warning.apply)(cond, str)

  val className: Gen[ClassName] =
    str1 map ClassName.apply

  val styleS: Gen[StyleS] = {
    def level(next: Option[Gen[StyleS]]): Gen[StyleS] =
      for {
        data <- cond.mapTo(avs(sized(0, 20, 6)))(sized(0, 8, 3))
        exts <- unsafeExts(next)
        cn   <- className.option
        cns  <- className.vector
        ws   <- warning.vector(sized(0, 20, 6))
      } yield {
        // println(s"${data.size} / ${exts.size} / ${ws.size}")
        val lData = Map(data.toSeq:_*)
        new StyleS(lData, exts, cn, cns, ws)
      }
    level(Some(level(Some(level(None)))))
  }
}
