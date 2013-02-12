package efa.rpg.items.specs

import efa.core.Folder
import efa.rpg.core.RpgItem
import efa.rpg.items.FolderFunctions
import efa.rpg.items.controller.{ItemPanel, IEditable}
import org.scalacheck._, Prop._
import scalaz._, Scalaz._, effect._

abstract class PanelFactoryProps[A:Arbitrary:Equal:IEditable:RpgItem](
  name: String
) extends Properties(name)
  with IoProps 
  with FolderFunctions {

  lazy val ed: IEditable[A] = implicitly

  property ("signal") = Prop.forAll { a: A ⇒ 
    val exp = a.success

    def res = for {
      comp   ← ed component (itemToPair(a), false)
      sig    ← ed signalIn comp runIO ()
      found  ← sig._2.now
    } yield (found ≟ exp) :| ("Exp: %s, found: %s" format (exp, found))

    propIo (res)
  }
}

// vim: set ts=2 sw=2 et:
