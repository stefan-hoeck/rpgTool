package efa.rpg.core

import efa.core.{Default, Name}
import efa.core.typeclass._
import efa.core.std.anyVal._
import org.scalacheck.Arbitrary
import scalaz.Order
import scalaz.std.anyVal._

case class Modifier(name: Name, value: Long)

object Modifier {
  implicit val orderInst: Order[Modifier] = order
  implicit val orderingInst: Ordering[Modifier] = orderInst.toScalaOrdering
  implicit val arbInst: Arbitrary[Modifier] = arbitrary
  implicit val defaultInst: Default[Modifier] = Default.derive
}
