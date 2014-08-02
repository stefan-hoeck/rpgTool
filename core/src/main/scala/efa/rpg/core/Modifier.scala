package efa.rpg.core

import efa.core.Shapeless._
import scalaz.Equal
import scalaz.std.anyVal._
import scalaz.std.string._

case class Modifier(name: String, value: Long)

object Modifier {
  implicit val ModifierEqual = deriveEqual[Modifier]
}
