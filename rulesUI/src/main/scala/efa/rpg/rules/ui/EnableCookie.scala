package efa.rpg.rules.ui

import efa.rpg.rules.{RuleSettings, RulesFolder}
import scalaz.State, scalaz.effect.IO

case class EnableCookie(st: State[RulesFolder,Unit]) {
  def run: IO[Unit] = RuleSettings mod st.exec
}

// vim: set ts=2 sw=2 et:
