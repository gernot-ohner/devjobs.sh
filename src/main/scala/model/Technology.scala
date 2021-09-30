package dev.ohner
package model

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

import java.util.UUID

// TODO why are these defs rather than vals?
class Technology(tag: Tag) extends Table[(UUID, String)](tag, "TECHNOLOGY") {
  def id = column[UUID]("TECH_ID", O.PrimaryKey)
  def name = column[String]("TECH_NAME")
  def * : ProvenShape[(UUID, String)] = (id, name)
  def name_index = index("name_idx", name, unique=true)
}

object Technology {
  def tuplesFromJobListing(jobListing: JobListing): Seq[(UUID, String)] = {
    jobListing.technologies.map(_.*)
  }
}

class TechnologyEntry(val name: String){
  val id: UUID = UUID.randomUUID()
  def * : (UUID, String) = (id, name)
}
