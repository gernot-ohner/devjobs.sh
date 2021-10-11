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
//libraryDependencies += "com.lihaoyi" %% "scalatags" % "0.9.4"

val http4sVersion = "0.23.4"
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-scalatags" % http4sVersion
)

lazy val doobieVersion = "1.0.0-RC1"
libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core"     % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-specs2"   % doobieVersion
)
