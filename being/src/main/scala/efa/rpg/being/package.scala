package efa.rpg

import efa.core.{Service, ValSt}
import efa.react.{Events, Connectors, SET}
import efa.rpg.being.loaders.BeingDo
import efa.rpg.being.spi.BeingLocal
import org.openide.windows.{CloneableTopComponent, TopComponent}
import org.netbeans.core.spi.multiview._
import scalaz._, Scalaz._, effect.IO

package object being {
  lazy val loc = Service.unique[BeingLocal](BeingLocal)

  type BeingOpenParams =
    (CloneableTopComponent, Events[Unit], Connectors)
  
  type Nel[+A] = NonEmptyList[A]

  type TC = TopComponent
  type CTC = CloneableTopComponent

  type COHandler = CloseOperationHandler
  type COState = CloseOperationState

  type MVInfo = MultiViewInfo
  type MVDesc = MultiViewDescription
  type MVElem = MultiViewElement
  type MVElemCallback = MultiViewElementCallback

  type Controller = (COHandler, BeingDo) ⇒ IO[BeingOpenParams]

  type UIInfo[B,C] = (SET[C, ValSt[B]], List[MVInfo])

  def uiInfo[A,B,P <: MVPanel[A,B]](f: IO[P]): IO[UIInfo[B,A]] =
    (f map (p ⇒ (p.set, List(MultiViewInfo(p, p.prefId, p.locName)))))

  def PersistenceNever = TopComponent.PERSISTENCE_NEVER
  def StateOk = CloseOperationState.STATE_OK

  private[being] def createMV (ds: List[MVDesc], coh: COHandler): IO[CTC] =
    IO(MultiViewFactory.createCloneableMultiView(ds.toArray, ds.head, coh))
}

// vim: set ts=2 sw=2 et:
