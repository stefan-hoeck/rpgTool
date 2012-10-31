package efa.rpg.core.spi

import efa.core.Localization

trait RpgLocal {

  final lazy val maleLoc =
    Localization("male", maleName, maleShort, maleName)
  final lazy val femaleLoc =
    Localization("female", femaleName, femaleShort, femaleName)

  def maleName: String

  def maleShort: String

  def femaleName: String

  def femaleShort: String

  def unknownDieRollerFormat: String

  def dieString: String

  def invalidLength (a: Int): String
}

object RpgLocal extends RpgLocal {
  def maleName = "männlich"

  def maleShort = "M"

  def femaleName = "weiblich"

  def femaleShort = "W"

  def unknownDieRollerFormat = "Ubekanntes Format für Würfelwurf"

  def dieString = "W"

  def invalidLength (a: Int) = "Es werden %d Werte benötigt" format a
}

// vim: set ts=2 sw=2 et:
