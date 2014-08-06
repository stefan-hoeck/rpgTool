package efa.rpg.items.controller

import efa.core._, Efa._
import efa.nb.dialog.DialogEditable
import efa.nb.node.{NodeOut, NbNode ⇒ N}
import efa.rpg.core.{RpgItem, HtmlDesc}
import efa.rpg.items._
import scalaz._, Scalaz._

trait ItemNodeFunctions {
  type ItemOut[A] = NodeOut[A,VSt[A]]

  type PairOut[A] = NodeOut[ItemPair[A],VSt[A]]

  def name[A:RpgItem]: ItemOut[A] = N.named

  def desc[A:RpgItem]: ItemOut[A] = N.described

  def htmlDesc[A:RpgItem]: ItemOut[A] =
    N.cookie[HtmlDesc,VSt[A]] ∙ rpg.htmlDesc

  def item[A:Unerased]: ItemOut[A] = N.cookie

  def contextRoot[A](implicit M: Unerased[A]): ItemOut[A] = {
    val cn = M.clazz.getCanonicalName replace (".", "-")
    val base = "ContextActions/RpgItemNode/"

    N contextRootsA List(base + "All", base + cn)
  }

  def delete[A:Equal:RpgItem]: PairOut[A] =
    N destroyEs IState.deleteItem[A] contramap (_._1)

  def rename[A:Equal:RpgItem] (v: ItemPair[A] ⇒ EndoVal[String])
    : PairOut[A] =
    N renameEs IState.renameItem[A] contramap (p ⇒ (p._1, v(p)))

  def renameDefault[A:Equal:RpgItem]: PairOut[A] =
    rename(IState.nameVal[A](false))

  def edit[A:Equal:RpgItem:IEditable]: PairOut[A] =
    N editS IState.updateItem[A]

  def defaultOut[A:Unerased:Equal:RpgItem:IEditable]: PairOut[A] =
    renameDefault[A] ⊹ edit ⊹ delete ⊹ 
    (name ⊹ desc ⊹ contextRoot ⊹ htmlDesc ⊹ item).contramap(_._1)
}

object ItemNodes extends ItemNodeFunctions
