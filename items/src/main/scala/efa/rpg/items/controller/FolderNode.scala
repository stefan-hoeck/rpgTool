package efa.rpg.items.controller

import efa.core._, Efa._
import efa.core.syntax.lookup
import efa.nb.node.{NodeOut, NbChildren, NbNode ⇒ N, PasteType}, NbChildren._
import efa.rpg.items._
import efa.rpg.core.RpgItem
import efa.rpg.items.{FolderFunctions ⇒ FF}
import org.openide.nodes.Node
import scalaz._, Scalaz._, effect.IO

object FolderNode {
  implicit def ItemPairUniqueId[A:RpgItem] =
    UniqueId.contramap{ p: ItemPair[A] ⇒ p._1 }

  implicit def FolderPairUniqueId[A] =
    UniqueId.contramap{ p: FolderPair[A] ⇒ p._1 }

  type FFactory[A] = NbChildren.Factory[FolderPair[A],VSt[A]]

  def name[A]: FullOut[A] = N name (FF.name[A] get _._1)

  def contextRoot[A](isRoot: Boolean): FullOut[A] = {
    val path =
      "ContextActions/ItemFolderNode/" + (isRoot ? "Root" | "Branch")

    N contextRootsA List(path)
  }

  def delete[A:Equal:RpgItem]: FullOut[A] =
    N destroyEs IState.deleteFolder[A] contramap (_._1)

  def rename[A:Equal] (v: FolderPair[A] ⇒ EndoVal[String]): FullOut[A] = 
    N renameEs IState.renameFolder[A] contramap (p ⇒ (p._1, v(p)))

  def renameDefault[A:Equal]: FullOut[A] = rename(_ ⇒ FF.nameVal)

  def copy[A:Manifest:RpgItem:Equal]: FullOut[A] = NodeOut((o, n) ⇒ p ⇒ {
    def adjust (pt: PasteType)(a: A): IO[Unit] = pt match {
      case PasteType.Cut  ⇒ o (IState moveItem (p._1, a) success)
      case PasteType.Move ⇒ o (IState moveItem (p._1, a) success)
      case PasteType.Copy ⇒ o (IState addItem (p._1, a) success)
    }

    def paster = (p: PasteType, n: Node) ⇒ 
      n.getLookup.head[A] >>= (_ map adjust(p) orZero)

    n setPasters List(paster)
  })

  def itemFactory[A:RpgItem](out: ItemNodes.PairOut[A]): FFactory[A] =
    leavesF(out)(FF.folderPairToItems[A])

  def folderFactory[A] (out: FullOut[A]): FFactory[A] =
    uidF(out)(FF.folderPairToFolders[A])

  def itemsNt[A:RpgItem:Equal:IEditable] (as: List[A]): FullOut[A] = {
    val dialog = N.addNtE[ItemPair[A],A]
    def addItem (p: FolderPair[A], a: A) = IState.addItem(p._1, a).success
    def single (a: A): FullOut[A] =
      dialog.contramap[FolderPair[A]](p ⇒ (a, p._2)) withIn addItem

    as foldMap single
  }

  def folderNt[A:Equal]: FullOut[A] = N.addNtE

  def defaultOut[A:Manifest:RpgItem:Equal:IEditable] (
    nodeOut: ItemNodes.PairOut[A], templates: List[A]
  ): FullOut[A] = {
    val ntOut: FullOut[A] =
      (N.clearNt: FullOut[A]) ⊹ folderNt ⊹ itemsNt(templates)

    val restOut: FullOut[A] = delete[A] ⊹ name ⊹ renameDefault ⊹
      ntOut ⊹ contextRoot(false) ⊹ copy

    lazy val allOut: FullOut[A] = restOut ⊹ chldOut
    lazy val fFactory: FFactory[A] = folderFactory (allOut)
    lazy val chldOut: FullOut[A] = children (fFactory, itemFactory (nodeOut))

    chldOut ⊹ name ⊹ ntOut ⊹ contextRoot(true) ⊹ copy
  }
}

// vim: set ts=2 sw=2 et:
