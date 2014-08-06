package efa.rpg.explorer.spi

import efa.core.Default

trait ExplorerLoc {
  def explorerTcName: String

  def explorerTcHint: String
}

object ExplorerLoc extends ExplorerLoc {
  implicit val defImpl: Default[ExplorerLoc] = Default.default(this)

  def explorerTcName = "Explorer"

  def explorerTcHint = "Verschiedene Rollenspieldaten"
}

// vim: set ts=2 sw=2 et:
