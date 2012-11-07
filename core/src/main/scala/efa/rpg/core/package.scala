package efa.rpg

import efa.core.Service
import efa.rpg.core.spi.RpgLocal
import scalaz._, Scalaz._

package object core {
  lazy val loc = Service.unique[RpgLocal](RpgLocal)

  type DB[+A] = Map[Int,A]

  def prettyMods(
    sum: Long,
    ms: List[Modifier],
    format: Long ⇒ String = (_: Long).toString
  ): String = {
    def head = "<b>%s: %s</b>" format (loc.total, format(sum))
    def rest = ms map (m ⇒ "%s: %s" format (m.name, format(m.value)))

    "<html>%s<br>%s</html>" format (head, rest mkString "<br>")
  }
}

// vim: set ts=2 sw=2 et:
