package efa.rpg.items.spi

import efa.rpg.items.controller.ItemsInfo

trait ItemsInfoProvider {
  def infos: Map[String, ItemsInfo]
}

// vim: set ts=2 sw=2 et:
