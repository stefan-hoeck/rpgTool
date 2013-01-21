package efa.rpg.core

import scalaz.@>

trait DescribedL[A] {
  def descL: A @> String

  def desc (a: A): String = descL get a
}

object DescribedL {
  def apply[A:DescribedL]: DescribedL[A] = implicitly
}

// vim: set ts=2 sw=2 et:
