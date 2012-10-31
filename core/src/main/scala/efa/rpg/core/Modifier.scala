package efa.rpg.core

import scalaz.Equal

case class Modifier(name: String, value: Long)

object Modifier {
  implicit val ModifierEqual: Equal[Modifier] = Equal.equalA
}
