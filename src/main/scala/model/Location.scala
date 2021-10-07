package dev.ohner
package model

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape


// TODO name should be an index
class Location(tag: Tag) extends Table[String](tag, "LOCATIONS") {
  def name = column[String]("LOC_NAME", O.PrimaryKey)
  def * : ProvenShape[String] = name
  def name_index = index("name_idx", name, unique=true)
}

object Location {
  def fromJobListing(jobListing: JobListing): Seq[String] = {
    jobListing.locations.map(_.*).distinct
  }
}

class LocationEntry(val name: String) {
  def * : String = name
}
