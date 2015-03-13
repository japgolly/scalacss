package japgolly

import scala.collection.GenTraversableOnce
import scalaz.{Equal, Foldable1, OneAnd}
import scalaz.std.AllInstances._

package object scalacss extends japgolly.scalacss.ScalaPlatform.Implicits {

  type Env = EnvF[Option]

  /**
   * A CSS value, like `"none"`, `"solid 3px black"`.
   */
  type Value = String

  final case class AV(attr: Attr, value: Value)

  type AVs = NonEmptyVector[AV]

  final case class ClassName(value: String)

  /**
   * Describes the context of a number of CSS attribute-value pairs.
   *
   * Examples: `"div"`, `".debug"`, `"h3.bottom"`, `"a:visited"`.
   */
  type CssSelector = String

  /**
   * A CSS attribute and its corresponding value.
   *
   * Example: `CssKV("margin-bottom", "12px")`
   */
  final case class CssKV(key: String, value: String)
  object CssKV {
    implicit val equality: Equal[CssKV] = Equal.equalA
  }

  /**
   * A stylesheet in its entirety. Normally turned into a `.css` file or a `&lt;style&gt;` tag.
   */
  type Css = Stream[(CssSelector, NonEmptyVector[CssKV])]
  implicit val cssEquality: Equal[Css] =
    streamEqual(tuple2Equal(stringInstance, nonEmptyVectorEquality(CssKV.equality)))

  type WarningMsg = String
  final case class Warning(cond: Cond, msg: WarningMsg)

  /**
   * Applicable style.
   *
   * A style that needs no more processing and can be applied to some target.
   */
  final case class StyleA(className: ClassName, style: StyleS)

  abstract class Mutex {
    @inline def apply[A](f: => A): A
  }

  // ===================================================================================================================
  type NonEmptyVector[A] = OneAnd[Vector, A]

  object NonEmptyVector {
    @inline def apply[A](h: A, t: A*): NonEmptyVector[A] =
      OneAnd(h, t.toVector)

    def end[A](init: Vector[A], last: A): NonEmptyVector[A] =
      if (init.isEmpty)
        OneAnd(last, Vector.empty)
      else
        OneAnd(init.head, init.tail :+ last)

    @inline def maybe[A, B](v: Vector[A], empty: => B)(f: NonEmptyVector[A] => B): B =
      if (v.isEmpty) empty else f(OneAnd(v.head, v.tail))
  }

  implicit val nonEmptyVectorFoldable1: Foldable1[NonEmptyVector] =
    OneAnd.oneAndFoldable[Vector]

  implicit def nonEmptyVectorEquality[A: Equal]: Equal[NonEmptyVector[A]] =
    OneAnd.oneAndEqual[Vector, A]

  @inline implicit class NonEmptyVectorExt[A](val self: NonEmptyVector[A]) extends AnyVal {
    @inline def modt(f: Vector[A] => Vector[A]): NonEmptyVector[A] =
      OneAnd(self.head, f(self.tail))

    @inline def :+(a: A): NonEmptyVector[A] =
      modt(_ :+ a)

    @inline def +:(a: A): NonEmptyVector[A] =
      OneAnd(a, self.head +: self.tail)

    @inline def ++(as: GenTraversableOnce[A]): NonEmptyVector[A] =
      modt(_ ++ as)

    def ++:(as: Vector[A]): NonEmptyVector[A] =
      if (as.isEmpty) self else OneAnd(as.head, as.tail ++ vector)

    @inline def vector: Vector[A] =
      self.head +: self.tail
  }

  /** Faster than Vector(a) */
  @inline private[scalacss] def Vector1[A](a: A): Vector[A] =
    Vector.empty :+ a
}