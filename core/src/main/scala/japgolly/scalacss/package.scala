package japgolly

import scalaz.NonEmptyList

package object scalacss {

  type Env = EnvF[Option]

  /**
   * A CSS value, like "none", "solid 3px black".
   */
  type Value = String

  final case class AV(attr: Attr, value: Value)

  type AVs = NonEmptyList[AV]

  final case class CssKV(key: String, value: String)
}