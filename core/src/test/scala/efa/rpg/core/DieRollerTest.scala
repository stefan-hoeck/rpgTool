package efa.rpg.core

import efa.core.{Read, ToXml}
import org.scalacheck.Properties

object DieRollerTest extends Properties("DieRoller") {
  include(Read.showLaws[DieRoller])
  include(ToXml.laws[DieRoller])
}

// vim: set ts=2 sw=2 et:
