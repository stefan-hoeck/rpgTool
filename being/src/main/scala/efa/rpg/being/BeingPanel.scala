package efa.rpg.being

import dire._, dire.swing._, Swing._
import efa.core.{Read, EndoVal}
import efa.nb.{VStSF, WidgetFunctions}
import efa.rpg.core._
import java.awt.Font
import scalaz._, Scalaz._, effect.IO

final case class BeingPanel[A,B,C](p: C, sf: VStSF[A,B]) {
}

trait BeingPanelFunctions extends WidgetFunctions {
  lazy val bold = {
    val f = new javax.swing.JLabel().getFont
    new Font(f.getName, Font.BOLD, f.getSize)
  }

  def disabledNumeric: IO[TextField] =
    TextField(hAlign := HAlign.Trailing, editable := false)

  def enumBox[A:RpgEnum]: IO[ComboBox[A]] = 
    ComboBox(RpgEnum[A].values, RpgEnum[A].valuesNel.head)

  def modifierToolTip[A:HasModifiers](
    k: ModifierKey, format: Long ⇒ String)(a: A): Option[String] =
    prettyModsKey(k, format) apply a some

  def modifiedProp[A:HasModifiers,B,C:TextComponent](
    k: ModifierKey, c: C, format: Long ⇒ String = (l: Long) ⇒ l.toString
  ): VStSF[A,B] =
    tooltipOut[A,B,C](k, c, format) ⊹ 
    outOnly(c.text ∙ { a: A ⇒ format(property(a, k)) })

  def tooltipOut[A:HasModifiers,B,C:Component](
    k: ModifierKey, c: C, format: Long ⇒ String = (l: Long) ⇒ l.toString
  ): VStSF[A,B] =
    outOnly(c.tooltip ∙ modifierToolTip[A](k, format))

  def outOnly[A,B](s: DataSink[A]): VStSF[A,B] = 
    SF.id[A].to(s) >> SF.never

  implicit val ModifierKeyAsElem: AsSingleElem[ModifierKey] =
    new AsSingleElem[ModifierKey] {
      def single(k: ModifierKey) = k.loc.locName.single
    }
}

object BeingPanel extends BeingPanelFunctions

//
//  def unitSET[X:UnitEnum] (
//    t: TextField,
//    x: X,
//    prec: Int,
//    v: EndoVal[Long],
//    l: B @> Long
//  ): VSET[B,B] = textIn[B,Long](
//    t, v, UnitEnum[X] showPretty (x, prec)
//  )(l)(Read readV UnitEnum[X].readPretty(x))
//}

// vim: set ts=2 sw=2 et:
