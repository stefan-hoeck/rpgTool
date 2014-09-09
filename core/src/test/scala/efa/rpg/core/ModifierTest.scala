package efa.rpg.core

import org.scalacheck.Properties
import scalaz.scalacheck.ScalazProperties.order

object ModifierTest extends Properties("Modifier"){
  include(order.laws[Modifier])
}

// vim: set ts=2 sw=2 et:
