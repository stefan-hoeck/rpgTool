package efa.rpg.explorer

//import efa.nb.UndoEdit
import efa.nb.node.FolderNode
import efa.nb.tc.{OutlineNb, WithOutline, TcProvider, ExplorerMgrTc}
import efa.rpg.preferences.Preferences.mainLogger
import java.awt.BorderLayout
import javax.swing.text.DefaultEditorKit
import org.openide.explorer.ExplorerUtils
import org.openide.explorer.view.OutlineView
import scalaz._, Scalaz._, effect.IO

class ExplorerTc (private val p: ExplorerParams)
  extends WithOutline(p._1, p._2, none) {
  private def this() = this(ExplorerTc.loadParams.unsafePerformIO)

  setName(loc.explorerTcName)
  setToolTipText(loc.explorerTcHint)
  
  private def mgr = getExplorerManager
  getActionMap.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(mgr))
  getActionMap.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(mgr))
  getActionMap.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(mgr))
  getActionMap.put("delete", ExplorerUtils.actionDelete(mgr, true))
  
  override protected def preferredID = ExplorerTcProvider.preferredId
  override protected val version = "1.0"
  override protected def initialize = IO.ioUnit
  override protected def cleanup = IO.ioUnit
  
  setLayout(new BorderLayout)
  add(outlineNb.peer, BorderLayout.CENTER)
  
  override def getUndoRedo = efa.rpg.items.controller.undoManager
}

object ExplorerTc {

  lazy val create: IO[ExplorerTc] = for {
    p   ← createParams
    _   ← mainLogger debug "Creating ExplorerTc"
  } yield new ExplorerTc(p)

  private lazy val createParams: IO[ExplorerParams] =
    ^(OutlineNb(), FolderNode forLayerPath "RpgTool/Explorer")(Pair.apply)

  private def loadParams = for {
    ps ← createParams
    _  ← mainLogger debug "Deserializing ExplorerTc"
  } yield ps
}

object ExplorerTcProvider
  extends TcProvider[ExplorerTc](ExplorerTc.create, "ExplorerTc"){}

// vim: set ts=2 sw=2 et:
