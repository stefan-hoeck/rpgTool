package efa.rpg.core

import efa.core.{Described ⇒ EfaDesc, Named}
import scalaz.{Show, Contravariant}

trait Described[A] extends Named[A] with EfaDesc[A] { self ⇒
  def desc (a: A): String

  def fullDesc (a: A): String

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

  val DescribedContravariant: Contravariant[Described] =
    new Contravariant[Described] {
      def contramap[A,B](d: Described[A])(f: B ⇒ A) =
        Described.contramap(f)(d)
    }
}

// vim: set ts=2 sw=2 et:
