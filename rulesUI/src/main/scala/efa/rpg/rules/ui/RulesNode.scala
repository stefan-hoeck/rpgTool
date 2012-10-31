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
    N.name[RuleSetting] (_.loc.locName) ⊹
    N.desc ("<html>%s</html>" format _.loc.desc) ⊹
    N.contextRootsA (List("ContextActions/RuleNode")) ⊹
    N.iconBase.contramap(_.active ? active | inactive) ⊹
    (N.cookie[EnableCookie] ∙ activateS)

  lazy val folderOut: FolderOut = {
    val rest: FolderOut =
      N.name[RulesFolder] (_.label._1) ⊹
      N.contextRootsA (List("ContextActions/RulesFolderNode")) ⊹
      N.iconBase.contramap(isActive(_) ? active | inactive) ⊹
      (N.cookie[EnableCookie] ∙ activateF)

    val settingsF: Factory[RulesFolder,Nothing] =
      (uniqueIdF(settingOut): Fac[List[RuleSetting]]) ∙ (_.data.toList)

    lazy val foldersF: Factory[RulesFolder,Nothing] =
      (uniqueIdF(allOut): Fac[List[RulesFolder]]) ∙ (_.folders.toList)

    lazy val chld: FolderOut = children(foldersF, settingsF)

    lazy val allOut: FolderOut = rest ⊹ chld

    allOut
  }

  private val active = "efa/rpg/rules/ui/activerule.png"
  private val inactive = "efa/rpg/rules/ui/inactiverule.png"
}

// vim: set ts=2 sw=2 et:
