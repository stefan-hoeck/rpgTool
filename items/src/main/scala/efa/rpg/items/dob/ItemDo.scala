package efa.rpg.items.dob

import dire._
import efa.rpg.preferences.Preferences.mainLogger
import efa.nb.{PureLookup, NbSystem}
import efa.nb.controller._
import efa.rpg.core.RpgItem
import efa.rpg.items.controller.ItemsInfo
import org.netbeans.api.actions.Savable
import org.openide.nodes.Node
import org.openide.filesystems.FileObject
import org.openide.loaders.{MultiDataObject, MultiFileLoader, DataFolder, DataObject}
import org.openide.util.{HelpCtx, Lookup}
import scalaz._, Scalaz._, effect.IO

class ItemDo(val fo: FileObject, loader: MultiFileLoader) 
  extends MultiDataObject(fo, loader) {
  self ⇒ 
  private[this] lazy val key: AnyRef = new Object

  private[this] lazy val (node, pureLookup) = (for {
    res ← ItemDo.fields(mod, fo.getName, key)
    _   ← mainLogger debug ("Loading data object for " + fo.getNameExt)
  } yield res).unsafePerformIO

  def mod: Out[Boolean] = b ⇒ IO(setModified(b))
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
  private[dob] def fields(mod: Out[Boolean], name: String, key: AnyRef)
    : IO[(Node,PureLookup)]= for {
    i   ← ItemsInfo forName name
    lkp ← PureLookup()
    si  = SavableInfo(key, name, saveOut(lkp, mod)) 
    _   ← NbSystem forever i.changes(si)
  } yield (i.rootNode, lkp)

  private[this] def saveOut(pl: PureLookup, mod: Out[Boolean])
    : Out[Savable \/ Savable] = {
      def unreg(s: Savable) = 
        pl.remove(s) >>
        mod(false) >>
        mainLogger.trace(s"$s: Saver unregistered")

      def reg(s: Savable) = 
        pl.add(s) >>
        mod(true) >>
        mainLogger.trace(s"$s: Saver registered")

      _ fold (unreg, reg)
    }
}

// vim: set ts=2 sw=2 et:
