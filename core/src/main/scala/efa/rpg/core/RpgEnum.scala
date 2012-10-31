package efa.rpg.core

import efa.core.Default
import org.scalacheck.{Arbitrary, Gen}
import scalaz._, Scalaz._

/**
 * A very rudimentray type class for enumerations
 * used in role playing games such as types of
 * coins, days and months in a calendar and
 * so on.
 *
 * By implementing this type class for a given type
 * A one assures that there exist no other possible
 * values of type A than the ones given in the
 * values parameter.
 */
trait RpgEnum[A] extends Equal[A] with Default[A] {
  def valuesNel: NonEmptyList[A]

  def values: List[A] = valuesNel.list

  override def equal (a1: A, a2: A) = a1 == a2

  override def equalIsNatural = true

  override lazy val default: A = valuesNel.head

  def arbitrary: Arbitrary[A] = Arbitrary(Gen oneOf values)
}

object RpgEnum {
  def apply[A:RpgEnum]: RpgEnum[A] = implicitly

  def values[A](a: A, as: A*): RpgEnum[A] = new RpgEnum[A] {
    override val valuesNel = NonEmptyList(a, as: _*)
  }
}

trait RpgEnumSpecs {
  def enumUnique[A:RpgEnum:Equal]: Boolean = {
    val as = RpgEnum[A].values

    as ∀ (a1 ⇒ as ∀ (a2 ⇒ (a1 == a2) ≟ (a1 ≟ a2)))
  }
}

// vim: set ts=2 sw=2 et:
