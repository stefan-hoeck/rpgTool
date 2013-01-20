package efa.rpg.rules

import efa.core.{Localization, Localized}
import efa.data.UniqueId
import scalaz._, Scalaz._

case class RuleSetting(loc: Localization, active: Boolean)

object RuleSetting {
  def fromLoc (l: Localization): RuleSetting = RuleSetting(l, true)

  val loc: RuleSetting @> Localization =
    Lens.lensu((a,b) ⇒ a copy (loc = b), _.loc)

  val active: RuleSetting @> Boolean =
    Lens.lensu((a,b) ⇒ a copy (active = b), _.active)

  implicit lazy val RuleSettingEqual: Equal[RuleSetting] = Equal.equalA

  implicit lazy val RuleSettingUniqueId
  : UniqueId[RuleSetting,String] with Localized[RuleSetting] =
    new UniqueId[RuleSetting,String] with Localized[RuleSetting] {
      def id (s: RuleSetting): String = loc(s).name
      def loc (s: RuleSetting) = s.loc
    }
}

// vim: set ts=2 sw=2 et:
