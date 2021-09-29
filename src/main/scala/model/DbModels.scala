package dev.ohner
package model

import service.Queries.{listings, listingsToLocations, locations}

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

class Location(tag: Tag) extends Table[(Int, String)](tag, "LOCATIONS") {
  def id = column[Int]("LOC_ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("LOC_NAME") // TODO how do I deal with synonyms/aliases?
  // TODO let the data flow through a stream that converts all of the synonyms into one name before
  //   commmiting to the db
  def listings = listingsToLocations
    .filter(_.locationId === id)
    .flatMap(x => x.listing)
  def * : ProvenShape[(Int, String)] = (id, name)
}

case class Listing(tag: Tag) extends Table[(Int, String)](tag, "LISTINGS") {
  def id = column[Int]("LISTING_ID", O.PrimaryKey, O.AutoInc)
  def company = column[String]("LISTING_COMPANY")
  def locations = listingsToLocations
    .filter(_.listingId === id)
    .flatMap(x => x.location)
  def * = (id, company)
}

class ListingToLocation(tag: Tag) extends Table[(Int, Int)](tag, "LISTING_TO_LOCATION") {
  def listingId = column[Int]("listingId")
  def locationId = column[Int]("locationId")
  def * = (listingId, locationId)
  def listing = foreignKey("listing_fk", listingId, listings)(_.id)
  def location = foreignKey("location_fk", locationId, locations)(_.id)
}

