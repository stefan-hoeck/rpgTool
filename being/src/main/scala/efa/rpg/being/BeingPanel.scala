package efa.rpg.being

import efa.nb.InputWidgets
import efa.react.swing.{GbPanel, UiFactory}
import efa.rpg.core.{Modifier, RpgEnum}
import java.awt.Font
import scala.swing.{Label, ComboBox}
import scalaz._, Scalaz._

trait BeingPanel[A,B] extends GbPanel with InputWidgets with UiFactory {
  def set: VSET[A,B]

  def enumBox[A:RpgEnum]: ComboBox[A] = comboBox[A](RpgEnum[A].valuesNel)
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
