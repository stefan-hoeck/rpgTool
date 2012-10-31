package efa.rpg.items.dob

import efa.nb.node.NbNode
import efa.rpg.items.controller.ItemsInfo
import scalaz._, Scalaz._, effect._

class InfoProvider extends efa.rpg.items.spi.ItemsInfoProvider {
  def infos = Map("test" → InfoProvider.info)
}

object InfoProvider {
  val info: IO[ItemsInfo] = for {
    n  ← NbNode.apply
  } yield ItemsInfo(n, TestEvents.ein)
}

// vim: set ts=2 sw=2 et:
