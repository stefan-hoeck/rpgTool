package efa.rpg.items.controller

//import efa.core._, Efa._
//import efa.nb.controller.StateTransFunctions
//import efa.nb.dialog.DialogEditable
//import efa.nb.node.NbNode
//import efa.rpg.core.{RpgItem, DB}
//import efa.rpg.items.saver.ItemSaver
//import efa.rpg.preferences.{Preferences ⇒ Pref}
//import efa.io.{LoggerIO, IOCached}
//import efa.react.{Signal, SIn, sTrans}
//import efa.rpg.items._
//import efa.rpg.items.saver.ItemSaver.xmlSaver
//import org.openide.nodes.Node
//import scalaz._, Scalaz._, effect.IO
//
///**
// * An ItemController provides the main functionality
// * of handling, storing, and displaying RpgItems.
// *
// * The actual items are stored in a Signal as objects of type
// * IState[I]. The controller provides a Node for displaying
// * items in the UI, as well as an event stream of IO actions
// * that save the Signal's actual value. The latter two (node
// * and save actions) are provided in form of an ItemsInfo
// * object and are used by the corresponding DataObjects to
// * display (via the Node) and save (via the save actions event
// * stream) the controller's actual state.
// *
// * The state of the
// * Node is automatically updated when the Signal changes, which
// * typcially happens when the user modifies the state in the UI
// * via the Node and its children.
// */
//final class ItemController[I] private (
//  is: Signal[IState[I]],
//  saver: ItemSaver[I],
//  l: LoggerIO,
//  n: Node
//) {
//  lazy val isIn: SIn[IState[I]] = sTrans in is
//
//  lazy val dbIn: SIn[DB[I]] = isIn map (_.map)
//
//  lazy val info: ItemsInfo =
//    ItemsInfo(n, isIn.events map saver.saveState(l))
//}
//
//object ItemController extends StateTransFunctions {
//
//  /**
//   * Creates an ItemController from a saver and a NodeOut
//   */
//  def apply[I:RpgItem:Equal] (
//    saver: ItemSaver[I],
//    nodeOut: List[I] ⇒ StOut[I],
//    ioLog: LoggerIO = Pref.mainLogger,
//    valLog: LoggerIO = Pref.itemsLogger
//  ): IO[ItemController[I]] = for {
//    n   ← NbNode.apply //The Node used to display items in the UI
//
//    //Signal[IState[I]] → Events[ValRes[State[IState[I]],Unit]]
//    set ← saver loadTemplates ioLog map (nodeOut(_) set n)
//
//    udSS = toSST(set to valLog.logValRes) >=> undoTrans
//
//    //Signal[IState[I]]
//    sig ← sTrans.loop(udSS)(saver loadState ioLog).distinct go
//  } yield new ItemController[I](sig._2, saver, ioLog, n)
//
//  def default[I:RpgItem:Equal:ToXml:Manifest:IEditable](
//    fileName: String, lblName: String, cl: Class[_]
//  ): IO[ItemController[I]] = {
//    val saver = xmlSaver[I](fileName, lblName, cl)
//    val itemOut = ItemNodes.defaultOut[I]
//    def nodeOut (ts: List[I]): StOut[I] =
//      FolderNode.defaultOut(itemOut, ts)∙ (s ⇒ (s.root, s))
//
//    apply(saver, nodeOut)
//  }
//}

// vim: set ts=2 sw=2 et:
