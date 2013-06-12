package efa.rpg.core

import javax.swing.{JEditorPane, JScrollPane, ScrollPaneConstants ⇒ SPC}
import javax.swing.text.html.HTMLEditorKit
import scalaz.effect.IO

object HtmlEditorPane {

  def apply(t: String): IO[JScrollPane] = IO {
    val ep = new JEditorPane {
      val kit = new HTMLEditorKit
      setEditorKit(kit)
      setDocument(kit.createDefaultDocument)
      setText(t)
    }

    val res = new JScrollPane(ep)
    res.setHorizontalScrollBarPolicy(SPC.HORIZONTAL_SCROLLBAR_NEVER)

    res
  }
}
