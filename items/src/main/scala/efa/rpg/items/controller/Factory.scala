package efa.rpg.items.controller

import efa.core.{ToXml, Unerased}
import efa.rpg.core.RpgItem
import scalaz.Equal

/** The factory methods provided in this trait are not referentially
  * transparent. This trait should therefore only be used to extend
  * singleton objects that define lazy vals using the functions provided
  * in this trait.
  */
trait Factory {
  private lazy val clazz = getClass

  /** Set to true in unit tests
    *
    * Setting this value to true prevents the starting of
    * the reactive system of an ItemController. This is
    * important in unit tests since there is no way of
    * shutting down the reactive system once it is running
    * (it shuts itself down automatically when running on
    * the Netbeans platform and the application is closed),
    * therefore the tests might run forever.
    */
  private[items] var isTest = new java.util.concurrent.atomic.AtomicBoolean

  private[items] def setTest(b: Boolean) { isTest.set(b) }

  def singleton[A:RpgItem:Equal:ToXml:Unerased:IEditable](
    p: (String, String)): ItemController[A] =
    ItemController.default[A](p._1, p._2, clazz, isTest.get).unsafePerformIO()
}

// vim: set ts=2 sw=2 et:
