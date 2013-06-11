package efa.rpg.rules.ui

import efa.nb.actions.CookieAllAction
import scalaz._, Scalaz._

class EnableRulesAction
   extends CookieAllAction[EnableCookie](loc.enableRulesAction) {
   def run(cs: Seq[EnableCookie]) = cs.toList foldMap (_.run)
}

// vim: set ts=2 sw=2 et:
