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
  "io.circe" %% "circe-parser",
).map(_ % circeVersion)

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3",
)

libraryDependencies += "org.postgresql" % "postgresql" % "42.2.24"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test"

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "scalatags" % "0.9.4",
  "com.lihaoyi" %% "cask" % "0.7.12"
)
