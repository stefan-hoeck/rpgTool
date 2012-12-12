package efa.rpg.being

import efa.core._, Efa._
import efa.io.{LoggerIO, IOCached}
import efa.nb.{UndoEdit, PureLookup}
import efa.nb.controller.{StateTransFunctions, Saver, SaveEvent}
import efa.nb.tc.PersistentComponent
import efa.react._
import efa.rpg.core.Described
import efa.rpg.being.loaders.BeingDo
import efa.rpg.preferences.Preferences
import efa.rpg.rules.{Rule, RuleSettings}
import java.awt.Image
import javax.swing.{Action, JToolBar}
import org.openide.util.{Lookup, HelpCtx}
import org.openide.awt.UndoRedo
import org.openide.filesystems.FileObject
import org.openide.loaders._
import scala.swing.Panel
import scalaz._, Scalaz._, effect.{IO, IORef}, Dual._
import org.openide.filesystems.FileObject

/**
 * ext is the file extension and mime the mime type.
 *
 * ex: hero, text/x-hero
 */
abstract class BeingLoader (
  ext: String, mime: String, controller: Controller
) extends UniFileLoader("efa.rpg.being.loaders.BeingDo"){
  import BeingLoader._
  
  override protected def defaultDisplayName = loc.loaderName
  override protected def actionsContext = "Loaders/" + mime + "/Actions"
  
  setExtensions(getExtensions)

  override protected def createMultiObject(fo: FileObject) = {
    def create: IO[BeingDo] = for {
      pl  ← PureLookup.apply
      bd  ← IO(new BeingDo(fo, this, pl))
      ref ← IO newIORef none[BeingOpenParams]
      _   ← pl add new BeingOpenSupport(bd, controller, ref)
    } yield bd

    create.unsafePerformIO
  }
  
  override protected def createPrimaryEntry(
    obj: MultiDataObject, fo: FileObject) = new FileEntry(obj, fo)

  override protected def createSecondaryEntry(
    obj: MultiDataObject, fo: FileObject) = new FileEntry(obj, fo)

  override def getExtensions = getProperty(PropExtensions) match {
    case null ⇒ {
        val res = new ExtensionList
        res addExtension ext
        putProperty(PropExtensions, res, false)
        res
      }
    case x: ExtensionList ⇒ x
    case _ ⇒ throw new AssertionError
  }

  override def setExtensions(e: ExtensionList): Unit = 
    putProperty(PropExtensions, e, true)
}

object BeingLoader extends StateTransFunctions {
  private val PropExtensions = UniFileLoader.PROP_EXTENSIONS

  def default[B:Equal:Default:TaggedToXml,C:Manifest:Described,W] (
    info: IO[UIInfo[B,C]],
    world: SIn[W],
    calc: (B,W) ⇒ C,
    rules: IO[Rules[C]]
  ): Controller = controller[B,C,W](
    info,
    world,
    calc,
    rules,
    BeingSaver.xmlSaver(),
    Preferences.mainLogger,
    Preferences.beingsLogger
  )
  
  /**
   * About the type parameters:
   * D is the BeingData, loaded from and stored in files. It usually contains
   * no or hardly any logic but the raw data defining the being.
   * C is the fully calculated being containing all the additional info
   * from rules and stuff that were not present in the raw data.
   * W is the changeable state of the world that is relevant for
   * calculating beings of type C. 
   */
  def controller[B:Equal:Default,C:Manifest:Described,W] (
    info: IO[UIInfo[B,C]],
    world: SIn[W],
    calc: (B,W) ⇒ C,
    rules: IO[Rules[C]],
    saver: BeingSaver[B],
    mainLog: LoggerIO,
    valLog: LoggerIO
  ): Controller = (coh, bd) ⇒ {
    def ein (
      st: SET[C,ValSt[B]], ctc: CTC, uo: Out[UndoEdit], rs: List[Rule[C]]
    ): EIn[Unit] = {
      implicit def Logger: LoggerIO = mainLog

      def calcTot (b: B, w: W, rules: Endo[C]): C = rules(calc(b, w))
      def loggedSt = st to valLog.logValRes //log failures
      def undoSST: SST[B,B] = UndoEdit.undoSST[B](uo) //undo/redo
      def bwSST: SST[B,W] = sTrans(_ ⇒ world run ()) //world in
      def rulesSST: SST[B,Endo[C]] = RuleSettings endoSST rs
      def nameOut: Out[C] = c ⇒ IO(ctc.setDisplayName(Described[C] name c))
      def cOut: Out[C] = nameOut ⊹ bd.pl.set[C] //adjust name
      def bcSST: SST[B,C] =
        sTrans.id[B] ⊛ bwSST ⊛ rulesSST apply calcTot to cOut
      def bbSST: SST[B,B] = toSST(bcSST >=> loggedSt).distinct >=> undoSST //B to B plus undo
      def bSin: SIn[B] = sTrans.loop(bbSST)(saver loadFromFo bd.fo) //Signal[B]
      def saveIOs: EIn[IO[Unit]] = bSin.events map saver.saveToFo(bd.fo)
      def saves: EIn[SaveEvent] = saveIOs >=> Saver.events(bd.fo.getNameExt)

      saves to bd.saveOut map (_ ⇒ ()) //handle SaveEvents at DataObject
    }

    for {
      uii  ← info
      mvis = uii._2.isEmpty ? defaultMVInfo | uii._2
      um   ← IO(new UndoRedo.Manager)
      ctc  ← createMV (mvis map editorDesc(um), coh)
      rs   ← rules
      es   ← ein (uii._1, ctc, UndoEdit managedOut um, rs) go
    } yield (ctc, es._2, es._1)
  }

  private[this] def defaultMVInfo: List[MVInfo] =
    List(MultiViewInfo(new scala.swing.GridBagPanel, "default", "Default"))

  private[being] class BeingOpenSupport (
    bd: BeingDo, c: Controller, ref: IORef[Option[BeingOpenParams]]
  )  extends org.openide.loaders.OpenSupport(bd.getPrimaryEntry)
     with org.openide.cookies.OpenCookie 
     with org.openide.cookies.CloseCookie 
     with COHandler {

     private def load: IO[CTC] = for {
       ps ← ref.read >>= (
              _.fold (
               c(this, bd) >>= (ps ⇒ ref write ps.some as ps)
             )(_.η[IO])
           )
     } yield ps._1

     private def doClose: IO[Unit] = ref.read >>= (
       _ map (_._3.toList foldMap (_.disconnect)) orZero
     )
    
    protected def createCloneableTopComponent = load.unsafePerformIO()

    def resolveCloseOperation(es: Array[COState]): Boolean =
      doClose >> ref.write(None) >> IO(canClose) unsafePerformIO
  }

  private def editorDesc (u: UndoRedo)(i: MVInfo): MVDesc =
  new MVDesc with Serializable {
    def getPersistenceType = PersistenceNever
    def getIcon: Image = null
    def getHelpCtx = HelpCtx.DEFAULT_HELP
    def getDisplayName = i.name
    def preferredID = i.preferredId

    def createElement: MVElem = new MVElem {
      private lazy val tb = new JToolBar
      override def getVisualRepresentation = i.panel.peer
      override def getToolbarRepresentation = tb
      override def setMultiViewCallback(cb: MVElemCallback){}
      override def canCloseElement = StateOk
      override def componentDeactivated(){}
      override def componentActivated(){}
      override def componentHidden(){}
      override def componentShowing(){}
      override def getUndoRedo: UndoRedo = u
      override def getActions: Array[Action] = Array.empty

      override def componentClosed() {i.panel match {
        case pc: PersistentComponent ⇒ pc.persist.unsafePerformIO
        case _ ⇒
      }}

      override def componentOpened() {i.panel match {
        case pc: PersistentComponent ⇒ pc.read.unsafePerformIO
        case _ ⇒
      }}

      override def getLookup = i.panel match {
        case l: Lookup.Provider ⇒ l.getLookup
        case _ ⇒ Lookup.EMPTY
      }
    }
  }
}

// vim: set ts=2 sw=2 et:
