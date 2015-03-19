package japgolly

import scala.collection.GenTraversableOnce
import scalaz.{Equal, Foldable1, OneAnd}
import scalaz.std.AllInstances._
import shapeless.lens

package object scalacss {

  /**
   * A CSS value, like `"none"`, `"solid 3px black"`.
   */
  type Value = String

  private final val _important = " !important"

  final case class AV(attr: Attr, value: Value) {
    def important: AV =
      if (value endsWith _important)
        this
      else
        copy(value = this.value + _important)

    @inline def apply(env: Env) =
      attr.gen(env)(value)
  }

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

    type Lens = shapeless.Lens[CssKV, String]
    val key  : Lens = lens[CssKV].key
    val value: Lens = lens[CssKV].value
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
   *
   * @param addClassNames Additional class names that the style has requested be appended.
   *                      Allows ScalaCSS styles to use classname-based CSS libraries like Bootstrap.
   */
  final case class StyleA(className: ClassName, addClassNames: Vector[ClassName], style: StyleS) {
    /** Value to be applied to a HTML element's `class` attribute. */
    val htmlClass: String =
      (className.value /: addClassNames)(_ + " " + _.value)
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

    @inline def ++(b: NonEmptyVector[A]): NonEmptyVector[A] =
      ++(b.vector)

    def ++:(as: Vector[A]): NonEmptyVector[A] =
      if (as.isEmpty) self else OneAnd(as.head, as.tail ++ vector)

    @inline def vector: Vector[A] =
      self.head +: self.tail
  }

  /** Faster than Vector(a) */
  @inline private[scalacss] def Vector1[A](a: A): Vector[A] =
    Vector.empty :+ a
}