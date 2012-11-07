package efa.rpg.core

import efa.core.{ToXml, Efa, Default}, Efa._
import org.scalacheck.{Arbitrary, Gen}, Arbitrary.arbitrary
import scala.xml.Node
import scalaz._, Scalaz._, scalacheck.ScalaCheckBinding._

case class ItemData(id: Int, name: String, desc: String)

object ItemData {
  self ⇒ 
  lazy val default = ItemData(0, "", "")

  def apply (n: String): ItemData = name set (default, n)

  //Lenses
  val id: ItemData @> Int =
    Lens.lensu((a,b) ⇒ a copy (id = b), _.id)

  val name: ItemData @> String =
    Lens.lensu((a,b) ⇒ a copy (name = b), _.name)

  val desc: ItemData @> String =
    Lens.lensu((a,b) ⇒ a copy (desc = b), _.desc)
  
  implicit def itemDataLenses[A] (l: Lens[A,ItemData]) = new {
    def id = l >=> self.id
    def name = l >=> self.name
    def desc = l >=> self.desc
  }
  
  implicit lazy val ItemDataDefault = Default default default

  implicit lazy val ItemDataEqual = Equal.equalA[ItemData]

  implicit lazy val ItemDataToXml = new ToXml[ItemData] {
    def fromXml (ns: Seq[Node]) =
      ^^(ns.readTag[Int]("id"),
      ns.readTag[String]("name"),
      ns.readTag[String]("desc"))(ItemData.apply)

    def toXml (a: ItemData) = 
      ("id" xml a.id) ++ ("name" xml a.name) ++ ("desc" xml a.desc)
  }

  implicit lazy val ItemDataArbitrary = Arbitrary (
    ^^(arbitrary[Int], Gen.identifier, Gen.identifier)(ItemData.apply)
  )
}

// vim: set ts=2 sw=2 et:
