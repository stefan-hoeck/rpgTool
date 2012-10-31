package efa.rpg.core

import scalaz.Equal

case class HtmlDesc (name: String, text: String) {
  def html = "<html>%s</html>" format text
}

object HtmlDesc {
  implicit val HtmlDescEqual: Equal[HtmlDesc] = Equal.equalA
}

// vim: set ts=2 sw=2 et:
