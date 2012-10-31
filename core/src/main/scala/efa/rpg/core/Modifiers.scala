package efa.rpg.core

import scalaz._, Scalaz._

sealed trait Modifiers {
  private[core] def ms: Map[ModifierKey, List[Modifier]]

  private[this] lazy val props: Map[ModifierKey, Long] =
    ms map (p ⇒ (p._1, p._2 foldMap (_.value)))
  
  def keySet = ms.keySet

  def property (key: ModifierKey) = props getOrElse (key, 0L)

  def get (k: ModifierKey): List[Modifier] = ms getOrElse (k, Nil)

  def set (k: ModifierKey, mods: List[Modifier]): Modifiers

  def mod (k: ModifierKey, f: List[Modifier] ⇒ List[Modifier]) =
    set (k, f(get(k)))

  def add (k: ModifierKey, m: Modifier) = mod(k, m :: _)

  def add (k: ModifierKey, ms: List[Modifier]) = mod(k, ms ::: _)

  def remove (k: ModifierKey, m: Modifier) = mod(k, _ filterNot (m ≟ ))

  def append (b: Modifiers): Modifiers
}

object Modifiers {

  def apply (ps: Pair[ModifierKey, List[Modifier]]*): Modifiers =
    Impl(Map(ps:_*))

  lazy val empty: Modifiers = Impl(Map.empty)

  implicit lazy val ModifiersEqual: Equal[Modifiers] = Equal.equalA

  implicit lazy val ModifiersMonoid = new Monoid[Modifiers] {
    val zero = empty
    def append (a: Modifiers, b: ⇒ Modifiers) = a append b
  }

  case class ModLens[A](l: A @> Modifiers) {
    def modMods (k: ModifierKey, f: List[Modifier] ⇒ List[Modifier])
      :State[A,Modifiers] = l mods (_ mod (k, f))

    def add (k: ModifierKey, m: Modifier) = modMods(k, m :: _)

    def add (k: ModifierKey, ms: List[Modifier]) = modMods (k, ms ::: _)

    def remove (k: ModifierKey, m: Modifier) = modMods(k, _ filterNot (m ≟ ))

    def property (k: ModifierKey): A ⇒ Long = l get _ property k

    def at (k: ModifierKey): Lens[A,List[Modifier]] =
      Lens.lensu((a, ms) ⇒ l set (a, l get a set (k, ms)), l get _ get k)
  }

  implicit def ModLensW[A](l: Lens[A,Modifiers]) = ModLens(l)

  private case class Impl (ms: Map[ModifierKey, List[Modifier]])
     extends Modifiers {
    def set (k: ModifierKey, mods: List[Modifier]): Modifiers =
      Impl (ms + (k → mods))

    def append (b: Modifiers): Modifiers = Impl (ms ⊹ b.ms)
  }
}

// vim: set ts=2 sw=2 et:
