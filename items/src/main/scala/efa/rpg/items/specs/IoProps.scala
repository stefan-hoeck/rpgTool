package efa.rpg.items.specs

import org.scalacheck.Prop
import scalaz.effect.IO

trait IoProps {
  def propIo (p: IO[Prop]): Prop = p.unsafePerformIO
}

// vim: set ts=2 sw=2 et:
