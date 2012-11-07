package efa.rpg.core.spi

import efa.core.Localization

trait RpgLocal {

  final lazy val maleLoc =
    Localization("male", maleName, maleShort, maleName)
  final lazy val femaleLoc =
    Localization("female", femaleName, femaleShort, femaleName)

  def dieString: String

  def femaleName: String

  def femaleShort: String

  def invalidLength (a: Int): String

  def maleName: String

  def maleShort: String

  def total: String

  def unknownDieRollerFormat: String

}

object RpgLocal extends RpgLocal {
  def maleName = "männlich"

  def maleShort = "M"

  def femaleName = "weiblich"

  def femaleShort = "W"

  def unknownDieRollerFormat = "Ubekanntes Format für Würfelwurf"

  def dieString = "W"

  def invalidLength (a: Int) = "Es werden %d Werte benötigt" format a

  def total = "Total"
}

// vim: set ts=2 sw=2 et:
