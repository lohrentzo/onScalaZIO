import Dependencies._

lazy val root = (project in file("."))
    .settings(
        inThisBuild(List(
          organization := "",
          scalaVersion := "2.12.7",
          version      := "0.1.0-SNAPSHOT"
        )),
        name := "onScalaZIO",
        libraryDependencies ++= Seq(
          "org.scalaz" %% "scalaz-zio" % "0.3.1"
        ),
        trapExit := false
      )
//  .settings(
//    name := "onScalaZIO",
//    libraryDependencies += scalaTest % Test,
//    libraryDependencies += "dev.zio" %% "zio" % "1.0.0-RC8-6" 
//  )

