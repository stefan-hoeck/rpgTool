package efa.rpg.items.dob

//import efa.core.Efa._
//import efa.rpg.items.controller.ItemsInfo
//import org.netbeans.api.actions.Savable
//import org.scalacheck._, Prop._
//import scalaz._, Scalaz._, effect._
//
//object ItemDoTest extends Properties("ItemDo") {
//
//  property("registered") = (for {
//    es ← TestEvents.get
//    ei ← ItemsInfo forName "test"
//    cs ← ei.changes.go
//  } yield cs._2 == es).unsafePerformIO
//
//  property("behavior") = {
//    val res = for {
//      es         ← TestEvents.get
//      modified   ← IO newIORef false
//      saveRes    ← IO newIORef ""
//      fields     ← ItemDo.fields (modified write _ void, "test")
//      lookup     = Savable.REGISTRY
//      mod1       ← modified.read
//      scOption1  ← lookup.head[Savable]
//      _          ← es fire saveRes.write("save 1")
//      mod2       ← modified.read
//      scOption2  ← lookup.head[Savable]
//      savable    = scOption2.get
//      _          = savable.save()
//      mod3       ← modified.read
//      save1      ← saveRes.read
//      scOption3  ← lookup.head[Savable]
//      _          ← es fire saveRes.write("save 2")
//      mod4       ← modified.read
//      scOption4  ← lookup.head[Savable]
//      _          = savable.save()
//      mod5       ← modified.read
//      save2      ← saveRes.read
//      scOption5  ← lookup.head[Savable]
//    } yield ((mod1 ≟ false) && scOption1.isEmpty) :| "initialization" &&
//      ((mod2 ≟ true) && scOption2.get == savable) :| "first fire" &&
//      ((mod3 ≟ false) && scOption3.isEmpty) :| "first save" &&
//      ((mod4 ≟ true) && scOption4.get == savable) :| "second fire" &&
//      ((mod5 ≟ false) && scOption5.isEmpty) :| "second save" &&
//      (save1 ≟ "save 1") :| "first save write" &&
//      (save2 ≟ "save 2") :| "second save write"
//
//    res.unsafePerformIO
//  }
//}

// vim: set ts=2 sw=2 et:
