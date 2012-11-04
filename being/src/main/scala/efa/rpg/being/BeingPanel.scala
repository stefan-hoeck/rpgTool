package efa.rpg.being

import efa.core.{Read, EndoVal}
import efa.nb.InputWidgets
import efa.react.{Out}
import efa.react.swing.{GbPanel, UiFactory, Swing}
import efa.rpg.core._
import java.awt.Font
import scala.swing.{Label, ComboBox, TextField, Component}
import scalaz._, Scalaz._

trait BeingPanel[A,B] extends GbPanel with InputWidgets with UiFactory {
  import BeingPanel._

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

  def modifiedProp (
    k: ModifierKey, format: Long ⇒ String = (l: Long) ⇒ l.toString
  )(t: TextField)(implicit M:HasModifiers[A]): VSET[A,B] =
    tooltipOut(k, format)(t) ⊹ 
    outOnly((a: A) ⇒ Swing.text(t)(format(property(a, k))))

  def tooltipOut (
    k: ModifierKey, format: Long ⇒ String = (l: Long) ⇒ l.toString
  )(c: Component)(implicit M:HasModifiers[A]): VSET[A,B] =
    outOnly(a ⇒ Swing.tooltip(c)(modifierToolTip(a, k, format)))


  implicit def ModifierKey2Elem (k: ModifierKey): Elem = k.loc.locName
}

object BeingPanel{
  lazy val bold = {
    val f = new scala.swing.Label().font
    new Font(f.getName, Font.BOLD, f.getSize)
  }

  def modifierToolTip[A:HasModifiers](
    a: A, k: ModifierKey, format: Long ⇒ String
  ): String = {
    def head = "<b>%s: %s</b>" format (loc.total, format(property(a, k)))
    def single (m: Modifier) = "%s: %s" format(m.name, format(m.value))
    def rest = mods(a, k) map single

    "<html>%s<br>%s</html>" format (head, rest mkString "<br>")
  }

  def mods[A:HasModifiers] (a: A, k: ModifierKey): List[Modifier] =
    HasModifiers[A] modifiers a get k

  def property[A:HasModifiers] (a: A, k: ModifierKey): Long =
    HasModifiers[A] modifiers a property k
}

// vim: set ts=2 sw=2 et:
