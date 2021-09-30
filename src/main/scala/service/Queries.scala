package dev.ohner
package service

import model.{Listing, ListingToLocation, ListingToTechnology, Location, Technology}

import slick.lifted.TableQuery

object Queries {

  lazy val locations = TableQuery[Location]
  lazy val listings = TableQuery[Listing]
  lazy val technologies = TableQuery[Technology]
  lazy val listingsToLocations = TableQuery[ListingToLocation]
  lazy val listingsToTechnologies = TableQuery[ListingToTechnology]

  val all = Vector(listingsToLocations, listingsToTechnologies, locations, listings, technologies)

}
