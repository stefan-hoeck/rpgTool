package efa.rpg.rules.spi

import efa.core.Default

trait RulesLocal {
  def rules: String
}

object RulesLocal extends RulesLocal {
  implicit val defImpl: Default[RulesLocal] = Default.default(this)

  def rules = "Regeln"
}

// vim: set ts=2 sw=2 et:
