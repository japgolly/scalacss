package scalacss.internal

final class Need[+A](thunk: () => A) {
  private[this] var _thunk = thunk
  lazy val value: A = {
    val t = _thunk
    _thunk = null
    t()
  }
}

object Need {
  @inline def apply[A](a: => A): Need[A] =
    new Need(() => a)
}