package efa.rpg.core

import efa.core.Default
import org.scalacheck.{Arbitrary, Gen, Properties}
import scalaz.{Enum, Order, NonEmptyList}
import scalaz.std.anyVal._
import scalaz.std.list._
import scalaz.syntax.std.option._
import scalaz.syntax.foldable._
import scalaz.syntax.equal._

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
trait RpgEnum[A] extends Default[A] with Enum[A] {
  private[this] lazy val toId: Map[A,Int] = values.zipWithIndex.toMap

  private[this] lazy val preds: Map[A,A] =
    (valuesNel.tail ::: List(valuesNel.head)) zip values toMap

  private[this] lazy val succs: Map[A,A] =
    values zip (valuesNel.tail ::: List(valuesNel.head)) toMap

  def valuesNel: NonEmptyList[A]

  def values: List[A] = valuesNel.list

  final override lazy val min = values.head.some

  final override lazy val max = values.last.some

  final def pred (a: A): A = preds (a)

  final def succ (a: A): A = succs (a)

  final override def equal (a1: A, a2: A) = a1 == a2

  final override def equalIsNatural = true

  final override lazy val default: A = valuesNel.head

  def arbitrary: Arbitrary[A] = Arbitrary(Gen oneOf values)

  final override def order (x: A, y: A) = Order[Int] order (toId(x), toId(y))
}

object RpgEnum extends RpgEnumSpecs {
  def apply[A:RpgEnum]: RpgEnum[A] = implicitly

  def values[A](a: A, as: A*): RpgEnum[A] = new RpgEnum[A] {
    override val valuesNel = NonEmptyList(a, as: _*)
  }
}

trait RpgEnumSpecs {
  def enumUnique[A:RpgEnum]: Boolean = {
    val as = RpgEnum[A].values

    as ∀ (a1 ⇒ as ∀ (a2 ⇒ (a1 == a2) ≟ (a1 ≟ a2)))
  }

  import scalaz.scalacheck.ScalazProperties.enum

  def laws[A:RpgEnum:Arbitrary] = new Properties("rpgEnum") {
    include(enum.laws[A])
    property("rpgEnums are unique") = enumUnique[A]
  }
}

// vim: set ts=2 sw=2 et:
