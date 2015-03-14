package japgolly.scalacss.mutable

// =================
// ====         ====
// ====   JVM   ====
// ====         ====
// =================


final class Mutex {
  @inline def apply[A](f: => A): A = synchronized(f)
}

object Mutex {
  implicit def mutex = new Mutex
}
