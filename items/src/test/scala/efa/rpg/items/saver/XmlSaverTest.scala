package efa.rpg.items.saver

import efa.core._
import efa.io._, EfaIO._
import efa.rpg.core.ItemData
import efa.rpg.items.{Advantage, IState, emptyFolder}
import efa.rpg.items.FolderFunctions._
import efa.rpg.preferences.Preferences.service
import java.io.File
import org.scalacheck._, Prop._
import scalaz._, Scalaz._, effect._

object XmlSaverTest extends Properties("XmlSaver") {
  val unit: Unit = ()

  property("prefs_registered") = service.isInstanceOf[TestPrefs]

  val dataPath = service.dataPath + "/Advantages.data"
  val tempPath = service.templatesPath + "/AdvantagesTemplates.data"

  val saver = 
    ItemSaver.xmlSaver[Advantage]("Advantages", "advantage", getClass)

  def folderGen = efa.rpg.items.Gens.rootGen

  implicit val folderSaver = folderToXml[Advantage]("advantage")

  val default: Folder[Advantage,String] = Folder(
    Stream(Advantage(ItemData(1, "Vorteil", "blub"), 2)),
    Stream.empty,
    "Vorteile"
  )

  val templates = List (
    Advantage(ItemData(-1, "Template", "knartz"), 4),
    Advantage(ItemData(-2, "Template2", "knortz"), 5)
  )

  implicit val logger = LoggerIO.consoleLogger filter Level.Warning

  private def testIO (io: LogDisIO[Prop]): Prop =
    logger.logDis[Prop](io, false).unsafePerformIO

  property("dataLoader") = Prop.forAll (folderGen) { f ⇒ 
    def res: LogDisIO[Prop] = for {
      data        ← saver.dataLoader
      dataLoaded  = (data ≟ default) :| "data loaded"
      dataCopied  ← dataPath.file map (_.exists :| "data copied")
      save        ← saver saver f
      saved       = (save ≟ unit) :| "data saved"
      reload      ← saver.dataLoader
      reloaded    = (reload ≟ f) :| "data loaded"
      ts          ← saver.templatesLoader
      tsLoaded    = (ts ≟ templates) :| "templates loaded"
      tsCopied    ← tempPath.file map (_.exists :| "templates copied")
      _           ← tempPath.delete
      tsDeleted   ← tempPath.file map (! _.exists :| "templates deleted")
      _           ← dataPath.delete
      dataDeleted ← dataPath.file map (! _.exists :| "data deleted")
    } yield dataLoaded && dataCopied && saved && reloaded && tsLoaded &&
      tsCopied && tsDeleted && dataDeleted

    testIO (res)
  }
}
