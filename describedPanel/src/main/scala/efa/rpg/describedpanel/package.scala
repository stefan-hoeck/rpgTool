package efa.rpg

import efa.core.Service
import efa.rpg.describedpanel.spi.DescribedPanelLocal
import javax.swing.JScrollPane

package object describedpanel {
  lazy val loc = Service.unique[DescribedPanelLocal](DescribedPanelLocal)

  type Page = (String, JScrollPane)
}

// vim: set ts=2 sw=2 et:
