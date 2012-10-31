package efa.rpg.being.nodes

import efa.nb.node.{FolderNode, FolderInfo}
import efa.rpg.preferences.Preferences.beingFolderIO
import scalaz._, Scalaz._

object BeingFolderRoot {
  private def imm = FolderInfo.immutableName

  def root: org.openide.nodes.Node =
    beingFolderIO >>= (FolderNode.root(_, imm, imm)) unsafePerformIO
}

// vim: set ts=2 sw=2 et:
