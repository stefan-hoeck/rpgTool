package efa.rpg.core

import efa.core.{UniqueIdL, NamedL, Id, Name, Desc}
import efa.core.syntax.lens
import scalaz.@>

trait RpgItem[A]
   extends Described[A]
   with UniqueIdL[A,Id]
   with NamedL[A] {

  def dataL: A @> ItemData

  lazy val nameL: A @> Name = dataL >> 'name

  lazy val idL: A @> Id = dataL >> 'id

  lazy val descL: A @> Desc = dataL >> 'desc

  def desc(a: A) = descL get a
}

object RpgItem {
  @inline def apply[A:RpgItem]: RpgItem[A] = implicitly

  def inst[A](l: A @> ItemData,
              short: A ⇒ Desc,
              full: A ⇒ Desc): RpgItem[A] = new RpgItem[A] {
    def dataL = l
    def shortDesc(a: A) = short(a)
    def fullDesc(a: A) = full(a)
  }
}

///**
// * Helper functions and implicits. Best used for companion
// * object.
// */
//  def shortDesc (a: A): String
//  def fullDesc (a: A): String = titleBody (a.name, a.desc)
//  protected def tagShortDesc (a: A, tags: Tag*): String =
//    namePlusTags (a.name, tags: _*)
//
// vim: set ts=2 sw=2 et:
