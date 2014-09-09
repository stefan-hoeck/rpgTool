package efa.rpg.core

import efa.core.{ToXml, Default, UIdL, NamedL, Id, Name, Desc, uidl, idL}
import efa.core.typeclass._
import efa.core.syntax.lens
import org.scalacheck.Arbitrary
import scalaz.Equal

final case class ItemData(id: Id, name: Name, desc: Desc)

object ItemData {
  val L = idL[ItemData]

  def apply(n: String): ItemData =
    (L >> 'name).set(defaultInst.default, Name(n))

  implicit val defaultInst: Default[ItemData] = Default.derive
  implicit val equalInst: Equal[ItemData] = equal
  implicit val toXmlInst: ToXml[ItemData] = ToXml.derive
  implicit val arbInst: Arbitrary[ItemData] = arbitrary
  implicit val uidlInst: UIdL[ItemData] = uidl(L >> 'id)
  implicit val nameLInst: NamedL[ItemData] = NamedL lens (L >> 'name)
}

// vim: set ts=2 sw=2 et:
