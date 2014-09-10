package efa.rpg.core

import efa.core.{Name, Id, Desc}
import efa.rpg.core.{HasModifiers ⇒ HM, Modified ⇒ M}

object syntax {
  implicit class id[A](val self: A) extends AnyVal {
    def plural(implicit F: UnitEnum[A]): String = F plural self

    def multiplier(implicit F: UnitEnum[A]): Long = F multiplier self

    def name(implicit F: RpgItem[A]): Name = F name self

    def shortDesc(implicit F: RpgItem[A]): Desc = F shortDesc self

    def fullDesc(implicit F: RpgItem[A]): Desc = F fullDesc self

    def setName(n: Name)(implicit F: RpgItem[A]): A = F.nameL.set(self, n)

    def modName(f: Name ⇒ Name)(implicit F: RpgItem[A]): A =
      F.nameL.mod(f, self)

    def id(implicit F: RpgItem[A]): Id = F id self

    def setId(i: Id)(implicit F: RpgItem[A]): A = F.idL.set(self, i)

    def modId(f: Id ⇒ Id)(implicit F: RpgItem[A]): A = F.idL.mod(f, self)

    def showPretty(nod: Int)(implicit F: UnitEnum[A]): Long ⇒ String =
      F showPretty (self, nod)

    def modifiers(implicit F: HM[A]): Modifiers = F modifiers self

    def modsFor(k: ModifierKey)(implicit F: HM[A]): List[Modifier] = F modsFor (self, k)

    def prop(k: ModifierKey)(implicit F: HM[A]): Long = F prop (self, k)
    
    def addMod(k: ModifierKey, m: Modifier)(implicit F: M[A]): A = F addMod (self, k, m)

    def addMods(k: ModifierKey, ms: List[Modifier])(implicit F: M[A]): A =
       F addMods (self, k, ms)

    def mod(
      name: String, value: Option[Long], key: ModifierKey
    )(f: (A,Long) ⇒ Long)(implicit F: M[A]): A = F.mod(self, name, value, key)(f)

    def modI(
      name: String, value: Option[Int], key: ModifierKey
    )(f: (A,Int) ⇒ Int)(implicit F: M[A]): A = F.modI(self, name, value, key)(f)

    def oModAdd(n: String, v: Long, k: ModifierKey)(implicit F: M[A]): A =
       F oModAdd (self, n, v, k)
  }
}

// vim: set ts=2 sw=2 et:
