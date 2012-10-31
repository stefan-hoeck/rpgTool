package efa.rpg.being

import efa.core.{TaggedToXml, Efa, Default}, Efa._
import efa.io.{FileIO, ValLogIO, LoggerIO}
import efa.react.Out
import java.io.{OutputStream, InputStream}
import org.openide.filesystems.FileObject
import scala.xml.PrettyPrinter
import scalaz._, Scalaz._, effect.IO

case class BeingSaver[A](
  save: (A, OutputStream) ⇒ ValLogIO[Unit],
  load: InputStream ⇒ ValLogIO[A]
) {
    
  def saveToFo (fo: FileObject)(implicit L: LoggerIO): Out[A] = a ⇒ for {
    os ← IO (fo.getOutputStream)
    _  ← L logValZ save(a, os)
  } yield ()

  def loadFromFo (fo: FileObject)(implicit D: Default[A], L: LoggerIO)
    : IO[A] = for {
      is ← IO (fo.getInputStream)
      a  ← L logValZ load(is)
    } yield a
}

object BeingSaver {
  lazy val defaultPretty = new PrettyPrinter(80, 2)

  private val writeError = "Error when writing being."

  def xmlSaver[A:TaggedToXml](pretty: PrettyPrinter = defaultPretty)
    : BeingSaver[A] = {
    def aToString (a: A) = pretty format (TaggedToXml[A].tag xml a)

    BeingSaver[A](
      (a,o) ⇒ FileIO.writeString(aToString(a), o, writeError, CharSet.UTF8),
      i ⇒ FileIO.readXmlStream[A](i)
    )
  }
}

// vim: set ts=2 sw=2 et:
