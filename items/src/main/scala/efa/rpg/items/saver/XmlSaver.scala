package efa.rpg.items.saver

import efa.core._, Efa._
import efa.io._, FileIO.{readXmlStream, writeXml}
import efa.rpg.items.{NameFolder, FolderFunctions}
import java.io.{InputStream, OutputStream}
import scala.xml.XML
import scalaz._, Scalaz._

object XmlSaver extends ValLogIOFunctions {

  def apply[I:ToXml] (lbl: String, streams: StreamsProvider): ItemSaver[I] = {

    implicit def folderToXml = FolderFunctions.folderToXml[I] (lbl)
    implicit def itemsToXml = ToXml.listToXml[I] (lbl)

    val StreamsProvider (dataIn, templatesIn, dataOut) = streams

    def writeData (f: NameFolder[I]): ValLogIO[Unit] = {
      def xml = "folder" xml f
      def msg = "Error when writing " + lbl

      dataOut ∗ (writeXml (xml, _, msg, CharSet.UTF8))
    }
    
    ItemSaver[I] (
      writeData,
      dataIn ∗ (readXmlStream[NameFolder[I]] (_)),
      templatesIn ∗ (readXmlStream[List[I]] (_))
    )
  }
}

// vim: set ts=2 sw=2 et:
