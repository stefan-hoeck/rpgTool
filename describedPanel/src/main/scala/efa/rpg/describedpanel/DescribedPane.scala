package efa.rpg.describedpanel

import dire.SIn
import dire.swing.swingSink
import efa.nb.{PureLookup, lookup, NbSystem}, lookup._
import efa.nb.tc.AsTc
import efa.rpg.core.{HtmlDesc,HtmlEditorPane}
import javax.swing.JTabbedPane
import org.openide.util.{Utilities, Lookup}
import scalaz._, Scalaz._, effect.IO

final class DescribedPane private (
  val pl: PureLookup,
  src: SIn[List[HtmlDesc]]
) extends JTabbedPane {

  def sin = src to swingSink(adjust)

  private def writePages(ps: List[Page]): IO[Unit] = IO {
    removeAll()
    ps foreach { case (s, sp) ⇒ addTab(s, sp) }
  }

  private def adjust(desc: List[HtmlDesc]): IO[Unit] = {
    def pageFor(d: HtmlDesc): IO[Page] =
      HtmlEditorPane(d.html) map ((d.name, _))

    for {
      ps ← desc traverse pageFor
      _  ← writePages(ps)
      _  ← pl set desc
    } yield ()
  }
}

object DescribedPane {
  def apply(lkp: Lookup = Utilities.actionsGlobalContext) =
    PureLookup() map (new DescribedPane(_, lkp.results[HtmlDesc]))

  implicit val tc = new AsTc[DescribedPane] {
    def preferredId = "DescribedTc"
    val version = "1.1"
    override def lookup(p: DescribedPane) = p.pl.l
    override def name = loc.describedTcName
    override def tooltip = loc.describedTcHint
    override def peer(p: DescribedPane) = p
    override def create = DescribedPane()

    override def initialize(p: DescribedPane) =
      NbSystem forever p.sin flatMap _
  }
}

// vim: set ts=2 sw=2 et:
