package efa.rpg.being

import dire.Out
import efa.core.{TaggedToXml, Efa, Default}, Efa._
import efa.io._, EfaIO._
import java.io.{OutputStream, InputStream}
import org.openide.filesystems.{FileObject, FileUtil}
import scala.xml.PrettyPrinter
import scalaz._, Scalaz._, effect.IO

case class BeingSaver[A](
  save: (A, FileObject) ⇒ LogDisIO[Unit],
  load: FileObject ⇒ LogDisIO[A]
) {
    
  def saveToFo(fo: FileObject)(implicit L: LoggerIO): Out[A] = a ⇒
    L logDisD save(a, fo)

  def loadFromFo(fo: FileObject)(implicit D: Default[A], L: LoggerIO)
    : IO[A] = L logDisD load(fo)
}

object BeingSaver {
  lazy val defaultPretty = new PrettyPrinter(80, 2)

  def xmlSaver[A:TaggedToXml](
    pretty: PrettyPrinter = defaultPretty
  ): BeingSaver[A] =
    BeingSaver[A]((a,o) ⇒ o.writeXml(a, Some(pretty)), _.readXml[A])
}

// vim: set ts=2 sw=2 et:
