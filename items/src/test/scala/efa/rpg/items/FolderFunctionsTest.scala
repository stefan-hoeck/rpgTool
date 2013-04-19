package efa.rpg.items

//import efa.core._, Efa._
//import org.scalacheck._, Arbitrary._, Prop._
//import scalaz._, Scalaz._, scalacheck.ScalaCheckBinding._
//
//object FolderFunctionsTest
//  extends Properties("FolderFunctions")
//  with FolderFunctions {
//
//  type AFolder = IFolder[Advantage]
//
//  implicit lazy val TreeArbitrary = Arbitrary (Gens.rootGen)
//
//  implicit lazy val AFolderArbitrary = Arbitrary[AFolder] (Gens.iRootGen)
//
//  implicit lazy val LabelArbitrary = Arbitrary (Gens.lblGen eval 0)
//
//  implicit lazy val ItemsArbitrary = Arbitrary (Gens.nAdvGen (3))
//
//  implicit lazy val folderXml = folderToXml[Advantage]("advantage")
//
//  property("folderToXml") = Prop.forAll { f: NameFolder[Advantage] ⇒ 
//    val xml = "test" xml f
//    xml.read[NameFolder[Advantage]] ≟ f.success
//  }
//
//  property("indexFolders") = Prop.forAll { f: NameFolder[Advantage] ⇒ 
//    val (idx,iFolder) = Folder indexFolders f
//    val ids: List[Int] = iFolder.allFolders map (_.label._2) toList
//    val fnd = ids sortWith (_ < _)
//    val exp = List.range(0,idx)
//
//    (fnd ≟ exp) :| "Exp: %s, Fnd: %s".format(exp, fnd)
//  }
//  
//  val selfL = Lens.lensId[AFolder]
//
//  property("labelGet") = Prop.forAll { f: AFolder ⇒ 
//    (selfL.label get f) ≟ f.label
//  }
//
//  property("labelSet") = Prop.forAll {
//    p: (AFolder,(String,Int)) ⇒ 
//    val (f,l) = p
//    (selfL.label get (selfL.label set (f,l))) ≟ l
//  }
//
//  property("foldersGet") = Prop.forAll { f: AFolder ⇒ 
//    (selfL.folders get f) ≟ f.folders
//  }
//
//  property("foldersSet") = Prop.forAll {
//    p: (AFolder,AFolder) ⇒ 
//    val (f,l) = p
//    (selfL.folders get (selfL.folders set (f,l.folders))) ≟ l.folders
//  }
//
//  property("nameGet") = Prop.forAll { f: AFolder ⇒ 
//    (selfL.name get f) ≟ f.label._1
//  }
//
//  property("nameSet") = Prop.forAll { p: (AFolder,String) ⇒ 
//    val (f,s) = p
//    (selfL.name get (selfL.name set (f,s))) ≟ s
//  }
//
//  property("itemsGet") = Prop.forAll { f: AFolder ⇒ 
//    (selfL.items get f) ≟ f.data
//  }
//
//  property("itemsSet") = Prop.forAll { p: (AFolder,Stream[Advantage]) ⇒ 
//    val (f,is) = p
//    (selfL.items get (selfL.items set (f,is))) ≟ is
//  }
//
//  property("idGet") = Prop.forAll { f: AFolder ⇒ 
//    (selfL.id get f) ≟ f.label._2
//  }
//
//  property("idSet") = Prop.forAll { p: (AFolder,Int) ⇒ 
//    val (f,i) = p
//    (selfL.id get (selfL.id set (f,i))) ≟ i
//  }
//
//  lazy val rootPlusSingleGen: Gen[(AFolder,AFolder)] = for {
//    root    ← Gens.iRootGen
//    subTree ← Gen oneOf root.allFolders.tail
//  } yield (root, subTree)
//
//  lazy val rootPlusItemGen: Gen[(AFolder,Advantage)] = for {
//    root ← Gens.iRootGen
//    item ← Gen oneOf root.allData
//  } yield (root, item)
//    
//  property("findFolder") = Prop.forAll(rootPlusSingleGen) { p ⇒
//    val (r, s) = p
//    r.findFolder(s≟) ≟ s.some
//  }
//    
//  property("findItem") = Prop.forAll(rootPlusItemGen) { p ⇒
//    val (r, s) = p
//    r.find(s≟) ≟ s.some
//  }
//    
//  property("filterFolder") = Prop.forAll(rootPlusSingleGen) { p ⇒
//    val (r, s) = p
//    r.filterFolder(s ≠).findFolder(s≟ ) ≟ none
//  }
//    
//  property("filterItem") = Prop.forAll(rootPlusItemGen) { p ⇒
//    val (r, s) = p
//    r.filter(s ≠).find(s≟) ≟ none[Advantage]
//  }
//    
//  property("updateFolder") = Prop.forAll(rootPlusSingleGen) { p ⇒
//    val (r, s) = p
//    val i = id get s
//    def newFolder = FolderFunctions.name.set (s, "")
//    def pred = (fld: AFolder) ⇒ id.get (fld) ≟ i
//    def updated = r updateFolder (s, newFolder)
//    (updated.findFolder(pred) ≟ newFolder.some) :| "updated" && 
//    (selfL.removeFolder(s).apply (r) ≟
//     selfL.removeFolder(newFolder).apply (updated)) :| "preserved"
//  }
//  
//  property("updateItem") = Prop.forAll(rootPlusItemGen) { p ⇒
//    val (r, item) = p
//    val i = item.id
//    def newItem = Advantage.data.name.set(item, "")
//    def pred = (itm: Advantage) ⇒ itm.id ≟ i
//    def updated = r.update(item, newItem)
//    updated.find(pred) ≟ newItem.some
//  }
//}

// vim: set ts=2 sw=2 et:
