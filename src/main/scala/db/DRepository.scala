package dev.ohner
package db

import model._

import cats.effect._
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux
import doobie.postgres.implicits._

object DRepository {

  // TODO obviously, I actually want to get this data from a config file
  val xa: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5432/postgres",
    "postgres",
    "example",
  )


  def listings: doobie.Query0[DListing] =
    sql"""
        SELECT id, company, fulltext
        FROM listings
    """.query[DListing]

  def listingsByCompany(name: String): doobie.Query0[DListing] =
    sql"""
        SELECT id, company, fulltext
        FROM listings
        where company LIKE $name
    """.query[DListing]

  def listingByTechnology(tech: String): doobie.Query0[DListing] =
    sql"""
        SELECT l.id, l.company, l.fulltext
        FROM listings as l
        INNER JOIN listing_to_technology as lt ON l.id = lt.listing_id
        WHERE lt.technology = $tech
    """.query[DListing]


  def listingByLocation(location: String): doobie.Query0[DListing] =
    sql"""
        SELECT l.id, l.company, l.fulltext
        FROM listings as l
        INNER JOIN listing_to_location as ll ON l.id = ll.listing_id
        WHERE ll.location = $location
    """.query[DListing]

  def listingByLocationAndTechnology(location: String, tech: String): doobie.Query0[DListing] =
    sql"""
        SELECT l.id, l.company, l.fulltext
        FROM listings as l
        INNER JOIN listing_to_location as ll on l.id = ll.listing_id
        INNER JOIN listing_to_technology as lt on l.id = lt.listing_id
        WHERE ll.location = $location
        AND   lt.technology = $tech
       """.query[DListing]

  def locations: doobie.Query0[DLocation] =
    sql"""
        SELECT *
        FROM locations
       """.query[DLocation]

  def technologies: doobie.Query0[DTechnology] =
    sql"""
        SELECT *
        FROM technologies
       """.query[DTechnology]
}
