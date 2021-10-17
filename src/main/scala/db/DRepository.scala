package dev.ohner
package db

import model._

import doobie.implicits._
import doobie.postgres.implicits._

import java.util.UUID

object DRepository {
  def insertLocationRelation(listingId: UUID, locationId: UUID) = {
    sql"""INSERT INTO listings_to_locations (listing_id, location_id) values ($listingId, $locationId)"""
  }

  def insertLocation(location: DLocation) = {
    val id = location.id
    val name = location.name
    println("preparing insert statement for " + name)
    sql"""INSERT INTO locations (id, name) values ($id, $name)"""
  }

  def insertTechnologyRelation(listingId: UUID, techId: UUID) = {
    sql"""INSERT INTO listings_to_technologies (listing_id, tech_id) values ($listingId, $techId)"""
  }

  def insertTechnology(tech: DTechnology) = {
    val id = tech.id
    val name = tech.name
    sql"""INSERT INTO technologies (id, name) values ($id, $name)"""
  }

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
             location_id uuid,
             CONSTRAINT fk_listing FOREIGN KEY(listing_id) REFERENCES listings(id),
             CONSTRAINT fk_location FOREIGN KEY (location_id) REFERENCES locations(id)
         )
       """

  def createTableListingsToTechnologies =
    sql"""
         CREATE TABLE IF NOT EXISTS listings_to_technologies (
             listing_id uuid,
             tech_id uuid,
             CONSTRAINT fk_listing FOREIGN KEY(listing_id) REFERENCES listings(id),
             CONSTRAINT fk_tech FOREIGN KEY (tech_id) REFERENCES technologies(id)
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

  // TODO can it happen that I have the same listing show up twice
  //   when I search for e.g. java?
  def listingsByTechnology(tech: String) =
    sql"""
        SELECT l.id, l.company, l.text
        FROM listings as l
        INNER JOIN listings_to_technologies lt on l.id = lt.listing_id
        INNER JOIN technologies as t ON t.id = lt.tech_id
        WHERE t.name = $tech
    """.query[DListing]

  def listingsByLocation(location: String) =
    sql"""
        SELECT l.id, l.company, l.text
        FROM listings as l
        INNER JOIN listings_to_locations ltl on l.id = ltl.listing_id
        INNER JOIN locations l2 on l2.id = ltl.location_id
        WHERE l2.name = $location
    """.query[DListing]

  def listingsByLocationAndTechnology(location: String, tech: String) =
    sql"""
        SELECT l.id, l.company, l.text
        FROM listings as l
        INNER JOIN listings_to_locations ltl on l.id = ltl.listing_id
        INNER JOIN locations l2 on l2.id = ltl.location_id
        INNER JOIN listings_to_technologies ltt on l.id = ltt.listing_id
        INNER JOIN technologies t on t.id = ltt.tech_id
        WHERE l2.name = $location
        AND t.name = $tech
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
