package japgolly.scalacss

// =================
// ====         ====
// ====   JVM   ====
// ====         ====
// =================

/**
 * JVM-specific configuration.
 */
object ScalaPlatform {

  trait Implicits {

    implicit def implicitNewMutex: Mutex =
      new Mutex {
        @inline override def apply[A](f: => A): A = this.synchronized(f)
      }

  }
}
