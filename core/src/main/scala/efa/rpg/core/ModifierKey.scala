package efa.rpg.core

import efa.core.{Localization, Efa}, Efa._
import scalaz._, Scalaz._
import shapeless.Iso

case class ModifierKey(loc: Localization, min: Long, max: Long) {
  override def toString = loc.locName
}

object ModifierKey {
  implicit val MIso = Iso.hlist(ModifierKey.apply _, ModifierKey.unapply _)
  implicit val ModifierKeyEqual: Equal[ModifierKey] = ccEqual
}
