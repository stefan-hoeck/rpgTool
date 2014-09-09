package efa.rpg.core


object GenderTest extends org.scalacheck.Properties("Gender") {
  include(LocEnum.laws[Gender])
}

// vim: set ts=2 sw=2 et:
