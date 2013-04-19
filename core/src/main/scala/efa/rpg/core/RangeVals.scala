package efa.rpg.core

import efa.core._, Efa._
import scala.xml.Node
import org.scalacheck.Gen

trait RangeVals {
  import RangeVals.{GenInfo, FullInfo, XmlInfo}

  def genInfo (min: Int, max: Int) =
    GenInfo(min, max, Validators interval (min, max), Gen choose (min, max))

  def genInfo (min: Long, max: Long) =
    GenInfo(min, max, Validators interval (min, max), Gen choose (min, max))

  def fullInfo (min: Int, max: Int, lbl: String) = {
    val v = Validators interval (min, max)

    FullInfo(GenInfo(min, max, v, Gen choose (min, max)), xmlInfo(v, lbl))
  }

  def fullInfo (min: Long, max: Long, lbl: String) = {
    val v = Validators interval (min, max)

    FullInfo(GenInfo(min, max, v, Gen choose (min, max)), xmlInfo(v, lbl))
  }

  def xmlInfo[A:ToXml](v: Validator[A,A], lbl: String) =
    XmlInfo[A](_.readTagD[A](lbl) flatMap v.run validation, lbl xml _)
}

object RangeVals extends RangeVals {
  final case class GenInfo[A] (
    min: A, max: A, validate: Validator[A, A], gen: Gen[A]
  ) {
    def read (s: String) (implicit R: Read[A]): ValRes[A] =
      R.validator andThen validate run s validation
  }

  final case class XmlInfo[A] (
    read: Seq[Node] ⇒ ValRes[A], write: A ⇒ Seq[Node]
  )

  final case class FullInfo[A] (genInfo: GenInfo[A], xml: XmlInfo[A]) {
    def min = genInfo.min
    def max = genInfo.max
    def validate = genInfo.validate
    def gen = genInfo.gen
    def read = xml.read
    def write = xml.write
  }
}

// vim: set ts=2 sw=2 et:
