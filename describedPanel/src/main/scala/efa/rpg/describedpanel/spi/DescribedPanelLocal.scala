package efa.rpg.describedpanel.spi

import efa.core.Default

trait DescribedPanelLocal {
  def describedAction: String
  def describedTcName: String
  def describedTcHint: String
}

object DescribedPanelLocal extends DescribedPanelLocal {
  implicit val defImpl: Default[DescribedPanelLocal] = Default.default(this)
  def describedAction = "Detailansicht"
  def describedTcName = "Detailansicht"
  def describedTcHint = "Zeigt detailierte Informationen zu " ++
    "ausgewählten Objekten an."
}

// vim: set ts=2 sw=2 et:
