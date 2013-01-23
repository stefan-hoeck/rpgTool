package efa.rpg.rules.ui

import efa.nb.node.{NbNode ⇒ N, NbChildren, NodeOut}, NbChildren._
import efa.rpg.rules._
import org.openide.nodes.Node
import scalaz._, Scalaz._

object RulesNode {

  lazy val root: Node = (for {
    n ← N.apply
    _ ← RuleSettings.in andThen folderOut.set(n) go
  } yield n).unsafePerformIO()

  type NOut[-A] = NodeOut[A,Nothing]
  type FolderOut = NOut[RulesFolder]
  type Fac[-A] = Factory[A,Nothing]
  
  lazy val settingOut: NOut[RuleSetting] =
    N.named[RuleSetting] ⊹
    N.described ⊹
    N.contextRootsA (List("ContextActions/RuleNode")) ⊹
    activeIcon ⊹
    enable

  lazy val folderOut: FolderOut = {
    val rest: FolderOut =
      N.named[RulesFolder] ⊹
      N.contextRootsA (List("ContextActions/RulesFolderNode")) ⊹
      activeIcon ⊹
      enable

    val settingsF: Factory[RulesFolder,Nothing] =
      (uniqueIdF(settingOut): Fac[List[RuleSetting]]) ∙ (_.data.toList)

    lazy val foldersF: Factory[RulesFolder,Nothing] =
      (uniqueIdF(allOut): Fac[List[RulesFolder]]) ∙ (_.folders.toList)

    lazy val chld: FolderOut = children(foldersF, settingsF)

    lazy val allOut: FolderOut = rest ⊹ chld

    allOut
  }

  private def activeIcon[A:ActiveL]: NodeOut[A,Nothing] =
    N.iconBase ∙ (
      ActiveL[A].active (_) ?
      "efa/rpg/rules/ui/activerule.png" |
      "efa/rpg/rules/ui/inactiverule.png"
    )

  private def enable[A:ActiveL]: NodeOut[A,Nothing] =
    N.cookie[EnableCookie] ∙ {a: A ⇒ EnableCookie(ActiveL[A] activate a)}
}

// vim: set ts=2 sw=2 et:
