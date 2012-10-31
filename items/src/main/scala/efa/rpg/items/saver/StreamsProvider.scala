package efa.rpg.items.saver

import java.io.{InputStream, OutputStream}
import efa.io.ValLogIO

sealed case class StreamsProvider (
  dataInStream: ValLogIO[InputStream],
  templatesInStream: ValLogIO[InputStream],
  dataOutStream: ValLogIO[OutputStream]
)

// vim: set ts=2 sw=2 et:
