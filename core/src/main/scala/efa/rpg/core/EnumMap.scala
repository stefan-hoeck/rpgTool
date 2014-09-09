//package efa.rpg.core
//
//import efa.core._, Efa._
//import org.scalacheck._
//import scalaz._, Scalaz._, scalacheck.ScalaCheckBinding._
//import scala.xml._
//import scala.xml.Node
//
//// @TODO: Documentation: Talk about conventions (delimiter = "/")
//sealed trait EnumMap[K,V] extends Function1[K,V] {
//  import EnumMap._
//
//  def map[W](f: V ⇒ W): EnumMap[K,W]
//
//  def +(p: (K,V)): EnumMap[K,V]
//
//  def mod(k: K, f: V ⇒ V): EnumMap[K,V] = this + (k -> f(apply(k)))
//
//  def ap[W](em: EnumMap[K,V ⇒ W]): EnumMap[K,W]
//}
//
//object EnumMap extends EnumMapInstances0 {
//
//  def apply[K:RpgEnum,V](m: Map[K,V]): EnumMap[K,V] = {
//    require(RpgEnum[K].values.length ≟ m.size)
//    Impl(m)
//  }
//
//  def apply[K:RpgEnum,V](v: V): EnumMap[K,V] =
//    fromMap(RpgEnum[K].values map ((_, v)) toMap)
//
//  private[core] def fromMap[K,V](m: Map[K,V]): EnumMap[K,V] = Impl(m)
//
//  def at[K,V](k: K): EnumMap[K,V] @> V =
//    Lens.lensu((a,b) ⇒ a + (k, b), _(k))
//  
//  implicit class EnumMapLenses[A,K,V](val l: A @> EnumMap[K,V]) extends AnyVal {
//    def at (k: K): A @> V = l >=> EnumMap.at(k)
//  }
// 
//  private case class Impl[K,V](m: Map[K,V]) extends EnumMap[K,V] {
//    def apply (k: K) = m(k)
//
//    def + (p: (K,V)): EnumMap[K,V] = Impl(m + p)
//
//    def map[W] (f: V ⇒ W): EnumMap[K,W] = Impl(m map {case (k,v) ⇒ (k, f(v))})
//
//    def ap[W] (em: EnumMap[K,V ⇒ W]): EnumMap[K,W] =
//      Impl(m map {case (k,v) ⇒ (k, em(k)(v))})
//  }
//}
//
//trait EnumMapInstances0 {
//  implicit def EnumMapDefault[K:RpgEnum,V:Default]: Default[EnumMap[K,V]] =
//    Default default EnumMap(!!![V])
//
//  implicit def EnumMapEqual[K:RpgEnum,V:Equal]: Equal[EnumMap[K,V]] =
//    Equal.equalBy(RpgEnum[K].values map _)
//
//  implicit def EnumMapApplicative[K:RpgEnum]
//    : Applicative[({type λ[α]=EnumMap[K,α]})#λ] with
//    Traverse[({type λ[α]=EnumMap[K,α]})#λ] =
//    new Applicative[({type λ[α]=EnumMap[K,α]})#λ] with
//    Traverse[({type λ[α]=EnumMap[K,α]})#λ] {
//      def point[A] (a: ⇒ A): EnumMap[K,A] = EnumMap(a)
//      def ap[A,B] (fa: ⇒ EnumMap[K,A])(f: ⇒ EnumMap[K,A ⇒ B]) = fa ap f
//      def traverseImpl[F[_]: Applicative, A, B](t: EnumMap[K,A])
//        (f: A => F[B]): F[EnumMap[K,B]] = {
//        def fPair (k: K) = f(t(k)) strengthL k
//
//        RpgEnum[K].values traverse fPair map (ps ⇒ EnumMap fromMap ps.toMap)
//      }
//    }
//    
//  implicit def EnumMapArbitrary[K:RpgEnum,V:Arbitrary]
//    : Arbitrary[EnumMap[K,V]] = {
//      def toPair (k: K): Gen[(K,V)] = Arbitrary.arbitrary[V] map ((k, _))
//      def pairs = RpgEnum[K].values traverse toPair
//      
//      Arbitrary(pairs map (ps ⇒ EnumMap fromMap ps.toMap))
//    }
//}
//
//// @TODO Remove this. Use direct type class derivation
//abstract class EnumMaps[K:RpgEnum,V:Show:Equal:Read] {
//  final protected lazy val ks: List[K] = RpgEnum[K].values
//
//  protected def delim: String
//
//  protected def tag: String
//  
//  protected def vGen: Gen[V]
//
//  protected def default: V
//
//  def validator: EndoVal[V]
//
//  lazy val validate: EndoVal[EnumMap[K,V]] = Validators.endoV (
//    _ traverse (v ⇒ (validator run v validation): ValRes[V]))
//
//  /**
//   * Prints a function from K to V as a string of its values delimetd by the given
//   * delim parameter.
//   */
//  def shows(em: EnumMap[K,V]): String =
//    ks map (em(_).shows) mkString delim
//
//  def read(s: String): ValRes[EnumMap[K,V]] = {
//    val size = ks.size
//
//    def validateSize(as: List[String]): ValRes[List[String]] =
//      if (as.size ≟ size) as.success else loc invalidLength size failureNel
//
//    def readV (s: String): ValRes[V] =
//      Read[V].validator andThen validator run s validation
//  
//    
//    def entries = s split delim toList
//
//    import Validation.FlatMap._
//    def allVs = validateSize(entries) flatMap (_ traverse readV)
//
//    allVs ∘ (vs ⇒ EnumMap fromMap (ks zip vs toMap))
//  }
//
//  def read (ns: Seq[Node]): ValRes[EnumMap[K,V]] = read(ns \ tag text)
//
//  def write (em: EnumMap[K,V]): Seq[Node] = 
//    Elem(null, tag, Null, TopScope, true, Text(shows(em)))
//
//  def gen: Gen[EnumMap[K,V]] =
//    ks traverse (_ ⇒ vGen) map (vs ⇒ EnumMap fromMap (ks zip vs toMap))
//
//  lazy val !! : EnumMap[K,V] = EnumMap(default)
//}
//
//// @TODO Remove this. Use direct type class derivation
//// Also, don't use EndoVal but use newtypes instead.
//object EnumMaps {
//  def apply[K:RpgEnum,V:Show:Equal:Read] (
//    endoVal: EndoVal[V],
//    valueGen: Gen[V],
//    defaultVal: V,
//    tagString: String, 
//    delimiter: String = "/"
//  ): EnumMaps[K,V] = new EnumMaps[K,V] {
//    val validator = endoVal
//    val vGen = valueGen
//    val delim = delimiter
//    val tag = tagString
//    val default = defaultVal
//  }
//
//  def default[K:RpgEnum,V:Show:Equal:Read:Default:Arbitrary] (
//    tagString: String, delimiter: String = "/"
//  ): EnumMaps[K,V] = apply (
//    Validators.dummy[V],
//    Arbitrary.arbitrary[V],
//    Default[V].default,
//    tagString,
//    delimiter
//  )
//
//  def int[K:RpgEnum](
//    min: Int,
//    max: Int,
//    defaultVal: Int,
//    tagString: String,
//    delimiter: String = "/"
//  ): EnumMaps[K,Int] = apply[K,Int] (
//    Validators interval (min, max),
//    Gen choose (min, max),
//    defaultVal,
//    tagString,
//    delimiter
//  )
//
//  def long[K:RpgEnum](
//    min: Long,
//    max: Long,
//    defaultVal: Long,
//    tagString: String,
//    delimiter: String = "/"
//  ): EnumMaps[K,Long] = apply[K,Long] (
//    Validators interval (min, max),
//    Gen choose (min, max),
//    defaultVal,
//    tagString,
//    delimiter
//  )
//}
//
//// vim: set ts=2 sw=2 et:
