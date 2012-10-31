package efa.rpg.core

trait HasModifiers[-A] {
  def modifiers (a: A): Modifiers
}

object HasModifiers {
  def apply[A:HasModifiers]: HasModifiers[A] = implicitly
}

// vim: set ts=2 sw=2 et:
