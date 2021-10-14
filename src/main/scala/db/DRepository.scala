package dev.ohner
package db

import model._

import doobie.implicits._
import doobie.postgres.implicits._

import java.util.UUID

object DRepository {
  def insertLocationRelation(listingId: UUID, location: DLocation) = {
    val name = location.name
    sql"""INSERT INTO listings_to_locations (listing_id, location) values ($listingId, $name)"""
  }

//  def insertLocation(location: DLocation) = {
//    val id = UUID.randomUUID()
//    val name = location.name
//    println("preparing insert statement for " + name)
//    sql"""INSERT INTO locations (id, name) values ($id, $name)"""
//  }

  def insertTechnologyRelation(listingId: UUID, tech: DTechnology) = {
    val name = tech.name
    sql"""INSERT INTO listings_to_technologies (listing_id, technology) values ($listingId, $name)"""
//    sql"""INSERT INTO technologies (id, name) values ($id, $name)"""
  }
//  def insertTechnology(tech: DTechnology) = {
//    val id = UUID.randomUUID()
//    val name = tech.name
//    sql"""INSERT INTO technologies (id, name) values ($id, $name)"""
//  }

  def insertListing(listing: DListing) = {
    val id = listing.id
    val company = listing.company
    val text = listing.text
    sql"""INSERT INTO
    listings (id, company, text) values (
         $id, $company, $text)"""
  }

  def createTableListingsToLocations =
    sql"""
         CREATE TABLE IF NOT EXISTS listings_to_locations (
             listing_id uuid,
             location varchar,
--              location_id uuid,
             CONSTRAINT fk_listing FOREIGN KEY(listing_id) REFERENCES listings(id)
--              CONSTRAINT fk_location FOREIGN KEY (location_id) REFERENCES locations(id)
         )
       """

  def createTableListingsToTechnologies =
    sql"""
         CREATE TABLE IF NOT EXISTS listings_to_technologies (
             listing_id uuid,
             technology varchar,
--              tech_id uuid,
             CONSTRAINT fk_listing FOREIGN KEY(listing_id) REFERENCES listings(id)
--              CONSTRAINT fk_tech FOREIGN KEY (tech_id) REFERENCES technologies(id)
         )
       """

  def createTableListings =
    sql"""
         CREATE TABLE IF NOT EXISTS listings (
             id uuid primary key,
             company varchar,
             text varchar
         )
       """

  def createTableLocations =
    sql"""
        CREATE TABLE IF NOT EXISTS locations (
            id uuid primary key,
            name varchar
        );
--         CREATE INDEX location_idx ON locations (name)
       """

  def createTableTechnologies =
    sql"""
        CREATE TABLE IF NOT EXISTS technologies (
            id uuid primary key,
            name varchar
        );
--         CREATE INDEX tech_idx on technologies (name);
       """

  def listings =
    sql"""
        SELECT id, company, text
        FROM listings
    """.query[DListing]

  def listingsByCompany(name: String) =
    sql"""
        SELECT id, company, text
        FROM listings
        where company LIKE $name
    """.query[DListing]

  def listingsByTechnology(tech: String) =
    sql"""
        SELECT l.id, l.company, l.text
        FROM listings as l
        INNER JOIN listings_to_technologies as lt ON l.id = lt.listing_id
        WHERE lt.technology = $tech
    """.query[DListing]

  def listingsByLocation(location: String) =
    sql"""
        SELECT l.id, l.company, l.text
        FROM listings as l
        INNER JOIN listings_to_locations as ll ON l.id = ll.listing_id
        WHERE ll.location = $location
    """.query[DListing]

  def listingsByLocationAndTechnology(location: String, tech: String) =
    sql"""
        SELECT l.id, l.company, l.text
        FROM listings as l
        INNER JOIN listings_to_locations as ll on l.id = ll.listing_id
        INNER JOIN listings_to_technologies as lt on l.id = lt.listing_id
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
