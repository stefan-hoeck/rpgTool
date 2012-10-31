package efa.rpg.rules

import efa.core.Service
import efa.nb.node.NbNode
import efa.rpg.rules.ui.spi.RulesUILocal
import scalaz._, Scalaz._

package object ui {
  lazy val loc = Service.unique[RulesUILocal](RulesUILocal)

  val l = Lens.self[RulesFolder]

  def isActive (f: RulesFolder): Boolean = f ∃ (_.active)

  def activateS (s: RuleSetting): EnableCookie = 
    EnableCookie (l update (s, RuleSetting.active.mod(x ⇒ !x, s)))

  def activateF (f: RulesFolder): EnableCookie = {
    val newA = ! isActive (f)
    val newF = f map (RuleSetting.active set (_, newA))

    EnableCookie (l updateFolder (f, newF))
  }
}

// vim: set ts=2 sw=2 et:
