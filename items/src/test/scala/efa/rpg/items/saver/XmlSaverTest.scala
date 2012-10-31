package efa.rpg.items.saver

import efa.core._
import efa.io._, FileIO.{file, deleteFile}
import efa.rpg.core.{ItemData}
import efa.rpg.items.{Advantage, IState, emptyFolder}
import efa.rpg.items.FolderFunctions._
import efa.rpg.preferences.Preferences
import java.io.File
import org.scalacheck._, Prop._
import scalaz._, Scalaz._, effect._

object XmlSaverTest
   extends Properties("XmlSaver")
   with ValLogIOFunctions {

  private val fs = System.getProperty("file.separator")

  private val dataFolder = Preferences.service.dataFolder

  private val dataPath = dataFolder + fs + "Advantages.data"

  private val templatesPath = dataFolder + fs +
    "Templates" + fs + "AdvantagesTemplates.data"

  val saver = xmlSaver[Advantage](
    "Advantages", "advantage", getClass, "data", "Templates")

  def folderGen = efa.rpg.items.Gens.rootGen

  implicit val folderSaver = folderToXml[Advantage]("advantage")

  val default: Folder[Advantage,String] = Folder(
    Stream(Advantage(ItemData(1, "Vorteil", "blub"), 2)),
    Stream.empty,
    "Vorteile"
  )

  val templates = List (
    Advantage (ItemData(-1, "Template", "knartz"), 4),
    Advantage (ItemData(-2, "Template2", "knortz"), 5)
  )

  implicit val logger = LoggerIO.consoleLogger filter Level.Warning

  private def testIO (io: ValLogIO[Prop]): Prop =
    logger.logVal[Prop] (io, false).unsafePerformIO

  property("dataLoader") = Prop.forAll (folderGen) { f ⇒ 
    def res: ValLogIO[Prop] = for {
      data        ← saver.dataLoader
      dataLoaded  = (data ≟ default) :| "data loaded"
      dataCopied  ← file (dataPath) ∘ (_.exists :| "data copied")
      save        ← saver saver f
      saved       = (save ≟ ()) :| "data saved"
      reload      ← saver.dataLoader
      reloaded    = (reload ≟ f) :| "data loaded"
      ts          ← saver.templatesLoader
      tsLoaded    = (ts ≟ templates) :| "templates loaded"
      tsCopied    ← file (templatesPath) ∘ (_.exists :| "templates copied")
      _           ← deleteFile (templatesPath)
      tsDeleted   ← file (templatesPath) ∘ (! _.exists :| "templates deleted")
      _           ← deleteFile (dataPath)
      dataDeleted ← file (dataPath) ∘ (! _.exists :| "data deleted")
    } yield dataLoaded && dataCopied && saved && reloaded && tsLoaded &&
      tsCopied && tsDeleted && dataDeleted

    testIO (res)
  }

  import java.io.{InputStream, ByteArrayInputStream,
    OutputStream, ByteArrayOutputStream}
  val emptyArr = IO(Array.empty[Byte])
  val inStream = emptyArr >>= (a ⇒ IO(new ByteArrayInputStream(a)))
  val outStream = IO(new ByteArrayOutputStream)
  val streamProvider =
    StreamsProvider(liftIO(inStream), liftIO(inStream), liftIO(outStream))
  val errorSaver = XmlSaver[Advantage]("advantage", streamProvider)
  
  property("error_when_loading") = {
    val res = for {
      s  ← errorSaver loadState logger
      ts ← errorSaver loadTemplates logger
    } yield (ts ≟ List.empty) :| "templates loaded" &&
      (s ≟ (IState fromFolder emptyFolder[Advantage])) :| "data loaded"

    res.unsafePerformIO
  }
}
