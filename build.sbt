name := "devjobs"

version := "0.1"

scalaVersion := "2.13.6"


idePackagePrefix := Some("dev.ohner")

lazy val circeVersion = "0.14.1"
lazy val http4sVersion = "0.23.5"
lazy val doobieVersion = "1.0.0-RC1"


libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
)

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-scalatags" % http4sVersion
)

libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core"     % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-scalatest"   % doobieVersion
)

libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.17.0"
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.24"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test"
libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.3.15"
libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.30.0"
