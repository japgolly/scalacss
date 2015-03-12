package japgolly.scalacss

// ================
// ====        ====
// ====   JS   ====
// ====        ====
// ================

/**
 * JS-specific configuration.
 */
object ScalaPlatform {

  trait Implicits {

    implicit val implicitNewMutex: Mutex =
      new Mutex {
        @inline override def apply[A](f: => A): A = f
      }

  }
}
