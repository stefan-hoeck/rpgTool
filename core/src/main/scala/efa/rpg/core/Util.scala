package efa.rpg.core

import efa.core._
import efa.data.{UniqueIdFunctions, UniqueIdLFunctions}
import org.scalacheck.{Arbitrary, Gen}

/**
 * Utility Functions
 */
trait Util
   extends HtmlTags 
   with UniqueIdFunctions
   with UniqueIdLFunctions {

  def !![B:Default]: B = Default[B].default

  def a[B:Arbitrary]: Gen[B] = Arbitrary.arbitrary
}

// vim: set ts=2 sw=2 et:
