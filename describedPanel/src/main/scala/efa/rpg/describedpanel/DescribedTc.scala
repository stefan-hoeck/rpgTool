package efa.rpg.describedpanel

import efa.nb.{PureLookup, LookupResultWrapper}
import efa.nb.tc.{EfaTc, TcProvider}
import efa.react.eTrans
import efa.rpg.core.{HtmlDesc,HtmlEditorPane}
import efa.rpg.preferences.Preferences.mainLogger
import java.awt.BorderLayout
import org.openide.util.{Utilities, Lookup}
import scala.collection.JavaConversions._
import scala.swing.{TabbedPane, ScrollPane}, TabbedPane.Page
import scalaz._, Scalaz._, effect.{IO, IORef}

class DescribedTc private (ip: DescribedTc.InnerPane) extends EfaTc {
  def this() = this (DescribedTc.unsafeParams)

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
    res ← IO(new DescribedTc(ip))
  } yield res

  private[describedpanel] def createInner(lkp: Lookup): IO[InnerPane] = for {
    ref   ← IO.newIORef[Map[HtmlDesc,Page]](Map.empty)
    pl    ← PureLookup.apply
    res   ← LookupResultWrapper[HtmlDesc](lkp)
    inner ← IO (new InnerPane(ref, pl, res))
    _     ← eTrans in res to inner.adjust go
  } yield inner

  private def unsafeParams: InnerPane = (for {
    ip ← createInner(Utilities.actionsGlobalContext)
    _  ← mainLogger debug "Deserializing DescribedTc"
  } yield ip).unsafePerformIO

  private[describedpanel] class InnerPane(
    private[describedpanel] val pageMap: IORef[Map[HtmlDesc,Page]],
    val pl: PureLookup,
    private val es: LookupResultWrapper[HtmlDesc]
  ) extends TabbedPane {

    private val readPages: IO[List[Page]] = IO(pages.toList)

    private def writePages (ps: List[Page]): IO[Unit] = IO{
      pages.clear()
      pages.insertAll(0, ps)
    }

    private def modPages (f: List[Page] ⇒ List[Page]): IO[Unit] =
      (readPages ∘ f) >>= writePages

    private[DescribedTc] def adjust(desc: List[HtmlDesc]): IO[Unit] = {
      def pageFor(d: HtmlDesc): IO[Page] = for {
        htmlP ← HtmlEditorPane (d.html)
        _     = htmlP.editable = false
        page  ← IO (new Page(d.name, htmlP))
      } yield page

      def removeGoners (gs: Set[HtmlDesc]): IO[Unit] = for {
        pm     ← pageMap.read
        _      ← modPages (_ filterNot (gs map pm))
        _      ← pageMap mod (_ -- gs)
        _      ← pl -- gs.toList
      } yield ()
      
      def addNew (news: List[HtmlDesc]): IO[Unit] = for {
        newPages ← news traverse pageFor
        _        ← modPages (_ ++ newPages)
        _        ← pageMap mod (_ ++ (news zip newPages))
        _        ← pl ++ news
      } yield ()

      for {
        pm     ← pageMap.read
        old    = pm.keySet & desc.toSet //nothing new
        _      ← removeGoners(pm.keySet -- old)
        _      ← addNew (desc filterNot old)
      } yield ()
    }
  }
}

object DescribedTcProvider extends TcProvider[DescribedTc] (DescribedTc.create) {
  override protected[describedpanel] val preferredId = "DescribedTc"
}
