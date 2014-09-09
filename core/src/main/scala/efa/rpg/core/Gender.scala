package efa.rpg.core

import efa.core.{Localization, IsLocalized}
import org.scalacheck.Arbitrary

sealed abstract class Gender(val loc: Localization) extends IsLocalized

object Gender {
  def male: Gender = Male
  def female: Gender = Female
  case object Male extends Gender(loc.maleLoc)
  case object Female extends Gender(loc.femaleLoc)

  implicit val enumInst: LocEnum[Gender] = LocEnum.values(female, male)

  implicit val arbInst: Arbitrary[Gender] = enumInst.arbitrary
}
