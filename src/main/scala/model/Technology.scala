package dev.ohner
package model

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

// TODO why are these defs rather than vals?
class Technology(tag: Tag) extends Table[String](tag, "TECHNOLOGY") {
  def name = column[String]("TECH_NAME", O.PrimaryKey)
  def * : ProvenShape[String] = name
  def name_index = index("name_idx", name, unique=true)
}

object Technology {
  def fromJobListing(jobListing: JobListing): Seq[String] = {
    jobListing.technologies.map(_.*).distinct
  }
}

// TODO what is the point of this now, that it's literally a wrapper around a string?
class TechnologyEntry(val name: String){
  def * : String = name
}
