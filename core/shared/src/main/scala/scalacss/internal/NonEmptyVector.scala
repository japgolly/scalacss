package scalacss.internal

import japgolly.univeq.UnivEq
import scala.annotation.nowarn

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

  def mapTail[B >: A](f: Vector[A] => Vector[B]): NonEmptyVector[B] =
    NonEmptyVector(head, f(tail))

  def :+[B >: A](a: B): NonEmptyVector[B] =
    mapTail(_ :+ a)

  def +:[B >: A](a: B): NonEmptyVector[B] =
    NonEmptyVector(a, head +: tail)

  def ++[B >: A](as: IterableOnce[B]): NonEmptyVector[B] =
    mapTail(_ ++ as)

  def ++[B >: A](b: NonEmptyVector[B]): NonEmptyVector[B] =
    ++(b.whole)

  def ++:[B >: A](as: Vector[B]): NonEmptyVector[B] =
    if (as.isEmpty) this else NonEmptyVector(as.head, as.tail ++ whole)

  def whole: Vector[A] =
    head +: tail

  def iterator: Iterator[A] =
    whole.iterator

  def reverse: NonEmptyVector[A] =
    if (tail.isEmpty) this else NonEmptyVector.end(tail.reverse, head)

  def foldLeft[B](z: B)(f: (B, A) => B): B =
    tail.foldLeft(f(z, head))(f)

  def foldMapLeft1[B](g: A => B)(f: (B, A) => B): B =
    tail.foldLeft(g(head))(f)

  def reduceMapLeft1[B](f: A => B)(g: (B, B) => B): B =
    foldMapLeft1(f)((b, a) => g(b, f(a)))

  def toSet[B >: A] = whole.toSet[B]
}

// =====================================================================================================================

object NonEmptyVector {
  def one[A](h: A): NonEmptyVector[A] =
    new NonEmptyVector(h, Vector.empty)

  def apply[A](h: A, t: A*): NonEmptyVector[A] =
    apply(h, t.toVector)

  def apply[A](h: A, t: Vector[A]): NonEmptyVector[A] =
    new NonEmptyVector(h, t)

  def endOV[A](init: Option[Vector[A]], last: A): NonEmptyVector[A] =
    init.fold(one(last))(end(_, last))

  def endO[A](init: Option[NonEmptyVector[A]], last: A): NonEmptyVector[A] =
    init.fold(one(last))(_ :+ last)

  def end[A](init: Vector[A], last: A): NonEmptyVector[A] =
    if (init.isEmpty)
      new NonEmptyVector(last, Vector.empty)
    else
      new NonEmptyVector(init.head, init.tail :+ last)

  def maybe[A, B](v: Vector[A], empty: => B)(f: NonEmptyVector[A] => B): B =
    if (v.isEmpty) empty else f(NonEmptyVector(v.head, v.tail))

  def option[A](v: Vector[A]): Option[NonEmptyVector[A]] =
    maybe[A, Option[NonEmptyVector[A]]](v, None)(Some.apply)

  @nowarn("cat=unused")
  implicit def univEq[A: UnivEq]: UnivEq[NonEmptyVector[A]] =
    UnivEq.force
}
