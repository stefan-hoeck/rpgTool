package efa.rpg.items.controller

import dire.ValidationFunctions, dire.validation.VSIn
import dire.swing.{Dim, TextField}
import efa.core.{Validators, EndoVal}
import efa.rpg.core.{RpgItem, ItemData, UnitEnum}
import scalaz._, Scalaz._, effect.IO

trait ItemPanelFunctions extends ValidationFunctions {
  def unitIn[A:UnitEnum](a: A, t: TextField, v: EndoVal[Long])
  : VSIn[Long] = t.value >=> validate(UnitEnum[A].readPretty(a) >=> v)

  def unitOut[A:UnitEnum](a: A, prec: Int, v: Long)
    : IO[TextField] = TextField.trailing(UnitEnum[A].showPretty(a, prec)(v))
}

object itemPanel extends ItemPanelFunctions

// vim: set ts=2 sw=2 et:
