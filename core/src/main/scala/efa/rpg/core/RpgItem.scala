package efa.rpg.core

import efa.core.{UniqueId, ToXml, Default}
import scalaz._, Scalaz._

trait RpgItem[A] extends Described[A] with WithId[A] {
  def dataL: A @> ItemData
  lazy val nameL: A @> String = dataL.name
  lazy val idL: A @> Int = dataL.id
  lazy val descL: A @> String = dataL.desc

  def id (a: A) = idL get a
  override def name (a: A) = nameL get a
  override def desc (a: A) = descL get a
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

  type Tag = Pair[String,String]

  def titleBody (title: String, body: String) =
    "<P><B>%s</B></P>%s" format (title, body)

  def html (title: String, body: String) =
    "<html>%s</html>" format titleBody(title, body)

  def tagShortDesc (a: A, tags: Tag*) = {
    def wrap(t: Tag) = "<P><B>%s: </B>%s</P>" format (t._1, t._2)

    html (a.name, tags map wrap mkString "")
  }

  import scala.xml.Node

  private def idXml = ToXml[ItemData]

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
