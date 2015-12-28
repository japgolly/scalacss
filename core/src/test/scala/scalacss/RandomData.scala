package scalacss

import nyaya.gen._
import nyaya.util._
import scalaz.NonEmptyList
import Style.{UnsafeExts, UnsafeExt}

object RandomData {

  val str  = Gen.alphaNumeric.string (32)
  val str1 = Gen.alphaNumeric.string1(32)

  def nev[A](g: Gen[A])(implicit ss: SizeSpec): Gen[NonEmptyVector[A]] =
    g.vector.flatMap(as => g.map(NonEmptyVector(_, as)))

  val attr: Gen[Attr] =
    Gen.choose(Attrs.values.head, Attrs.values.tail: _*)

  val value: Gen[Value] =
    str1

  val av: Gen[AV] =
    Gen.apply2(AV)(attr, value)

  def avs(implicit ss: SizeSpec): Gen[AVs] =
    nev(av).map(n => AVs(n.head, n.tail: _*))

  val pseduo: Gen[Pseudo] = {
    import Pseudo._

    val objects = NonEmptyList[Pseudo](
      Active, Checked, Disabled, Empty, Enabled, FirstChild, FirstOfType, Focus, Hover, InRange, Invalid, LastChild,
      LastOfType, Link, OnlyOfType, OnlyChild, Optional, OutOfRange, ReadOnly, ReadWrite, Required, Target, Valid,
      Visited, After, Before, FirstLetter, FirstLine, Selection)

    val needNthQuery = NonEmptyList[NthQuery => Pseudo](
      NthChild, NthLastChild, NthLastOfType, NthOfType)

    val needStr = NonEmptyList[String => Pseudo](
      Custom(_, PseudoClass), Custom(_, PseudoElement), Lang, Not(_), AttrExists)

    val need2Str = NonEmptyList[(String, String) => Pseudo](
      Pseudo.Attr, AttrContains, AttrStartsWith, AttrEndsWith)

    lazy val self: Gen[Pseudo] =
      Gen.frequency[Pseudo](
        objects.size -> Gen.chooseNE(objects),
        needNthQuery.size -> Gen.chooseNE(needNthQuery).flatMap(Gen.numeric.string1(20).map),
        needStr.size -> Gen.chooseNE(needStr).flatMap(Gen.alphaNumeric.string1(20).map),
        need2Str.size -> Gen.chooseNE(need2Str).flatMap(x => Gen.alphaNumeric.string1(20).pair.map(t => x(t._1, t._2))),
        2            -> Gen.lazily(self.map(Not(_))),
        1            -> Gen.lazily(self.list1(4).map(_.reduce(_ & _)))
      )
    self
  }

  val cond: Gen[Cond] =
    pseduo.option map (Cond(_, Vector.empty)) // TODO no media queries in random data

  val unsafeCssSelEndo: Gen[CssSelector => CssSelector] =
    Gen.alphaNumeric.string1(8).pair.map {
      case (a, b) => a + _ + b
    }

  def unsafeExt(g: Gen[StyleS]): Gen[UnsafeExt] =
    Gen.apply2(UnsafeExt)(unsafeCssSelEndo, g)

  def unsafeExts(o: Option[Gen[StyleS]]): Gen[UnsafeExts] =
    o.fold[Gen[UnsafeExts]](Gen pure Vector.empty)(g =>
      unsafeExt(g).vector(8 `JVM|JS` 3))

  val warning: Gen[Warning] =
    Gen.apply2(Warning)(cond, str)

  val className: Gen[ClassName] =
    str1 map ClassName

  val styleS: Gen[StyleS] = {
    def level(next: Option[Gen[StyleS]]): Gen[StyleS] =
      for {
        data <- cond.mapTo(avs(20 `JVM|JS` 6))(8 `JVM|JS` 3)
        exts <- unsafeExts(next)
        cn   <- className.option
        cns  <- className.vector
        ws   <- warning.vector(20 `JVM|JS` 6)
      } yield {
        // println(s"${data.size} / ${exts.size} / ${ws.size}")
        new StyleS(data, exts, cn, cns, ws)
      }
    level(Some(level(Some(level(None)))))
  }
}
