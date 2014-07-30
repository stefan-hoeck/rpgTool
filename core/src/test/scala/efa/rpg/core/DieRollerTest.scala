package efa.rpg.core

import efa.rpg.core.specs.ReadProps
import org.scalacheck.Prop

object DieRollerTest extends ReadProps[DieRoller]("DieRoller") {
  import DieRoller._
  
  val L = shapeless.lens[DieRoller]

  property("valdateCount") = Prop forAll validatedL(L >> 'count)(countVal)

  property("valdateDie") = Prop forAll validatedL(L >> 'die)(dieVal)

  property("valdatePlus") = Prop forAll validatedL(L >> 'plus)(plusVal)
}

// vim: set ts=2 sw=2 et:
