package efa.rpg.core.specs

import efa.core.{ToXmlSpecs, ToXml, ValidatorSpecs}
import org.scalacheck.{Properties, Prop, Arbitrary}
import scalaz.Equal

abstract class ToXmlProps[A:Equal:Arbitrary:ToXml](name: String)
   extends Properties(name) with ToXmlSpecs with ValidatorSpecs {

  property("toXml") = Prop forAll writeReadXml[A]

}

// vim: set ts=2 sw=2 et:
