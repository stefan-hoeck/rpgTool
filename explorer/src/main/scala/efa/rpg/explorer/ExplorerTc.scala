package efa.rpg.explorer

import efa.nb.UndoEdit
import efa.nb.node.FolderNode
import efa.nb.tc.{EfaTc, WithOutline, TcProvider, ExplorerMgrTc}
import efa.rpg.items.controller.undoIn
import efa.rpg.preferences.Preferences.mainLogger
import java.awt.BorderLayout
import javax.swing.text.DefaultEditorKit
import org.openide.awt.UndoRedo
import org.openide.explorer.ExplorerUtils
import org.openide.explorer.view.OutlineView
import scalaz._, Scalaz._, effect.IO

class ExplorerTc (private val p: ExplorerParams)
  extends WithOutline(p._1, none) {
  private def this() = this(ExplorerTc.unsafeParams)

  override protected val outline = new OutlineView(efa.core.loc.name)
  setName (loc.explorerTcName)
  setToolTipText (loc.explorerTcHint)
  
  private def mgr = getExplorerManager
  getActionMap.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(mgr))
  getActionMap.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(mgr))
  getActionMap.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(mgr))
  getActionMap.put("delete", ExplorerUtils.actionDelete(mgr, true))
  
  override protected def preferredID = ExplorerTcProvider.preferredId
  override protected val version = "1.0"
  
  setLayout(new BorderLayout)
  add(outline, BorderLayout.CENTER)
  
  override def getUndoRedo: UndoRedo = p._2
}

object ExplorerTc {

  lazy val create: IO[ExplorerTc] = for {
    p   ← createParams
    _   ← mainLogger debug "Creating ExplorerTc"
    res ← IO (new ExplorerTc (p))
  } yield res

  private lazy val createParams: IO[ExplorerParams] = for {
    n  ← FolderNode forLayerPath "RpgTool/Explorer"
    ur ← UndoEdit.undoManager(undoIn).run
  } yield (n, ur._2)

  private def unsafeParams: ExplorerParams = (for {
    ps ← createParams
    _  ← mainLogger debug "Deserializing ExplorerTc"
  } yield ps).unsafePerformIO
}

object ExplorerTcProvider extends TcProvider[ExplorerTc] (
  ExplorerTc.create, mainLogger
) {
  override protected[explorer] val preferredId = "ExplorerTc"
}

// vim: set ts=2 sw=2 et:
