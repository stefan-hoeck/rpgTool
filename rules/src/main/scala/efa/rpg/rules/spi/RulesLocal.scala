package efa.rpg.rules.spi

trait RulesLocal {
  def rules: String
}

object RulesLocal extends RulesLocal {
  def rules = "Regeln"
}

// vim: set ts=2 sw=2 et:
