package efa.rpg.rules.ui

import org.scalacheck._, Prop._
import scalaz._, Scalaz._

object RulesNodeTest extends Properties("RulesNode") {
  property("initialization") =
    RulesNode.root.getDisplayName ≟ efa.rpg.rules.loc.rules
}

// vim: set ts=2 sw=2 et:
