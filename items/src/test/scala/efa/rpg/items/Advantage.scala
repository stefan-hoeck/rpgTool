package efa.rpg.items

import efa.core._, Efa._
import efa.nb.dialog.DialogEditable
import efa.rpg.core.{RpgItem, RpgItemLike, RpgItemLikes, ItemData}
import efa.rpg.items.controller.{ItemPanel, editable}
import org.scalacheck._, Prop._, Arbitrary._
import scalaz._, Scalaz._, effect._, scalacheck.ScalaCheckBinding._

case class Advantage(data: ItemData, gp: Int)
   extends RpgItemLike[Advantage] {
  def data_= (v: ItemData) = copy (data = v)
}

object Advantage extends RpgItemLikes[Advantage] {
  val default = Advantage (!!, 0)

  def shortDesc (a: Advantage) = a.desc

  override def fullDesc (a: Advantage) = a.desc

  val data: Advantage @> ItemData =
    Lens.lensu((a,b) ⇒ a copy (data = b), _.data)
  
  import scala.xml.Node

  implicit val AdvantageXml = new ToXml[Advantage] {
    def toXml (a: Advantage) = dataToNode(a) ++ ("gp" xml a.gp)

    def fromXml (ns: Seq[Node]): ValRes[Advantage] =
      ^(readData(ns), ns.readTag[Int]("gp"))(Advantage.apply)
  }

  implicit val AdvantageArbitrary: Arbitrary[Advantage] = Arbitrary (
    ^(a[ItemData], a[Int])(Advantage.apply)
  )

  implicit lazy val AdvantageEqual: Equal[Advantage] = Equal.equalA

  implicit lazy val AdvantageEditable = editable(AdvantagePanel.create)
}

case class AdvantagePanel(p: ItemPair[Advantage])
   extends ItemPanel[Advantage](p) {
  val gpC = numField (item.gp.toString)

  def in  = ^(dataIn, intIn(gpC))(Advantage.apply)

  protected def elems =
    ("Name" above "Gp" above "Desc") beside (nameC above gpC above descElem)
}

object AdvantagePanel {
  def create (s: ItemPair[Advantage]) = IO(new AdvantagePanel(s))
}

// vim: set ts=2 sw=2 et:
