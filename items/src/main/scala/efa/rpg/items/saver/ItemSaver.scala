package efa.rpg.items.saver

import dire.SF
import efa.core.{ToXml, ValSt}
import efa.io._, EfaIO._
import efa.nb.VStSF
import efa.rpg.core.RpgItem
import efa.rpg.items.{NameFolder, IState, FolderFunctions, emptyFolder}
import efa.rpg.preferences.Preferences.service
import java.io.File
import scalaz._, Scalaz._, scalaz.effect.IO

/**
 * Provides ways to persist and load RpgItems.
 */
final case class ItemSaver[I] (
  saver: NameFolder[I] ⇒ LogDisIO[Unit],
  dataLoader: LogDisIO[NameFolder[I]],
  templatesLoader: LogDisIO[List[I]]
) {
  def loadState(l: LoggerIO)(implicit r: RpgItem[I]): IO[IState[I]] =
    l logDisD dataLoader.map(IState.fromFolder[I])

  def saveState (l: LoggerIO)(is: IState[I]): IO[Unit] =
    l logDisZ saver(FolderFunctions fromIdxFolder is.root)

  def loadTemplates (l: LoggerIO): IO[List[I]] =
    l logDisZ templatesLoader
}

object ItemSaver {

  def dummy[I] = {
    def fail[A] = logDisIO.fail[A]("Not supported")

    ItemSaver[I](_ ⇒ fail, fail, fail)
  }

  def xmlSaver[A:ToXml](
    name: String,
    lbl: String,
    clazz: Class[_],
    ext: String = "data"): ItemSaver[A] = {
    val templates = service.templatesFolder
    val data = s"${name}.${ext}"
    val temps = s"${name}${templates}.${ext}"
    val dataSrc = source(service.dataPath, (data, clazz))
    val tempSrc = source(service.templatesPath, (temps, clazz))
    implicit val folderToXml = FolderFunctions.folderToXml[A](lbl)
    implicit val itemsToXml = ToXml.listToXml[A](lbl)

    ItemSaver[A](
      f ⇒ dataSrc >>= { _ writeXml f },
      dataSrc >>= { _.readXml[NameFolder[A]] },
      tempSrc >>= { _.readXml[List[A]] }
    )
  }

  // Look for a file in a folder. If it does not exists, create it
  // and copy the contents of the given stream to it.
  private def source[A:AsFile](folder: A, c: ClassResource)
    : LogDisIO[File] = for {
      file ← folder.mkdirs >>= { _ fileAt c._1 }
      _    ← if (!file.exists) c copyTo file else ldiUnit
    } yield file
}

// vim: set ts=2 sw=2 et:
