package efa.rpg.preferences.spi

import efa.io.LoggerIO

abstract class PreferencesProvider {
  def dataFolder: String
  def beingFolder: String
  def userSettingsFolder: String
  def beingsLogger: LoggerIO
  def itemsLogger: LoggerIO
  def mainLogger: LoggerIO
}

object PreferencesProvider extends PreferencesProvider {
  private val fs = System.getProperty("file.separator")
  private val home = System.getProperty("user.home")
  private val rpg = ".rpgToolZ"
  private val root = home + fs + rpg
  override val dataFolder = root + fs + "data"
  override val beingFolder = root + fs + "beings"
  override val userSettingsFolder = root + fs
  override val beingsLogger = LoggerIO.consoleLogger
  override val itemsLogger = LoggerIO.consoleLogger
  override val mainLogger = LoggerIO.consoleLogger
}
