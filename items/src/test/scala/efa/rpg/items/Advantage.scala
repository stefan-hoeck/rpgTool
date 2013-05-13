package efa.rpg.items

import dire.swing._, Swing._
import efa.core._, Efa._
import efa.nb.dialog.{DialogEditable, DEInfo}
import efa.rpg.core.{RpgItem, RpgItemLike, RpgItemLikes, ItemData}
import efa.rpg.items.controller.{ItemPanelFunctions, editable}
import org.scalacheck._, Prop._, Arbitrary._
import scalaz._, Scalaz._, effect._, scalacheck.ScalaCheckBinding._

case class Advantage(data: ItemData, gp: Int)
   extends RpgItemLike[Advantage] {
  def data_= (v: ItemData) = copy (data = v)
}

object Advantage extends RpgItemLikes[Advantage] {
  def ap[F[_]:Applicative] = Applicative[F].liftA(Advantage.apply _)

  val default = Advantage (!!, 0)

  def shortDesc (a: Advantage) = a.desc

  override def fullDesc (a: Advantage) = a.desc

  val data: Advantage @> ItemData =
    Lens.lensu((a,b) ⇒ a copy (data = b), _.data)
  
  import scala.xml.Node

  implicit val AdvantageXml = new ToXml[Advantage] {
    def toXml (a: Advantage) = dataToNode(a) ++ ("gp" xml a.gp)

    def fromXml (ns: Seq[Node]): ValRes[Advantage] =
      ap[ValRes] apply (readData(ns), ns.readTag[Int]("gp"))
  }

  implicit val AdvantageArbitrary: Arbitrary[Advantage] = Arbitrary (
    ap[Gen] apply (a[ItemData], a[Int])
  )

  implicit lazy val AdvantageEqual: Equal[Advantage] = Equal.equalA

  import controller.itemPanel._

  implicit lazy val AdvantageEditable = editable[Advantage] { (s,b) ⇒
    for {
      dw ← dataWidgets(s, b)
      gp ← TextField trailing item(s).gp.toString

      elem = ("Name" above "Gp" above "Desc") beside 
             (dw.name above gp above dw.sp)

      in = dw.in ⊛ (gp.in >=> read[Int]) apply Advantage.apply
    } yield (elem, in)
  }
}

// vim: set ts=2 sw=2 et:
