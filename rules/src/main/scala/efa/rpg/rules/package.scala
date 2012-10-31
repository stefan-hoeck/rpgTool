package efa.rpg

import efa.core.{Folder, Localization, Service, UniqueId}
import efa.rpg.rules.spi.RulesLocal
import scalaz._, Scalaz._

package object rules {
  lazy val loc = Service.unique[RulesLocal](RulesLocal)

  type LocFolder = Folder[Localization,String]

  type RulesFolder = Folder[RuleSetting,(String, Int)]

  implicit lazy val RulesFolderUniqueId =
    UniqueId.get[RulesFolder,Int](_.label._2)
}

// vim: set ts=2 sw=2 et:
