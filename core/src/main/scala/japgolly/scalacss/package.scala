package japgolly

import scalaz.NonEmptyList

package object scalacss {

  type Env = EnvF[Option]

  /**
   * A CSS value, like `"none"`, `"solid 3px black"`.
   */
  type Value = String

  final case class AV(attr: Attr, value: Value)

  type AVs = NonEmptyList[AV]

  final case class AVsAndWarnings(avs: AVs, warnings: List[Warning])

  /**
   * Describes the context of a number of CSS attribute-value pairs.
   *
   * Examples: `"div"`, `".debug"`, `"h3.bottom"`, `"a:visited"`.
   */
  type CssSelector = String

  /**
   * A CSS attribute and its corresponding value.
   *
   * Example: `CssKV("margin-bottom", "12px")`
   */
  final case class CssKV(key: String, value: String)

  final case class ClassName(value: String)

  type Warning  = String
}