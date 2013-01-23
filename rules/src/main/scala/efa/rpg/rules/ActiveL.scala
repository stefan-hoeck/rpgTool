package efa.rpg.rules

import scalaz.State

trait ActiveL[A] {
  def active (a: A): Boolean
  def activate (a: A): State[RulesFolder,Unit]
}

object ActiveL {
  def apply[A:ActiveL]: ActiveL[A] = implicitly
}

// vim: set ts=2 sw=2 et:
