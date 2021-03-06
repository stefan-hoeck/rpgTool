package efa.rpg

import dire.SIn
import efa.core.{Service, ValSt}
import efa.io.AsFile
import efa.nb.VStSF
import efa.nb.tc.AsTc
import efa.rpg.being.loaders.BeingDo
import efa.rpg.being.spi.BeingLocal
import efa.rpg.rules.Rule
import org.openide.windows.{CloneableTopComponent, TopComponent}
import org.openide.filesystems.{FileObject, FileUtil}
import org.netbeans.core.spi.multiview._
import scalaz._, Scalaz._, effect.IO

package object being {
  lazy val loc = Service.unique[BeingLocal]

  type CTC = CloneableTopComponent

  type COHandler = CloseOperationHandler
  type COState = CloseOperationState

  type BeingOpenParams = (CTC, IO[Unit])

  type Controller = (COHandler, BeingDo) ⇒ IO[BeingOpenParams]

  type UIInfo[B,C] = (VStSF[C,B], List[MVInfo])

  type Rules[A] = List[Rule[A]]

  def uiInfo[A,B,C](f: IO[BeingPanel[A,B,C]])(implicit C: AsTc[C])
    : IO[UIInfo[B,A]] = {
    def info(c: C) = MVInfo(
      C peer c, C.preferredId, C.name, C read c, C persist c, C lookup c)

    f map (p ⇒ (p.sf, List(info(p.p))))
  }

  def StateOk = CloseOperationState.STATE_OK

  private[being] def createMV(ds: List[MultiViewDescription], coh: COHandler)
    : IO[CTC] =
    IO(MultiViewFactory.createCloneableMultiView(ds.toArray, ds.head, coh))

  implicit val FOAsFile: AsFile[FileObject] = new AsFile[FileObject] {
    override protected def fileIO(fo: FileObject) = IO(
      FileUtil toFile fo match {
        case null ⇒ sys.error("file not found: " + fo.getPath)
        case f    ⇒ f
      }
    )

    def name(fo: FileObject) = fo.getPath
  }
}

// vim: set ts=2 sw=2 et:
