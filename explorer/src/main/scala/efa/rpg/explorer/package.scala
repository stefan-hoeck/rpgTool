package efa.rpg

import efa.core.Service
import efa.nb.node.FolderNode
import efa.nb.tc.{OutlineNb, AsTc, OutlineTc}
import efa.rpg.explorer.spi.ExplorerLoc
import javax.swing.text.{DefaultEditorKit ⇒ Kit}
import org.openide.explorer.{ExplorerUtils ⇒ EU}
import org.openide.nodes.Node
import scalaz.effect.IO

package object explorer {
  lazy val loc = Service.unique[ExplorerLoc]

  type ExplorerParams = OutlineNb

  implicit val PsAsTc: AsTc[ExplorerParams] = new OutlineTc[ExplorerParams] {
    override def create = for {
      n ← FolderNode forLayerPath "RpgTool/Explorer"
      o ← OutlineNb(n)
      _ ← IO {
            val mgr = o.peer.getExplorerManager
            val map = o.peer.actionMap
            map.put(Kit.copyAction, EU actionCopy mgr)
            map.put(Kit.cutAction, EU actionCut mgr)
            map.put(Kit.pasteAction, EU actionPaste mgr)
            map.put("delete", EU.actionDelete(mgr, false))
          }
    } yield o

    override def outlineNb(p: ExplorerParams) = p
    override def preferredId = "ExplorerTc"
    override val version = "1.0"
    override def name = loc.explorerTcName
    override def tooltip = loc.explorerTcHint
    override def initialize(p: ExplorerParams) = _ ⇒ IO.ioUnit
    override def undoRedo(p: ExplorerParams) = 
      Some(efa.rpg.items.controller.undoManager)
  }
}

// vim: set ts=2 sw=2 et:
