package scalacss.internal.mutable

// ================
// ====        ====
// ====   JS   ====
// ====        ====
// ================

final class Mutex {
  @inline def apply[A](f: => A): A =
    f
}

object Mutex {
  implicit val mutex = new Mutex
}
