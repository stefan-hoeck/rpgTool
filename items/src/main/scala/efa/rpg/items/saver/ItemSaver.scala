package efa.rpg.items.saver

import scalaz._, Scalaz._, scalaz.effect.IO
import efa.rpg.items.{NameFolder, IState, FolderFunctions, emptyFolder}
import efa.rpg.core.RpgItem
import efa.io._

/**
 * Provides ways to persist and load RpgItems.
 */
sealed case class ItemSaver[I] (
  saver: NameFolder[I] ⇒ ValLogIO[Unit],
  dataLoader: ValLogIO[NameFolder[I]],
  templatesLoader: ValLogIO[List[I]]
) {

  import FolderFunctions.fromIdxFolder

  def loadState(l: LoggerIO)(implicit r: RpgItem[I]): IO[IState[I]] =
    l.logVal (dataLoader, emptyFolder) ∘ (IState fromFolder _)

  def saveState (l: LoggerIO)(is: IState[I]): IO[Unit] =
    l logValM saver (fromIdxFolder (is.root))

  def loadTemplates (l: LoggerIO): IO[List[I]] =
    l logValM templatesLoader
}

object ItemSaver {

  def dummy[I] = {
    def fail[A] = valLogIO.fail[A] ("Not supported")
    ItemSaver[I] (_ ⇒ fail, fail, fail)
  }

}

// vim: set ts=2 sw=2 et:
