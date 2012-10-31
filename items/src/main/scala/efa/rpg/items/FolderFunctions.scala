package efa.rpg.items

import scalaz._, Scalaz._
import collection.generic.Subtractable
import efa.core._, Efa._
import efa.rpg.core.RpgItem
import scala.xml.{Node, Attribute, Null, Text, MetaData}

trait FolderFunctions {
  self ⇒ 

  implicit def iFolderLenses[A,B] (l: A @> IFolder[B]) =
    IFolderLenses[A,B] (l)

  case class IFolderLenses[A,B] (lens: A @> IFolder[B])  {

    def name = lens >=> self.name

    def id = lens >=> self.id

    def items = lens >=> Folder.data

    def folders = lens >=> Folder.folders
  }

  def folders[A]: IFolder[A] @> Stream[IFolder[A]] = Folder.folders

  def items[A]: IFolder[A] @> Stream[A] = Folder.data

  def name[A]: IFolder[A] @> String = Folder.label >=> Lens.firstLens

  def id[A]: IFolder[A] @> Int = Folder.label >=> Lens.secondLens

  def empty[A] (id: Int, name: String): IFolder[A] =
    Folder(Stream.empty, Stream.empty, (name, id))
    
  def folderPairToItems[A:RpgItem](p: FolderPair[A]): List[ItemPair[A]] =
    items get p._1 sortBy RpgItem[A].name map ((_, p._2)) toList
    
  def folderPairToFolders[A](p: FolderPair[A]): List[FolderPair[A]] =
    folders get p._1 sortBy name[A].get map ((_, p._2)) toList

  val nameVal: Validator[String,String] =
    Validators.notEmptyString >=> Validators.maxStringLength (100)

  def itemToFolder[A] (a: A): NameFolder[A] =
    Folder(Stream(a), Stream.empty, loc.folder)

  def itemToPair[A:RpgItem] (a: A): ItemPair[A] =
    (a, IState fromFolder itemToFolder(a))

  def fromIdxFolder[A] (f: IFolder[A]): NameFolder[A] = f mapLabel (_._1)

  def folderToXml[A:ToXml] (lbl: String) = new ToXml[NameFolder[A]] {
    val seqToXml = ToXml.seqToXml[A](lbl)

    def toXml(f: NameFolder[A]): Seq[Node] = 
      ("name" xml f.label) ++
      (seqToXml toXml f.data.toSeq) ++
      (f.folders ∘ ("folder".xml(_)(this)))

    def fromXml (ns: Seq[Node]): ValRes[NameFolder[A]] = {
      def label = ns.readTag[String]("name")
      def data = seqToXml fromXml ns map (_.toStream)
      def forest = (ns \ "folder").toList traverse fromXml map (_.toStream)
      
      data ⊛ forest ⊛ label apply Folder.apply[A,String]
    }
  }
}

object FolderFunctions extends FolderFunctions

// vim: set ts=2 sw=2 et:
