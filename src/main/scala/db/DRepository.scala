package dev.ohner
package db

import config.{DbConfig, FullConfig}
import model._

import cats.effect._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor.Aux

class DRepository(val xa: Aux[IO, Unit]) {
  def listings =
    sql"""
        SELECT id, company, fulltext
        FROM listings
    """.query[DListing].to[List].transact(xa)

  def listingsByCompany(name: String) =
    sql"""
        SELECT id, company, fulltext
        FROM listings
        where company LIKE $name
    """.query[DListing].to[List].transact(xa)


  def listingByTechnology(tech: String) =
    sql"""
        SELECT l.id, l.company, l.fulltext
        FROM listings as l
        INNER JOIN listing_to_technology as lt ON l.id = lt.listing_id
        WHERE lt.technology = $tech
    """.query[DListing].to[List].transact(xa)


  def listingByLocation(location: String) =
    sql"""
        SELECT l.id, l.company, l.fulltext
        FROM listings as l
        INNER JOIN listing_to_location as ll ON l.id = ll.listing_id
        WHERE ll.location = $location
    """.query[DListing].to[List].transact(xa)

  def listingByLocationAndTechnology(location: String, tech: String) =
    sql"""
        SELECT l.id, l.company, l.fulltext
        FROM listings as l
        INNER JOIN listing_to_location as ll on l.id = ll.listing_id
        INNER JOIN listing_to_technology as lt on l.id = lt.listing_id
        WHERE ll.location = $location
        AND   lt.technology = $tech
    """.query[DListing].to[List].transact(xa)

  def locations =
    sql"""
        SELECT *
        FROM locations
    """.query[DLocation].to[List].transact(xa)

  def technologies =
    sql"""
        SELECT *
        FROM technologies
    """.query[DTechnology].to[List].transact(xa)
}

object DRepository {
  def fromDefaultConfig = {
    val dbConfig = FullConfig.load.map(c => c.database)
    dbConfig.map(dbc => new DRepository(
        Transactor.fromDriverManager[IO](dbc.driver, dbc.url, dbc.user, dbc.pw)))
  }
}
