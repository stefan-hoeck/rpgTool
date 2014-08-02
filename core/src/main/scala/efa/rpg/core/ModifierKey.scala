package efa.rpg.core

import efa.core.{Localization, IsLocalized}
import efa.core.Shapeless._
import scalaz.std.anyVal._

case class ModifierKey(loc: Localization, min: Long, max: Long)
  extends IsLocalized

object ModifierKey {
  implicit val ModifierKeyEqual = deriveEqual[ModifierKey]
}
