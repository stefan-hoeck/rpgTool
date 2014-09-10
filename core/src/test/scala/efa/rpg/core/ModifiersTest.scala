package efa.rpg.core

import org.scalacheck.{Properties, Prop}
import scalaz.syntax.foldable._
import scalaz.syntax.equal._
import scalaz.std.list._
import scalaz.std.anyVal._
import scalaz.scalacheck.ScalazProperties.{equal, monoid}

object ModifiersTest extends Properties("Modifiers") {
  include(equal.laws[Modifiers])
  include(monoid.laws[Modifiers])

  property("property addition under monoid append") = Prop forAll {
    p: (Modifiers, Modifiers) ⇒ 
      val (m1, m2) = p
      val m3 = m1 append m2

      m3.keySet.toList ∀ { k ⇒ m3(k) ≟ (m1(k) + m2(k)) }
  }

  property("modifiers concatenation under monoid append") = Prop forAll {
    p: (Modifiers, Modifiers) ⇒ 
      val (m1, m2) = p
      val m3 = m1 append m2

      m3.keySet.toList ∀ { k ⇒ m3.get(k) ≟ (m1.get(k) ::: m2.get(k)) }
  }
}

// vim: set ts=2 sw=2 et:
