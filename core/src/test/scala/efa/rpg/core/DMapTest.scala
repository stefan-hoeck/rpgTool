package efa.rpg.core

import efa.core.{ToXml, Read}
import efa.core.std.anyVal._
import scalaz.scalacheck.ScalazProperties.equal
import org.scalacheck.{Properties, Prop}
import scalaz.std.anyVal._
import scalaz.syntax.show._

object DMapTest extends Properties("DMap") {
  type DM = DMap[Gender,Int]

  include(equal.laws[DM])
  include(Read.showLaws[DM])
  include(ToXml.laws[DM])
}

// vim: set ts=2 sw=2 et:
