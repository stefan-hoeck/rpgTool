package efa.rpg.items.controller

//import efa.core._, Efa._
//import efa.nb.node.NbNode
//import efa.react._
//import efa.rpg.items._
//import efa.rpg.core.{HtmlDesc}
//import org.openide.cookies.EditCookie
//import org.scalacheck._, Prop._
//import scalaz._, Scalaz._, effect.IO
//
//object ItemNodeTest extends Properties ("ItemNode") with FolderFunctions {
//
//  type AState = ValRes[State[IState[Advantage], Unit]]
//
//  lazy val itemOut = ItemNodes.defaultOut[Advantage]
//
//  property ("defaultOut") = forAll { a: Advantage ⇒ 
//    val pair = itemToPair(a)
//    val hd = HtmlDesc(a.name, a.desc)
//
//    val res = for {
//      n   ← NbNode.apply
//      _   ← pair.η[SIn] andThen itemOut.set(n) runIO ()
//      ec  ← n.getLookup.head[EditCookie]
//      hdo ← n.getLookup.head[HtmlDesc]
//    } yield (n.getDisplayName ≟ a.name) :| "display name" &&
//            (n.getShortDescription ≟ a.desc) :| "shortDesc" &&
//            (hdo ≟ hd.some) :| "htmlDesc" &&
//            ec.nonEmpty :| "edit cookie" &&
//            n.canDestroy :| "delete" &&
//            n.canRename :| "rename"
//
//    evalPropIO (res)
//  }
//
//  val nesVal = Validators.notEmptyString
//
//  lazy val renameOut = ItemNodes.rename[Advantage](_ ⇒ nesVal)
//
//  property ("rename") = forAll { p: (Advantage,String) ⇒ 
//    val (a, s) = p
//    val pair = itemToPair(a)
//
//    val res = for {
//      stRef ← IO newIORef "".failureNel[St[Advantage]]
//      n     ← NbNode.apply // new node
//      _     ← pair.η[SIn] andThen renameOut.set(n) to (stRef write _) runIO ()
//      _     = n.setName (s) // change node's name. This should set stRef
//      v     ← stRef.read
//    } yield (nesVal(s).isRight ≟ v.isSuccess)
//
//    evalIO (res)
//  }
//
//  private def evalIO (io: IO[Boolean]) = io.unsafePerformIO
//
//  private def evalPropIO (io: IO[Prop]) = io.unsafePerformIO
//}

// vim: set ts=2 sw=2 et:
