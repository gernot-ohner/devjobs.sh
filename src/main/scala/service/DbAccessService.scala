package dev.ohner
package service

import service.Queries.{listings, listingsToLocations, locations}

import slick.dbio.DBIO
import slick.jdbc.JdbcBackend
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._

object DbAccessService {

  def establishConnection(): JdbcBackend.DatabaseDef = {
    Database.forURL(
      url = "jdbc:postgresql://localhost:5432/postgres",
      user = "postgres",
      password = "example",
      driver = "org.postgresql.Driver")
    // TODO don't forget to close this db connection
  }

  def fixtures(): DBIOAction[Unit, NoStream, Effect.Schema with Effect.Write] = {
    DBIO.seq(
      locations.schema.create,
      locations += (1, "New York City"),
      locations += (2, "Boston"),

      listings.schema.create,
      listings += (1, "Y Combinator"),
      listings += (2, "Amazon"),

      listingsToLocations.schema.create,
      listingsToLocations += (1, 1),
      listingsToLocations += (2, 1),
      listingsToLocations += (2, 2),
    )
  }
}
