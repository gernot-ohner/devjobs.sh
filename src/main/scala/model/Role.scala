package dev.ohner
package model

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

class Role(tag: Tag) extends Table[String](tag, "ROLES" ) {
  def name = column[String]("ROLE_NAME", O.PrimaryKey)
  def * : ProvenShape[String]= name
  def name_index = index("name_idx", name, unique=true)
}

object Role {
  def tuplesFromJobListing(jobListing: JobListing): Seq[String] = {
    jobListing.technologies.map(_.*)
  }
}

class RoleEntry(val name: String) {
  def * : String = name
}
