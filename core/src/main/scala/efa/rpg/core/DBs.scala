package efa.rpg.core

import efa.core.ToXml
import org.scalacheck.{Arbitrary, Gen}, Arbitrary.arbitrary
import scala.xml.Node
import scalaz._, Scalaz._, scalacheck.ScalaCheckBinding._

trait DBs {
  def db[A]: DB[A] = Map.empty

  implicit def DBequal[A:Equal] = new Equal[DB[A]] {
    def equal(a1: DB[A], a2: DB[A]): Boolean = {
      if (equalIsNatural) a1 == a2
      else (a1.keySet == a1.keySet) && {
        a1.forall { case (k, a) ⇒ Equal[A].equal(a, a2(k)) }
      }
    }
    override val equalIsNatural: Boolean = Equal[A].equalIsNatural
  }

  implicit def DBArbitrary[A:Arbitrary:WithId]: Arbitrary[DB[A]] =
    Maps mapArbitrary WithId[A].id

  def dbToXml[A:ToXml:WithId](lbl: String): ToXml[DB[A]] =
    Maps mapToXml (lbl, WithId[A].id)

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
