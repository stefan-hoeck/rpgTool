package efa.rpg.core

import efa.core.{Described ⇒ EfaDesc, Named, Name, Desc}
import scalaz.{Show, Contravariant}

trait Described[A] extends Named[A] with EfaDesc[A] { self ⇒
  def desc(a: A): Desc

  def fullDesc(a: A): Desc

  def htmlDesc (a: A) = HtmlDesc(name(a), fullDesc(a))
}

object Described {
  def apply[A:Described]: Described[A] = implicitly

  def contramap[A,B](f: B ⇒ A)(implicit A: Described[A])
    : Described[B] = new Described[B] {
      def desc(b: B) = A desc f(b)
      def shortDesc(b: B) = A shortDesc f(b)
      def fullDesc(b: B) = A fullDesc f(b)
      def name(b: B) = A name f(b)
    }
}

// vim: set ts=2 sw=2 et:
