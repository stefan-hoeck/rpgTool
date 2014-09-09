//package efa.rpg.core
//
//trait HasModifiers[-A] {
//  def modifiers (a: A): Modifiers
//  
//  def modsFor (a: A, k: ModifierKey): List[Modifier] = modifiers(a) get k
//
//  def prop(a: A, k: ModifierKey): Long = modifiers(a) property k
//}
//
//trait HasModifiersFunctions {
//  import efa.rpg.core.{HasModifiers ⇒ HM}
//
//  def modifiers[A:HM](a: A): Modifiers = HM[A] modifiers a
//
//  def modsFor[A:HM] (a: A, k: ModifierKey): List[Modifier] =
//    HM[A] modsFor (a, k)
//
//  def prop[A:HM](a: A, k: ModifierKey): Long = HM[A] prop (a, k)
//}
//
//object HasModifiers {
//  def apply[A:HasModifiers]: HasModifiers[A] = implicitly
//}
//
//// vim: set ts=2 sw=2 et:
