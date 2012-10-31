package efa.rpg.items.dob

import efa.rpg.preferences.Preferences.mainLogger
import efa.nb.PureLookup
import efa.nb.controller._
import efa.react.Out
import efa.rpg.core.RpgItem
import efa.rpg.items.controller.ItemsInfo
import org.openide.nodes.Node
import org.openide.filesystems.FileObject
import org.openide.loaders.{MultiDataObject, MultiFileLoader, DataFolder, DataObject}
import org.openide.util.{HelpCtx, Lookup}
import scalaz._, Scalaz._, effect.IO

class ItemDo(fo: FileObject, loader: MultiFileLoader) 
   extends MultiDataObject(fo, loader) {
  self ⇒ 

  private[this] lazy val (node, pureLookup) = (for {
    res ← ItemDo.fields(b ⇒ IO(setModified(b)), fo.getName)
    _   ← mainLogger debug ("Loading data object for " + fo.getNameExt)
  } yield res).unsafePerformIO

  override protected def createNodeDelegate = node
  override def isDeleteAllowed = false
  override def isCopyAllowed = false
  override def isMoveAllowed = false
  override def isRenameAllowed = false
  override def getHelpCtx = HelpCtx.DEFAULT_HELP
  override def handleCopy(f: DataFolder) = unsupported
  override def handleMove(f: DataFolder) = unsupported
  override def handleDelete = unsupported
  override def handleRename(s: String) = unsupported
  override def handleCreateFromTemplate(f: DataFolder, s: String) = unsupported
  override protected def getLookup = pureLookup.l
  private def unsupported = throw new UnsupportedOperationException
}

object ItemDo {
  private[dob] def fields (mod: Out[Boolean], name: String)
  : IO[(Node,PureLookup)]= for {
    i   ← ItemsInfo forName name
    lkp ← PureLookup.apply
    _   ← i.changes andThen Saver.events(name) to adjust(lkp, mod) go
  } yield (i.rootNode, lkp)

  private[this] def adjust(pl: PureLookup, modified: Out[Boolean])
    : Out[SaveEvent] = _ match {
      case Registered(s) ⇒
        pl.add(s) >>
        modified(true) >>
        mainLogger.trace(s.findDisplayName + ": Saver registered")
      case Unregistered(s) ⇒
        pl.remove(s) >>
        modified(false) >>
        mainLogger.trace(s.findDisplayName + ": Saver unregistered")
    }
}

// vim: set ts=2 sw=2 et:
