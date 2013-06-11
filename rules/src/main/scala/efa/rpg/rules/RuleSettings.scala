package efa.rpg.rules

import dire.{SIn, DataSink, SF}, dire.control.Var
import efa.core.Folder
import efa.core.std.lookup.provide
import efa.rpg.rules.spi.RulesProvider
import scalaz._, Scalaz._, effect.IO, Dual._

object RuleSettings {
  type LocFolders = List[LocFolder]

  private lazy val rfVar: Var[RulesFolder] =
    load >>= Var.newVar unsafePerformIO

  lazy val in: SIn[RulesFolder] = rfVar.in

  lazy val actives: SIn[Set[String]] = in map activeIds

  lazy val sink: DataSink[RulesFolder] = rfVar.sink

  def mod(f: RulesFolder ⇒ RulesFolder): IO[Unit] = rfVar mod f

  def endoSF[A,B](rs: List[Rule[B]]): SF[A,Endo[B]] =
    actives map (ns ⇒ rs foldMap (_ endo ns)) sf

  private[this] def load: IO[RulesFolder] = {
    def merge (fs: Seq[LocFolder]): Stream[LocFolder] = {
      def run (fs: LocFolders): LocFolders = fs match {
        case a :: b :: xs ⇒ {
          def mData = (a.data ++ b.data) sortBy (_.locName)
          def mFolders = merge (a.folders ++ b.folders)
          def merged = Folder(mData, mFolders, a.label)

          if (a.label ≟ b.label) run (merged :: xs) else a :: run (b :: xs)
        }
        case xs ⇒ xs
      }

      run (fs.toList sortBy (_.label)) toStream
    }

    def root (fs: LocFolders): RulesFolder = {
      def unInd = Folder(Stream.empty, merge(fs), loc.rules)

      (Folder indexFolders unInd _2) map RuleSetting.fromLoc
    }

    provide[LocFolder,RulesProvider] map root
  }

  private [rules] def activeIds(f: RulesFolder): Set[String] =
    f.allData filter (_.active) map (_.loc.name) toSet
}

// vim: set ts=2 sw=2 et:
