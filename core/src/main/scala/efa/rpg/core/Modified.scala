package efa.rpg.core

import efa.core.Name
import scalaz.{@>, State}
import scalaz.syntax.std.option._
import scalaz.syntax.std.boolean._
import scalaz.syntax.equal._
import scalaz.syntax.functor._
import scalaz.std.anyVal._
import scalaz.std.option.none

trait Modified[A] extends HasModifiers[A] {
  import Modified.notZero

  def addMod(a: A, k: ModifierKey, m: Modifier): A =
    addModS(k, m) exec a

  def addModS(k: ModifierKey, m: Modifier): State[A,Unit] =
    modifiersL add (k, m) void

  def addMods(a: A, k: ModifierKey, ms: List[Modifier]): A =
    addModsS(k, ms) exec a

  def addModsS(k: ModifierKey, ms: List[Modifier])
    : State[A,Unit] = modifiersL add (k, ms) void

  def mod (
    a: A,
    name: String,
    value: Option[Long],
    key: ModifierKey
  )(f: (A,Long) ⇒ Long): A = modS(f, name, value, key) exec a

  def modifiers (a: A) = modifiersL get a

  def modifiersL: A @> Modifiers

  def modI (
    a: A,
    name: String,
    value: Option[Int],
    key: ModifierKey
  )(f: (A,Int) ⇒ Int): A =
    oModAdd(a, name, value map (f(a, _)) getOrElse 0 toLong, key)

  def modS (
    f: (A,Long) ⇒ Long, 
    name: String,
    value: Option[Long],
    key: ModifierKey
  ): State[A,Unit] = for {
    a ← State.init[A]
    _ ← oModAddS(name, value map (f(a, _)) getOrElse 0L, key)
  } yield ()

  def oMod (name: String, value: Long): Option[Modifier] =
    notZero(value) map (Modifier(Name(name), _))

  def oModAdd (a: A, n: String, v: Long, k: ModifierKey): A =
    oModAddS(n, v, k) exec a

  def oModAddS (n: String, v: Long, k: ModifierKey): State[A,Unit] =
    oMod(n, v).fold(State.init[A].void)(addModS (k, _))
}

object Modified {
  def apply[A:Modified]: Modified[A] = implicitly

  def notZero(v: Long): Option[Long] = (v ≟ 0L) ? none[Long] | v.some

  def notZero(v: Int): Option[Int] = (v ≟ 0) ? none[Int] | v.some

  import efa.rpg.core.{Modified ⇒ M}

  def addModS[A:Modified](k: ModifierKey, m: Modifier): State[A,Unit] =
    Modified[A] addModS (k, m)

  def addModsS[A:Modified](k: ModifierKey, ms: List[Modifier]): State[A,Unit] =
    Modified[A] addModsS (k, ms)

  def modS[A:Modified] (
    f: (A,Long) ⇒ Long, 
    name: String,
    value: Option[Long],
    key: ModifierKey
  ): State[A,Unit] = Modified[A] modS (f, name, value, key)

  def oMod[A:Modified](name: String, value: Long): Option[Modifier] =
    Modified[A] oMod (name, value)

  def oModAddS[A:Modified](n: String, v: Long, k: ModifierKey): State[A,Unit] =
    Modified[A] oModAddS (n, v, k)
}

// vim: set ts=2 sw=2 et:
