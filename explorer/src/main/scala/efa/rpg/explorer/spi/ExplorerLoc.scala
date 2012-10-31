package efa.rpg.explorer.spi

trait ExplorerLoc {
  
  def explorerTcName: String

  def explorerTcHint: String

}

object ExplorerLoc extends ExplorerLoc {
  
  def explorerTcName = "Explorer"

  def explorerTcHint = "Verschiedene Rollenspieldaten"

}

// vim: set ts=2 sw=2 et:
