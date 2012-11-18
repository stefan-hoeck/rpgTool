package efa.rpg.core

trait HtmlTags {

  type Tag = Pair[String,String]

  def titleBody (title: String, body: String) =
    "<P><B>%s</B></P>%s" format (title, body)

  def html (title: String, body: String) =
    "<html>%s</html>" format titleBody(title, body)

  def nameShortDesc (n: String, tags: Tag*) = {
    def wrap(t: Tag) = "<P><B>%s: </B>%s</P>" format (t._1, t._2)

    html (n, tags map wrap mkString "")
  }
}

// vim: set ts=2 sw=2 et:
