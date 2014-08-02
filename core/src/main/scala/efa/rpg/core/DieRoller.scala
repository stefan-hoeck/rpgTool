package efa.rpg.core

import efa.core._, Efa._
import scala.util.Random
import scalaz._, Scalaz._

case class DieRoller(count: Int, die: Int, plus: Int) {
  import DieRoller._
  require(countVal(count).isRight)
  require(dieVal(die).isRight)
  require(plusVal(plus).isRight)
  
  //def roll = {
  //  def roll1(cnt: Int, sum: Int): Int = cnt match {
  //    case 0 ⇒ sum
  //    case x ⇒ roll1(cnt - 1, sum + rnd.nextInt(die) + 1)
  //  }
  //  roll1(count, 0) + plus
  //}
  
  override def toString = {
    def countF = (count > 1) ? count.toString | ""
    def plusF = plus match {
      case 0 ⇒ ""
      case x if (x > 0) ⇒ " + " + x
      case x ⇒ " - " + x.abs
    }
    "%s%s%d%s" format (countF, loc.dieString, die, plusF)
  } 

}

object DieRoller {
  lazy val default = DieRoller(1, 6, 0)
  
  //Validators
  private def intVal = Read[Int].validator
  lazy val (minCount, maxCount) = (1, 1000)
  lazy val countVal = Validators.interval(minCount, maxCount)
  lazy val dieVal = countVal
  lazy val plusVal = Validators.interval(-maxCount, maxCount)
  lazy val countValR = intVal >=> countVal
  lazy val dieValR = intVal >=> dieVal
  lazy val plusValR = intVal >=> plusVal
  
  lazy val fromString = {
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
 
    (s: String) ⇒ s match {
      case dr(a, b, c) ⇒ 
        ^^(readCount(a), readDie(b), readPlus(c))(DieRoller.apply)
      case _ ⇒  loc.unknownDieRollerFormat.failureNel
    }
  }

  implicit lazy val DieRollerDefault = Default default default

  implicit val DieRollerEqual = deriveEqual[DieRoller]

  implicit val DieRollerShow: Show[DieRoller] = Show shows (_.toString)

  implicit val DieRollerRead: Read[DieRoller] = Read readV fromString

  implicit val DieRollerToXml: ToXml[DieRoller] = ToXml.readShow

  import org.scalacheck._
  import scalaz.scalacheck.ScalaCheckBinding._

  implicit val DieRollerArbitrary: Arbitrary[DieRoller] = {
    val countGen = Gen.choose(minCount, maxCount)
    val dieGen = countGen
    val plusGen = Gen.choose(-maxCount, maxCount)
    Arbitrary(^^(countGen, dieGen, plusGen)(DieRoller.apply))
  }

  private lazy val rnd = new Random
}

// vim: set ts=2 sw=2 et:
