package efa.rpg.items.controller

/**
 * ItemInfos are used as a link between the ItemDo that represents the
 * files where RpgItems are stored and the corresponding RpgItems signal.
 */
import efa.core.syntax.lookup._
import efa.io.IOCached
import efa.nb.controller.SaveEvent
import efa.react.EIn
import efa.rpg.items.spi.ItemsInfoProvider
import org.openide.nodes.{Node, AbstractNode, Children}
import org.openide.util.Lookup
import scalaz._, Scalaz._, effect.IO

case class ItemsInfo (rootNode: Node, changes: EIn[IO[Unit]])

object ItemsInfo {

  private[this] lazy val dummy: IO[ItemsInfo] = for {
    n ← IO (new AbstractNode(Children.LEAF){})
  } yield ItemsInfo (n, ∅[EIn[IO[Unit]]])

  private[this] lazy val map: IOCached[Map[String,IO[ItemsInfo]]] = {
    def get = Lookup.getDefault.all[ItemsInfoProvider] map (
      _.foldLeft(Map.empty[String,IO[ItemsInfo]])(_ ++ _.infos)
    )

    IOCached(get)
  }

  def forName (name: String): IO[ItemsInfo] =
    map.get >>= (_ getOrElse (name, dummy))
}

// vim: set ts=2 sw=2 et:
