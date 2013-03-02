package efa.rpg.preferences

import efa.core.Service
import efa.io.{AsFile, logDisIO, LogDisIO}, logDisIO._, AsFile._
import efa.rpg.preferences.spi.PreferencesProvider
import java.io.File
import org.openide.filesystems.{FileObject, FileUtil}
import scalaz._, Scalaz._, effect.IO

object Preferences {
  lazy val service = Service.unique[PreferencesProvider](PreferencesProvider)

  def beingFolder: LogDisIO[File] = mkdirs(service.beingPath)

  def settingsFolder: LogDisIO[File] = mkdirs(service.settingsPath)

  lazy val beingFolderNb: LogDisIO[FileObject] =
    beingFolder >>= (f ⇒ point(FileUtil toFileObject f))

  lazy val beingFolderIO: IO[FileObject] =
    mainLogger logDis (beingFolderNb, dummyBeingFolder) map (
      _ ?? dummyBeingFolder)

  private lazy val dummyBeingFolder: FileObject = {
    val root = FileUtil.createMemoryFileSystem.getRoot
    root.createFolder("Beings (in memory)")
  }

  def beingsLogger = service.beingsLogger
  def itemsLogger = service.itemsLogger
  def mainLogger = service.mainLogger

  private def mkdirs(s: String) = AsFile[String] mkdirs s
}
