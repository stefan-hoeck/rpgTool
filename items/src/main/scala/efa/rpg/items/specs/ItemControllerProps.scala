package efa.rpg.items.specs

import efa.rpg.items.controller.ItemController
import org.scalacheck._, Prop._
import scalaz._, Scalaz._, effect.IO

trait ItemControllerProps extends dire.util.TestFunctions {

  def testLoading[A](ic: ItemController[A]): Prop =
    runN(ic.testIn, 1) match {
      case Nil ⇒ false :| "Returned empty list"
      case m :: Nil ⇒ m.nonEmpty :| "Returned empty map"
      case x ⇒ false :| "Returned more than one map"
    }
}

// vim: set ts=2 sw=2 et:
