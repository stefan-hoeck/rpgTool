package efa.rpg.items.controller

import efa.core.{Folder, Default}
import efa.nb.node.NbNode
import efa.rpg.items._, ItemsStateTest.IsArbitrary
import efa.react.{SIn}
import org.openide.nodes.Node
import org.scalacheck._, Prop._
import scalaz._, Scalaz._, effect.IO

object FolderNodeTest
   extends Properties("FolderNode")
   with FolderFunctions 
   with TreeFunctions {

  lazy val advOut: ItemNodes.FullOut[Advantage] =
    ItemNodes.name[Advantage] ∙ (_._1)

  property("name") = forAll { s: IState[Advantage] ⇒ 
    def res = for {
      n  ← NbNode.apply
      no = FolderNode.name[Advantage] set n
      _  ← sin(s) andThen no go
    } yield n.getDisplayName ≟ IState.root.name.get(s)

    evalIO (res)
  }

  property("children") = forAll { s: IState[Advantage] ⇒ 
    def res = for {
      n  ← NbNode.apply
      no = try{FolderNode.defaultOut[Advantage] (advOut, Nil)} catch {
             case e: StackOverflowError ⇒ e.printStackTrace; throw e
           }
      _  ← sin(s) andThen no.set(n) apply ()
      fNames = folderToNames (s.root)
      nNames = nodeToNames (n)
    } yield fNames ≟ nNames

    evalIO (res)
  }

  val emptyFA: NameFolder[Advantage] =
    Folder(Stream.empty, Stream.empty, loc.folder)

  val as = Stream.fill(2000)(Default.!!![Advantage]).zipWithIndex map {
    case (a,i) ⇒ Advantage.data.id.set(a, i)}

  val itemsWide: NameFolder[Advantage] =
    Folder(as, Stream.empty, loc.folder)

  property("itemsWide") = {
    val s = IState fromFolder itemsWide
    def res = for {
      n  ← NbNode.apply
      no = try{FolderNode.defaultOut[Advantage] (advOut, Nil)} catch {
             case e: StackOverflowError ⇒ e.printStackTrace; throw e
           }
      _  ← sin(s) andThen no.set(n) apply ()
      cCount = n.getChildren.getNodes(true).size
      fNames = folderToNames (s.root)
      nNames = nodeToNames (n)
    } yield (fNames ≟ nNames) && (cCount ≟ as.size)

    evalIO (res)
  }

  def sin (s: IState[Advantage]): SIn[FolderPair[Advantage]] =
    (s.root, s).η[SIn]

  private def evalIO (io: IO[Boolean]) = io.unsafePerformIO

  def folderToNames (f: IFolder[Advantage]): Tree[String] = {
    lazy val itemNames =
      f.data.sortWith(_.name < _.name) ∘ (a ⇒ leaf (a.name))
    lazy val folderNames =
      f.folders.sortBy(_.label._1) ∘ (folderToNames)

    node (f.label._1, folderNames #::: itemNames)
  }

  def nodeToNames (n: Node): Tree[String] = {
    def children = n.getChildren getNodes true toStream
    def childNames = children ∘ (nodeToNames)

    node (n.getDisplayName, childNames)
  }

}

// vim: set ts=2 sw=2 et:
