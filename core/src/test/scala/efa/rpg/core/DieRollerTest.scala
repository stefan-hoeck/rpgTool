package efa.rpg.core

import efa.rpg.core.specs.ReadProps
import org.scalacheck.Prop

object DieRollerTest extends ReadProps[DieRoller]("DieRoller") {
  import DieRoller._
  
  val L = scalaz.Lens.self[DieRoller]

  property("valdateCount") = Prop forAll validated(L.count.set)(countVal)

  property("valdateDie") = Prop forAll validated(L.die.set)(dieVal)

  property("valdatePlus") = Prop forAll validated(L.plus.set)(plusVal)
}

// vim: set ts=2 sw=2 et:
