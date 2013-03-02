package efa.rpg.items.saver

import efa.io.LoggerIO.consoleLogger

class TestPrefs extends efa.rpg.preferences.spi.PreferencesProvider {
  private val rpg = ".rpgToolTest"

  val rootFolder = home + fs + rpg
  def dataFolder = "data"
  def beingFolder = "beings"
  def templatesFolder = "Templates"
  def userSettingsFolder = "settings"

  override val beingsLogger = consoleLogger
  override val itemsLogger = consoleLogger
  override val mainLogger = consoleLogger
}

// vim: set ts=2 sw=2 et:
