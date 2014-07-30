import scala.language.postfixOps
import sbt._
import Keys._

object BuildSettings {
  val sv                = "2.11.2"
  val buildOrganization = "efa.rpg"
  val buildVersion      = "1.2.0-SNAPSHOT"
  val buildScalaVersion = sv
  val netbeansRepo      = "Netbeans" at "http://bits.netbeans.org/maven2/"

  val manifest          = SettingKey[File]("manifest", "Location of the Manifest.mf file")
  val removeManifest    = TaskKey[Unit]("remove-manifest", "Removes manifest file")

  val buildSettings = Seq (
    organization       := buildOrganization,
    version            := buildVersion,
    scalaVersion       := buildScalaVersion,
    resolvers          += netbeansRepo,
    publishTo          := Some(Resolver.file("file", 
      new File(Path.userHome.absolutePath+"/.m2/repository"))),
    manifest           <<= classDirectory in Compile apply (_ / "META-INF/MANIFEST.MF"),
    removeManifest     <<= manifest map (f ⇒ f.delete),
    fork               := true,
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Compile, packageSrc) := false,
    testOptions in Test += Tests.Setup( () => System.setProperty("java.vm.vendor", "Sun") ),

    scalacOptions      ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature",
      "-language:postfixOps",
      "-language:implicitConversions",
      "-language:higherKinds"
    )
  )
} 

object Dependencies {
  import BuildSettings.sv  

  val direV                = "0.2.0-SNAPSHOT"
  val efaNbV               = "0.3.1-SNAPSHOT"
  val nbV                  = "RELEASE80"
  val scalacheckV          = "1.11.4"
  val scalazV              = "7.1.0-RC2"
  val shapelessV           = "2.0.0"
  val utilV                = "0.2.3-SNAPSHOT"

  val dire                 = "dire"
  val nb                   = "org.netbeans.api"
  val scalaz               = "org.scalaz"
  val util                 = "efa"

  val efa_core             = (util %% "efa-core" % utilV).changing
  val efa_io               = (util %% "efa-io" % utilV).changing
  val efaNb                = ("efa.nb" %% "efa-nb" % efaNbV).changing
  val dire_core            = (dire %% "dire-core" % direV).changing
  val dire_swing           = (dire %% "dire-swing" % direV).changing

  val nbActions            = nb % "org-openide-actions" % nbV
  val nbAnnotations        = nb % "org-netbeans-api-annotations-common" % nbV
  val nbAutoupdateServices = nb % "org-netbeans-modules-autoupdate-services" % nbV
  val nbAutoupdateUi       = nb % "org-netbeans-modules-autoupdate-ui" % nbV
  val nbAwt                = nb % "org-openide-awt" % nbV
  val nbDialogs            = nb % "org-openide-dialogs" % nbV
  val nbExplorer           = nb % "org-openide-explorer" % nbV
  val nbFilesystems        = nb % "org-openide-filesystems" % nbV
  val nbLoaders            = nb % "org-openide-loaders" % nbV
  val nbLookup             = nb % "org-openide-util-lookup" % nbV
  val nbModules            = nb % "org-openide-modules" % nbV
  val nbModulesOptions     = nb % "org-netbeans-modules-options-api" % nbV
  val nbMultiview          = nb % "org-netbeans-core-multiview" % nbV
  val nbNodes              = nb % "org-openide-nodes" % nbV
  val nbOptions            = nb % "org-netbeans-modules-options-api" % nbV
  val nbOutline            = nb % "org-netbeans-swing-outline" % nbV
  val nbSettings           = nb % "org-netbeans-modules-settings" % nbV
  val nbUtil               = nb % "org-openide-util" % nbV
  val nbWindows            = nb % "org-openide-windows" % nbV

  val scalacheck           = "org.scalacheck" %% "scalacheck" % scalacheckV
  val scalaz_core          = scalaz %% "scalaz-core" % scalazV
  val scalaz_effect        = scalaz %% "scalaz-effect" % scalazV
  val scalaz_scalacheck    = scalaz %% "scalaz-scalacheck-binding" % scalazV
  val shapeless            = "com.chuusai" %% "shapeless" % shapelessV

  val deps                 = Seq(scalaz_core, scalaz_effect, scalaz_scalacheck,
                                 shapeless, scalacheck)
}

object UtilBuild extends Build {
  import Dependencies._
  import BuildSettings._

  def addDeps (ds: ModuleID*) = BuildSettings.buildSettings ++
    Seq(libraryDependencies ++= (ds ++ deps))

  lazy val util = Project (
    "rpg",
    file("."),
    settings = buildSettings
  ) aggregate(preferences, core) //(being, describedPanel, explorer, items,
              //rules, rulesUI)
  
  lazy val core = Project (
    "rpg-core",
    file("core"),
    settings = addDeps(efa_core)
  )

  lazy val being = Project (
    "rpg-being",
    file("being"),
    settings = addDeps(
      efa_core, efa_io, efaNb, dire_core, nbLoaders,
      nbFilesystems, nbMultiview, nbWindows, nbAwt, nbExplorer
    )
  ) dependsOn(core, preferences, rules)

  lazy val describedPanel = Project (
    "rpg-describedPanel",
    file("describedPanel"),
    settings = addDeps(efa_core, efa_io, efaNb, dire_core)
  ) dependsOn(core, preferences)

  lazy val explorer = Project (
    "rpg-explorer",
    file("explorer"),
    settings = addDeps(efa_core, efaNb, nbFilesystems, nbLoaders)
  ) dependsOn(items, preferences)

  lazy val items = Project (
    "rpg-items",
    file("items"),
    settings = 
      addDeps(efa_core, efa_io, efaNb, nbLoaders, nbFilesystems % "test") :+ 
      (parallelExecution in Test := false)
  ).dependsOn(core, preferences)

  lazy val preferences = Project (
    "rpg-preferences",
    file("preferences"),
    settings = addDeps(efa_core, efa_io, nbFilesystems)
  )
  
  lazy val rules = Project (
    "rpg-rules",
    file("rules"),
    settings = addDeps(efa_core, efa_io, dire_core)
  )
  
  lazy val rulesUI = Project (
    "rpg-rulesUI",
    file("rulesUI"),
    settings = addDeps(efa_core, efa_io, dire_core, efaNb, nbNodes)
  ) dependsOn(rules)
}

// vim: set ts=2 sw=2 et nowrap:
