package efa.rpg.core

import efa.core._
import scalaz._, Scalaz._
import scala.xml.{Node, Text}

trait LocEnum[A]
   extends RpgEnum[A]
   with Localized[A] 
   with Show[A]
   with Read[A]
   with ToXml[A] {

  lazy val map: Map[String, A] =
    values flatMap (a ⇒ names(a) map (_ → a)) toMap

  override def read (s: String): ValRes[A] =
    map get s toSuccess (efa.core.loc notFoundMsg s wrapNel)

  override def shows (a: A): String = loc(a).locName

  def toXml(a: A): Seq[Node] = Text(loc(a).name) 

  def fromXml (ns: Seq[Node]): ValRes[A] = read(ns.text)
}

object LocEnum {
  def apply[A:LocEnum]: LocEnum[A] = implicitly

  def values[A<:IsLocalized](a: A, as: A*): LocEnum[A] =
    new LocEnum[A] {
      override val valuesNel = NonEmptyList(a, as: _*)
      override def loc (a: A): Localization = a.loc
    }

  def tagged[A<:IsLocalized](a: A, as: A*)(t: String)
    : LocEnum[A] with TaggedToXml[A] =
    new LocEnum[A] with TaggedToXml[A] {
      override val valuesNel = NonEmptyList(a, as: _*)
      override def loc (a: A): Localization = a.loc
      override def tag = t
    }
}

// vim: set ts=2 sw=2 et:
