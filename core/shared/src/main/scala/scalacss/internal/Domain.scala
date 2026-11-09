package scalacss.internal

import scala.collection.Factory
import scala.collection.immutable.NumericRange
import scala.reflect.ClassTag


// Note: This file is copied from Nyaya.
// https://github.com/japgolly/nyaya


trait Domain[A] {
  val size: Int
  def apply(i: Int): A

  def subst[B >: A]: Domain[B] =
    map(a => a)

  def map[B](f: A => B): Domain[B] =
    new Domain.Mapped(this, f)

  def option: Domain[Option[A]] =
    new Domain.OptionT(this)

  def either[B](b: Domain[B]): Domain[Either[A, B]] =
    this +++ b

  def +++[B](b: Domain[B]): Domain[Either[A, B]] =
    new Domain.Disjunction(this, b)

  def ***[B](b: Domain[B]): Domain[(A, B)] =
    new Domain.Pair(this, b)

  def pair: Domain[(A, A)] =
    this *** this

  def triple: Domain[(A, A, A)] =
    this *** pair map (t => (t._1, t._2._1, t._2._2))

  def seq[S[_]](r: Range)(implicit c: Factory[A, S[A]]): Domain[S[A]] =
    new Domain.IntoSeq(this, r)(c)

  def array(s: Int)  (implicit t: ClassTag[A]): Domain[Array[A]] = seq[Array](s to s)
  def array(r: Range)(implicit t: ClassTag[A]): Domain[Array[A]] = seq[Array](r)

  def list(s: Int)  : Domain[List[A]] = seq[List](s to s)
  def list(r: Range): Domain[List[A]] = seq[List](r)

  def vector(s: Int)  : Domain[Vector[A]] = seq[Vector](s to s)
  def vector(r: Range): Domain[Vector[A]] = seq[Vector](r)

  def iterator: Iterator[A] =
    (0 until size).iterator.map(apply)
}

object Domain {

  final class Mapped[A, B](u: Domain[A], f: A => B) extends Domain[B] {
    override val size = u.size
    override def apply(i: Int) = f(u(i))
    override def map[C](g: B => C): Domain[C] =
      new Domain.Mapped(u, g compose f)
  }

  final class OptionT[A](u: Domain[A]) extends Domain[Option[A]] {
    override val size = u.size + 1
    override def apply(i: Int) = if (i == 0) None else Some(u(i - 1))
  }

  final class Disjunction[A, B](a: Domain[A], b: Domain[B]) extends Domain[Either[A, B]] {
    private[this] val as = a.size
    override val size = as + b.size
    override def apply(i: Int) = if (i < as) Left(a(i)) else Right(b(i - as))
  }

  final class Pair[A, B](a: Domain[A], b: Domain[B]) extends Domain[(A, B)] {
    private[this] val as = a.size
    override val size = as * b.size
    override def apply(i: Int) =
      if (as == 0)
        throw new IndexOutOfBoundsException(s"Index $i out of bounds for domain of size $size")
      else
        (a(i % as), b(i / as))
  }

  final class OverSeq[A](as: IndexedSeq[A]) extends Domain[A] {
    override val size = as.length
    override def apply(i: Int) = as(i)
  }

  final class IntoSeq[S[_], A](a: Domain[A], r: Range)(implicit c: Factory[A, S[A]]) extends Domain[S[A]] {
    private[this] val as = a.size
    val (size, starts) =
      if (as == 0)
        (if (r.contains(0)) 1 else 0, ArraySeq.empty[Int])
      else
        r.foldLeft((0, ArraySeq.empty[Int])) {
          case ((sum, q), i) => (sum + (if (i == 0) 1 else math.pow(as, i).toInt), q :+ sum)
        }

    override def apply(i0: Int): S[A] =
      if (as == 0) {
        if (r.contains(0) && i0 == 0)
          c.newBuilder.result()
        else
          throw new IndexOutOfBoundsException(s"Index $i0 out of bounds for domain of size $size")
      } else {
        var i = i0
        var s = starts.length - 1
        while (s > 0 && i < starts(s)) s -= 1
        var seqSize = r(s)
        i -= starts(s)
        val builder = c.newBuilder
        while (seqSize > 0) {
          builder += a(i % as)
          i /= as
          seqSize -= 1
        }
        builder.result()
      }
  }

  def ofValues[A](as: A*): Domain[A] =
    new OverSeq[A](as.toIndexedSeq)

  def ofRange(r: Range): Domain[Int] =
    new OverSeq[Int](r)

  def ofRangeN[A](r: NumericRange[A]): Domain[A] =
    new OverSeq[A](r)

  lazy val unit: Domain[Unit] =
    ofValues(())

  lazy val boolean: Domain[Boolean] =
    ofValues(true, false)

  lazy val byte: Domain[Byte] =
    ofRange(Byte.MinValue to Byte.MaxValue).map(_.toByte)
}
