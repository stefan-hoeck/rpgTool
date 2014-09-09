package efa.rpg.core

import efa.core.{Name, Desc}
import efa.core.typeclass._
import org.scalacheck.Arbitrary
import scalaz.Equal

case class HtmlDesc(name: Name, text: Desc) {
  def html: Desc = Desc(s"<html>$text</html>")
}

object HtmlDesc {
  implicit val equalInst: Equal[HtmlDesc] = equal
  implicit val arbInst: Arbitrary[HtmlDesc] = arbitrary
}

// vim: set ts=2 sw=2 et:
