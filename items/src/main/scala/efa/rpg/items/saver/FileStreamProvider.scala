package efa.rpg.items.saver

import scalaz._, Scalaz._, scalaz.effect._
import java.io.{InputStream ⇒ Is, OutputStream ⇒ Os, File}
import efa.rpg.core.{RpgItem}
import efa.rpg.preferences.Preferences
import efa.io._
import efa.io.FileIO._
import efa.core._

object FileStreamProvider extends ValLogIOFunctions {
  
  def apply (
    dataName: String,
    templatesName: String,
    clazz: Class[_],
    ext: String
  ): StreamsProvider = {

    def ioRes (name: String, folder: ValLogIO[File])
    : (ValLogIO[Is], ValLogIO[Os]) = {
      val resource: ValLogIO[Is] = resourceAsStream (name, clazz)
      val file: ValLogIO[File] = getOrCreate (folder, name, resource)
      def fileIs: ValLogIO[Is] = file ∗ (fileInputStream (_))
      def is: ValLogIO[Is] = validStream (name, fileIs, resource)
      def os: ValLogIO[Os] = file ∗ (fileOutputStream (_))

      (is, os)
    }

    def dataFolder = Preferences.dataFolder
    def templatesFolder = dataFolder ∗ (addDirs (_, templatesName))
    def dataFileName = dataName + "." + ext
    def templatesFileName = dataName + templatesName + "." + ext
    val (dataIs, dataOs) = ioRes (dataFileName, dataFolder)
    val templatesIs = ioRes (templatesFileName, templatesFolder)._1

    StreamsProvider (dataIs, templatesIs, dataOs)
  }
  
  private def validStream (
    name: String, stream: ValLogIO[Is], default: ValLogIO[Is]
  ): ValLogIO[Is] = {
    type BareIO[A] = IO[(Logs, DisRes[A])]

    //loggings in case the default resource has to be used
    def warnMsg =
      "Not able to find or create file " + name + ". Using default resource."
    def warning = Log warning warnMsg

    //add logging to default resource
    def loggedResource (ls: Logs, es: List[String]): BareIO[Is] = {
      def esLogs = DList fromList (warning :: es.map(Log error _))
      def logs = ls ++ esLogs //all logs
      def logged= mapW(default)(_ :++> logs) //add logs to default
      logged.run.run //unwrap
    }

    def foldDisRes (ls: Logs, v: DisRes[Is]) =
      v fold (es ⇒ loggedResource (ls, es.list), a ⇒ IO ((ls, a.right)))

    def v: BareIO[Is] = for {
      lv ← stream.run.run
      res ← foldDisRes (lv._1, lv._2)
    } yield res

    lift(v)
  }

  // Look for a file in a folder. If it does not exists, create it
  // and copy the contents of the given stream to it.
  private def getOrCreate (
    folder: ValLogIO[File], name: String, stream: ValLogIO[Is]
  ): ValLogIO[File] = {

    def copy (f: File): ValLogIO[Unit] = for {
      in  ← stream
      out ← FileIO.createFile (f) ∗ (FileIO fileOutputStream (_)) 
      _   ← FileIO copyBinary (in, out, name)
    } yield f

    for {
      fldr ← folder
      file ← success (new File (fldr, name))
      _    ← if (!file.exists) copy (file) else nullValLogIO
    } yield file
  }
}

// vim: set ts=2 sw=2 et:
