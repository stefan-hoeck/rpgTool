package efa.rpg.describedpanel

import dire.{SF, SIn}
import dire.swing.swingSink
import efa.nb.PureLookup, efa.nb.lookup._
import efa.nb.tc.{ReactiveTc, TcProvider}
import efa.rpg.core.{HtmlDesc,HtmlEditorPane}
import efa.rpg.preferences.Preferences.mainLogger
import java.awt.BorderLayout
import org.openide.util.{Utilities, Lookup}
import scala.collection.JavaConversions._
import scala.swing.{TabbedPane, ScrollPane}, TabbedPane.Page
import scalaz._, Scalaz._, effect.{IO, IORef}

class DescribedTc private(ip: DescribedTc.InnerPane)
  extends ReactiveTc(ip.sin) {
  def this() = this(DescribedTc.loadParams.unsafePerformIO)

  setName (loc.describedTcName)
  setToolTipText (loc.describedTcHint)
  setLayout(new BorderLayout)
  add(ip.peer, BorderLayout.CENTER)
  associateLookup(ip.pl.l)

  override protected def preferredID = DescribedTcProvider.preferredId
  override protected val version = "1.1"
}

object DescribedTc {
  def create: IO[DescribedTc] = for {
    ip  ← createInner(Utilities.actionsGlobalContext)
    _   ← mainLogger debug "Creating DescribedTc"
  } yield new DescribedTc(ip)

  private[describedpanel] def createInner(lkp: Lookup): IO[InnerPane] = for {
    ref   ← IO newIORef Map.empty[HtmlDesc,Page]
    pl    ← PureLookup.apply
  } yield new InnerPane(ref, pl, lkp.results[HtmlDesc])

  private def loadParams = for {
    ip ← createInner(Utilities.actionsGlobalContext)
    _  ← mainLogger debug "Deserializing DescribedTc"
  } yield ip

  private[describedpanel] class InnerPane(
    private[describedpanel] val pageMap: IORef[Map[HtmlDesc,Page]],
    private[describedpanel] val pl: PureLookup,
    src: SIn[List[HtmlDesc]]
  ) extends TabbedPane {

    def sin = src to swingSink(adjust)

    private val readPages: IO[List[Page]] = IO(pages.toList)

    private def writePages(ps: List[Page]): IO[Unit] = IO {
      pages.clear()
      pages.insertAll(0, ps)
    }

    private def modPages(f: List[Page] ⇒ List[Page]): IO[Unit] =
      readPages map f flatMap writePages

    private def adjust(desc: List[HtmlDesc]): IO[Unit] = {
      def pageFor(d: HtmlDesc): IO[Page] = for {
        htmlP ← HtmlEditorPane(d.html)
        _     = htmlP.editable = false
      } yield new Page(d.name, htmlP)

      def removeGoners (gs: Set[HtmlDesc]): IO[Unit] = for {
        pm     ← pageMap.read
        _      ← modPages(_ filterNot (gs map pm))
        _      ← pageMap mod (_ -- gs)
        _      ← pl -- gs.toList
      } yield ()
      
      def addNew(news: List[HtmlDesc]): IO[Unit] = for {
        newPages ← news traverse pageFor
        _        ← modPages(_ ++ newPages)
        _        ← pageMap mod (_ ++ (news zip newPages))
        _        ← pl ++ news
      } yield ()

      for {
        pm     ← pageMap.read
        old    = pm.keySet & desc.toSet //nothing new
        _      ← removeGoners(pm.keySet -- old)
        _      ← addNew(desc filterNot old)
      } yield ()
    }
  }
}

object DescribedTcProvider extends TcProvider(DescribedTc.create, "DescribeTc")
