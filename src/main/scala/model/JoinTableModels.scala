package dev.ohner
package model

import service.Queries._

import slick.jdbc.PostgresProfile.api._

import java.util.UUID

class ListingToLocation(tag: Tag) extends Table[(UUID, String)](tag, "LISTING_TO_LOCATION") {
  def listingId = column[UUID]("listingId")
  def locationId = column[String]("location")
  def * = (listingId, locationId)
  def listing = foreignKey("listing_fk", listingId, listings)(_.id)
  def location = foreignKey("location_fk", locationId, locations)(_.name)
}

class ListingToTechnology(tag: Tag) extends Table[(UUID, String)](tag, "LISTING_TO_TECHNOLOGY") {
  def listingId = column[UUID]("listingId")
  def technologyId = column[String]("technology")
  def * = (listingId, technologyId)
  def listing = foreignKey("listing_fk", listingId, listings)(_.id)
  def technology = foreignKey("technology_fk", technologyId, technologies)(_.name)
}
