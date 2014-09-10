package efa.rpg.core

trait HasModifiers[A] {
  def modifiers(a: A): Modifiers
  
  def modsFor(a: A, k: ModifierKey): List[Modifier] = modifiers(a) get k

  def prop(a: A, k: ModifierKey): Long = modifiers(a) property k
}

object HasModifiers {
  def apply[A:HasModifiers]: HasModifiers[A] = implicitly
}

// vim: set ts=2 sw=2 et:
