package dev.ohner
package service

import model.{Listing, ListingToLocation, Location}

import slick.lifted.TableQuery

object Queries {

  lazy val locations = TableQuery[Location]
  lazy val listings = TableQuery[Listing]
  lazy val listingsToLocations = TableQuery[ListingToLocation]

}
