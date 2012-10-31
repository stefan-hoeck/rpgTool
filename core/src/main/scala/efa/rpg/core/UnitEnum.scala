package efa.rpg.core

import efa.core._, Efa._
import scalaz._, Scalaz._

trait UnitEnum[A] extends LocEnum[A] {

  override lazy val map: Map[String, A] =
    values flatMap (a ⇒ (plural(a) :: names(a)) map (_ → a)) toMap

  def plural (a: A): String
  def multiplier (a: A): Long

  def showPretty (a: A, nod: Int): Long ⇒ String = l ⇒ 
    "%."+nod+"f %s" format (l.toDouble / multiplier(a).toDouble, shortName(a))

  def readPretty (a: A): String ⇒ ValRes[Long] = s ⇒ 
    s.replace(shortName(a), "").trim.read[Double] ∘
    (l ⇒ (l * multiplier(a)).round toLong)
}

trait IsUnit extends IsLocalized {
  def plural: String
  def multiplier: Long
}

object UnitEnum {
  def apply[A:UnitEnum]: UnitEnum[A] = implicitly

  def values[A<:IsUnit](a: A, as: A*): UnitEnum[A] =
    new UnitEnum[A] {
      override val valuesNel = NonEmptyList(a, as: _*)
      override def loc (a: A): Localization = a.loc
      override def plural (a: A): String = a.plural
      override def multiplier (a: A): Long = a.multiplier
    }
}

// vim: set ts=2 sw=2 et:
