name := "sunrise.scala"

organization := "com.oschrenk.spacetime"

version := "0.6.0-SNAPSHOT"

scalaVersion := "2.12.4"

crossScalaVersions := Seq("2.11.11", "2.12.4")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

scalacOptions ++= Seq(
    "-target:jvm-1.8",
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-Ywarn-unused-import",
    "-Xlint",
    "-Ywarn-dead-code",
    "-Xfuture")

initialCommands := "import com.oschrenk.sunrise._"

bintrayOmitLicense := true
