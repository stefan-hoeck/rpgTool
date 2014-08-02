package efa.rpg.items

import dire.{SIn, validation, Out}, validation.validate
import dire.swing._, Swing._
import efa.core.{ValRes, ValSt}
import efa.nb.dialog.{DialogEditable, DEInfo}
import efa.nb.node.NodeOut
import efa.rpg.core.{RpgItem, Described}
import scalaz._, Scalaz._, scalaz.effect.IO

package object controller {
  type St[I] = State[IState[I],Unit]

  type VSt[I] = ValRes[St[I]]

  type IEditable[A] = DialogEditable[ItemPair[A],A]

  type FullOut[A] = NodeOut[FolderPair[A],VSt[A]]

  type StOut[A] = NodeOut[IState[A],VSt[A]]

  final val undoManager = new org.openide.awt.UndoRedo.Manager

  private[controller] val undoOut = efa.nb.undo out undoManager

  def editable[A:RpgItem](f: (ItemPair[A],Boolean) ⇒ DEInfo[A],
                          size: Dim ⇒ Dim = sizeF): IEditable[A] =
    DialogEditable.io(f(_,_) map { case (e,s) ⇒ (e adjustSize size, s) })

  private val sizeF: Dim ⇒ Dim =
    { case (w, h) ⇒ (400 max w min 1000, h min 600) }

  private[controller] def rpg[A:RpgItem] = RpgItem[A]

  implicit def FolderEditable[A:Equal] =
    DialogEditable.io1[FolderPair[A],VSt[A]](folderPanel[A])

  implicit def IStateShow[A:Show]: Show[ItemPair[A]] =
    Show.shows(Show[A] shows _._1)

  implicit def FolderPairShow[A]: Show[FolderPair[A]] =
    Show.shows(_ ⇒ loc.folder)

  private def folderPanel[A:Equal](p: FolderPair[A]): DEInfo[VSt[A]] = for {
    name ← TextField(text := loc.folder)
    elem = Elem(efa.core.loc.name) beside name prefWidth 400
    in   = name.in >=> validate(FolderFunctions.nameVal ∘
             { IState.addFolder(p._1,_).success[NonEmptyList[String]] })
  } yield (elem, in)
}

// vim: set ts=2 sw=2 et:
