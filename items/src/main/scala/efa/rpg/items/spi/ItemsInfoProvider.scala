package efa.rpg.items.spi

import scalaz.effect.IO
import efa.rpg.items.controller.ItemsInfo

trait ItemsInfoProvider {
  def infos: Map[String, IO[ItemsInfo]]
}

// vim: set ts=2 sw=2 et:
