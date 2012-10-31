package efa.rpg

import efa.rpg.explorer.spi.ExplorerLoc
import efa.core.Service
import org.openide.awt.UndoRedo
import org.openide.nodes.Node

package object explorer {
  lazy val loc = Service.unique[ExplorerLoc](ExplorerLoc)

  type ExplorerParams = (Node, UndoRedo)
}

// vim: set ts=2 sw=2 et:
