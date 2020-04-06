package scalacss.core.macros

import utest._

object WipTest extends TestSuite {

  override def tests = Tests {

    "main" - {
      Wip.main(":fullscreen{}") ==> ":-webkit-full-screen{}\n:-ms-fullscreen{}\n:fullscreen{}"
    }
  }
}
