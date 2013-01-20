package efa.rpg.core

import efa.core.{ToXml, Default}
import efa.data.{UniqueIdL, NamedL}
import scalaz._, Scalaz._

trait RpgItem[A] extends Described[A] with UniqueIdL[A,Int] with NamedL[A] {
  def dataL: A @> ItemData
  lazy val nameL: A @> String = dataL.name
  lazy val idL: A @> Int = dataL.id
  lazy val descL: A @> String = dataL.desc

  def desc (a: A): String = descL get a
}

trait RpgItemLike[+A] {
  def data: ItemData
  def name: String = data.name
  def id: Int = data.id
  def desc: String = data.desc
  def data_= (v: ItemData): A
}

object RpgItem {
  def apply[A:RpgItem]: RpgItem[A] = implicitly
}

/**
 * Helper functions and implicits. Best used for companion
 * object.
 */
trait RpgItemLikes[A<:RpgItemLike[A]] extends Util {
  self ⇒ 

  def default: A
  def shortDesc (a: A): String
  def fullDesc (a: A): String = titleBody (a.name, a.desc)

  import scala.xml.Node

  private def idXml = ToXml[ItemData]

  protected def tagShortDesc (a: A, tags: Tag*): String =
    nameShortDesc(a.name, tags: _*)

  protected def dataToNode (a: A): Seq[Node] = idXml toXml a.data

  protected def readData (ns: Seq[Node]) = idXml fromXml ns

  implicit lazy val asRpgItem = new RpgItem[A] {
    lazy val dataL = Lens.lensu[A,ItemData](_ data_= _, _.data)
    def shortDesc (a: A) = self shortDesc a
    def fullDesc (a: A) = self fullDesc a
  }

  implicit lazy val asDefault = Default default default

  implicit lazy val asEqual = Equal.equalA[A]
}

// vim: set ts=2 sw=2 et:
