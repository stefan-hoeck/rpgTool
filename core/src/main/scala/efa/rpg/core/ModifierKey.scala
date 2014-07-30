package efa.rpg.core

import efa.core.{Localization, Efa}, Efa._
import scalaz._, Scalaz._

case class ModifierKey(loc: Localization, min: Long, max: Long) {
  override def toString = loc.locName
}

object ModifierKey {
  implicit val ModifierKeyEqual: Equal[ModifierKey] = deriveEqual[ModifierKey]
}
