package efa.rpg.core

import efa.core.ToXml
import efa.data.IntId
import org.scalacheck.{Arbitrary, Gen}, Arbitrary.arbitrary
import scala.xml.Node
import scalaz._, Scalaz._, scalacheck.ScalaCheckBinding._

trait DBs {
  def db[A]: DB[A] = Map.empty

  implicit def DBArbitrary[A:Arbitrary:IntId]: Arbitrary[DB[A]] =
    Maps mapArbitrary IntId[A].id

  def dbToXml[A:ToXml:IntId](lbl: String): ToXml[DB[A]] =
    Maps mapToXml (lbl, IntId[A].id)

  implicit val DBTraverse = new Traverse[DB] {
    def traverseImpl[F[_] : Applicative, A, B](t: DB[A])(f: A => F[B])
      : F[DB[B]] = {
        def fPair (p: (Int,A)): F[(Int,B)] = f(p._2) strengthL p._1

        t.toList traverse fPair map (_.toMap)
      }
  }
}

object DBs extends DBs

// vim: set ts=2 sw=2 et:
