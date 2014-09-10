package efa.rpg.core

import org.scalacheck.Properties
import scalaz.scalacheck.ScalazProperties.order


object ModifierKeyTest extends Properties("ModifierKey") {
  include(order.laws[ModifierKey])
}

// vim: set ts=2 sw=2 et:
