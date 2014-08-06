package efa.rpg.rules.ui.spi

import efa.core.Default

trait RulesUILocal {
  def enableRulesAction: String
}

object RulesUILocal extends RulesUILocal {
  implicit val defImpl: Default[RulesUILocal] = Default.default(this)

  def enableRulesAction: String = "Aktiv"
}

// vim: set ts=2 sw=2 et:
