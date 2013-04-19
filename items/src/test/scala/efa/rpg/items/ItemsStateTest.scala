package efa.rpg.items

//import efa.rpg.core.ItemData
//import org.scalacheck._, Prop._
//import scalaz._, Scalaz._
//
//object ItemsStateTest
//  extends Properties ("ItemsState") 
//  with FolderFunctions {
//
//  lazy val stateGen = Gens.rootGen map (IState.fromFolder[Advantage])
//
//  implicit lazy val IsArbitrary = Arbitrary (stateGen)
//
//  lazy val stateItemGen = for {
//    st ← stateGen
//    it ← Gen oneOf (st.map.toSeq) map (_._2)
//  } yield (st,it)
//
//  lazy val stateFolderGen = for {
//    st ← stateGen
//    it ← Gen oneOf (st.root.allFolders.tail)
//  } yield (st,it)
//
//  property("fromFolder") = Prop.forAll { is: IState[Advantage] ⇒
//    validState (is)
//  }
//
//  property("deleteItem") = Prop.forAll (stateItemGen) { p ⇒ 
//    val (st, itm) = p
//    val newSt = IState deleteItem itm exec st
//
//    (newSt.map.get (itm.id) ≟ none) :| "removed from map" &&
//    (newSt.root.find(itm ≟ ) ≟ none) :| "removed from folders" &&
//    validState (newSt)
//  }
//
//  property("deleteFolder") = Prop.forAll (stateFolderGen) { p ⇒ 
//    val (st, f) = p
//    val newSt = IState deleteFolder f exec st
//    val goners = f.allData
//
//    (goners.∀ (g ⇒ newSt.map.get (g.id) ≟ none)) :| "removed from map" &&
//    (newSt.root.findFolder(f ≟ ) ≟ none) :| "removed from folders" &&
//    validState (newSt)
//  }
//
//  property("updateItem") = Prop.forAll (stateItemGen) { p ⇒ 
//    val (st, itm) = p
//    val newItm = Advantage.data.name.set(itm, "ürgl")
//    val newSt = IState updateItem newItm exec st
//
//    (newSt.map (itm.id) ≟ newItm) :| "updated map" &&
//    (newSt.root.find(newItm ≟ ) ≟ newItm.some) :| "updated folders" &&
//    validState (newSt)
//  }
//
//  property("renameFolder") = Prop.forAll (stateFolderGen) { p ⇒ 
//    val (st, f) = p
//    val newN = name (f) + "ürgl"
//    val newF = FolderFunctions.name.set (f, newN)
//    val newSt = IState renameFolder (f, newN) exec st
//    val pred = (x: IFolder[Advantage]) ⇒ 
//      FolderFunctions.name.get (x) ≟ newN
//
//    (newSt.root.findFolder(pred) ≟ newF.some) :| "updated folder" &&
//    validState (newSt)
//  }
//
//  property("renameItem") = Prop.forAll (stateItemGen) { p ⇒ 
//    val (st, itm) = p
//    val newN = itm.name + "ürgl"
//    val newI = Advantage.data.name.set(itm, newN)
//    val newSt = IState renameItem (itm, newN) exec st
//    val pred = (x: Advantage) ⇒ x.name ≟ newN
//
//    (newSt.root.find(pred) ≟ newI.some) :| "updated folders" &&
//    (newSt.map (itm.id) ≟ newI) :| "updated map" &&
//    validState (newSt)
//  }
//  
//  property("addFolder") = Prop.forAll (stateFolderGen) { p ⇒ 
//    val (st, f) = p
//    val newSt = IState addFolder (f, "folder") exec st
//    val oldId = st.folderId
//    val newF = folderById (newSt, oldId).get
//    
//    (FolderFunctions.name.get (newF) ≟ "folder") :| "set name" &&
//    (newSt.folderId ≟ (oldId + 1)) :| "increased id" &&
//    validState (newSt)
//  }
//  
//  property("addItem") = Prop.forAll (stateFolderGen) { p ⇒ 
//    val (st, f) = p
//    val newItm = Advantage (ItemData (0, "test", "desc"), 1000)
//    val oldId = st.itemId
//    val newSt = IState addItem (f, newItm) exec st
//    val foundItm = itemById (newSt, oldId).get
//    
//    (foundItm.name ≟ "test") :| "added item" &&
//    (foundItm.id ≟ oldId) :| "set id" &&
//    (newSt.itemId ≟ (oldId + 1)) :| "increased id" &&
//    (newSt.map (oldId) ≟ foundItm) :| "added to map" &&
//    validState (newSt)
//  }
//
//  def validState (is: IState[Advantage]): Prop = {
//    val fIds = is.root.allFolders ∘ (id get _)
//    val items = is.root.allData
//    val itemIds = items ∘ (_.id)
//    val map = (itemIds zip items).toMap
//
//    (is.folderId > fIds.max) :| "folderId" &&
//    (is.itemId > itemIds.max) :| "itemId" &&
//    (is.map == map) :| "map" &&
//    (is.map.size ≟ items.size) :| "mapSize"
//  }
//
//  def folderById (st: IState[Advantage], id: Int): Option[IFolder[Advantage]] =
//    st.root.findFolder(FolderFunctions.id.get (_) ≟ id)
//
//  def itemById (st: IState[Advantage], id: Int): Option[Advantage] =
//    st.root.find(_.id ≟ id)
//}

// vim: set ts=2 sw=2 et:
