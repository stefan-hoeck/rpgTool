import sbt._
import Keys._

object BuildSettings {
  import Resolvers._

  val sv = "2.10.0"
  val buildOrganization = "efa.rpg"
  val buildVersion = "1.0.0-SNAPSHOT"
  val buildScalaVersion = sv

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version := buildVersion,
    scalaVersion := buildScalaVersion,
    resolvers ++= repos,
    scalacOptions ++= Seq ("-deprecation", "-feature", "-language:higherKinds",
      "-language:postfixOps"),
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
  import BuildSettings.sv  

  val utilVersion = "0.2.0-SNAPSHOT"
  val reactVersion = "0.1.0"
  val util = "efa"
  val react = "efa.react"
  val efaCore = util %% "efa-core" % utilVersion changing

  val efaData = util %% "efa-data" % utilVersion changing

  val efaIo = util %% "efa-io" % utilVersion changing

  val efaNb = util %% "efa-nb" % utilVersion changing

  val efaReact = react %% "react-core" % reactVersion

  val efaReactSwing = react %% "react-swing" % reactVersion


  val nbV = "RELEASE71"

  val scalaSwing = "org.scala-lang" % "scala-swing" % sv
 
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

  val scalaz_core = "org.scalaz" %% "scalaz-core" % "7.0.0-M7"
  val scalaz_effect = "org.scalaz" %% "scalaz-effect" % "7.0.0-M7"
  val scalaz_scalacheck =
    "org.scalaz" %% "scalaz-scalacheck-binding" % "7.0.0-M7"
  val scalaz_scalacheckT = scalaz_scalacheck % "test"

  val scalacheck = "org.scalacheck" %% "scalacheck" % "1.10.0"
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
    "rpg-core",
    file("core"),
    settings = addDeps (scalazCheckET ++ Seq(scalaSwing, efaCore, efaData))
  )

  lazy val being = Project (
    "rpg-being",
    file("being"),
    settings = addDeps (scalazCheckET ++
      Seq (scalaSwing, efaCore, efaIo, efaNb, efaReact, efaData, nbLoaders,
        nbFilesystems, nbMultiview, nbWindows, nbAwt, nbExplorer))
  ) dependsOn (core, preferences, rules)

  lazy val describedPanel = Project (
    "rpg-describedPanel",
    file("describedPanel"),
    settings = addDeps (scalazCheckET ++
      Seq (scalaSwing, efaCore, efaData, efaIo, efaNb, efaReact))
  ) dependsOn (core, preferences)

  lazy val explorer = Project (
    "rpg-explorer",
    file("explorer"),
    settings = addDeps (scalazCheckET ++
      Seq (scalaSwing, efaCore, efaData, efaNb, nbFilesystems, nbLoaders))
  ) dependsOn (items, preferences)

  lazy val items = Project (
    "rpg-items",
    file("items"),
    settings = addDeps (scalazCheckET ++
      Seq (scalaSwing, efaCore, efaData, efaIo, efaNb, nbLoaders, nbFilesystems % "test")) :+ (
        parallelExecution in Test := false
      )
  ) dependsOn (core, preferences)

  lazy val preferences = Project (
    "rpg-preferences",
    file("preferences"),
    settings = addDeps (scalazCheckET ++Seq (scalaSwing, efaCore,
      efaIo, nbFilesystems))
  )
  
  lazy val rules = Project (
    "rpg-rules",
    file("rules"),
    settings = addDeps (scalazCheckET ++ Seq(efaCore, efaIo, efaReact, efaData))
  )
  
  lazy val rulesUI = Project (
    "rpg-rulesUI",
    file("rulesUI"),
    settings = addDeps (scalazCheckET ++
      Seq(efaCore, efaIo, efaReact, efaNb, nbNodes))
  ) dependsOn (rules)
}

// vim: set ts=2 sw=2 et nowrap:
