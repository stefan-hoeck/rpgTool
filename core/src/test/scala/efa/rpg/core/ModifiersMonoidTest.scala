//package efa.rpg.core
//
//import efa.core.Localization
//import org.scalacheck._, Prop._
//import scalaz._, Scalaz._, scalacheck.ScalaCheckBinding._
//
//object ModifiersMonoidTest extends Properties("ModifiersMonoid") {
//
//
//  def key (s: String) = ModifierKey(new Localization(s, s), 0L, 100L)
//
//  lazy val modifierGen =
//    ^(Gen.identifier, Gen.choose(0L, 100L))(Modifier.apply)
//   
//  lazy val modifiersGen =
//    Gen choose (1, 10) flatMap (n ⇒ Gen listOfN (n, modifierGen))
//
//  lazy val keys = 1 to 10 map (i ⇒ key(i.toString))
//
//  lazy val modsGen: Gen[Modifiers] = for {
//    ks ← Gen someOf keys map (_.toList)
//    ms ← ks traverse (_ ⇒ modifiersGen)
//  } yield Modifiers(ks zip ms: _*)
//
//  implicit lazy val ModifiersArbitrary = Arbitrary(modsGen)
//
//  property("specs") = Prop.forAll {p: (Modifiers, Modifiers) ⇒ 
//    val (a,b) = p
//    lazy val both = a ⊹ b
//
//    (both.keySet == (a.keySet ++ b.keySet)) :| "keyset" &&
//    both.keySet.forall(k ⇒ both.get(k) ≟ (a.get(k) ++ b.get(k))) :| "appended"
//  }
//}
//
//// vim: set ts=2 sw=2 et:
