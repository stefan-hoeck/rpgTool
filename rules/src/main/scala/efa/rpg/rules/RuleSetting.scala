package efa.rpg.rules

import efa.core.{Localization, Localized, UniqueId, Named,
                 Described, DescribedFunctions}
import scalaz._, Scalaz._

case class RuleSetting(loc: Localization, active: Boolean)

object RuleSetting extends DescribedFunctions {
  def fromLoc (l: Localization): RuleSetting = RuleSetting(l, true)

  val locL: RuleSetting @> Localization =
    Lens.lensu((a,b) ⇒ a copy (loc = b), _.loc)

  val activeL: RuleSetting @> Boolean =
    Lens.lensu((a,b) ⇒ a copy (active = b), _.active)

  implicit lazy val RuleSettingEqual: Equal[RuleSetting] = Equal.equalA

  implicit lazy val RuleSettingUniqueId =
    new UniqueId[RuleSetting,String]
    with Localized[RuleSetting] 
    with Named[RuleSetting] 
    with Described[RuleSetting] 
    with ActiveL[RuleSetting] {
      def id (s: RuleSetting): String = loc(s).name
      def loc (s: RuleSetting) = s.loc
      def name (s: RuleSetting) = s.loc.locName
      def shortDesc (s: RuleSetting) = wrapHtml (s.loc.desc)
      def active (s: RuleSetting) = s.active
      def activate (s: RuleSetting) = 
        Lens.self[RulesFolder] update (s, activeL mod (! _, s))
    }
}

// vim: set ts=2 sw=2 et:
