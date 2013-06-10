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
    FolderNode.defaultOut(itemOut, as) ∙ (s ⇒ (s.root, s))

  def controller(a: Advantage) = ItemController.create[Advantage](
    saver(a), folderOut, logger, logger, false)

  def controllerT(a: Advantage) = ItemController.create[Advantage](
    saver(a), folderOut, logger, logger, true)


  property("loading_dbIn") = forAll {a: Advantage ⇒ 
    val res = for {
      c  ← controller(a)
      m  = runN(c.dbIn, 1).head
    } yield (m ≟ Map(a.id → a)) :| "map loaded"

    eval(res)
  }

  property("loading_node_set") = forAll { a: Advantage ⇒ 
    runUI(a) ≟ List(Map(a.id → a))
  }

  property("rename") = forAll {a: Advantage ⇒ 
    val newName = a.name ++ "blub"
    val newA = RpgItem[Advantage].nameL.set(a, newName)

    val m = runUI(a, Rename(newName))
    val exp = List(Map(a.id → a), Map(a.id → newA))

    (m ≟ exp) :| s"renamed: $exp but was $m"
  }

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

  private def runUI(a: Advantage, es: Event*): List[DB[Advantage]] =
    simulate(es.toList, true)(eventSf(a))

  private def eval(i: IO[Prop]): Prop = i.unsafePerformIO

  sealed trait Event
  case class Rename(s: String) extends Event

  private def eventSf(a: Advantage)(o: Out[Unit])
    : IO[SF[Event,DB[Advantage]]] = for {
      c  ← controllerT(a)
      n  = c.info.rootNode
      sf ← IO {
             def onE(e: Event): IO[Unit] = e match {
               case Rename(s) ⇒ 
                 IO(n.getChildren.getNodes(true).head.setName(s))
             }
             
             SF.id[Event].syncTo(onE) >> c.sf.map(_.map)
            }
    } yield sf
}

// vim: set ts=2 sw=2 et:
