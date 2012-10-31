package efa.rpg.items.controller

import efa.core.ToXml
import efa.io.IOCached
import efa.react.SIn
import efa.rpg.core.{RpgItem, DB}
import scalaz._, Scalaz._, effect.IO

trait ControllerFactory {
  val clazz: Class[_] = getClass

  type CachedSIn[A] = IOCached[SIn[A]]

  protected def cached[A:RpgItem:Equal:ToXml:Manifest:IEditable] (
    names: Pair[String,String]
  ): IOCached[ItemController[A]] =
    IOCached(ItemController.default(names._1, names._2, clazz))

  protected def signal[A](ic: IOCached[ItemController[A]])
    : CachedSIn[DB[A]] = ic map (_.dbIn)

  implicit val CachedSInApplicative: Applicative[CachedSIn] =
    Applicative[IOCached].compose[SIn]

}

// vim: set ts=2 sw=2 et:
