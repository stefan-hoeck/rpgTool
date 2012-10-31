package efa.rpg.core

import efa.core.ToXml
import org.scalacheck.{Arbitrary, Gen}, Arbitrary.arbitrary
import scala.xml.Node

trait Maps {

  def mapArbitrary[A:Arbitrary,K] (f: A ⇒ K): Arbitrary[Map[K,A]] =
    Arbitrary (Gen listOf arbitrary[A] map (toMap(_, f)))

  def mapToXml[A:ToXml,K](lbl: String, f: A ⇒ K): ToXml[Map[K,A]] =
    new ToXml[Map[K,A]] {
      val asToXml = ToXml.seqToXml[A](lbl)

      def fromXml (ns: Seq[Node]) = asToXml fromXml ns map (toMap(_, f))

      def toXml (map: Map[K,A]) = asToXml toXml (map.toSeq map (_._2))
    }

  private def toMap[A,K](as: Seq[A], f: A ⇒ K): Map[K,A] =
    as map f zip as toMap
}

object Maps extends Maps

// vim: set ts=2 sw=2 et:
