package efa.rpg.core

import efa.core.Default
import scalaz.{Equal, Monoid, State, Lens, @>}
import scalaz.syntax.foldable._
import scalaz.syntax.equal._
import scalaz.syntax.monoid._
import scalaz.std.list._
import scalaz.std.map._
import scalaz.std.anyVal._
import org.scalacheck.{Arbitrary, Gen}

sealed trait Modifiers extends Function1[ModifierKey,Long] {
  private[core] def ms: Map[ModifierKey, List[Modifier]]

  private[this] lazy val props: Map[ModifierKey, Long] =
    ms map (p ⇒ (p._1, p._2 foldMap (_.value)))
  
  def keySet = ms.keySet

  def apply(key: ModifierKey): Long = property(key)

  def property(key: ModifierKey): Long = props getOrElse (key, 0L)

  def get(k: ModifierKey): List[Modifier] = ms getOrElse (k, Nil)

  def set(k: ModifierKey, mods: List[Modifier]): Modifiers

  def mod(k: ModifierKey, f: List[Modifier] ⇒ List[Modifier]) =
    set(k, f(get(k)))

  def add(k: ModifierKey, m: Modifier) = mod(k, m :: _)

  def add(k: ModifierKey, ms: List[Modifier]) = mod(k, ms ::: _)

  def remove(k: ModifierKey, m: Modifier) = mod(k, _ filterNot (m ≟ ))

  def append(b: Modifiers): Modifiers
}

object Modifiers {

  def apply (ps: (ModifierKey, List[Modifier])*): Modifiers =
    Impl(ps.toList foldMap (Map(_)))

  val empty: Modifiers = Impl(Map.empty)

  implicit val equalInst: Equal[Modifiers] = Equal equalBy (_.ms)

  implicit val monoidInst: Monoid[Modifiers] = new Monoid[Modifiers] {
    val zero = empty
    def append(a: Modifiers, b: ⇒ Modifiers) = a append b
  }

  implicit val defaultInst: Default[Modifiers] = Default default empty

  implicit val arbInst: Arbitrary[Modifiers] = {
    val pGen = for {
      n ← Gen choose (1, 3)
      k ← Arbitrary.arbitrary[ModifierKey]
      l ← Gen.listOfN(n, Arbitrary.arbitrary[Modifier])
    } yield (k, l)

    val listGen = for {
      n  ← Gen choose (0, 3)
      ps ← Gen.listOfN(n, pGen)
    } yield ps

    Arbitrary(listGen map (ps ⇒ apply(ps :_*)))
  }

  implicit class ModLens[A](val l: A @> Modifiers) extends AnyVal {
    def modMods(k: ModifierKey, f: List[Modifier] ⇒ List[Modifier])
      :State[A,Modifiers] = l mods (_ mod (k, f))

    def add (k: ModifierKey, m: Modifier) = modMods(k, m :: _)

    def add (k: ModifierKey, ms: List[Modifier]) = modMods (k, ms ::: _)

    def remove (k: ModifierKey, m: Modifier) = modMods(k, _ filterNot (m ≟ ))

    def property (k: ModifierKey): A ⇒ Long = l get _ property k

    def at (k: ModifierKey): Lens[A,List[Modifier]] =
      Lens.lensu((a, ms) ⇒ l set (a, l get a set (k, ms)), l get _ get k)
  }

  private case class Impl (ms: Map[ModifierKey, List[Modifier]])
     extends Modifiers {
    def set(k: ModifierKey, mods: List[Modifier]): Modifiers =
      Impl(ms + (k → mods))

    def append(b: Modifiers): Modifiers = Impl(ms ⊹ b.ms)
  }
}

// vim: set ts=2 sw=2 et:
