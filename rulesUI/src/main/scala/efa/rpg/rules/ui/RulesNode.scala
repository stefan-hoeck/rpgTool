package efa.rpg.rules.ui

import efa.nb.node.{NbNode ⇒ N, NbChildren, NodeOut}, NbChildren._
import efa.rpg.rules._
import org.openide.nodes.Node
import scalaz._, Scalaz._

object RulesNode {

  lazy val root: Node = (for {
    n ← N()
    _ ← efa.nb.NbSystem.forever(RuleSettings.in >=> folderOut.sf(n))
  } yield n).unsafePerformIO()

  type NOut[A] = NodeOut[A,Any]
  type FolderOut = NOut[RulesFolder]
  type Fac[A] = Factory[A,Any]
  
  lazy val settingOut: NOut[RuleSetting] =
    (N.named: NOut[RuleSetting]) ⊹
    N.described ⊹
    N.contextRootsA (List("ContextActions/RuleNode")) ⊹
    activeIcon ⊹
    enable

  lazy val folderOut: FolderOut = {
    val rest: FolderOut =
      (N.named: FolderOut) ⊹
      N.contextRootsA (List("ContextActions/RulesFolderNode")) ⊹
      activeIcon ⊹
      enable

    val settingsF: Factory[RulesFolder,Any] =
      uidF(settingOut){ r: RulesFolder ⇒ r.data }

    lazy val foldersF: Factory[RulesFolder,Any] =
      uidF(allOut){ r: RulesFolder ⇒ r.folders }

    lazy val chld: FolderOut = children(foldersF, settingsF)

    lazy val allOut: FolderOut = rest ⊹ chld

    allOut
  }

  private def activeIcon[A:ActiveL]: NodeOut[A,Any] =
    N.iconBase ∙ (
      ActiveL[A].active(_) ?
      "efa/rpg/rules/ui/activerule.png" |
      "efa/rpg/rules/ui/inactiverule.png"
    )

  private def enable[A:ActiveL]: NodeOut[A,Any] =
    N.cookie[EnableCookie,Any] ∙ { a: A ⇒
      EnableCookie(ActiveL[A] activate a)
    }
}

// vim: set ts=2 sw=2 et:
