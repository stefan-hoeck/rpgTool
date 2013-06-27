package efa.rpg.items.controller

import dire.{SF, SIn, Out}
import efa.core.{Folder, Default}
import efa.nb.node.{NbNode, NodeOut}
import efa.rpg.items._, ItemsStateTest.IsArbitrary
import org.openide.nodes.Node
import org.scalacheck._, Prop._
import scalaz._, Scalaz._, effect.IO

object FolderNodeTest
   extends Properties("FolderNode")
   with FolderFunctions 
   with TreeFunctions 
   with dire.util.TestFunctions {

  lazy val advOut: ItemNodes.FullOut[Advantage] =
    ItemNodes.name[Advantage] ∙ (_._1)

  property("name") = forAll { s: IState[Advantage] ⇒ 
    def res = for {
      n  ← NbNode()
      _  = simulate(pair(s), false)(testSF(FolderNode.name[Advantage], n))
    } yield n.getDisplayName ≟ IState.root.name.get(s)

    evalIO (res)
  }

  property("children") = forAll { s: IState[Advantage] ⇒ 
    def res = for {
      n  ← NbNode()
      _  = simulate(pair(s), false)(
             testSF(FolderNode.defaultOut[Advantage](advOut, Nil), n))
      fNames = folderToNames(s.root)
      nNames = nodeToNames(n)
    } yield (fNames ≟ nNames) :| {
      val should = fNames.toList mkString "\n"
      val was = nNames.toList mkString "\n"

      s"Exp:\n$should \nFound:\n$was"
    }

    evalPropIO (res)
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
      n  ← NbNode()
      _  = simulate(pair(s), false)(
             testSF(FolderNode.defaultOut[Advantage](advOut, Nil), n))
      cCount = n.getChildren.getNodes(true).size
      fNames = folderToNames(s.root)
      nNames = nodeToNames(n)
    } yield (fNames ≟ nNames) && (cCount ≟ as.size)

    evalIO (res)
  }

  def pair(s: IState[Advantage]): List[FolderPair[Advantage]] =
    List((s.root, s))

  private def evalIO (io: IO[Boolean]) = io.unsafePerformIO

  private def evalPropIO (io: IO[Prop]) = io.unsafePerformIO

  def folderToNames (f: IFolder[Advantage]): Tree[String] = {
    lazy val itemNames =
      f.data.sortWith(_.name < _.name) ∘ (a ⇒ leaf (a.name))
    lazy val folderNames =
      f.folders.sortBy(_.label._1) ∘ (folderToNames)

    node(f.label._1, folderNames #::: itemNames)
  }

  def nodeToNames (n: Node): Tree[String] = {
    def children = n.getChildren getNodes true toStream
    def childNames = children ∘ (nodeToNames)

    node(n.getDisplayName, childNames)
  }

  def testSF[A,B](no: NodeOut[A,B], n: NbNode)(o: Out[Any]) =
    IO(no sfSim (n, o))
}

// vim: set ts=2 sw=2 et:
