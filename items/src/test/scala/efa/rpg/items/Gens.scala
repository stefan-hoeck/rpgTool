package efa.rpg.items

//import efa.core.Folder
//import org.scalacheck._, Arbitrary.arbitrary
//import scalaz._, Scalaz._, scalacheck.ScalaCheckBinding._
//
//object Gens {
//
//  // the following Generators generate sequences and trees of
//  // advantages with increasing indices. Unfortunately, there is
//  // no monad transformer GenT, so that type StGen could be a
//  // monad. We therefore cannot create trees of arbitrary (aka
//  // random) depth. The trees thus generated are strictly
//  // three levels deep, with each tree containing 3 subtrees
//  // and 3 Advantages.
//  type StInt[A] = State[Int,A]
//  type StGen[A] = StInt[Gen[A]]
//
//  implicit val StGenFunctor: Functor[StGen] =
//    Functor[StInt].compose[Gen]
//
//  implicit val StGenApplicative: Applicative[StGen] =
//    Applicative[StInt].compose[Gen]
//
//  def liftSt[A] (g: Gen[A]): StGen[A] = state (g)
//
//  def nAdvGen (n: Int): Gen[Stream[Advantage]] =
//    Stream.fill (n)(genSt).sequence eval 0
//  
//  val advantagesGen = Gen choose (1,100) flatMap (nAdvGen)
//
//  def genSt: StGen[Advantage] = {
//    def gen (i: Int) =
//      arbitrary[Advantage] ∘ (Advantage.data.id.set(_, i))
//
//    get[Int] >>= (i ⇒ put(i + 1) as gen(i))
//  }
//
//  type Label = (Stream[Advantage], String)
//
//  private[this] val size = 3
//
//  lazy val adsGen: StGen[Stream[Advantage]] = 
//    Stream.fill (size)(genSt).sequence
//
//  lazy val lblGen: StGen[Label] =
//    adsGen ⊛ liftSt(Gen.identifier) apply Tuple2.apply
//
//  def treeGen (fGen: StGen[NameFolder[Advantage]])
//    : StGen[NameFolder[Advantage]] = {
//    val subForestGen = Stream.fill(size)(fGen).sequence
//
//    lblGen ⊛ subForestGen apply ((l, fs) ⇒ Folder(l._1, fs, l._2))
//  }
//
//  lazy val leafGen: StGen[NameFolder[Advantage]] =
//    lblGen ∘ (l ⇒ Folder (l._1, Stream.empty, l._2))
//
//  lazy val rootGen: Gen[NameFolder[Advantage]] =
//    treeGen (treeGen (leafGen)) eval 0
//
//  lazy val iRootGen: Gen[IFolder[Advantage]] =
//    rootGen ∘ (Folder indexFolders _ _2)
//
//}

// vim: set ts=2 sw=2 et:
