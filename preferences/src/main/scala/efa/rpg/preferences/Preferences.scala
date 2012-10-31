package efa.rpg.preferences

import efa.core.Service
import efa.io.{FileIO, valLogIO, ValLogIO}, valLogIO._
import efa.rpg.preferences.spi.PreferencesProvider
import java.io.File
import org.openide.filesystems.{FileObject, FileUtil}
import scalaz._, Scalaz._, effect.IO

object Preferences {
  lazy val service =
    Service.unique[PreferencesProvider](PreferencesProvider)
  import FileIO.mkdirs

  lazy val dataFolder: ValLogIO[File] = mkdirs (service.dataFolder)

  lazy val beingFolder: ValLogIO[File] = mkdirs (service.beingFolder)

  lazy val userSettingsFolder: ValLogIO[File] =
    mkdirs (service.userSettingsFolder)

  lazy val beingFolderNb: ValLogIO[FileObject] =
    beingFolder >>= (f ⇒ point (FileUtil toFileObject f))

  lazy val beingFolderIO: IO[FileObject] =
    mainLogger logVal (beingFolderNb, dummyBeingFolder) map (
      _ ?? dummyBeingFolder)

  private lazy val dummyBeingFolder: FileObject = {
    val root = FileUtil.createMemoryFileSystem.getRoot
    root.createFolder ("Beings (in memory)")
  }

  def beingsLogger = service.beingsLogger
  def itemsLogger = service.itemsLogger
  def mainLogger = service.mainLogger
}
