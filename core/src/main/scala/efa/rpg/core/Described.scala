package efa.rpg.core

import efa.core.{Described ⇒ EfaDesc, Named}
import scala.swing.Component
import scalaz.Show

trait Described[A] extends Named[A] with EfaDesc[A] {
  def desc (a: A): String

  def fullDesc (a: A): String

  def htmlDesc (a: A) = HtmlDesc(name(a), fullDesc(a))
}

object Described {
  def apply[A:Described]: Described[A] = implicitly
}

// vim: set ts=2 sw=2 et:
