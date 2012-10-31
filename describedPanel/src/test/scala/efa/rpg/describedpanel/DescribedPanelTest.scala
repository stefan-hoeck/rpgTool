package efa.rpg.describedpanel

import efa.nb.PureLookup
import efa.rpg.core.HtmlDesc
import org.scalacheck._, Prop._
import scalaz._, Scalaz._, effect._, scalacheck.ScalaCheckBinding._

object DescribedPanelTest extends Properties("DescribedPanel") {
  val hdGen = ^(Gen.identifier, Gen.identifier)(HtmlDesc.apply)
  val hdsGen = for {
    n ← Gen choose (0, 10)
    hds ← Gen listOfN(n, hdGen)
  } yield hds.toSet

  property("adjust") = Prop.forAll(hdsGen){ hds ⇒ 
    val res = for {
      pl ← PureLookup.apply
      ip ← DescribedTc.createInner(pl.l)
      _  ← pl ++ hds.toList
      m ← ip.pageMap.read
    } yield ((m.keySet == hds) && (ip.pages.size ≟ hds.size))

    res.unsafePerformIO
  }
}

// vim: set ts=2 sw=2 et:
