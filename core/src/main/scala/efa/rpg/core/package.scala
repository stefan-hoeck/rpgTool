package efa.rpg

import efa.core.Service
import efa.rpg.core.spi.RpgLocal
import scalaz._, Scalaz._

package object core {
  lazy val loc = Service.unique[RpgLocal]

//  type DB[+A] = Map[Int,A]

//  def prettyMods(
//    sum: Long,
//    ms: List[Modifier],
//    format: Long ⇒ String = (_: Long).toString
//  ): String = {
//    def head = "<b>%s: %s</b>" format (loc.total, format(sum))
//    def rest = ms map (m ⇒ "%s: %s" format (m.name, format(m.value)))
//
//    "<html>%s<br>%s</html>" format (head, rest mkString "<br>")
//  }
//
//  def prettyModsKey[A:HasModifiers](
//    k: ModifierKey, 
//    format: Long ⇒ String = (_: Long).toString
//  ): A ⇒ String = a ⇒ prettyMods(property(a,k), mods (a, k), format) 
//
//  def prettyModsKeyO[A:HasModifiers](
//    k: ModifierKey, 
//    format: Long ⇒ String = (_: Long).toString
//  ): A ⇒ Option[String] = prettyModsKey(k, format) andThen (_.some)
//
//  def mods[A:HasModifiers] (a: A, k: ModifierKey): List[Modifier] =
//    HasModifiers[A] modifiers a get k
//
//  def property[A:HasModifiers] (a: A, k: ModifierKey): Long =
//    HasModifiers[A] modifiers a property k
}

// vim: set ts=2 sw=2 et:
