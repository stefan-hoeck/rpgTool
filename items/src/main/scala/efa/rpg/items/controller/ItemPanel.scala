package efa.rpg.items.controller

import dire.validation.VSIn
import dire.swing._, Swing._
import efa.core.{Validators, EndoVal}
import efa.nb.WidgetFunctions
import efa.rpg.core.{RpgItem, ItemData, UnitEnum}
import efa.rpg.items.{ItemPair, IState}
import scalaz._, Scalaz._, effect.IO

final class ItemDataUI[A](
    val p: ItemPair[A],
    v: EndoVal[String],
    val name: TextField,
    val desc: TextArea,
    val sp: ScrollPane)(implicit A: RpgItem[A]) {
  import dire.validation.{SfVApplicative, validate, success}
  def item: A = p._1

  def id: Int = A id item

  def in: VSIn[ItemData] =
    id.η[VSIn] ⊛
    (name.in >=> validate(v)) ⊛
    (desc.in >=> success) apply ItemData.apply
}

trait ItemPanelFunctions extends WidgetFunctions {
  def dataWidgets[A](p: ItemPair[A], isCreate: Boolean)
                    (implicit A: RpgItem[A]): IO[ItemDataUI[A]] = for {
    name ← TextField(text := A.name(p._1))
    desc ← TextArea(text := A.desc(p._1))
    sp   ← ScrollPane(desc)
  } yield new ItemDataUI(p, IState.nameVal(isCreate)(p), name, desc, sp)

  def item[A](p: ItemPair[A]): A = p._1

  def unitIn[A:UnitEnum](a: A, t: TextField, v: EndoVal[Long])
  : VSIn[Long] = t.in >=> validate(UnitEnum[A].readPretty(a) >=> v)

  def unitOut[A:UnitEnum](a: A, prec: Int, v: Long)
    : IO[TextField] = TextField.trailing(UnitEnum[A].showPretty(a, prec)(v))
}

object itemPanel extends ItemPanelFunctions

// vim: set ts=2 sw=2 et:
