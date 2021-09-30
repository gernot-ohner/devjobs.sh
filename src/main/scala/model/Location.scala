package dev.ohner
package model

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

import java.util.UUID

// TODO name should be an index
class Location(tag: Tag) extends Table[(UUID, String)](tag, "LOCATIONS") {
  def id = column[UUID]("LOC_ID", O.PrimaryKey)
  def name = column[String]("LOC_NAME")
  def * : ProvenShape[(UUID, String)] = (id, name)
  def name_index = index("name_idx", name, unique=true)
}

object Location {
  def tuplesFromJobListing(jobListing: JobListing): Seq[(UUID, String)] = {
    jobListing.locations.map(_.*)
  }
}

class LocationEntry(val name: String) {
  val id: UUID = UUID.randomUUID()
  def * : (UUID, String) = (id, name)
}
