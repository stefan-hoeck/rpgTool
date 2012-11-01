package efa.rpg.being

import efa.core.{Read, EndoVal}
import efa.nb.InputWidgets
import efa.react.swing.{GbPanel, UiFactory}
import efa.rpg.core.{Modifier, RpgEnum, UnitEnum}
import java.awt.Font
import scala.swing.{Label, ComboBox, TextField}
import scalaz._, Scalaz._

trait BeingPanel[A,B] extends GbPanel with InputWidgets with UiFactory {
  def set: VSET[A,B]

  def enumBox[X:RpgEnum]: ComboBox[X] = comboBox[X](RpgEnum[X].valuesNel)

  def unitSET[X:UnitEnum] (
    t: TextField,
    x: X,
    prec: Int,
    v: EndoVal[Long],
    l: B @> Long
  ): VSET[B,B] = textIn[B,Long](
    t, v, UnitEnum[X] showPretty (x, prec)
  )(l)(Read readV UnitEnum[X].readPretty(x))
}

object BeingPanel{
  lazy val bold = {
    val f = new scala.swing.Label().font
    new Font(f.getName, Font.BOLD, f.getSize)
  }

  def modifierToolTip(
    mods: List[Modifier],
    format: Long ⇒ String = (l: Long) ⇒ l.toString
  ): String = {
    def sum = mods foldMap (_.value)
    def head = "<b>%s: %s</b>" format (loc.total, format(sum))
    def single (m: Modifier) = "%s: %s" format(m.name, format(m.value))
    def rest = mods map single

    "<html>%s<br>%s</html>" format (head, rest mkString "<br>")
  }
}

// vim: set ts=2 sw=2 et:
