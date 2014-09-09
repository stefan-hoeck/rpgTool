package efa.rpg.core

import efa.core.{DisRes, ValRes, Read, Validators, Default, ToXml}
import efa.core.std.anyVal._
import scalaz.{Applicative, Equal, Show}
import scalaz.std.anyVal._
import scalaz.std.tuple._
import scalaz.syntax.std.boolean._
import scalaz.syntax.validation._
import shapeless.contrib.scalaz.lift._

final class DieRoller private(val count: Int, val die: Int, val plus: Int) {
  override def toString = {
    def countF = (count > 1) ? count.toString | ""
    def plusF = plus match {
      case 0 ⇒ ""
      case x if (x > 0) ⇒ " + " + x
      case x ⇒ " - " + x.abs
    }
    s"$countF${loc.dieString}$die$plusF"
  } 

  //def roll = {
  //  def roll1(cnt: Int, sum: Int): Int = cnt match {
  //    case 0 ⇒ sum
  //    case x ⇒ roll1(cnt - 1, sum + rnd.nextInt(die) + 1)
  //  }
  //  roll1(count, 0) + plus
  //}
}

object DieRoller {
  private def create(c: Int, d: Int, p: Int) = new DieRoller(c,d,p)

  private def liftA[F[_]:Applicative] = Applicative[F].liftA(create _)

  def apply(count: Int, die: Int, plus: Int): DisRes[DieRoller] =
    liftA[DisRes].apply(countVal(count), dieVal(die), plusVal(plus))
  
  //Validators
  private val intVal = Read[Int].validator
  val (minCount, maxCount) = (1, 1000)
  private val countVal = Validators.interval(minCount, maxCount)
  private def dieVal = countVal
  private val plusVal = Validators.interval(-maxCount, maxCount)
  private val countValR = intVal >=> countVal
  private val dieValR = intVal >=> dieVal
  private val plusValR = intVal >=> plusVal

  def fromString(s: String) = {
    def dieString = loc.dieString
    val dr = ("""(\d*)""" + dieString + """(\d+)\s*([\+\-]\s*\d+)?""").r
    val plus = """\+\s*(\d+)""".r
    val minus = """\-\s*(\d+)""".r

    def readPlus (s: String): ValRes[Int] = s match {
      case null ⇒ 0.success
      case plus(x) ⇒ plusValR run x validation
      case minus(x) ⇒ plusValR run x map (-_) validation
    }

    def readCount (s: String): ValRes[Int] = s match {
      case "" ⇒ 1.success
      case a ⇒ countValR run a validation
    }

    def readDie (s: String): ValRes[Int] = dieValR run s validation
 
    s match {
      case dr(a, b, c) ⇒ liftA[ValRes].apply(readCount(a), readDie(b), readPlus(c))
      case _ ⇒  loc.unknownDieRollerFormat.failureNel
    }
  }

  implicit val defaultInst: Default[DieRoller] =
    Default default (new DieRoller(1,6,0))

  implicit val equalInst: Equal[DieRoller] =
    Equal.equalBy(x ⇒ (x.count, x.die, x.plus))

  implicit val showInst: Show[DieRoller] = Show shows (_.toString)

  implicit val readInst: Read[DieRoller] = Read readV fromString

  implicit val toXmlInst: ToXml[DieRoller] = ToXml.readShow

  import org.scalacheck._
  import scalaz.scalacheck.ScalaCheckBinding._

  implicit val DieRollerArbitrary: Arbitrary[DieRoller] = {
    val countGen = Gen.choose(minCount, maxCount)
    val dieGen = countGen
    val plusGen = Gen.choose(-maxCount, maxCount)
    Arbitrary(liftA[Gen].apply(countGen, dieGen, plusGen))
  }
}

// vim: set ts=2 sw=2 et:
