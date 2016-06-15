package scalacss.internal.mutable

// =================
// ====         ====
// ====   JVM   ====
// ====         ====
// =================

final class Mutex {
  def apply[A](f: => A): A =
    synchronized(f)
}

object Mutex {
  implicit def mutex = new Mutex
}
