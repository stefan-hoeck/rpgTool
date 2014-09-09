package efa.rpg.core

import efa.core.ToXml
import org.scalacheck.Properties
import scalaz.scalacheck.ScalazProperties.equal

object ItemDataTest extends Properties("ItemData") {
  include(ToXml.laws[ItemData])
  include(equal.laws[ItemData])
}

// vim: set ts=2 sw=2 et:
