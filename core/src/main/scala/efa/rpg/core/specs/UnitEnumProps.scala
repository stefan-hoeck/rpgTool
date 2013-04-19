package efa.rpg.core.specs

import efa.core.{ValRes, Efa, Validator}, Efa._
import efa.rpg.core.{UnitEnum, RpgEnumSpecs}
import org.scalacheck.{Prop, Arbitrary}
import scalaz._, Scalaz._

abstract class UnitEnumProps[A:UnitEnum:Arbitrary] (name: String)
  extends LocEnumProps[A](name) {

  property("pluralRead") = 
    UnitEnum[A].values ∀ (a ⇒ UnitEnum[A].plural(a).read[A] ≟ a.success)

  def showPretty(a: A, nod: Int): Long ⇒ String =
    UnitEnum[A].showPretty(a, nod)

  def readPretty(a: A): Validator[String,Long] = UnitEnum[A] readPretty a
}

// vim: set ts=2 sw=2 et:
