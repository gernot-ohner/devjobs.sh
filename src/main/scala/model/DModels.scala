package dev.ohner
package model

import java.util.UUID

case class DListing(id: UUID, company: String, text: String) {}
case class DLocation(id: UUID, name: String)
case class DTechnology(id: UUID, name: String)
case class DListingToLocation(listingId: UUID, locationId: UUID)
case class DListingToTechnology(listingId: UUID, technologyId: UUID)
