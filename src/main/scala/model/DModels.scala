package dev.ohner
package model

import io.chrisdavenport.fuuid.FUUID

import java.util.UUID

case class DListing(id: FUUID, company: String, text: String) {}
case class DLocation(id: FUUID, name: String)
case class DTechnology(id: FUUID, name: String)
case class DListingToLocation(listingId: FUUID, locationId: FUUID)
case class DListingToTechnology(listingId: FUUID, technologyId: UUID)
