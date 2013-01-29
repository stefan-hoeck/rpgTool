package efa.rpg

import efa.core.{Folder, Localization, Service, UniqueId, Named}
import efa.rpg.rules.spi.RulesLocal
import scalaz._, Scalaz._

package object rules {
  lazy val loc = Service.unique[RulesLocal](RulesLocal)

  type LocFolder = Folder[Localization,String]

  type RulesFolder = Folder[RuleSetting,(String, Int)]

  implicit lazy val RulesFolderUniqueId = 
    new UniqueId[RulesFolder,Int]
    with Named[RulesFolder] 
    with ActiveL[RulesFolder] {
      def id (f: RulesFolder) = f.label._2
      def name (f: RulesFolder) = f.label._1
      def active (f: RulesFolder) = f find (_.active) nonEmpty
      def activate (f: RulesFolder) = {
        val newA = ! active (f)
        val newF = f map (RuleSetting.activeL set (_, newA))

        Lens.self[RulesFolder] updateFolder (f, newF)
      }
    }
}

// vim: set ts=2 sw=2 et:
