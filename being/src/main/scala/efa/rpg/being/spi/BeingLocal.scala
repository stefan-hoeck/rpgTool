package efa.rpg.being.spi

import efa.core.Default

trait BeingLocal {
  def folder: String
  def loaderName: String
  def total: String
}

object BeingLocal extends BeingLocal {
  implicit val defImpl: Default[BeingLocal] = Default.default(this)
  def folder = "Ordner"
  def loaderName = "Lebewesen Dateien"
  def total = "Total"
}

// vim: set ts=2 sw=2 et:
