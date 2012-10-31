package efa.rpg.core

import efa.core.{Localization, IsLocalized}

sealed abstract class Gender(val loc: Localization) extends IsLocalized

object Gender {
  case object Male extends Gender(loc.maleLoc)
  case object Female extends Gender(loc.femaleLoc)

  implicit lazy val GenderLocEnum =
    LocEnum.tagged[Gender](Female, Male)("gender")

  implicit lazy val GenderArbitrary = GenderLocEnum.arbitrary
}
