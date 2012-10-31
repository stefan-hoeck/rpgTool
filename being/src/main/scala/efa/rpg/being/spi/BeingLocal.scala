package efa.rpg.being.spi

trait BeingLocal {
  def folder: String
  def loaderName: String
  def total: String
}

object BeingLocal extends BeingLocal {
  def folder = "Ordner"
  def loaderName = "Lebewesen Dateien"
  def total = "Total"
}

// vim: set ts=2 sw=2 et:
