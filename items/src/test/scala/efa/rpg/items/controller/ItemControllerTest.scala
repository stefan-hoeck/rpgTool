package efa.rpg.items.controller

import dire.swing.UndoEdit
import efa.core._
import efa.io._, logDisIO._
import efa.nb.node.{NodeOut, NbNode}
import efa.rpg.core.ItemData
import efa.rpg.items._
import efa.rpg.items.saver.ItemSaver
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

  property("loading") = forAll {a: Advantage ⇒ 
    val res = for {
      c  ← advantageController(a)
      m  = runN(c.dbIn, 1).head
    } yield (m ≟ Map(a.id → a)) :| "map loaded"

    eval(res)
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

  private def eval(i: IO[Prop]): Prop = i.unsafePerformIO
}

// vim: set ts=2 sw=2 et:
