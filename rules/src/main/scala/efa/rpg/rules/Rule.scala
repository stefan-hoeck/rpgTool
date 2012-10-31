package efa.rpg.rules

import scalaz.Endo

case class Rule[A](id: String, f: A ⇒ A) {
  def endo (ids: Set[String]): Endo[A] =
    if (ids(id)) Endo endo f else Endo.idEndo
}

// vim: set ts=2 sw=2 et:
