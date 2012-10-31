package efa.rpg.items.controller

import efa.core.Validators
import efa.nb.VSIn
import efa.nb.dialog.DialogPanel
import efa.rpg.core.{RpgItem, ItemData}
import efa.rpg.items.{ItemPair, IState}
import scala.swing.GridBagPanel.Fill
import scala.swing.{TextArea, ScrollPane, Label}
import scalaz._, Scalaz._, effect.IO

abstract class ItemPanel[I:RpgItem](p: ItemPair[I]) extends DialogPanel {
  def item = p._1
  lazy val nameC = textField (RpgItem[I] name item)
  lazy val descC = new TextArea (RpgItem[I] desc item)
  lazy val descPane = new ScrollPane(descC)

  protected def descElem: Single = descPane fillV 1
  protected def descLbl: Single =
    Single (new Label(efa.core.loc.desc), f = Fill.None, wx = 0D)

  protected def nameVal = IState nameVal p

  protected lazy val dataIn: VSIn[ItemData] =
    ^(RpgItem[I].id(item).η[VSIn],
    stringIn (nameC, nameVal),
    stringIn (descC))(ItemData.apply)

  def in: VSIn[I]
  protected def elems: Elem

  protected def sizeF: (Int, Int) ⇒ (Int, Int) =
    (w, h) ⇒ (400 max w min 1000, h min 600)

  final def adjust: IO[Unit] =  IO{elems.add(); adjSize (sizeF)}
}

// vim: set ts=2 sw=2 et:
