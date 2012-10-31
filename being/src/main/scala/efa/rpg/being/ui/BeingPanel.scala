package efa.rpg.being.ui
//
//import efa.core.{ValRes}
//import efa.nb.{UiFactory, UiStateController, GbPanel}
//import efa.react.{Events, Out}
//import efa.rpg.being.loc
//import efa.rpg.core.Modifier
//import java.awt.Font
//import scala.swing.{Label, TextField, Alignment}
//import scalaz._, Scalaz._, effects._
//
//trait BeingPanelFactory[D,C] extends UiFactory with UiStateController {
//  import BeingPanelFactory._
//  import UiStateController.VStates
//
//  type EditPanel <: MyPanel
//
//  type FullInfo = (EditPanel, Out[C], VStates[D])
//
//  protected abstract class MyPanel extends GbPanel {
//    def elems: Elem
//  }
//
//  protected def create: IO[EditPanel]
//
//  protected def events (p: EditPanel): IO[VStates[D]]
//
//  protected def out (p: EditPanel): IO[Out[C]]
//
//  final def info: IO[FullInfo] = for {
//    ep ← create
//    _  ← io(ep.elems.add)
//    o  ← out(ep)
//    es ← events(ep)
//  } yield (ep, o, es)
//}
//
//object BeingPanelFactory {
//  lazy val bold = {
//    val f = new Label().font
//    new Font(f.getName, Font.BOLD, f.getSize)
//  }
//
//  def modifierToolTip(mods: Seq[Modifier], format: Long ⇒ String): String = {
//    def sum = mods foldMap (_.value)
//    def head = "<b>%s: %s</b>" format (loc.total, format(sum))
//    def single (m: Modifier) = "%s: %s" format(m.name, format(m.value))
//    def rest = mods map single
//
//    "<html>%s<br>%s</html>" format (head, rest mkString "<br>")
//  }
//}
//
//// vim: set ts=2 sw=2 et:
