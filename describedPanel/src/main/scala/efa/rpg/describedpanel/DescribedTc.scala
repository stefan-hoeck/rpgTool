package efa.rpg.describedpanel

import dire.SIn
import dire.swing.swingSink
import efa.nb.PureLookup, efa.nb.lookup._
import efa.nb.tc.{ReactiveTc, TcProvider}
import efa.rpg.core.{HtmlDesc,HtmlEditorPane}
import efa.rpg.preferences.Preferences.mainLogger
import java.awt.BorderLayout
import javax.swing.{JTabbedPane, JScrollPane}
import org.openide.util.{Utilities, Lookup}
import scala.collection.JavaConversions._
import scalaz._, Scalaz._, effect.{IO, IORef}

class DescribedTc private(ip: DescribedTc.InnerPane)
  extends ReactiveTc(ip.sin) {
  def this() = this(DescribedTc.loadParams.unsafePerformIO)

  setName (loc.describedTcName)
  setToolTipText (loc.describedTcHint)
  setLayout(new BorderLayout)
  add(ip, BorderLayout.CENTER)
  associateLookup(ip.pl.l)

  override protected def preferredID = DescribedTcProvider.preferredId
  override protected val version = "1.1"
}

object DescribedTc {
  type Page = (String, JScrollPane)

  def create: IO[DescribedTc] = for {
    ip  ← createInner(Utilities.actionsGlobalContext)
    _   ← mainLogger debug "Creating DescribedTc"
  } yield new DescribedTc(ip)

  private[describedpanel] def createInner(lkp: Lookup): IO[InnerPane] = for {
    pl    ← PureLookup.apply
  } yield new InnerPane(pl, lkp.results[HtmlDesc])

  private def loadParams = for {
    ip ← createInner(Utilities.actionsGlobalContext)
    _  ← mainLogger debug "Deserializing DescribedTc"
  } yield ip

  private[describedpanel] class InnerPane(
    private[describedpanel] val pl: PureLookup,
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
        _  ← pl.clear
        _  ← pl ++ desc
      } yield ()
    }
  }
}

object DescribedTcProvider extends TcProvider(DescribedTc.create, "DescribeTc")
