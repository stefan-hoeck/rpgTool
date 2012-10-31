package efa.rpg.core.specs

import efa.core.{ReadSpecs, ToXml, Read}
import org.scalacheck.{Prop, Arbitrary}
import scalaz.{Equal, Show}

abstract class ReadProps[A:Read:Show:Equal:Arbitrary:ToXml] (name: String)
   extends ToXmlProps[A] (name) with ReadSpecs {

  property("showRead") = Prop forAll showRead[A]

  property("readAll") = Prop forAll readAll[A]
}

// vim: set ts=2 sw=2 et:
