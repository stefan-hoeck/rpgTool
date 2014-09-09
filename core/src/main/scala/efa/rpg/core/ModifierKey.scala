package efa.rpg.core

import efa.core.{Localization, IsLocalized}
import efa.core.typeclass._
import scalaz.Equal
import scalaz.std.anyVal._

final case class ModifierKey(loc: Localization, min: Long, max: Long)
  extends IsLocalized

object ModifierKey {
  implicit val equalInst: Equal[ModifierKey] = equal
}
