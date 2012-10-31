package efa.rpg.core.specs

import efa.core.ReadSpecs
import efa.rpg.core.LocEnum
import org.scalacheck.{Prop, Arbitrary}

abstract class LocEnumProps[A:LocEnum:Arbitrary](name: String)
   extends RpgEnumProps[A](name) {
  property("localizedRead") = Prop forAll ReadSpecs.localizedRead[A]
}

// vim: set ts=2 sw=2 et:
