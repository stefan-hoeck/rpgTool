package efa.rpg.preferences.spi

import efa.io.LoggerIO

trait PreferencesProvider {
  val fs = System.getProperty("file.separator")
  val home = System.getProperty("user.home")

  def rootFolder: String
  def dataFolder: String
  def beingFolder: String
  def templatesFolder: String
  def userSettingsFolder: String

  lazy val dataPath = s"${rootFolder}${fs}${dataFolder}"
  lazy val beingPath = s"${rootFolder}${fs}${beingFolder}"
  lazy val templatesPath = s"${dataPath}${fs}${templatesFolder}"
  lazy val settingsPath = s"${rootFolder}${fs}${userSettingsFolder}"


  def beingsLogger: LoggerIO
  def itemsLogger: LoggerIO
  def mainLogger: LoggerIO
}

object PreferencesProvider extends PreferencesProvider {
  private val rpg = ".rpgToolZ"

  val rootFolder = home + fs + rpg
  def dataFolder = "data"
  def beingFolder = "beings"
  def templatesFolder = "Vorlagen"
  def userSettingsFolder = "settings"

  override val beingsLogger = LoggerIO.consoleLogger
  override val itemsLogger = LoggerIO.consoleLogger
  override val mainLogger = LoggerIO.consoleLogger
}
