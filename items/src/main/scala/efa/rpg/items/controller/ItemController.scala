package efa.rpg.items.controller

import dire._, dire.control.Var, dire.swing.{swingSink, swingOut}
import efa.core._, Efa._
import efa.nb.VStSF
import efa.nb.controller.{StateTrans ⇒ ST, SavableInfo, Saver}
import efa.nb.dialog.DialogEditable
import efa.nb.node.{NbNode, NodeOut}
import efa.rpg.core.{RpgItem, DB}
import efa.rpg.items.saver.ItemSaver
import efa.rpg.preferences.{Preferences ⇒ Pref}
import efa.io.LoggerIO
import efa.rpg.items._
import efa.rpg.items.saver.ItemSaver.xmlSaver
import org.openide.nodes.Node
import scalaz._, Scalaz._, effect.IO

/**
 * An ItemController provides the main functionality
 * of handling, storing, and displaying RpgItems.
 *
 * The actual items are stored in a Signal as objects of type
 * IState[I]. The controller provides a Node for displaying
 * items in the UI, as well as an event stream of IO actions
 * that save the Signal's actual value. The latter two (node
 * and save actions) are provided in form of an ItemsInfo
 * object and are used by the corresponding DataObjects to
 * display (via the Node) and save (via the save actions event
 * stream) the controller's actual state.
 *
 * The state of the
 * Node is automatically updated when the Signal changes, which
 * typcially happens when the user modifies the state in the UI
 * via the Node and its children.
 */
final class ItemController[I:RpgItem:Equal] private (
  is: Var[Option[IState[I]]],
  saver: ItemSaver[I],
  ioLog: LoggerIO,
  valLog: LoggerIO,
  nodeOut: StOut[I],
  node: NbNode
) {
  lazy val itemsIn: SIn[IState[I]] = is.in collectO identity

  lazy val dbIn: SIn[DB[I]] = itemsIn map (_.map)

  lazy val info: ItemsInfo = ItemsInfo(node,
    si ⇒ itemsIn >=> Saver.sf(si, saver.saveState(ioLog)) void)

  /** Use for unit tests */
  lazy val testIn: SIn[DB[I]] = sf >> dbIn

  private[controller] def sf: SIn[IState[I]] = testSF(_ ⇒ IO.ioUnit)

  private[controller] def testSF(o: Out[Any]): SIn[IState[I]] = {
    //SF for user interface plus logging of invalid input
    def uiSf = nodeOut sfSim (node, o) to swingSink(valLog.logValRes)

    ST.completeIsolated(uiSf, undoOut)(
      saver loadState ioLog) asyncTo { is put _.some }
  }
}

object ItemController {

  /**
   * Creates an ItemController from a saver and a NodeOut
   */
  def apply[I:RpgItem:Equal] (
    saver: ItemSaver[I],
    nodeOut: List[I] ⇒ StOut[I],
    isTest: Boolean,
    ioLog: LoggerIO = Pref.mainLogger,
    valLog: LoggerIO = Pref.itemsLogger
  ): IO[ItemController[I]] = 
    create(saver, nodeOut, ioLog, valLog, isTest)

  def default[I:RpgItem:Equal:ToXml:Manifest:IEditable](
    fileName: String, lblName: String, cl: Class[_], isTest: Boolean)
  : IO[ItemController[I]] = {
    val saver = xmlSaver[I](fileName, lblName, cl)
    val itemOut = ItemNodes.defaultOut[I]
    def nodeOut(ts: List[I]): StOut[I] =
      FolderNode.defaultOut(itemOut, ts) ∙ (s ⇒ (s.root, s))

    apply(saver, nodeOut, isTest)
  }

  private[controller] def create[I:RpgItem:Equal] (
    saver: ItemSaver[I],
    nodeOut: List[I] ⇒ StOut[I],
    ioLog: LoggerIO,
    valLog: LoggerIO,
    isTest: Boolean
  ): IO[ItemController[I]] = for {
    n   ← NbNode() //The Node used to display items in the UI
    v   ← Var newVar none[IState[I]]
    ts  ← saver loadTemplates ioLog
    out = nodeOut(ts)
    ic  = new ItemController[I](v, saver, ioLog, valLog, out, n)
    _   ← isTest ? IO(IO.ioUnit) | efa.nb.NbSystem.forever(ic.sf)
  } yield ic
}

// vim: set ts=2 sw=2 et:
