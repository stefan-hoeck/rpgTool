package efa.rpg.core

import efa.core._
import efa.data.{UniqueIdFunctions, UniqueIdLFunctions, DescribedFunctions}
import org.scalacheck.{Arbitrary, Gen}

/**
 * Utility Functions
 */
trait Util
   extends DescribedFunctions
   with UniqueIdFunctions
   with UniqueIdLFunctions {

  def !![B:Default]: B = Default[B].default

  def a[B:Arbitrary]: Gen[B] = Arbitrary.arbitrary

  def db[A]: DB[A] = Map.empty
}

// vim: set ts=2 sw=2 et:
