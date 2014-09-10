package efa.rpg.core

import efa.core.Localization
import org.scalacheck.Arbitrary

sealed trait TestLocEnum {
  lazy val loc: Localization = new Localization(toString, toString)
}

object TestLocEnum {
  case object A extends TestLocEnum
  case object B extends TestLocEnum
  case object C extends TestLocEnum
  case object D extends TestLocEnum

  implicit val enumInst: LocEnum[TestLocEnum] =
    LocEnum.values[TestLocEnum](A, B, C, D)(_.loc)

  implicit val arbInst: Arbitrary[TestLocEnum] = enumInst.arbitrary
}

// vim: set ts=2 sw=2 et:
