package efa.rpg.items.controller

import efa.core.ToXml
import efa.rpg.core.RpgItem
import scalaz.Equal

/** The factory methods provided in this trait are not referentially
  * transparent. This trait should therefore only be used to extend
  * singleton objects that define lazy vals using the functions provided
  * in this trait.
  */
trait Factory {
  private lazy val clazz = getClass

  def singleton[A:RpgItem:Equal:ToXml:Manifest:IEditable](
    p: (String, String)): ItemController[A] =
    ItemController.default[A](p._1, p._2, clazz).unsafePerformIO()
}

// vim: set ts=2 sw=2 et:
