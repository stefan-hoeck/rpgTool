package efa.rpg.rules

import efa.core.Service
import efa.rpg.rules.ui.spi.RulesUILocal

package object ui {
  lazy val loc = Service.unique[RulesUILocal](RulesUILocal)
}

// vim: set ts=2 sw=2 et:
