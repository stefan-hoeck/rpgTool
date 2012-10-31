package efa.rpg

import efa.core.Service
import efa.rpg.describedpanel.spi.DescribedPanelLocal

package object describedpanel {
  lazy val loc = Service.unique[DescribedPanelLocal](DescribedPanelLocal)
}

// vim: set ts=2 sw=2 et:
