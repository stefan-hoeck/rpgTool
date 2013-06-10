package efa.rpg.items.controller

import dire.{Out, SIn, SF}
import dire.swing.UndoEdit
import efa.core._
import efa.io._, logDisIO._
import efa.nb.node.{NodeOut, NbNode}
import efa.rpg.core.{ItemData, DB, RpgItem}
import efa.rpg.items._
import efa.rpg.items.saver.ItemSaver
import org.openide.nodes.Node
import org.scalacheck._, Prop._, Arbitrary.arbitrary
import scalaz._, Scalaz._, effect._, scalacheck.ScalaCheckBinding._

object ItemControllerTest
  extends Properties("ItemController")
  with dire.util.TestFunctions {
  val logger = LoggerIO.consoleLogger

  def folder(a: Advantage): LogDisIO[Folder[Advantage,String]] = 
    point(Folder(Stream(a), Stream.empty, "Folder"))

  val templatesIO: LogDisIO[List[Advantage]] = point(Nil)

  def saver(a: Advantage) = ItemSaver[Advantage](
    a ⇒ liftIO (IO.ioUnit), folder(a), templatesIO)

  val itemOut = ItemNodes.defaultOut[Advantage] 

  def folderOut (as: List[Advantage]): StOut[Advantage] =
    FolderNode.defaultOut (itemOut, as) ∙ (s ⇒ (s.root, s))

  def advantageController(a: Advantage) =
    ItemController[Advantage](saver(a), folderOut, logger, logger)

  def advantageControllerT(a: Advantage, o: Out[Unit]) =
    ItemController.create[Advantage](saver(a), folderOut, logger, logger, o, true)

  property("loading_dbIn") = forAll {a: Advantage ⇒ 
    val res = for {
      c  ← advantageController(a)
      m  = runN(c.dbIn, 1).head
    } yield (m ≟ Map(a.id → a)) :| "map loaded"

    eval(res)
  }

  property("loading_node_set") = forAll {a: Advantage ⇒ 
    var node: Node = null
    val  m  = simulate(List[Event](), true)(eventSf(a, node = _))
    val  n  = node.getChildren.getNodes(true).head.getDisplayName

    (m ≟ List(Map(a.id → a))) :| "map loaded" &&
    (n ≟ a.name) :| s"name set"
  }

//  property("rename") = forAll {a: Advantage ⇒ 
//    val newName = a.name ++ "blub"
//    val newA = RpgItem[Advantage].nameL.set(a, newName)
//    var node: Node = null
//
//    val m = simulate(List[Event](Rename(newName)), true)(eventSf(a, node = _))
//    val n = node.getChildren.getNodes(true).head.getDisplayName
//    val exp = List(Map(a.id → a), Map(a.id → newA))
//
//    (m ≟ exp) :| s"renamed: $exp but was $m" &&
//    (newName ≟ n) :| s"name set"
//  }

  val errorSaver = ItemSaver.xmlSaver[Advantage]("blub", "blub", getClass)
  
  val errorController =
    ItemController[Advantage](errorSaver, folderOut, logger, logger)

  property("error_when_loading") = {
    val res = for {
      c  ← errorController
      m  = runN(c.dbIn, 1).head
    } yield (m == Map.empty) :| "map loaded"

    eval(res)
  }

  private def eval(i: IO[Prop]): Prop = i.unsafePerformIO

  sealed trait Event
  case class Rename(s: String) extends Event

  private def eventSf(a: Advantage, setNode: Node ⇒ Unit)(o: Out[Unit])
    : IO[SF[Event,DB[Advantage]]] = for {
      c  ← advantageControllerT(a, o)
      n  = c.info.rootNode
      _  = setNode(n)
      sf ← IO {
             def onE(e: Event): IO[Unit] = e match {
               case Rename(s) ⇒ IO.putStrLn(s"Name set $s") >> IO(n.setName(s)) 
             }
             
             SF.id[Event].syncTo(onE) >> c.sf >> c.dbIn
           }
    } yield sf
}

// vim: set ts=2 sw=2 et:
