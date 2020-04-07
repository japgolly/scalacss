package scalacss.core.macros

import utest._
import Wip.Implicits._

object WipTest extends TestSuite {

  override def tests = Tests {

    "autoprefix" - {
      Wip.main(":fullscreen{display:grid}") ==> ":-webkit-full-screen{display:grid}:-ms-fullscreen{display:grid}:fullscreen{display:grid}"
    }

    "vars" - {
      val h = "30px"
      css"""
           |a{
           |  margin: $h 0 $h 1em
           |}
           |""" ==> "a{margin:30px 0 30px 1em}"
    }

    "overlap" - {

      css"""
           |a{
           |  margin: 0;
           |  margin-top:18px;
           |  margin-top:40px;
           |}
           |""" ==> "a{margin:30px 0 30px 1em}"

    }
  }
}
