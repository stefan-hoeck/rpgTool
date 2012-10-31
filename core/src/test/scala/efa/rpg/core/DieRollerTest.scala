package efa.rpg.core

import efa.rpg.core.specs.ReadProps
import org.scalacheck.Prop

object DieRollerTest extends ReadProps[DieRoller]("DieRoller") {
  
  import DieRoller._

  property("valdateCount") = Prop forAll validated(count.set)(countVal)

  property("valdateDie") = Prop forAll validated(die.set)(dieVal)

  property("valdatePlus") = Prop forAll validated(plus.set)(plusVal)
}

// vim: set ts=2 sw=2 et:
