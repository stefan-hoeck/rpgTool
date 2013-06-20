package efa.rpg.being.loaders

import dire.Out
import efa.nb.PureLookup
import efa.nb.controller._
import efa.nb.node.NbNode
import org.openide.filesystems.FileObject
import org.openide.loaders.{MultiFileLoader, MultiDataObject, DataNode, OpenSupport}
import org.openide.nodes.Children
import org.openide.util.Lookup
import org.openide.util.lookup.ProxyLookup
import scalaz._, Scalaz._, effect.IO

class BeingDo private[being] (
  private[being] val fo: FileObject,
  loader: MultiFileLoader,
  private[being] val pl: PureLookup
) extends MultiDataObject(fo, loader) {
  private lazy val lkp = new ProxyLookup(getCookieSet.getLookup, pl.l)
  
  override protected lazy val createNodeDelegate =
    new DataNode(this, Children.LEAF, getLookup){setDisplayName(fo.getName)}
  override def getLookup = lkp

  private val modified: Out[Boolean] = b ⇒ IO(setModified(b))

  private[being] def saveInfo = SavableInfo(
    this, fo.getName,
    _ fold (pl.remove(_) >> modified(false), pl.add(_) >> modified(true))
  )
}
  
object BeingDo {
  //Upon creation we need to do the following:
  //Create a SaveEvent stream transformer
  //  upon registering a Save set DataObject to modified and add Saver to Lookup: DONE
  //  upon unregistering a Save set DataObject to not modified and remove Saver: DONE
  //
  //Create an OpenSupport that
  //  Unpon opening does the following
  //    Create a Multiview from a bunch of panels: DONE
  //    Create a Signal transformer from these panels including Undo/Redo: DONE
  //    Run the signal transformer by loading the Being from File: DONE
  //    Fires to the save event stream when changing: DONE
  //    Adds being to lookup when changing: DONE
  //    Renames Multiview when changing: DONE
  //    
  //  Upon closing does the following
  //    Remove all traces from Signal event and Panels: DONE
}

// vim: set ts=2 sw=2 et:
