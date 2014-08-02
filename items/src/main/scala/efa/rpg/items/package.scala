package efa.rpg

import scalaz._, Scalaz._
import efa.core.{Service, Folder, UniqueId}
import efa.rpg.items.spi.ItemsLocal

package object items {

  lazy val loc = Service.unique[ItemsLocal](ItemsLocal)

  type NameFolder[A] = Folder[A,String]

  type IFolder[A] = Folder[A,(String, Int)]

  type ItemPair[A] = (A,IState[A])

  type FolderPair[A] = (IFolder[A],IState[A])

  def emptyFolder[A]: Folder[A,String] =
    Folder(Stream.empty, Stream.empty, loc.folder)

  implicit def IFolderUniqueId[A] =
    UniqueId.get[IFolder[A],Int] (_.label._2)
}

// vim: set ts=2 sw=2 et:
