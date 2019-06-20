import Dependencies._

ThisBuild / scalaVersion     := "2.12.6"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := ""
ThisBuild / organizationName := ""

lazy val root = (project in file("."))
  .settings(
    name := "onScalaZIO",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "dev.zio" %% "zio" % "1.0.0-RC8-6" 
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
