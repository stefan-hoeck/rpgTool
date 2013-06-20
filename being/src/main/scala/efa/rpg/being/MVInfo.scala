package efa.rpg.being

import org.openide.util.Lookup
import scalaz.effect.IO

case class MVInfo(
  panel: javax.swing.JComponent,
  preferredId: String,
  name: String,
  read: IO[Unit],
  persist: IO[Unit],
  lookup: Lookup)

object MVInfo {
  def default: IO[MVInfo] = IO {
    MVInfo(
      new javax.swing.JPanel,
      "default",
      "Default",
      IO.ioUnit,
      IO.ioUnit,
      Lookup.EMPTY
    )
  }
}

// vim: set ts=2 sw=2 et:
