package efa.rpg.being

import dire._, dire.swing.swingSink
import efa.core._, Efa._
import efa.io.LoggerIO
import efa.nb.{PureLookup, VStSF, NbSystem, undo}
import efa.nb.controller.{StateTransFunctions, SavableInfo, Saver}
import efa.nb.tc.PersistenceType.Never
import efa.rpg.core.Described
import efa.rpg.being.loaders.BeingDo
import efa.rpg.preferences.Preferences
import efa.rpg.rules.{Rule, RuleSettings}
import java.awt.Image
import javax.swing.{Action, JToolBar}
import org.netbeans.core.spi.multiview._
import org.openide.cookies.{OpenCookie, CloseCookie}
import org.openide.util.{Lookup, HelpCtx}
import org.openide.awt.UndoRedo
import org.openide.filesystems.FileObject
import org.openide.loaders._
import scalaz._, Scalaz._, effect.{IO, IORef}, Dual._
import org.openide.filesystems.FileObject

/**
 * ext is the file extension and mime the mime type.
 *
 * ex: hero, text/x-hero
 */
abstract class BeingLoader(ext: String, mime: String, c: Controller)
  extends UniFileLoader("efa.rpg.being.loaders.BeingDo"){
  import BeingLoader._
  
  override protected def defaultDisplayName = loc.loaderName
  override protected def actionsContext = "Loaders/" + mime + "/Actions"
  
  setExtensions(getExtensions)

  override protected def createMultiObject(fo: FileObject) = {
    def create: IO[BeingDo] = for {
      pl  ← PureLookup.apply
      bd  ← IO(new BeingDo(fo, this, pl))
      ref ← IO newIORef none[BeingOpenParams]
      _   ← pl add new BeingOpenSupport(bd, c, ref)
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
    def sin(
      st: VStSF[C,B], ctc: CTC, um: UndoRedo.Manager, rs: List[Rule[C]]
    ): SIn[B] = {
      type RuleWorld = (W, Endo[C])
      implicit def Logger: LoggerIO = mainLog

      //RuleWorld
      def calcRW(b: B, w: RuleWorld): C = w._2 apply calc(b, w._1)
      def sinRW = ^(world, RuleSettings endoIn rs)(Pair.apply)

      //Display name in view and and set being at data object's lookup
      def displayOut: Out[C] = c ⇒ IO(ctc.setDisplayName(Described[C] name c))
      def nameSink = swingSink(displayOut ⊹ bd.pl.set[C])

      def loggedSt = SF.id[C] to nameSink andThen
                     st asyncTo valLog.logValRes //log failures
      
      def load = saver loadFromFo bd.fo //Load being from file

      def saveSF = Saver.sf(bd.saveInfo, saver saveToFo bd.fo)

      def sinB = complete(sinRW, undo out um)(loggedSt)(calcRW, load)

      sinB branch saveSF
    }

    for {
      uii    ← info
      mvis   ← uii._2.isEmpty ? defaultMVInfo | IO(uii._2)
      um     ← IO(new UndoRedo.Manager)
      ctc    ← createMV(mvis map editorDesc(um), coh)
      rs     ← rules
      kill   ← NbSystem forever sin(uii._1.events, ctc, um, rs)
    } yield (ctc, kill)
  }

  private[this] def defaultMVInfo: IO[List[MVInfo]] =
    MVInfo.default map (List(_))

  private[being] class BeingOpenSupport (
    bd: BeingDo, c: Controller, ref: IORef[Option[BeingOpenParams]]
  ) extends OpenSupport(bd.getPrimaryEntry)
    with OpenCookie 
    with CloseCookie 
    with COHandler {

    private def load: IO[CTC] = for {
      op ← ref.read
      ps ← op.cata(_.η[IO], doLoad)
    } yield ps._1

    private def doLoad = c(this, bd) >>= (ps ⇒ ref write ps.some as ps)

    private def doClose: IO[Unit] = ref.read >>= (_ map (_._2) orZero)
    
    protected def createCloneableTopComponent = load.unsafePerformIO()

    override def resolveCloseOperation(es: Array[COState]): Boolean = true

    override def close() = 
      IO(super.close()) >>
      IO.putStrLn("closing") >>
      doClose >>
      ref.write(None).as(true) unsafePerformIO
  }

  private def editorDesc (u: UndoRedo)(i: MVInfo) =
    new MultiViewDescription with Serializable {
      def getPersistenceType = Never.v
      def getIcon: Image = null
      def getHelpCtx = HelpCtx.DEFAULT_HELP
      def getDisplayName = i.name
      def preferredID = i.preferredId

      def createElement = new MultiViewElement {
        private lazy val tb = new JToolBar
        override def getVisualRepresentation = i.panel
        override def getToolbarRepresentation = tb
        override def setMultiViewCallback(cb: MultiViewElementCallback){}
        override def canCloseElement = StateOk
        override def componentDeactivated(){}
        override def componentActivated(){}
        override def componentHidden(){}
        override def componentShowing(){}
        override def getUndoRedo: UndoRedo = u
        override def getActions: Array[Action] = Array.empty
        override def componentClosed() { i.persist.unsafePerformIO() }
        override def componentOpened() { i.read.unsafePerformIO() }
        override def getLookup = i.lookup
      }
    }
}

// vim: set ts=2 sw=2 et nowrap:
