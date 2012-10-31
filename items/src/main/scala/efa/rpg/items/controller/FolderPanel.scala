package efa.rpg.items.controller

import efa.nb.VSIn
import efa.nb.dialog.DialogPanel
import efa.rpg.items.{FolderFunctions, IState, FolderPair, loc}
import scalaz._, Scalaz._

class FolderPanel[A] (p: FolderPair[A]) extends DialogPanel {
  val nameC = textField (loc.folder)

  efa.core.loc.name beside nameC add()

  setWidth(400)

  def in (implicit E: Equal[A]): VSIn[VSt[A]]  =
    stringIn (nameC, FolderFunctions.nameVal) ∘
    (IState.addFolder(p._1, _).success)
}

// vim: set ts=2 sw=2 et:
