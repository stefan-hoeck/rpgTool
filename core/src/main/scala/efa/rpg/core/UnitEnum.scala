package efa.rpg.core

import efa.core.{Localization, Validator, Validators}
import efa.core.syntax.string
import efa.core.std.anyVal._
import scalaz.NonEmptyList

trait UnitEnum[A] extends LocEnum[A] {

  override lazy val map: Map[String, A] =
    values flatMap (a ⇒ (plural(a) :: names(a)) map (_ → a)) toMap

  def plural(a: A): String
  def multiplier(a: A): Long

  def showPretty(a: A, nod: Int): Long ⇒ String = l ⇒ 
    "%."+nod+"f %s" format (l.toDouble / multiplier(a).toDouble, shortName(a))

  def readPretty(a: A): Validator[String,Long] = Validators{ s ⇒ 
    def multiply(d: Double) = (d * multiplier(a)).round.toLong
    def value = s.replace(shortName(a), "").trim.read[Double].disjunction

    value map multiply
  }
}

/** Helper trait to declutter the creation of UnitEnum
  * instances. Just mixin this trait in your sealed data type
  * and call UnitEnum.values to create an instance of UnitEnum
  */
trait IsUnit {
  def loc: Localization
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

  import org.scalacheck.{Arbitrary, Properties}
  def laws[A:Arbitrary:UnitEnum] = new Properties("UnitEnum") {
    include(LocEnum.laws[A])
  }
}

// vim: set ts=2 sw=2 et:
