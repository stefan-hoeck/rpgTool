package efa.rpg.items.controller

import dire.{SIn, SF, Out}
import efa.core._, Efa._
import efa.nb.node.{NbNode, NodeOut}
import efa.rpg.items._
import efa.rpg.core.{HtmlDesc}
import org.openide.cookies.EditCookie
import org.scalacheck._, Prop._
import scalaz._, Scalaz._, effect.IO

object ItemNodeTest extends Properties ("ItemNode")
  with FolderFunctions 
  with dire.util.TestFunctions {

  type AState = ValRes[State[IState[Advantage], Unit]]

  lazy val itemOut = ItemNodes.defaultOut[Advantage]

  def itemSF(n: NbNode)(o: Out[Any]) = IO(itemOut sfSim (n, o))

  property ("defaultOut") = forAll { a: Advantage ⇒ 
    val pair = itemToPair(a)
    val hd = HtmlDesc(a.name, a.desc)

    val res = for {
      n   ← NbNode()
      _   = simulate(List(pair), false)(itemSF(n))
      ec  ← n.getLookup.head[EditCookie]
      hdo ← n.getLookup.head[HtmlDesc]
    } yield (n.getDisplayName ≟ a.name) :| "display name" &&
            (n.getShortDescription ≟ a.desc) :| "shortDesc" &&
            (hdo ≟ hd.some) :| "htmlDesc" &&
            ec.nonEmpty :| "edit cookie" &&
            n.canDestroy :| "delete" &&
            n.canRename :| "rename"

    evalPropIO (res)
  }

  val nesVal = Validators.notEmptyString

  lazy val renameOut = ItemNodes.rename[Advantage](_ ⇒ nesVal)

  property ("rename") = forAll { p: (Advantage,String) ⇒ 
    val (a, s) = p

    simulate(List(s), true)(eventSf(a)) ∀ { _.isSuccess ≟ nesVal(s).isRight }
  }

  private def evalIO(io: IO[Boolean]) = io.unsafePerformIO

  private def evalPropIO(io: IO[Prop]) = io.unsafePerformIO

  private def eventSf(a: Advantage)(o: Out[Any])
    : IO[SF[String,VSt[Advantage]]] = for {
      n  ← NbNode()
      sf ← IO {
             def onE(s: String): IO[Unit] = IO(n.setName(s))

             (SF.id[String] syncTo onE) >> 
             (SF.once(itemToPair(a)) >=> renameOut.sfSim(n, o).syncTo(o))
           }
    } yield sf
}

// vim: set ts=2 sw=2 et:
