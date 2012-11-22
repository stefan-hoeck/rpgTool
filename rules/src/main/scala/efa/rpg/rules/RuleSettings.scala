package efa.rpg.rules

import efa.core.Folder
import efa.core.std.lookup.provide
import efa.io.IOCached
import efa.react.{Var, SIn, Signal, sTrans, SST}
import efa.rpg.rules.spi.RulesProvider
import scalaz._, Scalaz._, effect.IO, Dual._

object RuleSettings {
  type LocFolders = List[LocFolder]

  lazy val in: SIn[RulesFolder] = sTrans inIO rfVar.get

  lazy val actives: SIn[Set[String]] = {
    val sig = IOCached((in map activeIds go) map (_._2))

    sTrans inIO sig.get
  }

  def endoSST[A,B](rs: List[Rule[B]]): SST[A,Endo[B]] =
    sTrans(_ ⇒ actives map (ns ⇒ rs foldMap (_ endo ns)) run ())

  def out (f: RulesFolder): IO[Unit] = rfVar.get >>= (_ put f)

  def mod (f: RulesFolder ⇒ RulesFolder): IO[Unit] = rfVar.get >>= (_ mod f)

  private lazy val rfVar: IOCached[Var[RulesFolder]] =
    IOCached(load >>= (Signal newVar _))

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

  private [rules] def activeIds (f: RulesFolder): Set[String] =
    f.allData filter (_.active) map (_.loc.name) toSet
}

// vim: set ts=2 sw=2 et:
