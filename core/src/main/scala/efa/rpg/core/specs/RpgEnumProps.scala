package efa.rpg.core.specs

import efa.core.{ToXml, Read}
import efa.rpg.core.{RpgEnum, RpgEnumSpecs}
import org.scalacheck.{Prop, Arbitrary}
import scalaz.{Equal, Show}

abstract class RpgEnumProps[A:RpgEnum:Read:Show:Equal:Arbitrary:ToXml]
(name: String) extends ReadProps[A](name) with RpgEnumSpecs {
  property("enumUnique") = enumUnique[A]
}

// vim: set ts=2 sw=2 et:
