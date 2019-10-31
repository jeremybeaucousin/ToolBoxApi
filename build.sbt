name := """ToolBoxApi"""
organization := "fr.scalian"

version := "1.0-SNAPSHOT"

maintainer := "Jeremy BEAUCOUSIN<jeremy.beaucousin@scalian.com>"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.0"

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += ehcache

libraryDependencies += "com.hhandoko" %% "play27-scala-pdf" % "4.2.0"

// Tests
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "fr.scalian.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "fr.scalian.binders._"

// mainClass in assembly := Some("play.core.server.ProdServerStart")
// fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value)
// 
// assemblyMergeStrategy in assembly := {
//   case manifest if manifest.contains("MANIFEST.MF") =>
//     // We don't need manifest files since sbt-assembly will create
//     // one with the given settings
//     MergeStrategy.discard
//   case referenceOverrides if referenceOverrides.contains("reference-overrides.conf") =>
//     // Keep the content for all reference-overrides.conf files
//     MergeStrategy.concat
//   case x =>
//     // For all the other files, use the default sbt-assembly merge strategy
//     val oldStrategy = (assemblyMergeStrategy in assembly).value
//     oldStrategy(x)
// }
