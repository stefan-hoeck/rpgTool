package efa.rpg.rules.ui.spi

trait RulesUILocal {
  def enableRulesAction: String
}

object RulesUILocal extends RulesUILocal {
  def enableRulesAction: String = "Aktiv"
}

// vim: set ts=2 sw=2 et:
