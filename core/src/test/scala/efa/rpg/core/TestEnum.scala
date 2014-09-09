package efa.rpg.core

import efa.core.{Localization, IsLocalized}
import org.scalacheck.Arbitrary

sealed trait TestLocEnum extends IsLocalized {
  lazy val loc: Localization = new Localization(toString, toString)
}

object TestLocEnum {
  case object A extends TestLocEnum
  case object B extends TestLocEnum
  case object C extends TestLocEnum
  case object D extends TestLocEnum

  implicit val enumInst: LocEnum[TestLocEnum] =
    LocEnum.values[TestLocEnum](A, B, C, D)

  implicit val arbInst: org.scalacheck.Arbitrary[TestLocEnum] = enumInst.arbitrary
}

// vim: set ts=2 sw=2 et:
