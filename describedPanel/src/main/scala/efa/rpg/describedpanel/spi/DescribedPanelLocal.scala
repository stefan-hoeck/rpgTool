package efa.rpg.describedpanel.spi

trait DescribedPanelLocal {
  def describedAction: String
  def describedTcName: String
  def describedTcHint: String
}

object DescribedPanelLocal extends DescribedPanelLocal {
  def describedAction = "Detailansicht"
  def describedTcName = "Detailansicht"
  def describedTcHint = "Zeigt detailierte Informationen zu " ++
    "ausgewählten Objekten an."
}

// vim: set ts=2 sw=2 et:
