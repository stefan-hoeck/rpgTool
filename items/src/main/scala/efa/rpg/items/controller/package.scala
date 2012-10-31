package efa.rpg.items

import efa.core.ValRes
import efa.nb.{UndoEdit, VSIn}
import efa.nb.dialog.DialogEditable
import efa.nb.node.NbNode.ValSt
import efa.nb.node.NodeOut
import efa.rpg.core.RpgItem
import efa.react.{EIn, Source, Out, eTrans, SST}
import scala.swing.Component
import scalaz.{Equal, State, Show}
import scalaz.effect.IO

package object controller {
  type St[I] = State[IState[I],Unit]

  type VSt[I] = ValRes[St[I]]

  type IEditable[A] = DialogEditable[ItemPair[A],A]

  type FullOut[A] = NodeOut[FolderPair[A],VSt[A]]

  type StOut[A] = NodeOut[IState[A],VSt[A]]

  private[this] val cached = efa.io.IOCached (Source[UndoEdit])

  lazy val undoIn: EIn[UndoEdit] = eTrans inIO cached.get

  lazy val undoOut: Out[UndoEdit] = ue ⇒ cached.get flatMap (_ fire ue)

  def undoTrans[A]: SST[A,A] = UndoEdit undoSST undoOut

  def editable[A:RpgItem,C<:ItemPanel[A]](p: ItemPair[A] ⇒ IO[C])
    : IEditable[A] = DialogEditable.io[ItemPair[A],A,C](
      ip ⇒ for { c ← p(ip); _ ← c.adjust } yield c)(_.in)

  private[controller] def rpg[A:RpgItem] = RpgItem[A]

  implicit def FolderEditable[A:Equal] =
    new DialogEditable[FolderPair[A],VSt[A]] {
      type Comp = FolderPanel[A]
      def component (p: FolderPair[A]) = IO(new FolderPanel(p))
      def signalIn (c: Comp) = c.in
      override def name (p: FolderPair[A]) = loc.folder
    }

  implicit def IStateShow[A:Show]: Show[ItemPair[A]] =
    Show.shows(Show[A] shows _._1)
}

// vim: set ts=2 sw=2 et:
