package japgolly.scalacss

import scalaz.OneAnd
import scalaz.syntax.foldable1._

/**
 * TODO Doc this file
 */
final case class Compose(rules: Compose.Rules) {

  def apply(a: StyleS, b: StyleS): StyleS = {
    var warnings = a.warnings ++ b.warnings

    @inline def absorbWarning[A](c: Cond, t: (A, Vector[WarningMsg])): A = {
      t._2.foreach{w =>
        warnings :+= Warning(c, w)
      }
      t._1
    }

    val newData = {
      @inline def mergeAVs(c: Cond, avs: AVs, into: AVs): AVs =
        into.foldLeft(avs)((q, av) => mergeAV(c, av, q))

      @inline def mergeAV(c: Cond, av: AV, into: AVs): AVs = {
        val (ko, ok) = into.vector.partition(i => av.attr.cmp(i.attr).conflict)
        NonEmptyVector.maybe(ko, OneAnd(av, ok))(ko1 =>
          absorbWarning(c, rules.mergeAttrs(av, ko1)) ++ ok)
      }

      b.data.foldLeft(a.data) { case (data, (c, newAVs)) =>
        data.updated(c,
          data.get(c).fold(newAVs)(oldAVs =>
            // TODO concat&silent should be able to change the following line
            // No point looking for conflicts if they won't be considered
            mergeAVs(c, newAVs, into = oldAVs)))}
    }

    val exts = a.unsafeExts ++ b.unsafeExts

    val cn: Option[String] =
      (a.className, b.className) match {
        case (None,       None) => None
        case (r@ Some(_), None) => r // Should this also warn
        case (None, r@ Some(_)) => r // about propagation too?
        case (Some(l), Some(r)) => absorbWarning(Cond.empty, rules.mergeClassNames(l, r))
      }

    new StyleS(newData, exts, cn, warnings)
  }

  def apply[B](a: StyleS, b: StyleF[B]): StyleF[B] =
    b.mod(apply(a, _))

  def apply[B](a: StyleS, b: StyleF.P[B]): StyleF.P[B] =
    StyleF.P(d => apply(a, b(d)))

  def apply[A](a: StyleF[A], b: StyleS): StyleF[A] =
    a.mod(apply(_, b))

  def apply[A, B](a: StyleF[A], b: StyleF[B]): StyleF[(A, B)] =
    new StyleF[(A, B)](i => apply(a f i._1, b f i._2), a.domain *** b.domain)

  def apply[A, B](a: StyleF[A], b: StyleF.P[B]): Domain[B] => StyleF[(A, B)] =
    d => apply(a, b(d))

  def apply[A](a: StyleF.P[A], b: StyleS): StyleF.P[A] =
    StyleF.P(d => apply(a(d), b))

  def apply[A, B](a: StyleF.P[A], b: StyleF[B]): Domain[A] => StyleF[(A, B)] =
    d => apply(a(d), b)

  def apply[A, B](a: StyleF.P[A], b: StyleF.P[B]): (Domain[A], Domain[B]) => StyleF[(A, B)] =
    (da, db) => apply(a(da), b(db))
}

object Compose {

  trait Rules {
    def mergeClassNames(lo: String, hi: String): (Option[String], Vector[WarningMsg])
    def mergeAttrs     (lo: AV,     hi: AVs)   : (AVs           , Vector[WarningMsg])
  }

  object Rules {
    type AttrMerge = (AV, AVs) => AVs

    val append: AttrMerge =
      (lo, hi) => lo +: hi

    val replace: AttrMerge =
      (_, hi) => hi

    def silent(merge: AttrMerge): Rules =
      new Rules {
        override def mergeClassNames(lo: String, hi: String) = (Some(hi)     , Vector.empty)
        override def mergeAttrs(lo: AV, hi: AVs)             = (merge(lo, hi), Vector.empty)
      }

    def warn(merge: AttrMerge): Rules =
      new Rules {
        override def mergeClassNames(lo: String, hi: String) =
          (Some(hi), Vector1(s"Overriding explicit className '$lo' with '$hi'."))

        override def mergeAttrs(lo: AV, hi: AVs) = {
          def show1(x: AV) = x.attr.id
          def showN(x: AVs) = if (x.tail.isEmpty) show1(x.head) else x.vector.map(show1).mkString("{", ",", "}")
          (merge(lo, hi), Vector1(s"${show1(lo)} overridden by ${showN(hi)}."))
        }
      }
  }
}