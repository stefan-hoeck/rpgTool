package efa.rpg

import efa.core.{Folder, Localization, Service, UniqueId, Named}
import efa.rpg.rules.spi.RulesLocal
import scalaz._, Scalaz._
import shapeless.{Lens ⇒ _, _}

package object rules {
  lazy val loc = Service.unique[RulesLocal]

  type LocFolder = Folder[Localization,String]

  type RulesFolder = Folder[RuleSetting,(String, Int)]

  implicit lazy val RulesFolderUniqueId = 
    new UniqueId[RulesFolder,Int]
    with Named[RulesFolder] 
    with ActiveL[RulesFolder] {
      def id (f: RulesFolder) = f.label._2
      def name (f: RulesFolder) = f.label._1
      def active (f: RulesFolder) = f find (_.active) nonEmpty
      def activate (f: RulesFolder) = {
        val newA = ! active (f)
        val newF = f map (RuleSetting.activeL set (_, newA))

        Lens.lensId[RulesFolder] updateFolder (f, newF)
      }
    }

  //def folders[A,B,H<:HList](p: A :: H)(implicit P: IsFolderPath[A,B,H])
  //  : List[RulesFolder] = P folders p
  //  
  //trait IsFolderPath[A,B,H<:HList] {
  //  def head(l: A :: H): A = l.head
  //  def last(l: A :: H): B
  //  def folders(l: A :: H): List[RulesFolder]
  //}

  //object IsFolderPath {
  //  implicit def RootIsFolderPath[A,B] = new IsFolderPath[A,B,RulesFolder :: B :: HNil] {
  //    def last(l: A :: RulesFolder :: B :: HNil) = l.last
  //    def folders(l: A :: RulesFolder :: B :: HNil) = List(l.tail.head)
  //  }

  //  implicit def UpperIsFolderPath[A,B,H<:HList](implicit W: IsFolderPath[A,B,H]) =
  //    new IsFolderPath[A,B,RulesFolder :: H] {
  //      def last(l: A :: RulesFolder :: H) = W.last(l.head :: l.tail.tail)
  //      def folders(l: A :: RulesFolder :: H) = l.tail.head :: W.folders(l.head :: l.tail.tail)
  //    }
  //  
  //  val f1 = Folder[RuleSetting,(String,Int)](Stream.empty, Stream.empty, ("", 0))
  //  val f2 = f1

  //  folders("blub" :: f1 :: "blab" :: HNil)
  //  folders("blub" :: f2 :: f1 :: "blab" :: HNil)
  //  folders("blub" :: f1 :: f2 :: f1 :: "blab" :: HNil)
  //}

}

// vim: set ts=2 sw=2 et:
