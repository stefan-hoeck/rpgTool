package efa.rpg.being

import dire._, dire.swing._, Swing._
import efa.core.{Read, EndoVal, Validators, Efa}, Efa._
import efa.nb.{VStSF, WidgetFunctions}
import efa.rpg.core._
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.border.Border
import scalaz._, Scalaz._, effect.IO

final case class BeingPanel[A,B,C](p: C, sf: VStSF[A,B])

trait BeingPanelFunctions extends WidgetFunctions {
  lazy val bold = {
    val f = new javax.swing.JLabel().getFont
    new Font(f.getName, Font.BOLD, f.getSize)
  }

  def numeric: IO[TextField] = TextField trailing ""

  def disabledNumeric: IO[TextField] =
    TextField(hAlign := HAlign.Trailing, editable := false)

  def enumBox[A:RpgEnum]: IO[ComboBox[A]] = 
    ComboBox(RpgEnum[A].values, RpgEnum[A].valuesNel.head)

  def intSf[A](sf: SF[String,String],
               v: EndoVal[Int] = Validators.dummy[Int])
              (l: A @> Int): VStSF[A,A] = readSf(sf, v)(l)

  def longSf[A](sf: SF[String,String],
                v: EndoVal[Long] = Validators.dummy[Long])
               (l: A @> Long): VStSF[A,A] = readSf(sf, v)(l)

  def modifierToolTip[A:HasModifiers](
    k: ModifierKey, format: Long ⇒ String)(a: A): Option[String] =
    prettyModsKey(k, format) apply a some

  def modifiedProp[A:HasModifiers,B,C:TextComponent](
    k: ModifierKey, c: C, format: Long ⇒ String = (l: Long) ⇒ l.toString
  ): VStSF[A,B] =
    tooltipOut[A,B,C](k, c, format) ⊹ 
    outOnly(c.text ∙ { a: A ⇒ format(property(a, k)) })

  def readSf[A:Read:Show,B]
    (sf: SF[String,String], v: EndoVal[A])(l: B @> A): VStSF[B,B] = {
    val rs = ((sf >=> read[A]) ∙ { a: A ⇒ a.shows }) >=> reValidate(v)

    lensedV(rs)(l)
  }

  def stringSf[A](sf: SF[String,String],
                  v: EndoVal[String] = Validators.dummy[String])
                 (l: A @> String): VStSF[A,A] = readSf(sf, v)(l)

  def tooltipOut[A:HasModifiers,B,C:Component](
    k: ModifierKey, c: C, format: Long ⇒ String = (l: Long) ⇒ l.toString
  ): VStSF[A,B] =
    outOnly(c.tooltip ∙ modifierToolTip[A](k, format))

  def outOnly[A,B](s: DataSink[A]): VStSF[A,B] = 
    SF.id[A].to(s) >> SF.never

  def unitSf[A,B](
    sf: SF[String,String],
    unit: A,
    prec: Int,
    v: EndoVal[Long],
    l: B @> Long
  )(implicit A: UnitEnum[A]): VStSF[B,B] = {
    def validate(s: String) = A.readPretty(unit)(s) >>= v.run validation

    lensedV(sf ∘ validate ∙ A.showPretty(unit, prec))(l)
  }

  implicit val ModifierKeyAsElem: AsSingleElem[ModifierKey] =
    new AsSingleElem[ModifierKey] {
      def single(k: ModifierKey) = k.loc.locName.single
    }
}

trait BeingPanelInstances {
  implicit def BPAsElem[A,B,C:AsSingleElem]: AsSingleElem[BeingPanel[A,B,C]] =
    new AsSingleElem[BeingPanel[A,B,C]] {
      def single(p: BeingPanel[A,B,C]) = p.p fillV 0
    }
}

object BeingPanel extends BeingPanelFunctions with BeingPanelInstances

// vim: set ts=2 sw=2 et:
