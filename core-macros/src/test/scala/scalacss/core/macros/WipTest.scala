package scalacss.core.macros

import utest._

object WipTest extends TestSuite {

  override def tests = Tests {

    "main" - {
      Wip.main() ==> "123"
    }
  }
}
