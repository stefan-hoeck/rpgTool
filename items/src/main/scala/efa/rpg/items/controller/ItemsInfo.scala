package efa.rpg.items.controller

/**
 * ItemInfos are used as a link between the ItemDo that represents the
 * files where RpgItems are stored and the corresponding RpgItems signal.
 */
import dire.SIn 
import efa.core.syntax.lookup._
import efa.nb.controller.SavableInfo
import efa.rpg.items.spi.ItemsInfoProvider
import org.openide.nodes.{Node, AbstractNode, Children}
import org.openide.util.Lookup
import scalaz._, Scalaz._, effect.IO

case class ItemsInfo(
    rootNode: Node,
    changes: SavableInfo ⇒ SIn[Unit])

object ItemsInfo {

  private[this] lazy val dummy: ItemsInfo = 
    ItemsInfo(new AbstractNode(Children.LEAF){}, _ ⇒ ∅[SIn[Unit]])

  private[this] lazy val map: Map[String,ItemsInfo] = {
    def get = Lookup.getDefault.all[ItemsInfoProvider] map (
      _.foldLeft(Map.empty[String,ItemsInfo])(_ ++ _.infos)
    )

    get.unsafePerformIO()
  }

  def forName (name: String): ItemsInfo = map getOrElse (name, dummy)
}

// vim: set ts=2 sw=2 et:
