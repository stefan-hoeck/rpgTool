import sbt._
import Keys._

object BuildSettings {
  import Resolvers._

  val buildOrganization = "efa.rpg"
  val buildVersion = "1.0.0-SNAPSHOT"
  val buildScalaVersion = "2.9.2"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version := buildVersion,
    scalaVersion := buildScalaVersion,
    resolvers ++= repos,
    scalacOptions ++= Seq ("-deprecation"),
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Compile, packageSrc) := false
  )
} 

object Resolvers {
 val netbeansRepo = "Netbeans" at "http://bits.netbeans.org/maven2/"
 val scalatoolsRepo = "Scala-Tools Maven2 Repository Releases" at
   "http://scala-tools.org/repo-releases"
 val sonatypeRepo = "releases" at
   "http://oss.sonatype.org/content/repositories/releases"

 val repos = Seq (netbeansRepo, scalatoolsRepo, sonatypeRepo)
}

object Dependencies {

  val utilVersion = "0.1.0-SNAPSHOT"
  val reactVersion = "0.1.0-SNAPSHOT"
  val util = "efa"
  val react = "efa.react"
  val efaCore = util %% "core" % utilVersion changing

  val efaIo = util %% "io" % utilVersion changing

  val efaNb = util %% "nb" % utilVersion changing

  val efaReact = react %% "core" % reactVersion changing

  val efaReactSwing = react %% "swing" % reactVersion changing


  val nbV = "RELEASE71"

  val scalaSwing = "org.scala-lang" % "scala-swing" % "2.9.2" 
 
  val nbUtil = "org.netbeans.api" % "org-openide-util" % nbV
  val nbLookup = "org.netbeans.api" % "org-openide-util-lookup" % nbV
  val nbExplorer = "org.netbeans.api" % "org-openide-explorer" % nbV
  val nbWindows = "org.netbeans.api" % "org-openide-windows" % nbV
  val nbNodes = "org.netbeans.api" % "org-openide-nodes" % nbV
  val nbFilesystems = "org.netbeans.api" % "org-openide-filesystems" % nbV
  val nbLoaders = "org.netbeans.api" % "org-openide-loaders" % nbV
  val nbModules = "org.netbeans.api" % "org-openide-modules" % nbV
  val nbAwt = "org.netbeans.api" % "org-openide-awt" % nbV
  val nbSettings = "org.netbeans.api" % "org-netbeans-modules-settings" % nbV
  val nbActions = "org.netbeans.api" % "org-openide-actions" % nbV
  val nbDialogs = "org.netbeans.api" % "org-openide-dialogs" % nbV
  val nbOutline = "org.netbeans.api" % "org-netbeans-swing-outline" % nbV
  val nbAutoupdateUi = "org.netbeans.api" % "org-netbeans-modules-autoupdate-ui" % nbV
  val nbAutoupdateServices = "org.netbeans.api" % "org-netbeans-modules-autoupdate-services" % nbV
  val nbModulesOptions = "org.netbeans.api" % "org-netbeans-modules-options-api" % nbV
  val nbMultiview = "org.netbeans.api" % "org-netbeans-core-multiview" % nbV

  val scalaz_core = "org.scalaz" %% "scalaz-core" % "7.0.0-M4"
  val scalaz_effect = "org.scalaz" %% "scalaz-effect" % "7.0.0-M4"
  val scalaz_scalacheck =
    "org.scalaz" %% "scalaz-scalacheck-binding" % "7.0.0-M4"
  val scalaz_scalacheckT = scalaz_scalacheck % "test"

  val scalacheck = "org.scalacheck" %% "scalacheck" % "1.9"
  val scalacheckT = scalacheck % "test"
  val scalazCheckT = Seq(scalaz_core, scalaz_scalacheckT, scalacheckT)
  val scalazCheckET = scalazCheckT :+ scalaz_effect
}

object UtilBuild extends Build {
  import Resolvers._
  import Dependencies._
  import BuildSettings._

  def addDeps (ds: Seq[ModuleID]) =
    BuildSettings.buildSettings ++ Seq (libraryDependencies ++= ds)

  lazy val util = Project (
    "rpg",
    file("."),
    settings = buildSettings
  ) aggregate (being, core, describedPanel, explorer,
               items, preferences, rules, rulesUI)
  
  lazy val core = Project (
    "core",
    file("core"),
    settings = addDeps (scalazCheckET ++ Seq(scalaSwing, efaCore))
  )

  lazy val being = Project (
    "being",
    file("being"),
    settings = addDeps (scalazCheckET ++
      Seq (scalaSwing, efaCore, efaIo, efaNb, efaReact, nbLoaders,
        nbFilesystems, nbMultiview, nbWindows, nbAwt, nbExplorer))
  ) dependsOn (core, preferences, rules)

  lazy val describedPanel = Project (
    "describedPanel",
    file("describedPanel"),
    settings = addDeps (scalazCheckET ++
      Seq (scalaSwing, efaCore, efaIo, efaNb, efaReact))
  ) dependsOn (core, preferences)

  lazy val explorer = Project (
    "explorer",
    file("explorer"),
    settings = addDeps (scalazCheckET ++
      Seq (scalaSwing, efaCore, efaNb, nbFilesystems, nbLoaders))
  ) dependsOn (items, preferences)

  lazy val items = Project (
    "items",
    file("items"),
    settings = addDeps (scalazCheckET ++
      Seq (scalaSwing, efaCore, efaIo, efaNb, nbLoaders, nbFilesystems % "test")) :+ (
        parallelExecution in Test := false
      )
  ) dependsOn (core, preferences)

  lazy val preferences = Project (
    "preferences",
    file("preferences"),
    settings = addDeps (scalazCheckET ++Seq (scalaSwing, efaCore,
      efaIo, nbFilesystems))
  )
  
  lazy val rules = Project (
    "rules",
    file("rules"),
    settings = addDeps (scalazCheckET ++ Seq(efaCore, efaIo, efaReact))
  )
  
  lazy val rulesUI = Project (
    "rulesUI",
    file("rulesUI"),
    settings = addDeps (scalazCheckET ++
      Seq(efaCore, efaIo, efaReact, efaNb, nbNodes))
  ) dependsOn (rules)
}

// vim: set ts=2 sw=2 et:
