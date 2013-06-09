package efa.rpg.items

import efa.rpg.core.specs.ToXmlProps
import org.scalacheck._, Prop._
import scalaz._, Scalaz._, scalacheck.ScalaCheckBinding._

object AdvantageProps extends ToXmlProps[Advantage]("Advantage") {

  property("advantagesIndex") = Prop.forAll(Gens.advantagesGen) { as ⇒ 
    val exp = List.range(0, as.length)
    val fnd = as map (_.id) toList

    (fnd ≟ exp) :| s"Exp: $fnd but was: $exp"
  }

}

// vim: set ts=2 sw=2 et:
