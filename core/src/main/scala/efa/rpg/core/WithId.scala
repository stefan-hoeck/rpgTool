package efa.rpg.core

import efa.core.UniqueId

trait WithId[-A] extends UniqueId[A,Int] {
  def id (a: A): Int
}

object WithId extends WithIdFunctions with RangeVals {
  def apply[A:WithId]: WithId[A] = implicitly
}

trait WithIdFunctions {
  def withId[A](f: A ⇒ Int): WithId[A] =
    new WithId[A]{def id(a: A) = f(a)}
}

// vim: set ts=2 sw=2 et:
