package efa.rpg.core

import efa.core.Localization
import scalaz.{Equal,Scalaz}

case class ModifierKey(loc: Localization, min: Long, max: Long) {
  override def toString = loc.locName
}

object ModifierKey {
  implicit val ModifierKeyEqual: Equal[ModifierKey] = Equal.equalA
}
