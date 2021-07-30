package scalacss.internal

import cats.data.NonEmptyList
import nyaya.gen._
import nyaya.util._
import scala.collection.immutable.SortedMap
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
      Active, Checked, Disabled, Empty, Enabled, FirstChild, FirstOfType, Focus, Hover, InRange, Invalid, LastChild,
      LastOfType, Link, OnlyOfType, OnlyChild, Optional, OutOfRange, ReadOnly, ReadWrite, Required, Target, Valid,
      Visited, After, Before, FirstLetter, FirstLine, Selection)

    val needNthQuery = NonEmptyList.of[NthQuery => Pseudo](
      NthChild, NthLastChild, NthLastOfType, NthOfType)

    val needStr = NonEmptyList.of[String => Pseudo](
      Custom(_, PseudoType.Class), Custom(_, PseudoType.Element), Lang, new Not(_), AttrExists)

    val need2Str = NonEmptyList.of[(String, String) => Pseudo](
      Pseudo.Attr _, AttrContains _, AttrStartsWith _, AttrEndsWith _)

    lazy val self: Gen[Pseudo] =
      Gen.frequency[Pseudo](
        objects.size      -> Gen.chooseNE(objects),
        needNthQuery.size -> Gen.chooseNE(needNthQuery).flatMap(Gen.numeric.string(1 to 20).map),
        needStr.size      -> Gen.chooseNE(needStr).flatMap(Gen.alphaNumeric.string(1 to 20).map),
        need2Str.size     -> Gen.chooseNE(need2Str).flatMap(x => Gen.alphaNumeric.string(1 to 20).pair.map(t => x(t._1, t._2))),
        2                 -> Gen.lazily(self.map(Not(_))),
        1                 -> Gen.lazily(self.list(1 to 4).map(_.reduce(_ & _)))
      )
    self
  }

  val cond: Gen[Cond] =
    pseduo.option map (Cond(_, Vector.empty)) // TODO no media queries in random data

  val unsafeCssSelEndo: Gen[CssSelector => CssSelector] =
    Gen.alphaNumeric.string(1 to 8).pair.map {
      case (a, b) => a + _ + b
    }

  def unsafeExt(g: Gen[StyleS]): Gen[UnsafeExt] =
    Gen.apply3(UnsafeExt)(unsafeCssSelEndo, cond, g)

  private def sized(from: Int, jvm: Int, js: Int): SizeSpec =
    from to (jvm `JVM|JS` js)

  def unsafeExts(o: Option[Gen[StyleS]]): Gen[UnsafeExts] =
    o.fold[Gen[UnsafeExts]](Gen pure Vector.empty)(g =>
      unsafeExt(g).vector(sized(1, 8, 3)))

  val warning: Gen[Warning] =
    Gen.apply2(Warning)(cond, str)

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
        val lData = SortedMap(data.toSeq:_*)
        new StyleS(lData, exts, cn, cns, ws)
      }
    level(Some(level(Some(level(None)))))
  }
}
