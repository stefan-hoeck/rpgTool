package efa.rpg.items

import efa.core.ToXml

package object saver {

  def xmlSaver[I:ToXml] (
    dataName: String,
    lbl: String, 
    clazz: Class[_],
    ext: String = "data",
    templatesName: String = efa.rpg.items.loc.templates
  ): ItemSaver[I] =
    XmlSaver (lbl, FileStreamProvider (dataName, templatesName, clazz, ext))
}

// vim: set ts=2 sw=2 et:
