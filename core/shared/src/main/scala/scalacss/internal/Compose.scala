package scalacss.internal

/**
 * Style composition logic.
 *
 * This is responsible for merges styles together, and producing warnings.
 * It has the power to perform merges on conflicting CSS values, and selectively ignore merges.
 */
final case class Compose(rules: Compose.Rules) {

  def apply(a: StyleS, b: StyleS): StyleS = {
    var warnings = a.warnings ++ b.warnings

    def absorbWarning[A](c: Cond, t: (A, Vector[WarningMsg])): A = {
      t._2.foreach { w => warnings :+= Warning(c, w) }
      t._1
    }

    def mergeAVs(c: Cond, newAVs: AVs, into: AVs): AVs = {

      // Remove exact matches
      var newData = newAVs.data
      for {(a, nvs) <- newData; ovs <- into.get(a)} {
        val o = ovs.toSet
        val newVals = nvs.whole.filterNot(o.contains)
        NonEmptyVector.maybe(newVals, newData -= a)(n => newData = newData.updated(a, n))
      }

      // Find conflicts
      newData.foldLeft(into) { case (results, (a, nvs)) =>
        results.filterKeys(k => a.cmp(k).conflict) match {
          case None =>
            // No conflicts - safe to add
            results.addAll(a, nvs)
          case Some(conflicts) =>
            // Handle conflict via rules
            val r = absorbWarning(c, rules.mergeAVs(conflicts, AVs(a, nvs)))
            val delete = conflicts.data.keySet -- r.order.whole
            results.filterKeys(!delete.contains(_)) match {
              case None    => r
              case Some(o) => r.data.foldLeft(o)((o, b) => o.modify(b._1, _ => b._2))
            }
        }
      }
    }

    val newData =
      b.data.foldLeft(a.data) { case (data, (cond, newAVs)) =>
        data.updated(cond,
          data.get(cond).fold(newAVs)(oldAVs =>
            mergeAVs(cond, newAVs, into = oldAVs)))
      }

    val exts = a.unsafeExts ++ b.unsafeExts

    val cns = a.addClassNames ++ b.addClassNames

    new StyleS(newData, exts, className = None, cns, warnings)
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

  lazy val safe: Compose =
    new Compose(Rules(Rules.append, Rules.warn))

  lazy val trust: Compose =
    new Compose(Rules(Rules.append, Rules.silent))

  trait Rules {
    def mergeAVs(lo: AVs, hi: AVs): (AVs, Vector[WarningMsg])
  }

  object Rules {
    type MergeRule = (AVs, AVs) => AVs
    type WarnRule  = (AVs, AVs) => Vector[WarningMsg]

    def apply(m: MergeRule, w: WarnRule): Rules =
      new Rules {
        override def mergeAVs(lo: AVs, hi: AVs): (AVs, Vector[WarningMsg]) =
          (m(lo, hi), w(lo, hi))
      }

    def append: MergeRule =
      (lo, hi) => lo ++ hi

    def replace: MergeRule =
      (_, hi) => hi

    def silent: WarnRule =
      (_, _) => Vector.empty

    def warn: WarnRule =
      (lo, hi) => {
        def showA(a: Attr): String = a.id
        def showV(vs: NonEmptyVector[Value]): String =
          if (vs.tail.isEmpty) vs.head else vs.whole.mkString("[", "; ", "]")
        def showAVt(t: (Attr, NonEmptyVector[Value])) =
          showAV(t._1, t._2)
        def showAV(a: Attr, vs: NonEmptyVector[Value]): String =
//          s"(${showA(a)}: ${showV(vs)})"
          s"${showA(a)}: ${showV(vs)}"
        def showAVs(avs: AVs): String = {
          val s = avs.iterator.map(showAVt)
//          if (s.lengthCompare(1) == 0) s.head else s.mkString("{ ", ", ", " }")
          s.mkString("{", ", ", "}")
        }
        hi.iterator
          .map(t => s"{${showAVt(t)}} conflicts with ${showAVs(lo)}")
          .toVector
      }
  }
}