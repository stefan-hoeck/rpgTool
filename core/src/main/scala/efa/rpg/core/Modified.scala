package efa.rpg.core

import scalaz._, Scalaz._

trait Modified[A] extends HasModifiers[A] {
  import Modified.notZero

  def addMod (a: A, k: ModifierKey, m: Modifier): A =
    addModS(k, m) exec a

  def addModS (k: ModifierKey, m: Modifier): State[A,Unit] =
    modifiersL add (k, m) void

  def addMods (a: A, k: ModifierKey, ms: List[Modifier]): A =
    addModsS(k, ms) exec a

  def addModsS (k: ModifierKey, ms: List[Modifier])
    : State[A,Unit] = modifiersL add (k, ms) void

  def mod (
    a: A,
    name: String,
    value: Option[Long],
    key: ModifierKey
  )(f: (A,Long) ⇒ Long): A = modS(f, name, value, key) exec a

  def modifiers(a: A) = modifiersL get a

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
    a ← init[A]
    _ ← oModAddS(name, value map (f(a, _)) getOrElse 0L, key)
  } yield ()

  def oMod (name: String, value: Long): Option[Modifier] =
    notZero(value) ∘ (Modifier(name, _))

  def oModAdd (a: A, n: String, v: Long, k: ModifierKey): A =
    oModAddS(n, v, k) exec a

  def oModAddS (n: String, v: Long, k: ModifierKey): State[A,Unit] =
    oMod(n, v) fold (addModS (k, _), init[A].void)
}

trait ModifiedFunctions extends HasModifiersFunctions {
  import efa.rpg.core.{Modified ⇒ M}

  def addMod[A:M] (a: A, k: ModifierKey, m: Modifier): A =
    M[A] addMod (a, k, m)

  def addModS[A:M] (k: ModifierKey, m: Modifier): State[A,Unit] =
    M[A] addModS (k, m)

  def addMods[A:M] (a: A, k: ModifierKey, ms: List[Modifier]): A =
    M[A] addMods (a, k, ms)

  def addModsS[A:M] (k: ModifierKey, ms: List[Modifier]): State[A,Unit] =
    M[A] addModsS (k, ms)

  def mod[A:M] (
    a: A, name: String, value: Option[Long], key: ModifierKey
  )(f: (A,Long) ⇒ Long): A = M[A].mod(a, name, value, key)(f)

  def modI[A:M] (
    a: A, name: String, value: Option[Int], key: ModifierKey
  )(f: (A,Int) ⇒ Int): A = M[A].modI(a, name, value, key)(f)

  def modS[A:M] (
    f: (A,Long) ⇒ Long, 
    name: String,
    value: Option[Long],
    key: ModifierKey
  ): State[A,Unit] = M[A] modS (f, name, value, key)

  def oMod[A:M] (name: String, value: Long): Option[Modifier] =
    M[A] oMod (name, value)

  def notZero (v: Long): Option[Long] = (v ≟ 0L) ? none[Long] | v.some

  def notZero (v: Int): Option[Int] = (v ≟ 0) ? none[Int] | v.some

  def oModAdd[A:M] (a: A, n: String, v: Long, k: ModifierKey): A =
    M[A] oModAdd (a, n, v, k)

  def oModAddS[A:M] (n: String, v: Long, k: ModifierKey): State[A,Unit] =
    M[A] oModAddS (n, v, k)
  
}

object Modified extends ModifiedFunctions {
  def apply[A:Modified]: Modified[A] = implicitly
}

// vim: set ts=2 sw=2 et:
