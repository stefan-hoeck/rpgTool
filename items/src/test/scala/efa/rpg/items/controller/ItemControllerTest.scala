package efa.rpg.items.controller

import efa.core._
import efa.io._, valLogIO._
import efa.nb.UndoEdit
import efa.nb.node.{NodeOut, NbNode}
//import efa.react.{Events}
import efa.rpg.core.ItemData
import efa.rpg.items._
import efa.rpg.items.saver.{ItemSaver, XmlSaver, StreamsProvider}
import org.scalacheck._, Prop._, Arbitrary.arbitrary
import scalaz._, Scalaz._, effect._, scalacheck.ScalaCheckBinding._

object ItemControllerTest extends Properties("ItemController") {
  val logger = LoggerIO.consoleLogger

  def folder (a: Advantage): ValLogIO[Folder[Advantage,String]] = 
    point(Folder(Stream(a), Stream.empty, "Folder"))

  val templatesIO: ValLogIO[List[Advantage]] = point(Nil)

  def saver (a: Advantage) = ItemSaver[Advantage](
    a ⇒ liftIO (IO.ioUnit), folder(a), templatesIO)

  val itemOut = ItemNodes.defaultOut[Advantage] 

  def folderOut (as: List[Advantage]): StOut[Advantage] =
    FolderNode.defaultOut (itemOut, as) ∙ (s ⇒ (s.root, s))

  def advantageController (a: Advantage) =
    ItemController[Advantage](saver(a), folderOut, logger, logger)

  property("loading") = forAll {a: Advantage ⇒ 
    val res = for {
      c  ← advantageController(a)
      m  ← c.dbIn apply () flatMap (_._2.now)
      cs = c.info.rootNode.getChildren.getNodes(true)
    } yield (m == Map(a.id → a)) :| "map loaded" &&
      (cs.head.getDisplayName ≟ a.name) :| "node loaded"

    eval(res)
  }

  val pairGen = ^(arbitrary[Advantage], Gen.identifier)(Pair.apply)

  property("editing") = forAll(pairGen) {p: (Advantage,String) ⇒ 
    val (a,s) = p
    val res = for {
      c  ← advantageController(a)
      cs = c.info.rootNode.getChildren.getNodes(true)
      _  = cs.head.setName(s)
      m  ← c.dbIn apply () flatMap (_._2.now)
      cs2 = c.info.rootNode.getChildren.getNodes(true)
    } yield
      (m == Map(a.id → Advantage.data.name.set(a,s))) :|
      ("map updated: " + m) && 
      (cs2.head.getDisplayName ≟ s) :| "node updated"

    eval(res)
  }

  property("undo") = forAll(pairGen) {p: (Advantage,String) ⇒ 
    val ud = UndoEdit(IO.ioUnit, IO.ioUnit)
    val (a,s) = p
    val res = for {
      ref ← IO newIORef ud
      c   ← advantageController(a)
      _   ← undoIn to (ref write _) apply ()
      cs = c.info.rootNode.getChildren.getNodes(true)
      _  = cs.head.setName(s)
      name1 = c.info.rootNode.getChildren.getNodes(true).head.getDisplayName
      ud1 ← ref.read //actual UndoRedo
      _   ←  ud1.un //undo
      m   ←  c.dbIn apply () flatMap (_._2.now)
      name2 = c.info.rootNode.getChildren.getNodes(true).head.getDisplayName
    } yield (m == Map(a.id → a)) :| "map undone" &&
      (name1 ≟ s) :| "name set" &&
      (name2 ≟ a.name) :| "node updated" &&
      (ud1 != ud) :| "UndoRedo fired after setting"

    eval(res)
  }

  property("input_numberOfChanges") = forAll(pairGen) {p ⇒ 
    val (a,s) = p
    val res = for {
      ref ← IO newIORef 0
      c   ← advantageController(a)
      _   ← c.dbIn.events to (_ ⇒ ref mod (1+) void) apply ()
      cs = c.info.rootNode.getChildren.getNodes(true).head.setName(s)
      count ← ref.read
    } yield (count ≟ 1) :| "set only once"

    eval(res)
  }

  property("undo_numberOfChanges") = forAll(pairGen) {p ⇒ 
    val ud = UndoEdit(IO.ioUnit, IO.ioUnit)
    val (a,s) = p
    val res = for {
      ref ← IO newIORef ud
      countR ← IO newIORef 0
      c   ← advantageController(a)
      _   ← undoIn to (ref write _) apply ()
      _   ← c.dbIn.events to (_ ⇒ countR mod (1+) void) apply ()
      _ = c.info.rootNode.getChildren.getNodes(true).head.setName(s)
      c1  ← countR.read
      ud1 ← ref.read //actual UndoRedo
      _   ←  ud1.un //undo
      c2 ← countR.read
    } yield ((c1 + 1) ≟ c2) :| "undone once" &&
      (1 ≟ c1) :| "set once"

    eval(res)
  }

  import java.io.{InputStream, ByteArrayInputStream,
    OutputStream, ByteArrayOutputStream}
  val emptyArr = IO(Array.empty[Byte])
  val inStream = emptyArr >>= (a ⇒ IO(new ByteArrayInputStream(a)))
  val outStream = IO(new ByteArrayOutputStream)
  val streamProvider =
    StreamsProvider(liftIO(inStream), liftIO(inStream), liftIO(outStream))
  val errorSaver = XmlSaver[Advantage]("advantage", streamProvider)
  
  val errorController =
    ItemController[Advantage](errorSaver, folderOut, logger, logger)

  property("error_when_loading") = {
    val res = for {
      c  ← errorController
      m  ← c.dbIn apply () flatMap (_._2.now)
    } yield (m == Map.empty) :| "map loaded"

    eval(res)
  }

  private def eval (i: IO[Prop]): Prop = i.unsafePerformIO
}

// vim: set ts=2 sw=2 et:
