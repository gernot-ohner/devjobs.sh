package dev.ohner
package db

import model._

import doobie.implicits._
import doobie.postgres.implicits._

object DRepository {

  def listings =
    sql"""
        SELECT id, company, fulltext
        FROM listings
    """.query[DListing]

  def listingsByCompany(name: String) =
    sql"""
        SELECT id, company, fulltext
        FROM listings
        where company LIKE $name
    """.query[DListing]

  def listingsByTechnology(tech: String) =
    sql"""
        SELECT l.id, l.company, l.fulltext
        FROM listings as l
        INNER JOIN listing_to_technology as lt ON l.id = lt.listing_id
        WHERE lt.technology = $tech
    """.query[DListing]

  def listingsByLocation(location: String) =
    sql"""
        SELECT l.id, l.company, l.fulltext
        FROM listings as l
        INNER JOIN listing_to_location as ll ON l.id = ll.listing_id
        WHERE ll.location = $location
    """.query[DListing]

  def listingsByLocationAndTechnology(location: String, tech: String) =
    sql"""
        SELECT l.id, l.company, l.fulltext
        FROM listings as l
        INNER JOIN listing_to_location as ll on l.id = ll.listing_id
        INNER JOIN listing_to_technology as lt on l.id = lt.listing_id
        WHERE ll.location = $location
        AND   lt.technology = $tech
    """.query[DListing]

  def locations =
    sql"""
        SELECT *
        FROM locations
    """.query[DLocation]

  def technologies =
    sql"""
        SELECT *
        FROM technologies
    """.query[DTechnology]
}
