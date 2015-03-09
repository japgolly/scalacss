import shapeless._
import shapeless.syntax.singleton._
import scala.language.reflectiveCalls

object KeyConflictPoC {

  class Key{ type T }
  def Key(w: Witness) = new Key{ type T = w.T }
  val k1 = Key('k1)
  val k2 = Key('k2)
  val k3 = Key('k3)
  val k4 = Key('k4)

  val k12 = k1 :: k2 :: HNil
  val k34 = k3 :: k4 :: HNil
  val k123 = k12 :+ k3

  sealed trait Conflict
  sealed trait NoConflict extends Conflict
  sealed trait ConflictEx extends Conflict
  sealed trait ConflictMg extends Conflict

  trait CheckConflict[L <: HList, K] {
    type Out <: Conflict
    def out: Out = null.asInstanceOf[Out]
  }
  type CheckConflictAux[L <: HList, K, O <: Conflict] = CheckConflict[L, K]{ type Out = O }
  def CheckConflict[L <: HList, K, O <: Conflict]: CheckConflictAux[L,K,O] =
    new CheckConflict[L, K] {
      override type Out = O
      // override def out = o
      // override def apply(l: L, k: K): O = null
    }

  implicit def confT[N<:HNil,K] = CheckConflict[N, K, NoConflict]
  implicit def confH2[H, T<:HList, K](implicit ev: H =:!= K, next: CheckConflict[T, K]) = CheckConflict[H::T,K,next.Out]
  implicit def confH1[H, T<:HList, K](implicit ev: H =:= K) = CheckConflict[H::T, K, ConflictEx]

  def test[L <: HList, K <: Key](l: L, k: K)(implicit cc: CheckConflict[L, K]): cc.Out = cc.out

  def _test1 = {
    val a: NoConflict = test(HNil, k3)
    val b: NoConflict = test(k12, k3)
    val c: ConflictEx = test(k123, k1)
    val d: ConflictEx = test(k123, k2)
    val e: ConflictEx = test(k123, k3)
    val f: NoConflict = test(k123, k4)
  }

  trait CRes[L <: HList, K, C <: Conflict] { type Out ; def apply(l: L, k: K): Out }
  type CResAux[L <: HList, K, C <: Conflict, O] = CRes[L, K, C] { type Out = O }
  def CRes[L <: HList, K, C <: Conflict, O](f: (L,K)=>O): CResAux[L, K, C, O] =
    new CRes[L,K,C]{
      override type Out = O
      override def apply(l: L, k: K): O = f(l,k)
    }

  implicit def nores[L <: HList, K] =
    CRes[L,K,NoConflict,K::L]((l,k) => k :: l)

  def resolve[L <: HList, K <: Key, C <: Conflict]
    (l: L, k: K)
    (implicit cc: CheckConflictAux[L,K,C], cr: CRes[L,K,C])
    : cr.Out = cr(l,k)

  def resolveO[L <: HList, K <: Key] (l: L, k: K) (implicit cc: CheckConflictAux[L,K,ConflictEx]) = k::l

  def resolve2[L <: HList, K <: Key, C <: Conflict] (l: L, k: K) (implicit cc: CheckConflictAux[L,K,C]) =
    new {
      def ov(implicit cr: CRes[L,K,C]) : cr.Out = cr(l,k)
    }

  def manov[L <: HList, K <: Key] =
    CRes[L,K,ConflictEx,K::L]((l,k) => k :: l)

  def _test2 = {
    resolve(k12, k3)
    // resolve(k12, k2)  // overriding = error
    // resolveO(k12, k3) // not overriding = error
    resolveO(k12, k2)
    resolve2(k12, k2).ov(manov)

    // implicit def allowOverrideOfK1[L <: HList, K <: k1.type] =
      // CRes[L, K, ConflictEx, K::L]((l, k) => k :: l)
    // implicit def allowOverrideOfK2[L <: HList] =
      // CRes[L, k1.type, ConflictEx, k1.type::L]((l, k) => k :: l)
    implicit def allowOverrideOfK3[L <: HList, K <: Key](implicit ev: k1.type <:< K) =
      CRes[L, K, ConflictEx, K::L]((l, k) => k :: l)


    resolve(k12, k1) // (confH1, allowOverrideOfK3)
    // resolve(k12, k2) // (confH1, allowOverrideOfK3)
  }

}
// step 1. Force differently named composition methods dep on override or not.
// step 2. Merge strategies/policies? Specified on keys, on merge, or both?
