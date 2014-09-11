package efa.rpg.core

import efa.core.{Default, Read, ValRes, ToXml}
import efa.core.syntax.string
import org.scalacheck.{Arbitrary, Gen}
import scalaz.{@>, Lens, Equal, Show}
import scalaz.std.anyVal._
import scalaz.std.list._
import scalaz.syntax.equal._
import scalaz.syntax.show._
import scalaz.syntax.traverse._
import scalaz.syntax.validation._

/** Newtype wrapper for Function1 specially designed to be
  * used with RpgEnums (small sets of unique values).
  *
  * When RpgEnums are used as keys (function input), we can
  * define instances of Equal, Arbitrary, Read, Show, and ToXml
  * for DMaps.
  */
// @TODO Add instances for Monad etc. See scalaz.Function1 instances
final class DMap[A,B] private (f: A ⇒ B) extends Function1[A,B] {
  def apply(a: A): B = f(a)

  def subst(a: A, b: B)(implicit A:Equal[A]): DMap[A,B] = new DMap( aa ⇒ 
    if (aa ≟ a) b else f(aa)
  )

  def map[C](g: B ⇒ C): DMap[A,C] = new DMap(f andThen g)

  def flatMapF[C](g: B ⇒ A ⇒ C): DMap[A,C] = new DMap(a ⇒ g(f(a))(a))

  def flatMap[C](g: B ⇒ DMap[A,C]): DMap[A,C] = flatMapF(g)
}

object DMap extends DMapInstances0 {
  def apply[A,B](b: ⇒ B)(ps: (A,B)*): DMap[A,B] = {
    lazy val m = ps.toMap

    new DMap[A,B](a ⇒ m getOrElse (a, b))
  }

  // bs must be of the same length as RpgEnum[A].values, otherwise
  // this function is not total
  private[core] def fromList[A:RpgEnum,B](bs: List[B]): DMap[A,B] =
    new DMap[A,B](RpgEnum[A].values zip bs toMap)

  def single[A,B](b: ⇒ B): DMap[A,B] = new DMap(_ ⇒ b)

  def at[A:Equal,B](a: A): DMap[A,B] @> B =
    Lens.lensu((dmap,b) ⇒ dmap subst (a, b), _(a))

  def read[A:RpgEnum,B:Read](s: String): ValRes[DMap[A,B]] = {
    lazy val l = RpgEnum[A].values.length

    s split delim match {
      case ss if ss.length ≟ l ⇒ ss.toList traverse (_.read[B]) map fromList[A,B]
      case _                   ⇒ loc.invalidLength(l).failureNel
    }
  }
  
  implicit class DMapLenses[X,A,B](val l: X @> DMap[A,B]) extends AnyVal {
    def at(a: A)(implicit A: Equal[A]): X @> B = l >=> DMap.at(a)
  }
}

trait DMapInstances0 {
  private[core] def delim = "/"

  implicit def defaultInst[A,B:Default]: Default[DMap[A,B]] =
    Default default DMap.single[A,B](Default[B].default)

  implicit def equalInst[A:RpgEnum,B:Equal]: Equal[DMap[A,B]] =
    Equal.equalBy(RpgEnum[A].values map _)

  implicit def showInst[A:RpgEnum,B:Show]: Show[DMap[A,B]] = Show shows { dm ⇒
    RpgEnum[A].values map (a ⇒ dm(a).shows) mkString delim
  }

  implicit def readInst[A:RpgEnum,B:Read]: Read[DMap[A,B]] =
    Read readV DMap.read[A,B]

  implicit def toXmlInst[A:RpgEnum,B:Read:Show]: ToXml[DMap[A,B]] =
    ToXml.readShow

  import scalaz.scalacheck.ScalaCheckBinding._

  implicit def arbInst[A:RpgEnum,B:Arbitrary]: Arbitrary[DMap[A,B]] = {
    def gen(a: A) = Arbitrary.arbitrary[B]

    Arbitrary(RpgEnum[A].values traverse gen map DMap.fromList[A,B])
  }
}

//// vim: set ts=2 sw=2 et:
