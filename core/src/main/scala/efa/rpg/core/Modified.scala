package efa.rpg.core

import scalaz.@>

trait Modified[A] extends HasModifiers[A] {
  def modifiersL: A @> Modifiers

  def modifiers(a: A) = modifiersL get a
}

object Modified {
  def apply[A:Modified]: Modified[A] = implicitly
}

// vim: set ts=2 sw=2 et:
