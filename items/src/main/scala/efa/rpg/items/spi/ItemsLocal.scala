package efa.rpg.items.spi

import efa.core.DisRes
import scalaz.syntax.either._
import scalaz.syntax.nel._

trait ItemsLocal {
  def exists (n: String): String

  def folder: String

  def templates: String

  final def existsLeft (n: String): DisRes[String] =
    exists(n).wrapNel.left
}

object ItemsLocal extends ItemsLocal {

  def exists (n: String): String = "Name bereits vorhanden: " + n

  def folder = "Ordner"

  def templates = "Vorlagen"
}

// vim: set ts=2 sw=2 et:
