package efa.rpg.core

import efa.core.Localization
import org.scalacheck.Arbitrary

sealed abstract class Gender(val loc: Localization)

object Gender {
  def male: Gender = Male
  def female: Gender = Female
  case object Male extends Gender(loc.maleLoc)
  case object Female extends Gender(loc.femaleLoc)

  implicit val enumInst: LocEnum[Gender] = LocEnum.values(female, male)(_.loc)
  implicit val arbInst: Arbitrary[Gender] = enumInst.arbitrary
}
