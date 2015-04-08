package japgolly.scalacss

import scala.collection.GenTraversableOnce
import scalaz.{Order, Equal}
import scalaz.std.vector.{vectorEqual, vectorOrder}

final class NonEmptyVector[+A](val head: A, val tail: Vector[A]) {
  override def toString = "NonEmpty" + whole.toString

  override def hashCode = head.## * 31 + tail.##

  override def equals(o: Any) = o match {
    case that: NonEmptyVector[Any] => this.head == that.head && this.tail == that.tail
    case _ => false
  }

  def map[B](f: A => B): NonEmptyVector[B] =
    NonEmptyVector(f(head), tail map f)

  def flatMap[B](f: A => NonEmptyVector[B]): NonEmptyVector[B] =
    reduceMapLeft1(f)(_ ++ _)

  def foreach[U](f: A => U): Unit = {
    f(head)
    tail foreach f
  }

  @inline def mapTail[B >: A](f: Vector[A] => Vector[B]): NonEmptyVector[B] =
    NonEmptyVector(head, f(tail))

  @inline def :+[B >: A](a: B): NonEmptyVector[B] =
    mapTail(_ :+ a)

  @inline def +:[B >: A](a: B): NonEmptyVector[B] =
    NonEmptyVector(a, head +: tail)

  @inline def ++[B >: A](as: GenTraversableOnce[B]): NonEmptyVector[B] =
    mapTail(_ ++ as)

  @inline def ++[B >: A](b: NonEmptyVector[B]): NonEmptyVector[B] =
    ++(b.whole)

  def ++:[B >: A](as: Vector[B]): NonEmptyVector[B] =
    if (as.isEmpty) this else NonEmptyVector(as.head, as.tail ++ whole)

  def whole: Vector[A] =
    head +: tail

  def reverse: NonEmptyVector[A] =
    if (tail.isEmpty) this else NonEmptyVector.end(tail.reverse, head)

  def foldLeft[B](z: B)(f: (B, A) => B): B =
    tail.foldLeft(f(z, head))(f)

  def foldMapLeft1[B](g: A => B)(f: (B, A) => B): B =
    tail.foldLeft(g(head))(f)

  def reduceMapLeft1[B](f: A => B)(g: (B, B) => B): B =
    foldMapLeft1(f)((b, a) => g(b, f(a)))

  @inline def toStream = whole.toStream
}

// =====================================================================================================================

object NonEmptyVector extends NonEmptyVectorImplicits0 {
  @inline def apply[A](h: A, t: A*): NonEmptyVector[A] =
    apply(h, t.toVector)

  @inline def apply[A](h: A, t: Vector[A]): NonEmptyVector[A] =
    new NonEmptyVector(h, t)

  def end[A](init: Vector[A], last: A): NonEmptyVector[A] =
    if (init.isEmpty)
      new NonEmptyVector(last, Vector.empty)
    else
      new NonEmptyVector(init.head, init.tail :+ last)

  @inline def maybe[A, B](v: Vector[A], empty: => B)(f: NonEmptyVector[A] => B): B =
    if (v.isEmpty) empty else f(NonEmptyVector(v.head, v.tail))

  @inline def option[A](v: Vector[A]): Option[NonEmptyVector[A]] =
    maybe[A, Option[NonEmptyVector[A]]](v, None)(Some.apply)

  implicit def equality[A: Equal]: Equal[NonEmptyVector[A]] =
    vectorEqual[A].contramap(_.whole)
}

trait NonEmptyVectorImplicits0 {
  implicit def order[A: Order]: Order[NonEmptyVector[A]] =
    vectorOrder[A].contramap(_.whole)
}
