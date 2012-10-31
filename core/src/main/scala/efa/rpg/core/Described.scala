package efa.rpg.core

import scala.swing.Component
import scalaz.Show

trait Described[A] extends Show[A] {
  def desc (a: A): String
  def name (a: A): String
  def shortDesc (a: A): String
  def fullDesc (a: A): String

  def htmlDesc (a: A) = HtmlDesc(name(a), fullDesc(a))

  override def shows (a: A) = name(a)
}

object Described {
  def apply[A:Described]: Described[A] = implicitly
}

// vim: set ts=2 sw=2 et:
