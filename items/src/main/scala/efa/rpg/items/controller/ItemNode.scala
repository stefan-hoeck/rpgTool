package efa.rpg.items.controller

import efa.core._, Efa._
import efa.nb.dialog.DialogEditable
import efa.nb.node.{NodeOut, NbNode ⇒ N}
import efa.rpg.core.{RpgItem, HtmlDesc}
import efa.rpg.items._
import scalaz._, Scalaz._

trait ItemNodeFunctions {
  type OutOnly[-A] = NodeOut[A,Nothing]

  type FullOut[A] = NodeOut[ItemPair[A],VSt[A]]

  def name[A:RpgItem]: OutOnly[A] = N name rpg.name

  def desc[A:RpgItem]: OutOnly[A] = N desc rpg.shortDesc

  def htmlDesc[A:RpgItem]: OutOnly[A] = N.cookie[HtmlDesc] ∙ rpg.htmlDesc

  def item[A:Manifest]: OutOnly[A] = N.cookie

  def contextRoot[A](implicit M: Manifest[A]): OutOnly[A] = {
    val cn = M.runtimeClass.getCanonicalName replace (".", "-")
    val base = "ContextActions/RpgItemNode/"

    N contextRootsA List(base + "All", base + cn)
  }

  def delete[A:Equal:RpgItem]: FullOut[A] =
    N destroyEs IState.deleteItem[A] contramap (_._1)

  def rename[A:Equal:RpgItem] (v: ItemPair[A] ⇒ EndoVal[String])
    : FullOut[A] =
    N renameEs IState.renameItem[A] contramap (p ⇒ (p._1, v(p)))

  def renameDefault[A:Equal:RpgItem]: FullOut[A] =
    rename(IState.nameVal[A])

  def edit[A:Equal:RpgItem:IEditable]: FullOut[A] =
    N editDialogEs IState.updateItem[A]

  def defaultOut[A:Manifest:Equal:RpgItem:IEditable]: FullOut[A] =
    renameDefault[A] ⊹ edit ⊹ delete ⊹ 
    (name ⊹ desc ⊹ contextRoot ⊹ htmlDesc ⊹ item).contramap(_._1)
}

object ItemNodes extends ItemNodeFunctions
