package efa.rpg.items.specs

import efa.core.ValRes
import efa.core.Default.!!!
import efa.nb.dialog.DialogEditable
import efa.rpg.core.RpgItem
import efa.rpg.items.{ItemPair, IState}
import efa.rpg.items.controller.IEditable
import org.scalacheck._, Prop._
import scalaz._, Scalaz._, effect.IO

abstract class EditableProps[A:RpgItem:Arbitrary:Equal:IEditable](name: String)
  extends Properties(name) with dire.util.TestFunctions {

  property("editable") = forAll { a: A ⇒ 
    val ip = (a, !!![IState[A]])
    val exp = List[ValRes[A]](a.success)

    val res = for {
      p  ← implicitly[DialogEditable[ItemPair[A],A]] info (ip, true)
      as = runN(p._2 syncTo { IO putStrLn _.toString }, 1)
    } yield (as ≟ exp) :| s"Exp: $a; but found $as"

    res.unsafePerformIO
  }
}

// vim: set ts=2 sw=2 et:
