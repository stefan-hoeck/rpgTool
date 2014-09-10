package efa.rpg.core

import efa.core.{Localization, Localized}
import efa.core.typeclass._
import scalaz.Order
import scalaz.std.anyVal._
import org.scalacheck.{Arbitrary, Gen}

// @TODO Do we really need the min and max values here?
final case class ModifierKey(loc: Localization, min: Long, max: Long)

object ModifierKey {
  implicit val orderInst: Order[ModifierKey] = order
  implicit val locInst: Localized[ModifierKey] = Localized get (_.loc)

  // For tests we only need a small set of possible
  // modifier keys. This ensures that when we create
  // an arbitrary instance of Modifiers, we typically
  // have several Modifiers for each key.
  implicit val arbInst: Arbitrary[ModifierKey] = Arbitrary(
    Gen oneOf (
      ModifierKey(new Localization("str", "Str"), -10L, 10L),
      ModifierKey(new Localization("lp", "LP"), -20L, 20L),
      ModifierKey(new Localization("ebe", "EBE"), 0L, 10L)
    )
  )
  
}
