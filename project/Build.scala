import sbt._
import Keys._

object BuildSettings {
  val sv = "2.10.3"
  val buildOrganization = "efa.rpg"
  val buildVersion = "1.1.0-SNAPSHOT"
  val buildScalaVersion = sv
  val netbeansRepo = "Netbeans" at "http://bits.netbeans.org/maven2/"

  val manifest = SettingKey[File]("manifest", "Location of the Manifest.mf file")
  val removeManifest = TaskKey[Unit]("remove-manifest", "Removes manifest file")

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version := buildVersion,
    scalaVersion := buildScalaVersion,
    manifest <<= classDirectory in Compile apply (_ / "META-INF/MANIFEST.MF"),
    removeManifest <<= manifest map (f ⇒ f.delete),
    resolvers += netbeansRepo,
    scalacOptions ++= Seq ("-deprecation", "-feature", "-language:higherKinds",
      "-language:postfixOps", "-unchecked"),
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Compile, packageSrc) := false,
    testOptions in Test += Tests.Setup( () => System.setProperty("java.vm.vendor", "Sun") ) 
  )
} 

object Dependencies {
  import BuildSettings.sv  

  val utilV = "0.2.2-SNAPSHOT"
  val direV = "0.1.0-SNAPSHOT"
  val efaNbV = "0.3.0-SNAPSHOT"
  val nbV = "RELEASE71"
  val scalazV = "7.0.4"

  val nb = "org.netbeans.api"
  val util = "efa"
  val dire = "dire"
  val scalaz = "org.scalaz"

  val efaCore = util %% "efa-core" % utilV changing

  val efaIo = util %% "efa-io" % utilV changing

  val efaNb = "efa.nb" %% "efa-nb" % efaNbV changing

  val direCore = dire %% "dire-core" % direV changing

  val direSwing = dire %% "dire-swing" % direV changing
 
  val nbUtil = nb % "org-openide-util" % nbV
  val nbLookup = nb % "org-openide-util-lookup" % nbV
  val nbExplorer = nb % "org-openide-explorer" % nbV
  val nbWindows = nb % "org-openide-windows" % nbV
  val nbNodes = nb % "org-openide-nodes" % nbV
  val nbFilesystems = nb % "org-openide-filesystems" % nbV
  val nbLoaders = nb % "org-openide-loaders" % nbV
  val nbModules = nb % "org-openide-modules" % nbV
  val nbAwt = nb % "org-openide-awt" % nbV
  val nbSettings = nb % "org-netbeans-modules-settings" % nbV
  val nbActions = nb % "org-openide-actions" % nbV
  val nbDialogs = nb % "org-openide-dialogs" % nbV
  val nbOutline = nb % "org-netbeans-swing-outline" % nbV
  val nbAutoupdateUi = nb % "org-netbeans-modules-autoupdate-ui" % nbV
  val nbAutoupdateServices = nb % "org-netbeans-modules-autoupdate-services" % nbV
  val nbModulesOptions = nb % "org-netbeans-modules-options-api" % nbV
  val nbMultiview = nb % "org-netbeans-core-multiview" % nbV

  val shapeless = "com.chuusai" %% "shapeless" % "1.2.3"
  val scalaz_core = scalaz %% "scalaz-core" % scalazV
  val scalaz_effect = scalaz %% "scalaz-effect" % scalazV
  val scalaz_scalacheck = scalaz %% "scalaz-scalacheck-binding" % scalazV

  val scalacheck = "org.scalacheck" %% "scalacheck" % "1.10.0"

  val coolness = Seq(scalaz_core, scalaz_effect, scalaz_scalacheck,
                     shapeless, scalacheck)
}

object UtilBuild extends Build {
  import Resolvers._
  import Dependencies._
  import BuildSettings._

  def addDeps (ds: ModuleID*) = BuildSettings.buildSettings ++
    Seq(libraryDependencies ++= (ds ++ coolness))

  lazy val util = Project (
    "rpg",
    file("."),
    settings = buildSettings
  ) aggregate(being, core, describedPanel, explorer, items, preferences,
              rules, rulesUI)
  
  lazy val core = Project (
    "rpg-core",
    file("core"),
    settings = addDeps(efaCore)
  )

  lazy val being = Project (
    "rpg-being",
    file("being"),
    settings = addDeps(
      efaCore, efaIo, efaNb, direCore, nbLoaders,
      nbFilesystems, nbMultiview, nbWindows, nbAwt, nbExplorer
    )
  ) dependsOn(core, preferences, rules)

  lazy val describedPanel = Project (
    "rpg-describedPanel",
    file("describedPanel"),
    settings = addDeps(efaCore, efaIo, efaNb, direCore)
  ) dependsOn(core, preferences)

  lazy val explorer = Project (
    "rpg-explorer",
    file("explorer"),
    settings = addDeps(efaCore, efaNb, nbFilesystems, nbLoaders)
  ) dependsOn(items, preferences)

  lazy val items = Project (
    "rpg-items",
    file("items"),
    settings = 
      addDeps(efaCore, efaIo, efaNb, nbLoaders, nbFilesystems % "test") :+ 
      (parallelExecution in Test := false)
  ).dependsOn(core, preferences)

  lazy val preferences = Project (
    "rpg-preferences",
    file("preferences"),
    settings = addDeps(efaCore, efaIo, nbFilesystems)
  )
  
  lazy val rules = Project (
    "rpg-rules",
    file("rules"),
    settings = addDeps(efaCore, efaIo, direCore)
  )
  
  lazy val rulesUI = Project (
    "rpg-rulesUI",
    file("rulesUI"),
    settings = addDeps(efaCore, efaIo, direCore, efaNb, nbNodes)
  ) dependsOn(rules)
}

// vim: set ts=2 sw=2 et nowrap:
