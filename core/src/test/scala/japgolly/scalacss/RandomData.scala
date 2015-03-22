package japgolly.scalacss

import japgolly.nyaya.util._
import japgolly.nyaya.test._
import scalaz.{Need, NonEmptyList, OneAnd}
import Style.{UnsafeExts, UnsafeExt}

object RandomData {

  val limbig = 20 `JVM|JS` 6

  val (str, str1) = (Gen.alphanumericstring lim 32, Gen.alphanumericstring1 lim 32)

  def nev[A](g: Gen[A]): GenS[NonEmptyVector[A]] =
    g.vector.flatMap(as => g.map(OneAnd(_, as)))

  val attr: Gen[Attr] =
    Gen oneofL Attrs.values

  val value: Gen[Value] =
    str1

  val av: Gen[AV] =
    Gen.apply2(AV)(attr, value)

  val avs: GenS[AVs] =
    nev(av)

  val pseduo: Gen[Pseudo] = {
    import Pseudo._

    val objects = NonEmptyList[Pseudo](
      Active, Checked, Disabled, Empty, Enabled, FirstChild, FirstOfType, Focus, Hover, InRange, Invalid, LastChild,
      LastOfType, Link, OnlyOfType, OnlyChild, Optional, OutOfRange, ReadOnly, ReadWrite, Required, Target, Valid,
      Visited, After, Before, FirstLetter, FirstLine, Selection)

    val needInt = NonEmptyList[Int => Pseudo](
      NthChild, NthLastChild, NthLastOfType, NthOfType)

    val needStr = NonEmptyList[String => Pseudo](
      Custom, Lang, Not(_))

    lazy val self: Gen[Pseudo] =
      Gen.frequency[Pseudo](
        objects.size -> Gen.oneofL(objects),
        needInt.size -> Gen.oneofL(needInt).flatMap(Gen.positiveint.map),
        needStr.size -> Gen.oneofL(needStr).flatMap(str1.lim(20).map),
        2            -> Gen.lazily(self.map(Not(_))),
        1            -> Gen.lazily(self.list1.lim(4).map(_.list.reduce(_ & _)))
      )
    self
  }

  val cond: Gen[Cond] =
    pseduo.option map (Cond(_, Vector.empty)) // TODO no media queries in random data

  val unsafeCssSelEndo: Gen[CssSelector => CssSelector] =
    str.lim(8).pair.map {
      case (a, b) => a + _ + b
    }

  def unsafeExt(g: Gen[StyleS]): Gen[UnsafeExt] =
    Gen.apply2(UnsafeExt)(unsafeCssSelEndo, g)

  def unsafeExts(o: Option[Gen[StyleS]]): Gen[UnsafeExts] =
    o.fold[Gen[UnsafeExts]](Gen insert Vector.empty)(g =>
      unsafeExt(g).vector.lim(8 `JVM|JS` 3).sup)

  val warning: Gen[Warning] =
    Gen.apply2(Warning)(cond, str)

  val className: Gen[ClassName] =
    str1 map ClassName

  val styleS: Gen[StyleS] = {
    def level(next: Option[Gen[StyleS]]): Gen[StyleS] =
      for {
        data <- cond.mapTo(avs lim limbig).lim(8 `JVM|JS` 3)
        exts <- unsafeExts(next)
        cn   <- className.option
        cns  <- className.vector
        ws   <- warning.vector.lim(limbig)
      } yield {
        // println(s"${data.size} / ${exts.size} / ${ws.size}")
        new StyleS(data, exts, cn, cns, ws)
      }
    level(Some(level(Some(level(None)))))
  }
}
