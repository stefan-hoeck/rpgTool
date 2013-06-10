package efa.rpg

import efa.rpg.explorer.spi.ExplorerLoc
import efa.core.Service
import efa.nb.tc.OutlineNb
import org.openide.nodes.Node

package object explorer {
  lazy val loc = Service.unique[ExplorerLoc](ExplorerLoc)

  type ExplorerParams = (OutlineNb, Node)
}

// vim: set ts=2 sw=2 et:
