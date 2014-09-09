//package efa.rpg.core
//
//import efa.core.{ToXml, ToXmlSpecs, ReadSpecs, Validators, Localization}
//import org.scalacheck._, Prop._
//import scalaz._, Scalaz._
//
//object EnumMapTest
//  extends Properties("EnumMap") with ToXmlSpecs with ReadSpecs {
//
//  type TestEMap = EnumMap[TestLocEnum,Int]
//
//  //property("arbitrary") = Prop.forAll {em: TestEMap ⇒ 
//  //  TestLocEnum.values ∀ (v ⇒ em.em(v) >= 0 && em.em(v) <= 100)
//  //}
//
//  //property("read") = Prop forAll showRead[TestEMap]
//  //
//  //property("readFail") = Prop forAll readAll[TestEMap]
//
//  //property("toXml") = Prop forAll writeReadXml[TestEMap]
//}
//
//// vim: set ts=2 sw=2 et:
