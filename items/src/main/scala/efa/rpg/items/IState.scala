package efa.rpg.items

import efa.core.{Folder, EndoVal, Validators, Default}
import efa.rpg.core.{RpgItem, DB}
import scalaz._, Scalaz._

case class IState[+I] (
  root: IFolder[I], map: DB[I], folderId: Int, itemId: Int
)

object IState extends FolderFunctions {

  type UIState[I] = State[IState[I],Unit]

  def fromFolder[I:RpgItem](f: NameFolder[I]): IState[I] = {
    val r = RpgItem[I]
    val (folderId, root) = Folder indexFolders f
    val ids = root.allData ∘ r.id 
    val map = (ids zip root.allData) toMap
    val itemId = ids.isEmpty ? 0 | (ids.max + 1)

    IState (root, map, folderId, itemId)
  }

  def root[I]: IState[I] @> IFolder[I] =
    Lens.lensu((a,b) ⇒ a copy (root = b), _.root)

  def map[I]: IState[I] @> DB[I] =
    Lens.lensu((a,b) ⇒ a copy (map = b), _.map)

  def folderId[I]: IState[I] @> Int =
    Lens.lensu((a,b) ⇒ a copy (folderId = b), _.folderId)
  
  def itemId[I]: IState[I] @> Int =
    Lens.lensu((a,b) ⇒ a copy (itemId = b), _.itemId)

  private def rpg[I:RpgItem] = RpgItem[I]

  /**
   * Removes an item from the Folder tree as well as the map
   */
  def deleteItem[I:Equal:RpgItem] (i: I): UIState[I] =
    (map[I] -= rpg.id(i)) >> (root[I] remove i)

  /**
   * Removes a part of the Folder tree. All items in that part are also
   * removed from the map.
   */
  def deleteFolder[I:Equal:RpgItem] (f: IFolder[I]): UIState[I] =
    (map[I] --= (f.allData map rpg.id)) >>
    (root[I] removeFolder f)

  /**
   * Updates an item in the Folder tree. The old item is removed from
   * the map, while the new one is added.
   */
  def updateItem[I:Equal:RpgItem] (n: I): UIState[I] = for {
    _ ← map[I] += (rpg.id (n) → n)
    _ ← root[I].updateWhere(n)(rpg.id(_) ≟ rpg.id(n))
  } yield ()

  /**
   * Renames a folder.
   */
  def renameFolder[I:Equal] (f: IFolder[I], s: String): UIState[I] =
    root[I].updateFolder (f, name[I].set (f, s))
  
  /**
   * Renames an item. This also changes the item map.
   */
  def renameItem[I:Equal:RpgItem] (i: I, s: String): UIState[I] =
    updateItem (rpg.nameL set (i, s))

  /**
   * Adds an empty folder with the given name to parent folder p.
   * The folder id is increased by one afterwards.
   */
  def addFolder[I:Equal] (p: IFolder[I], s: String): UIState[I] = for {
    os ← init[IState[I]]
    nf = folders[I] mod (empty (os.folderId, s) #:: _, p)
    _  ← root[I].updateFolder (p, nf)
    _  ← folderId += 1
  } yield ()

  /**
   * Adds an item to the parent folder p.
   * Adjust's the items id and increases the state's itemId by 1.
   */
  def addItem[I:Equal:RpgItem] (p: IFolder[I], i: I): UIState[I] = for {
    os ← init[IState[I]]
    ni = RpgItem[I].idL set (i, os.itemId)
    nf = items[I] mod (ni +: _, p)
    _  ← root[I] updateFolder (p, nf)
    _  ← map[I] += (os.itemId → ni)
    _  ← itemId += 1
  } yield ()

  /**
   * Adds an item to the parent folder p.
   * Adjust's the items id and increases the state's itemId by 1.
   */
  def moveItem[I:Equal:RpgItem] (p: IFolder[I], i: I): UIState[I] = for {
    _ ← deleteItem(i)
    _ ← addItem(p, i)
  } yield ()

  def nameVal[A:RpgItem](isCreate: Boolean)(p: ItemPair[A]): EndoVal[String] = {
    def names: Set[String] = {
      val s = p._2.map map { p ⇒ RpgItem[A] name p._2 } toSet
      
      isCreate ? s | (s - RpgItem[A].name(p._1))
    }

    Validators.notEmptyString >=>
    Validators.maxStringLength (200) >=>
    Validators.uniqueString(names, efa.core.loc.name)
  }
  
  implicit def IStateEqual[A:Equal]: Equal[IState[A]] = 
    Equal.equalBy (s ⇒ (s.root, s.map, s.folderId, s.itemId))

  implicit def IStateDefault[A:RpgItem]: Default[IState[A]] = 
    Default default fromFolder[A](emptyFolder)
}

// vim: set ts=2 sw=2 et:
