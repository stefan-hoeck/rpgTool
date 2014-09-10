package efa.rpg.core

import efa.core.{Localized, Read, ToXml, IsLocalized, ValRes, Localization}
import org.scalacheck.{Arbitrary, Properties}
import scalaz.{Show, NonEmptyList}
import scala.xml.{Node, Text}
import scalaz.syntax.std.option._
import scalaz.syntax.nel._

trait LocEnum[A]
   extends RpgEnum[A]
   with Localized[A] 
   with Show[A]
   with Read[A]
   with ToXml[A] {

  lazy val map: Map[String, A] = values flatMap { a ⇒
    (a.toString :: names(a)) map (_ → a)
  } toMap

  override def read(s: String): ValRes[A] =
    map get s toSuccess (efa.core.loc notFoundMsg s wrapNel)

  override def shows(a: A): String = loc(a).locName

  def toXml(a: A): Seq[Node] = Text(loc(a).name) 

  def fromXml(ns: Seq[Node]): ValRes[A] = read(ns.text)
}

object LocEnum extends LocEnumSpecs {
  def apply[A:LocEnum]: LocEnum[A] = implicitly

  def values[A](a: A, as: A*)(get: A ⇒ Localization): LocEnum[A] =
    new LocEnum[A] {
      override val valuesNel = NonEmptyList(a, as: _*)
      override def loc(a: A): Localization = get(a)
    }

//  def tagged[A<:IsLocalized](a: A, as: A*)(t: String)
//    : LocEnum[A] with TaggedToXml[A] =
//    new LocEnum[A] with TaggedToXml[A] {
//      override val valuesNel = NonEmptyList(a, as: _*)
//      override def loc (a: A): Localization = a.loc
//      override def tag = t
//    }
}

trait LocEnumSpecs {
  def laws[A:LocEnum:Arbitrary] = new Properties("locEnum") {
    include(RpgEnum.laws[A])
    include(Read.localizedLaws[A])
    include(ToXml.laws[A])
  }
}

// vim: set ts=2 sw=2 et:
