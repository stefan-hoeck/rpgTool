package efa.rpg.rules

import scalaz._, Scalaz._, Tags.Dual

case class Rule[A](id: String, f: A ⇒ A) {
  def endo(ids: Set[String]): Endo[A] @@ Dual =
    Dual(if (ids(id)) Endo endo f else Endo.idEndo)
}

object Rule {
  def state[A] (id: String, s: State[A,Unit]): Rule[A] =
    Rule[A](id, s exec _)
}

// vim: set ts=2 sw=2 et:
