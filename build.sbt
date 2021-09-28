name := "devjobs"

version := "0.1"

scalaVersion := "2.13.6"


idePackagePrefix := Some("dev.ohner")

libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.3.14"
libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.28.0"

val circeVersion = "0.14.1"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)
