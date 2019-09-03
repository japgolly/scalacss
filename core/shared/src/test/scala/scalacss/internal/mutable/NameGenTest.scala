package scalacss.internal.mutable

import japgolly.microlibs.testutil.TestUtil._
import scalacss.internal.ClassNameHint
import scalacss.internal.mutable.Register.NameGen
import utest._

object NameGenTest extends TestSuite {

  override def tests = Tests {
    "nameGenUniqueAndAscii" - {
      val classesAmount = 1000
      val classNames = (1 to classesAmount).map(_ => NameGen.short().next(ClassNameHint("placeholder"))._1.value)
      val uniqueClassNames = classNames.toSet

      assertEq(uniqueClassNames.size, classesAmount)
      assertEq(uniqueClassNames.count(_.matches("^[a-z0-9_]+$")), classesAmount)

      NameGen.resetNextShortPrefix()
    }
  }
}
