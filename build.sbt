name := """ToolBoxApi"""
organization := "fr.scalian"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.0"

libraryDependencies += guice
libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.18.7-play27"
libraryDependencies += "org.reactivemongo" %% "reactivemongo-play-json" % "0.18.7-play27"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "fr.scalian.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "fr.scalian.binders._"
