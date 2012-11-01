package efa.rpg.being

trait MVPanel[A,B] extends BeingPanel[A,B] {
  def prefId: String
  def locName: String
}

// vim: set ts=2 sw=2 et:
