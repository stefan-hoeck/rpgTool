package efa.rpg

import efa.core.Service
import efa.rpg.core.spi.RpgLocal

package object core {
  lazy val loc = Service.unique[RpgLocal](RpgLocal)

  type DB[+A] = Map[Int,A]
}

// vim: set ts=2 sw=2 et:
