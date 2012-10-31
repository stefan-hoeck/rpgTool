package efa.rpg.core

import javax.swing.{JEditorPane, JScrollPane}
import javax.swing.text.html.HTMLEditorKit
import scala.swing.ScrollPane
import scalaz.effect.IO

class HtmlEditorPane private (t: String) extends ScrollPane {
  def this() = this("")
  
  text = t
  lazy val ep = new JEditorPane {
    val kit = new HTMLEditorKit
    setEditorKit(kit)
    setDocument(kit.createDefaultDocument)
  }
  
  override lazy val peer = {
    new JScrollPane(ep){
      setHorizontalScrollBarPolicy(ScrollPane.BarPolicy.Never.horizontalPeer)
    }
  }
  
  def text = ep.getText
  def text_= (s: String) { ep.setText(s) }
  def editable = ep.isEditable
  def editable_= (b: Boolean) { ep.setEditable(b) }
}

object HtmlEditorPane {

  def apply (t: String): IO[HtmlEditorPane] = IO (new HtmlEditorPane (t))
}
