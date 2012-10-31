package efa.rpg.core

import efa.core.{Localization, IsLocalized}

sealed trait TestLocEnum extends IsLocalized {
  lazy val loc: Localization = new Localization(toString, toString)
}

object TestLocEnum {
  case object A extends TestLocEnum
  case object B extends TestLocEnum
  case object C extends TestLocEnum
  case object D extends TestLocEnum

  implicit lazy val TLocEnum =
    LocEnum.tagged[TestLocEnum](A, B, C, D)("test")

  implicit lazy val TLocEnumArbitrary = TLocEnum.arbitrary
}

// vim: set ts=2 sw=2 et:
